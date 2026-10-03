package controladores;

import catalogo.Categoria;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Evento;
import model.Usuario;
import persistencia.CompraPersistencia;
import persistencia.EventoPersistencia;
import persistencia.UsuarioPersistencia;
import util.Alertas;
import util.Navegacion;
import util.Sesion;

/**
 * Controlador del panel de administración ({@code PanelAdmin.fxml}).
 *
 * <p>Agrupa la gestión de eventos, la gestión de usuarios y el reporte de ventas por
 * categoría en tres pestañas.
 */
public class PanelAdminController {

  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private EventoPersistencia eventoPersistencia;
  private UsuarioPersistencia usuarioPersistencia;
  private CompraPersistencia compraPersistencia;

  // ===== Pestana Eventos =====
  @FXML
  private TableView<Evento> tablaEventos;

  @FXML
  private TableColumn<Evento, String> colNombre;

  @FXML
  private TableColumn<Evento, String> colCategoria;

  @FXML
  private TableColumn<Evento, String> colFecha;

  @FXML
  private TableColumn<Evento, Integer> colStock;

  @FXML
  private TableColumn<Evento, String> colPrecio;

  @FXML
  private TextField txtNombre;

  @FXML
  private ComboBox<Categoria> cmbCategoria;

  @FXML
  private DatePicker dpFecha;

  @FXML
  private TextField txtLugar;

  @FXML
  private TextField txtInventario;

  @FXML
  private TextField txtPrecio;

  // ===== Pestana Usuarios =====
  @FXML
  private TableView<Usuario> tablaUsuarios;

  @FXML
  private TableColumn<Usuario, String> colNombreUsuario;

  @FXML
  private TableColumn<Usuario, String> colCorreoUsuario;

  @FXML
  private TableColumn<Usuario, String> colRolUsuario;

  @FXML
  private TableColumn<Usuario, String> colEstadoUsuario;

  // ===== Pestana Reportes =====
  @FXML
  private ComboBox<Categoria> cmbCategoriaReporte;

  @FXML
  private TextArea txtReporte;

  @FXML
  private void initialize() {
    configurarTablaEventos();
    configurarTablaUsuarios();
    cmbCategoria.getItems().setAll(Categoria.values());
    cmbCategoriaReporte.getItems().setAll(Categoria.values());
    tablaEventos.getSelectionModel().selectedItemProperty()
        .addListener((observable, anterior, seleccionado) -> mostrarEnFormulario(seleccionado));

    eventoPersistencia = new EventoPersistencia();
    usuarioPersistencia = new UsuarioPersistencia();
    compraPersistencia = new CompraPersistencia();
    cargarEventos();
    cargarUsuarios();
  }

  /** Corre una tarea en un hilo demonio para no bloquear la interfaz. */
  private void ejecutarEnSegundoPlano(Task<?> tarea) {
    Thread hilo = new Thread(tarea);
    hilo.setDaemon(true);
    hilo.start();
  }

  private void cargarEventos() {
    Task<List<Evento>> tarea = new Task<>() {
      @Override
      protected List<Evento> call() {
        return eventoPersistencia.listarTodos();
      }
    };
    tarea.setOnSucceeded(evento -> tablaEventos.getItems().setAll(tarea.getValue()));
    tarea.setOnFailed(evento -> Alertas.mostrarError("Panel de administración",
        "No se pudieron cargar los eventos. Verifica que Docker esté en ejecución."));
    ejecutarEnSegundoPlano(tarea);
  }

  private void cargarUsuarios() {
    Task<List<Usuario>> tarea = new Task<>() {
      @Override
      protected List<Usuario> call() {
        return usuarioPersistencia.listarTodos();
      }
    };
    tarea.setOnSucceeded(evento -> tablaUsuarios.getItems().setAll(tarea.getValue()));
    tarea.setOnFailed(evento -> Alertas.mostrarError("Panel de administración",
        "No se pudieron cargar los usuarios. Verifica que Docker esté en ejecución."));
    ejecutarEnSegundoPlano(tarea);
  }

  private void configurarTablaEventos() {
    colNombre.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().getNombreEvento()));
    colCategoria.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().getCategoria()));
    colFecha.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().getFecha().format(FORMATO_FECHA)));
    colStock.setCellValueFactory(dato ->
        new SimpleIntegerProperty(dato.getValue().getInventarioDisponible()).asObject());
    colPrecio.setCellValueFactory(dato ->
        new SimpleStringProperty(
            String.format(Locale.US, "$%.2f", dato.getValue().getPrecioBoleto())));
  }

  private void configurarTablaUsuarios() {
    colNombreUsuario.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().getNombre()));
    colCorreoUsuario.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().getCorreo()));
    colRolUsuario.setCellValueFactory(dato ->
        new SimpleStringProperty(formatearRol(dato.getValue())));
    colEstadoUsuario.setCellValueFactory(dato ->
        new SimpleStringProperty(dato.getValue().isActivo() ? "Activo" : "Inactivo"));

    colEstadoUsuario.setCellFactory(columna -> new TableCell<>() {
      @Override
      protected void updateItem(String estado, boolean vacia) {
        super.updateItem(estado, vacia);
        setText(null);
        if (vacia || estado == null) {
          setGraphic(null);
          return;
        }
        Label etiqueta = new Label(estado);
        etiqueta.getStyleClass().add(estado.equals("Activo") ? "estado-activo" : "estado-inactivo");
        setGraphic(etiqueta);
      }
    });
  }

  private String formatearRol(Usuario usuario) {
    String rol = usuario.getRol().name();
    return rol.charAt(0) + rol.substring(1).toLowerCase();
  }

  private void mostrarEnFormulario(Evento evento) {
    if (evento == null) {
      return;
    }
    txtNombre.setText(evento.getNombreEvento());
    cmbCategoria.setValue(Categoria.desdeValorBD(evento.getCategoria()));
    dpFecha.setValue(evento.getFecha());
    txtLugar.setText(evento.getLugar());
    txtInventario.setText(String.valueOf(evento.getInventarioDisponible()));
    txtPrecio.setText(String.format(Locale.US, "%.2f", evento.getPrecioBoleto()));
  }

  private boolean formularioValido() {
    if (txtNombre.getText().isBlank() || cmbCategoria.getValue() == null
        || dpFecha.getValue() == null || txtLugar.getText().isBlank()) {
      Alertas.mostrarAdvertencia("Formulario de evento",
          "Completa todos los campos del formulario.");
      return false;
    }
    if (dpFecha.getValue().isBefore(LocalDate.now())) {
      Alertas.mostrarAdvertencia("Formulario de evento",
          "La fecha del evento no puede ser pasada.");
      return false;
    }
    try {
      if (Integer.parseInt(txtInventario.getText().trim()) < 0) {
        Alertas.mostrarAdvertencia("Formulario de evento", "El inventario no puede ser negativo.");
        return false;
      }
    } catch (NumberFormatException e) {
      Alertas.mostrarAdvertencia("Formulario de evento", "Ingresa un inventario válido (ej. 100).");
      return false;
    }
    try {
      if (Double.parseDouble(txtPrecio.getText().trim()) <= 0) {
        Alertas.mostrarAdvertencia("Formulario de evento", "El precio debe ser mayor a cero.");
        return false;
      }
    } catch (NumberFormatException e) {
      Alertas.mostrarAdvertencia("Formulario de evento", "Ingresa un precio válido (ej. 25.00).");
      return false;
    }
    return true;
  }

  @FXML
  private void agregarEvento() {
    if (!formularioValido() || eventoPersistencia == null) {
      return;
    }
    final String nombre = txtNombre.getText().trim();
    final Evento nuevo;
    try {
      nuevo = new Evento(
          nombre,
          cmbCategoria.getValue().getValorBD(),
          dpFecha.getValue(),
          txtLugar.getText().trim(),
          Integer.parseInt(txtInventario.getText().trim()),
          Double.parseDouble(txtPrecio.getText().trim()));
    } catch (RuntimeException e) {
      Alertas.mostrarError("Agregar evento", "Datos del formulario inválidos.");
      return;
    }
    Task<Boolean> tarea = new Task<>() {
      @Override
      protected Boolean call() {
        if (eventoPersistencia.buscarPorId(nombre) != null) {
          return false;
        }
        eventoPersistencia.guardar(nuevo);
        return true;
      }
    };
    tarea.setOnSucceeded(evento -> {
      if (Boolean.TRUE.equals(tarea.getValue())) {
        tablaEventos.getItems().add(nuevo);
        Alertas.mostrarInformacion("Agregar evento", "Evento agregado correctamente.");
      } else {
        Alertas.mostrarAdvertencia("Agregar evento",
            "Ya existe un evento con el nombre \"" + nombre + "\".");
      }
    });
    tarea.setOnFailed(evento ->
        Alertas.mostrarError("Agregar evento", "No se pudo guardar el evento."));
    ejecutarEnSegundoPlano(tarea);
  }

  @FXML
  private void editarEvento() {
    final Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
    if (seleccionado == null) {
      Alertas.mostrarAdvertencia("Editar evento", "Selecciona un evento de la tabla.");
      return;
    }
    if (!formularioValido() || eventoPersistencia == null) {
      return;
    }
    final Evento actualizado;
    try {
      actualizado = new Evento(
          seleccionado.getNombreEvento(),
          cmbCategoria.getValue().getValorBD(),
          dpFecha.getValue(),
          txtLugar.getText().trim(),
          Integer.parseInt(txtInventario.getText().trim()),
          Double.parseDouble(txtPrecio.getText().trim()));
    } catch (RuntimeException e) {
      Alertas.mostrarError("Editar evento", "Datos del formulario inválidos.");
      return;
    }
    Task<Void> tarea = new Task<>() {
      @Override
      protected Void call() {
        eventoPersistencia.actualizarEvento(actualizado);
        return null;
      }
    };
    tarea.setOnSucceeded(evento -> {
      int indice = tablaEventos.getItems().indexOf(seleccionado);
      tablaEventos.getItems().set(indice, actualizado);
      Alertas.mostrarInformacion("Editar evento", "Cambios guardados correctamente.");
    });
    tarea.setOnFailed(evento ->
        Alertas.mostrarError("Editar evento", "No se pudo actualizar el evento."));
    ejecutarEnSegundoPlano(tarea);
  }

  @FXML
  private void eliminarEvento() {
    final Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
    if (seleccionado == null) {
      Alertas.mostrarAdvertencia("Eliminar evento", "Selecciona un evento de la tabla.");
      return;
    }
    if (eventoPersistencia == null) {
      return;
    }
    if (!Alertas.confirmar("Eliminar evento",
        "¿Deseas eliminar el evento \"" + seleccionado.getNombreEvento() + "\"?")) {
      return;
    }
    Task<Void> tarea = new Task<>() {
      @Override
      protected Void call() {
        eventoPersistencia.eliminar(seleccionado.getNombreEvento());
        return null;
      }
    };
    tarea.setOnSucceeded(evento -> {
      tablaEventos.getItems().remove(seleccionado);
      Alertas.mostrarInformacion("Eliminar evento", "Evento eliminado correctamente.");
    });
    tarea.setOnFailed(evento ->
        Alertas.mostrarError("Eliminar evento", "No se pudo eliminar el evento."));
    ejecutarEnSegundoPlano(tarea);
  }

  @FXML
  private void activarUsuario() {
    cambiarEstadoUsuario("Activar usuario", true);
  }

  @FXML
  private void desactivarUsuario() {
    cambiarEstadoUsuario("Desactivar usuario", false);
  }

  private void cambiarEstadoUsuario(String accion, boolean nuevoEstado) {
    final Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
    if (seleccionado == null) {
      Alertas.mostrarAdvertencia(accion, "Selecciona un usuario de la tabla.");
      return;
    }
    if (usuarioPersistencia == null) {
      return;
    }
    if (seleccionado.isActivo() == nuevoEstado) {
      Alertas.mostrarAdvertencia(accion,
          "El usuario ya está " + (nuevoEstado ? "activo." : "inactivo."));
      return;
    }
    Task<Void> tarea = new Task<>() {
      @Override
      protected Void call() {
        usuarioPersistencia.actualizarEstado(seleccionado.getCorreo(), nuevoEstado);
        return null;
      }
    };
    tarea.setOnSucceeded(evento -> {
      seleccionado.setActivo(nuevoEstado);
      tablaUsuarios.refresh();
      Alertas.mostrarInformacion(accion, "Estado del usuario actualizado.");
    });
    tarea.setOnFailed(evento ->
        Alertas.mostrarError(accion, "No se pudo actualizar el estado del usuario."));
    ejecutarEnSegundoPlano(tarea);
  }

  @FXML
  private void generarReporte() {
    final Categoria categoria = cmbCategoriaReporte.getValue();
    if (categoria == null) {
      Alertas.mostrarAdvertencia("Generar reporte", "Selecciona una categoría.");
      return;
    }
    if (compraPersistencia == null) {
      return;
    }
    Task<String> tarea = new Task<>() {
      @Override
      protected String call() {
        Platform.runLater(() -> txtReporte.setText("Generando reporte..."));
        return compraPersistencia.generarReportePorCategoria(categoria.getValorBD());
      }
    };
    tarea.setOnSucceeded(evento -> txtReporte.setText(tarea.getValue()));
    tarea.setOnFailed(evento ->
        Alertas.mostrarError("Generar reporte", "No se pudo generar el reporte."));
    ejecutarEnSegundoPlano(tarea);
  }

  @FXML
  private void cerrarSesion() {
    if (Alertas.confirmar("Cerrar sesión", "¿Deseas cerrar tu sesión?")) {
      Sesion.cerrar();
      Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
    }
  }
}
