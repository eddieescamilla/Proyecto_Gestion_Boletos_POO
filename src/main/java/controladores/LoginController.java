package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import util.Alertas;
import util.Navegacion;

public class LoginController {

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtClave;

    @FXML
    private void iniciarSesion() {
        if (txtCorreo.getText().isBlank() || txtClave.getText().isBlank()) {
            Alertas.mostrarAdvertencia("Iniciar sesión", "Ingresa tu correo y tu contraseña.");
            return;
        }
        // Semana 8: validar con GestorUsuarios y abrir Eventos o PanelAdmin según el rol
        Alertas.mostrarInformacion("Iniciar sesión", "La validación de credenciales se conectará en la semana 8.");
    }

    @FXML
    private void irARegistro() {
        Navegacion.cambiarPantalla("Registro.fxml", "Registrarse");
    }
}