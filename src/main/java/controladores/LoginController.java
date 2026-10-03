package controladores;

import catalogo.RolUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.GestorUsuarios;
import model.Usuario;
import util.Alertas;
import util.Navegacion;
import util.Sesion;

/**
 * Controlador de la pantalla de inicio de sesión ({@code Login.fxml}).
 *
 * <p>Valida las credenciales con {@link GestorUsuarios} y abre la pantalla que corresponde al
 * rol del usuario.
 */
public class LoginController {

  @FXML
  private TextField txtCorreo;

  @FXML
  private PasswordField txtClave;

  @FXML
  private void iniciarSesion() {
    String correo = txtCorreo.getText().trim();
    String clave = txtClave.getText();
    if (correo.isBlank() || clave.isBlank()) {
      Alertas.mostrarAdvertencia("Iniciar sesión", "Ingresa tu correo y tu contraseña.");
      return;
    }

    Usuario usuario;
    try {
      usuario = new GestorUsuarios().iniciarSesion(correo, clave);
    } catch (RuntimeException e) {
      Alertas.mostrarError("Iniciar sesión",
          "No se pudo conectar con la base de datos. Verifica que Docker esté en ejecución.");
      return;
    }

    if (usuario == null) {
      Alertas.mostrarError("Iniciar sesión",
          "Correo o contraseña incorrectos, o la cuenta está inactiva.");
      return;
    }

    Sesion.setUsuarioActual(usuario);

    if (usuario.getRol() == RolUsuario.ADMINISTRADOR) {
      Navegacion.cambiarPantalla("PanelAdmin.fxml", "Panel de Administración");
    } else {
      Navegacion.cambiarPantalla("Eventos.fxml", "Eventos Disponibles");
    }
  }

  @FXML
  private void irARegistro() {
    Navegacion.cambiarPantalla("Registro.fxml", "Registrarse");
  }
}