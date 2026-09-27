package controladores;

import catalogo.Categoria;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import util.Alertas;
import util.Navegacion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PanelAdminController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        // Semana 8: cargar eventos y usuarios desde la base de datos
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
                new SimpleStringProperty(String.format(Locale.US, "$%.2f", dato.getValue().getPrecioBoleto())));
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
            Alertas.mostrarAdvertencia("Formulario de evento", "Completa todos los campos del formulario.");
            return false;
        }
        if (dpFecha.getValue().isBefore(LocalDate.now())) {
            Alertas.mostrarAdvertencia("Formulario de evento", "La fecha del evento no puede ser pasada.");
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
        if (!formularioValido()) {
            return;
        }
        // Semana 8: guardar el evento con EventoPersistencia
        Alertas.mostrarInformacion("Agregar evento", "El evento se guardará en la semana 8.");
    }

    @FXML
    private void editarEvento() {
        if (tablaEventos.getSelectionModel().getSelectedItem() == null) {
            Alertas.mostrarAdvertencia("Editar evento", "Selecciona un evento de la tabla.");
            return;
        }
        if (!formularioValido()) {
            return;
        }
        // Semana 8: actualizar el evento con EventoPersistencia
        Alertas.mostrarInformacion("Editar evento", "Los cambios se guardarán en la semana 8.");
    }

    @FXML
    private void eliminarEvento() {
        Evento seleccionado = tablaEventos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alertas.mostrarAdvertencia("Eliminar evento", "Selecciona un evento de la tabla.");
            return;
        }
        if (Alertas.confirmar("Eliminar evento",
                "¿Deseas eliminar el evento \"" + seleccionado.getNombreEvento() + "\"?")) {
            // Semana 8: eliminar el evento con EventoPersistencia
            Alertas.mostrarInformacion("Eliminar evento", "La eliminación se conectará en la semana 8.");
        }
    }

    @FXML
    private void activarUsuario() {
        cambiarEstadoUsuario("Activar usuario");
    }

    @FXML
    private void desactivarUsuario() {
        cambiarEstadoUsuario("Desactivar usuario");
    }

    private void cambiarEstadoUsuario(String accion) {
        if (tablaUsuarios.getSelectionModel().getSelectedItem() == null) {
            Alertas.mostrarAdvertencia(accion, "Selecciona un usuario de la tabla.");
            return;
        }
        // Semana 8: actualizar el estado del usuario con UsuarioPersistencia
        Alertas.mostrarInformacion(accion, "El cambio de estado se conectará en la semana 8.");
    }

    @FXML
    private void generarReporte() {
        Categoria categoria = cmbCategoriaReporte.getValue();
        if (categoria == null) {
            Alertas.mostrarAdvertencia("Generar reporte", "Selecciona una categoría.");
            return;
        }
        // Semana 8: txtReporte.setText(compraPersistencia.generarReportePorCategoria(categoria.getValorBD()));
        txtReporte.setText("El reporte de " + categoria + " se generará en la semana 8.");
    }

    @FXML
    private void cerrarSesion() {
        if (Alertas.confirmar("Cerrar sesión", "¿Deseas cerrar tu sesión?")) {
            Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
        }
    }
}
