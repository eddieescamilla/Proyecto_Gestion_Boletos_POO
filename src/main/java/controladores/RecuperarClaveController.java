package controladores;

import hilos.EjecutorTareas;
import java.util.Locale;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.Usuario;
import persistencia.UsuarioPersistencia;
import util.Alertas;
import util.Navegacion;
import util.PasswordHasher;

/**
 * Controlador de la pantalla de recuperación de clave ({@code RecuperarClave.fxml}).
 *
 * <p>El flujo actual es un MVP: verifica que el correo exista en la base y, si existe,
 * sobrescribe la clave con el hash BCrypt de la nueva. No hay verificación por correo/SMS,
 * así que no es apto para producción tal como está; queda levantada la limitación en la
 * UI para que nadie se confunda y en el issue #56 para iterar.
 */
public class RecuperarClaveController {

  @FXML
  private TextField txtCorreo;

  @FXML
  private PasswordField txtClaveNueva;

  @FXML
  private PasswordField txtClaveConfirmar;

  @FXML
  private void restablecer(ActionEvent evento) {
    String correo = txtCorreo.getText().trim().toLowerCase(Locale.ROOT);
    String nueva = txtClaveNueva.getText();
    String confirmar = txtClaveConfirmar.getText();

    if (correo.isBlank() || nueva.isBlank() || confirmar.isBlank()) {
      Alertas.mostrarAdvertencia("Recuperar clave", "Completa todos los campos.");
      return;
    }
    if (!correo.contains("@")) {
      Alertas.mostrarAdvertencia("Recuperar clave", "Ingresa un correo electrónico válido.");
      return;
    }
    if (!nueva.equals(confirmar)) {
      Alertas.mostrarAdvertencia("Recuperar clave", "La clave nueva y su confirmación no coinciden.");
      return;
    }
    if (nueva.length() < 6) {
      Alertas.mostrarAdvertencia("Recuperar clave", "La clave debe tener al menos 6 caracteres.");
      return;
    }

    Button boton = (Button) evento.getSource();
    boton.setDisable(true);

    Task<Boolean> tarea = new Task<>() {
      @Override
      protected Boolean call() {
        UsuarioPersistencia persistencia = new UsuarioPersistencia();
        Usuario usuario = persistencia.buscarPorId(correo);
        if (usuario == null) {
          return false;
        }
        return persistencia.actualizarClave(correo, PasswordHasher.hash(nueva));
      }
    };
    tarea.setOnSucceeded(e -> {
      boton.setDisable(false);
      if (!tarea.getValue()) {
        Alertas.mostrarError("Recuperar clave",
            "No hay ninguna cuenta registrada con ese correo.");
        return;
      }
      Alertas.mostrarInformacion("Recuperar clave",
          "La clave se actualizó correctamente. Ya puedes iniciar sesión.");
      volver();
    });
    tarea.setOnFailed(e -> {
      boton.setDisable(false);
      Alertas.mostrarError("Recuperar clave",
          "No se pudo conectar con la base de datos. Verifica que Docker esté en ejecución.");
    });
    EjecutorTareas.ejecutar(tarea);
  }

  @FXML
  private void volver() {
    Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
  }
}
