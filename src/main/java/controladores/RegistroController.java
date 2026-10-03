package controladores;

import hilos.EjecutorTareas;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.GestorUsuarios;
import util.Alertas;
import util.Navegacion;

/**
 * Controlador de la pantalla de registro de usuarios ({@code Registro.fxml}).
 *
 * <p>El registro se guarda en segundo plano para que la interfaz no se bloquee mientras se
 * cifra la contraseña y se consulta la base de datos.
 */
public class RegistroController {

  @FXML
  private TextField txtNombre;

  @FXML
  private TextField txtCorreo;

  @FXML
  private PasswordField txtClave;

  @FXML
  private void crearCuenta(ActionEvent evento) {
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

    // Deshabilitar el boton mientras la tarea esta en vuelo para que un segundo clic no
    // dispare un segundo intento de registro en paralelo.
    Button boton = (Button) evento.getSource();
    boton.setDisable(true);

    Task<Boolean> tarea = new Task<>() {
      @Override
      protected Boolean call() {
        return new GestorUsuarios().registrar(nombre, correo, clave);
      }
    };
    tarea.setOnSucceeded(e -> {
      boton.setDisable(false);
      if (!tarea.getValue()) {
        Alertas.mostrarError("Crear cuenta", "Ya existe una cuenta con ese correo.");
        return;
      }
      Alertas.mostrarInformacion("Crear cuenta",
          "Tu cuenta se creó correctamente. Ya puedes iniciar sesión.");
      volver();
    });
    tarea.setOnFailed(e -> {
      boton.setDisable(false);
      Alertas.mostrarError("Crear cuenta",
          "No se pudo conectar con la base de datos. Verifica que Docker esté en ejecución.");
    });
    EjecutorTareas.ejecutar(tarea);
  }

  @FXML
  private void volver() {
    Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
  }
}