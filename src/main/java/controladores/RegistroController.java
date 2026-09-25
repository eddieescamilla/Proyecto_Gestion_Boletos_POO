package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.Alertas;
import util.Navegacion;

public class RegistroController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtClave;

    @FXML
    private void crearCuenta() {
        if (txtNombre.getText().isBlank() || txtCorreo.getText().isBlank()
                || txtClave.getText().isBlank()) {
            Alertas.mostrarAdvertencia("Crear cuenta", "Completa todos los campos.");
            return;
        }
        if (!txtCorreo.getText().contains("@")) {
            Alertas.mostrarAdvertencia("Crear cuenta", "Ingresa un correo electrónico válido.");
            return;
        }
        // Semana 8: registrar con GestorUsuarios y volver al Login
        Alertas.mostrarInformacion("Crear cuenta", "El registro se conectará en la semana 8.");
    }

    @FXML
    private void volver() {
        Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
    }
}