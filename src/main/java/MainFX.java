import javafx.application.Application;
import javafx.stage.Stage;
import util.Navegacion;

/** Punto de entrada de la aplicación con interfaz gráfica en JavaFX. */
public class MainFX extends Application {

  /**
   * Configura la ventana principal y abre la pantalla de inicio de sesión.
   *
   * @param ventana ventana principal que proporciona JavaFX
   */
  @Override
  public void start(Stage ventana) {
    Navegacion.setVentanaPrincipal(ventana);
    Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
    ventana.setResizable(false);
    ventana.show();
  }

  /**
   * Lanza la aplicación JavaFX.
   *
   * @param args argumentos de la línea de comandos
   */
  public static void main(String[] args) {
    launch(args);
  }
}