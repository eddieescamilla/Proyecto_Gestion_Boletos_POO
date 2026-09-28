package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.GestorUsuarios;
import util.Alertas;
import util.Navegacion;

/** Controlador de la pantalla de registro de usuarios ({@code Registro.fxml}). */
public class RegistroController {

  @FXML
  private TextField txtNombre;

  @FXML
  private TextField txtCorreo;

  @FXML
  private PasswordField txtClave;

  @FXML
  private void crearCuenta() {
    String nombre = txtNombre.getText().trim();
    String correo = txtCorreo.getText().trim();
    String clave = txtClave.getText();
    if (nombre.isBlank() || correo.isBlank() || clave.isBlank()) {
      Alertas.mostrarAdvertencia("Crear cuenta", "Completa todos los campos.");
      return;
    }
    if (!correo.contains("@")) {
      Alertas.mostrarAdvertencia("Crear cuenta", "Ingresa un correo electrónico válido.");
      return;
    }

    boolean registrado;
    try {
      registrado = new GestorUsuarios().registrar(nombre, correo, clave);
    } catch (RuntimeException e) {
      Alertas.mostrarError("Crear cuenta",
          "No se pudo conectar con la base de datos. Verifica que Docker esté en ejecución.");
      return;
    }

    if (!registrado) {
      Alertas.mostrarError("Crear cuenta", "Ya existe una cuenta con ese correo.");
      return;
    }

    Alertas.mostrarInformacion("Crear cuenta",
        "Tu cuenta se creó correctamente. Ya puedes iniciar sesión.");
    volver();
  }

  @FXML
  private void volver() {
    Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
  }
}