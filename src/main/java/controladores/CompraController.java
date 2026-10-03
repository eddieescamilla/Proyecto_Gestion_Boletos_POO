package controladores;

import catalogo.TipoPago;
import hilos.EjecutorTareas;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Compra;
import model.Comprador;
import model.Evento;
import persistencia.CompraPersistencia;
import persistencia.EventoPersistencia;
import persistencia.RegistroCompra;
import util.Alertas;
import util.Navegacion;
import util.Sesion;

/**
 * Controlador de la pantalla de compra de boletos ({@code Compra.fxml}).
 *
 * <p>Muestra el detalle del evento seleccionado, calcula el total según la cantidad de
 * boletos y registra la compra consultando descuentos configurables, descontando
 * inventario de forma atómica y guardando el registro.
 */
public class CompraController {

  @FXML
  private Label lblUsuario;

  private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  @FXML
  private Label lblEvento;

  @FXML
  private Label lblCategoria;

  @FXML
  private Label lblFecha;

  @FXML
  private Label lblLugar;

  @FXML
  private Label lblPrecio;

  @FXML
  private Label lblDisponibles;

  @FXML
  private Label lblTotal;

  @FXML
  private TextField txtCantidad;

  @FXML
  private TextField txtCodigoDescuento;

  @FXML
  private ComboBox<TipoPago> cmbMetodoPago;

  private Evento evento;
  private Compra compraEnCurso;
  private double totalConDescuento;

  @FXML
  private void initialize() {
    lblUsuario.setText(Sesion.textoBienvenida());
    cmbMetodoPago.getItems().setAll(TipoPago.values());
    txtCantidad.textProperty().addListener((observable, anterior, nuevo) -> {
      compraEnCurso = null;
      actualizarTotal();
    });
    txtCodigoDescuento.textProperty().addListener((observable, anterior, nuevo) -> {
      compraEnCurso = null;
    });
  }

  /**
   * Recibe el evento seleccionado en la pantalla de Eventos y muestra su detalle.
   *
   * @param evento evento que el usuario quiere comprar
   */
  public void setEvento(Evento evento) {
    this.evento = evento;
    lblEvento.setText(evento.getNombreEvento());
    lblCategoria.setText(evento.getCategoria());
    lblFecha.setText(evento.getFecha().format(FORMATO_FECHA));
    lblLugar.setText(evento.getLugar());
    lblPrecio.setText(String.format(Locale.US, "$%.2f", evento.getPrecioBoleto()));
    lblDisponibles.setText(evento.getInventarioDisponible() + " boletos");
  }

  private void actualizarTotal() {
    double total = 0;
    if (evento != null) {
      try {
        int cantidad = Integer.parseInt(txtCantidad.getText().trim());
        if (cantidad > 0) {
          total = cantidad * evento.getPrecioBoleto();
        }
      } catch (NumberFormatException e) {
        total = 0;
      }
    }
    totalConDescuento = total;
    lblTotal.setText(String.format(Locale.US, "Total: $%.2f", total));
  }

  @FXML
  private void aplicarDescuento() {
    String codigo = txtCodigoDescuento.getText();
    if (codigo == null || codigo.isBlank()) {
      Alertas.mostrarAdvertencia("Código de descuento", "Ingresa un código de descuento.");
      return;
    }
    Comprador comprador = compradorEnSesion();
    if (comprador == null) {
      return;
    }
    int cantidad = leerCantidadValida();
    if (cantidad <= 0) {
      return;
    }

    Compra compra = new Compra(evento, comprador, cantidad);
    compra.calcularTotal();
    Task<Boolean> tarea = new Task<>() {
      @Override
      protected Boolean call() {
        return compra.aplicarDescuento(codigo);
      }
    };
    tarea.setOnSucceeded(e -> {
      if (!tarea.getValue()) {
        Alertas.mostrarAdvertencia("Código de descuento",
            "El código no es válido o ya no está activo.");
        return;
      }
      compraEnCurso = compra;
      totalConDescuento = compra.getTotal();
      lblTotal.setText(String.format(Locale.US, "Total: $%.2f (descuento aplicado)",
          totalConDescuento));
    });
    tarea.setOnFailed(e -> Alertas.mostrarError("Código de descuento",
        "No se pudo validar el código. Verifica que Docker esté en ejecución."));
    EjecutorTareas.ejecutar(tarea);
  }

  @FXML
  private void confirmarCompra() {
    int cantidad = leerCantidadValida();
    if (cantidad <= 0) {
      return;
    }
    if (evento != null && !evento.verificarDisponibilidad(cantidad)) {
      Alertas.mostrarAdvertencia("Comprar boletos", "No hay suficientes boletos disponibles.");
      return;
    }
    if (cmbMetodoPago.getValue() == null) {
      Alertas.mostrarAdvertencia("Comprar boletos", "Selecciona un método de pago.");
      return;
    }
    Comprador comprador = compradorEnSesion();
    if (comprador == null) {
      return;
    }

    Compra compra = compraEnCurso;
    if (compra == null || compra.getCantidadBoletos() != cantidad) {
      compra = new Compra(evento, comprador, cantidad);
      compra.calcularTotal();
    }
    final Compra compraFinal = compra;
    final double totalFinal = compra.getTotal();

    Task<Boolean> tarea = new Task<>() {
      @Override
      protected Boolean call() {
        EventoPersistencia eventoPersistencia = new EventoPersistencia();
        boolean descontado = eventoPersistencia.descontarInventarioAtomico(
            evento.getNombreEvento(), cantidad);
        if (!descontado) {
          return false;
        }
        RegistroCompra registro = new RegistroCompra(
            comprador.getCorreo(),
            evento.getNombreEvento(),
            evento.getCategoria(),
            cantidad,
            totalFinal,
            LocalDate.now());
        new CompraPersistencia().guardar(registro);
        return true;
      }
    };

    tarea.setOnSucceeded(e -> {
      if (Boolean.TRUE.equals(tarea.getValue())) {
        Alertas.mostrarInformacion("Comprar boletos",
            String.format(Locale.US,
                "Compra registrada correctamente por $%.2f.", totalFinal));
        compraEnCurso = null;
        Navegacion.cambiarPantalla("Eventos.fxml", "Eventos Disponibles");
      } else {
        Alertas.mostrarError("Comprar boletos",
            "No hay inventario suficiente para completar la compra.");
      }
    });
    tarea.setOnFailed(e -> Alertas.mostrarError("Comprar boletos",
        "No se pudo registrar la compra. Verifica que Docker esté en ejecución."));

    EjecutorTareas.ejecutar(tarea);
  }

  private int leerCantidadValida() {
    try {
      int cantidad = Integer.parseInt(txtCantidad.getText().trim());
      if (cantidad <= 0) {
        Alertas.mostrarAdvertencia("Comprar boletos", "La cantidad debe ser mayor a cero.");
        return 0;
      }
      return cantidad;
    } catch (NumberFormatException e) {
      Alertas.mostrarAdvertencia("Comprar boletos", "Ingresa una cantidad válida.");
      return 0;
    }
  }

  private Comprador compradorEnSesion() {
    if (!(Sesion.getUsuarioActual() instanceof Comprador comprador)) {
      Alertas.mostrarError("Comprar boletos",
          "No hay una sesión de cliente activa. Vuelve a iniciar sesión.");
      Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
      return null;
    }
    return comprador;
  }


  @FXML
  private void cancelar() {
    Navegacion.cambiarPantalla("Eventos.fxml", "Eventos Disponibles");
  }
}
