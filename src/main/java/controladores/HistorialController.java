package controladores;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import persistencia.RegistroCompra;
import util.Alertas;
import util.Navegacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HistorialController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private DatePicker dpDesde;

    @FXML
    private DatePicker dpHasta;

    @FXML
    private TableView<RegistroCompra> tablaCompras;

    @FXML
    private TableColumn<RegistroCompra, String> colFecha;

    @FXML
    private TableColumn<RegistroCompra, String> colEvento;

    @FXML
    private TableColumn<RegistroCompra, Integer> colCantidad;

    @FXML
    private TableColumn<RegistroCompra, String> colTotal;

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getFecha().format(FORMATO_FECHA)));
        colEvento.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getNombreEvento()));
        colCantidad.setCellValueFactory(dato ->
                new SimpleIntegerProperty(dato.getValue().getCantidadBoletos()).asObject());
        colTotal.setCellValueFactory(dato ->
                new SimpleStringProperty(String.format(Locale.US, "$%.2f", dato.getValue().getTotal())));
        // Semana 8: cargar en la tabla las compras del usuario con HistorialCompras
    }

    @FXML
    private void filtrar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        if (desde == null || hasta == null) {
            Alertas.mostrarAdvertencia("Historial de compras", "Selecciona las dos fechas.");
            return;
        }
        if (desde.isAfter(hasta)) {
            Alertas.mostrarAdvertencia("Historial de compras",
                    "La fecha \"Desde\" no puede ser posterior a la fecha \"Hasta\".");
            return;
        }
        // Semana 8: filtrar las compras con HistorialCompras
        Alertas.mostrarInformacion("Historial de compras", "El filtro por fechas se conectará en la semana 8.");
    }

    @FXML
    private void volver() {
        Navegacion.cambiarPantalla("Eventos.fxml", "Eventos Disponibles");
    }
}