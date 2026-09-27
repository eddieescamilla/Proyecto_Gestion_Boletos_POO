package controladores;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Evento;
import util.Alertas;
import util.Navegacion;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventosController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TableView<Evento> tablaEventos;

    @FXML
    private TableColumn<Evento, String> colNombre;

    @FXML
    private TableColumn<Evento, String> colCategoria;

    @FXML
    private TableColumn<Evento, String> colFecha;

    @FXML
    private TableColumn<Evento, String> colLugar;

    @FXML
    private TableColumn<Evento, Integer> colStock;

    @FXML
    private TableColumn<Evento, String> colPrecio;

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getNombreEvento()));
        colCategoria.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getCategoria()));
        colFecha.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getFecha().format(FORMATO_FECHA)));
        colLugar.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getLugar()));
        colStock.setCellValueFactory(dato ->
                new SimpleIntegerProperty(dato.getValue().getInventarioDisponible()).asObject());
        colPrecio.setCellValueFactory(dato ->
                new SimpleStringProperty(String.format(Locale.US, "$%.2f", dato.getValue().getPrecioBoleto())));
        // Semana 8: cargar los eventos desde la base de datos con SistemaGestionBoletos
    }

    @FXML
    private void comprar() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.mostrarAdvertencia("Comprar boletos", "Selecciona un evento de la tabla.");
            return;
        }
        CompraController compra = Navegacion.cambiarPantalla("Compra.fxml", "Comprar Boletos");
        compra.setEvento(seleccionado);
    }

    @FXML
    private void verHistorial() {
        Navegacion.cambiarPantalla("Historial.fxml", "Historial de Compras");
    }

    @FXML
    private void cerrarSesion() {
        if (Alertas.confirmar("Cerrar sesión", "¿Deseas cerrar tu sesión?")) {
            Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
        }
    }
}