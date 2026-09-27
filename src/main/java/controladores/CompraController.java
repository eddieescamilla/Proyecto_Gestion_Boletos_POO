package controladores;

import catalogo.TipoPago;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Evento;
import util.Alertas;
import util.Navegacion;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CompraController {

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

    @FXML
    private void initialize() {
        cmbMetodoPago.getItems().setAll(TipoPago.values());
        txtCantidad.textProperty().addListener((observable, anterior, nuevo) -> actualizarTotal());
    }

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
                // Si la cantidad no es un numero, el total se queda en 0
            }
        }
        lblTotal.setText(String.format(Locale.US, "Total: $%.2f", total));
    }

    @FXML
    private void aplicarDescuento() {
        if (txtCodigoDescuento.getText().isBlank()) {
            Alertas.mostrarAdvertencia("Código de descuento", "Ingresa un código de descuento.");
            return;
        }
        // Semana 8: validar el codigo con el patron Strategy de descuentos
        Alertas.mostrarInformacion("Código de descuento", "La validación del código se conectará en la semana 8.");
    }

    @FXML
    private void confirmarCompra() {
        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            Alertas.mostrarAdvertencia("Comprar boletos", "Ingresa una cantidad válida.");
            return;
        }
        if (cantidad <= 0) {
            Alertas.mostrarAdvertencia("Comprar boletos", "La cantidad debe ser mayor a cero.");
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
        // Semana 8: registrar la compra con Compra y CompraPersistencia
        Alertas.mostrarInformacion("Comprar boletos", "La compra se registrará en la semana 8.");
    }

    @FXML
    private void cancelar() {
        Navegacion.cambiarPantalla("Eventos.fxml", "Eventos Disponibles");
    }
}