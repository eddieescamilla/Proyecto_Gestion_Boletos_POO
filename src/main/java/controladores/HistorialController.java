package controladores;

import hilos.EjecutorTareas;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Comprador;
import model.HistorialCompras;
import persistencia.CompraPersistencia;
import persistencia.RegistroCompra;
import util.Alertas;
import util.Navegacion;
import util.Sesion;

/** Controlador de la pantalla de historial de compras ({@code Historial.fxml}). */
public class HistorialController {

  @FXML
  private Label lblUsuario;

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

  private HistorialCompras historial;

  @FXML
  private void initialize() {
    lblUsuario.setText(Sesion.textoBienvenida());
    historial = new HistorialCompras(new CompraPersistencia());

    colFecha.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().getFecha().format(FORMATO_FECHA)));
    colEvento.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().getNombreEvento()));
    colCantidad.setCellValueFactory(dato ->
        new SimpleIntegerProperty(dato.getValue().getCantidadBoletos()).asObject());
    colTotal.setCellValueFactory(dato ->
        new SimpleStringProperty(String.format(Locale.US, "$%.2f", dato.getValue().getTotal())));

    cargarCompras();
  }

  private void cargarCompras() {
    Comprador comprador = compradorEnSesion();
    if (comprador == null) {
      return;
    }
    Task<List<RegistroCompra>> tarea = new Task<>() {
      @Override
      protected List<RegistroCompra> call() {
        return historial.buscarPorCliente(comprador.getCorreo());
      }
    };
    tarea.setOnSucceeded(e -> mostrarEnTabla(tarea.getValue()));
    tarea.setOnFailed(e -> Alertas.mostrarError("Historial de compras",
        "No se pudo cargar el historial. Verifica que Docker esté en ejecución."));
    EjecutorTareas.ejecutar(tarea);
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
    Comprador comprador = compradorEnSesion();
    if (comprador == null) {
      return;
    }
    Task<List<RegistroCompra>> tarea = new Task<>() {
      @Override
      protected List<RegistroCompra> call() {
        return historial.buscarPorClienteEnRango(comprador.getCorreo(), desde, hasta);
      }
    };
    tarea.setOnSucceeded(e -> mostrarEnTabla(tarea.getValue()));
    tarea.setOnFailed(e -> Alertas.mostrarError("Historial de compras",
        "No se pudo filtrar el historial. Verifica que Docker esté en ejecución."));
    EjecutorTareas.ejecutar(tarea);
  }

  private void mostrarEnTabla(List<RegistroCompra> compras) {
    ObservableList<RegistroCompra> datos = FXCollections.observableArrayList(compras);
    tablaCompras.setItems(datos);
  }

  private Comprador compradorEnSesion() {
    if (!(Sesion.getUsuarioActual() instanceof Comprador comprador)) {
      Alertas.mostrarError("Historial de compras",
          "No hay una sesión de cliente activa. Vuelve a iniciar sesión.");
      Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
      return null;
    }
    return comprador;
  }


  @FXML
  private void volver() {
    Navegacion.cambiarPantalla("Eventos.fxml", "Eventos Disponibles");
  }
}
