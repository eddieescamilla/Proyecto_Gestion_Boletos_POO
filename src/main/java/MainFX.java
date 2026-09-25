import javafx.application.Application;
import javafx.stage.Stage;
import util.Navegacion;

public class MainFX extends Application {

    @Override
    public void start(Stage ventana) {
        Navegacion.setVentanaPrincipal(ventana);
        Navegacion.cambiarPantalla("Login.fxml", "Iniciar Sesión");
        ventana.setResizable(false);
        ventana.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}