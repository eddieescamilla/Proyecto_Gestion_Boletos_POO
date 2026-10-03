package controladores;

import catalogo.RolUsuario;
import hilos.EjecutorTareas;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
 * <p>Valida las credenciales con {@link GestorUsuarios} en segundo plano, para que la interfaz
 * no se bloquee mientras se consulta la base de datos y se verifica la contraseña, y abre la
 * pantalla que corresponde al rol del usuario.
 */
public class LoginController {

  @FXML
  private TextField txtCorreo;

  @FXML
  private PasswordField txtClave;

  @FXML
  private void iniciarSesion(ActionEvent evento) {
    String correo = txtCorreo.getText().trim();
    String clave = txtClave.getText();
    if (correo.isBlank() || clave.isBlank()) {
      Alertas.mostrarAdvertencia("Iniciar sesión", "Ingresa tu correo y tu contraseña.");
      return;
    }

    // Deshabilitar el boton mientras la tarea esta en vuelo para que un segundo clic no
    // dispare un segundo intento de login en paralelo.
    Button boton = (Button) evento.getSource();
    boton.setDisable(true);

    Task<Usuario> tarea = new Task<>() {
      @Override
      protected Usuario call() {
        return new GestorUsuarios().iniciarSesion(correo, clave);
      }
    };
    tarea.setOnSucceeded(e -> {
      boton.setDisable(false);
      abrirPantallaSegunRol(tarea.getValue());
    });
    tarea.setOnFailed(e -> {
      boton.setDisable(false);
      Alertas.mostrarError("Iniciar sesión",
          "No se pudo conectar con la base de datos. Verifica que Docker esté en ejecución.");
    });
    EjecutorTareas.ejecutar(tarea);
  }

  private void abrirPantallaSegunRol(Usuario usuario) {
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