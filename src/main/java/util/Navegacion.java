package util;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navegacion {

  private static final String TITULO_BASE = "Sistema de Gestión de Boletos";
  private static final String HOJA_ESTILOS = "/estilos.css";

  private static Stage ventanaPrincipal;

  private Navegacion() {
  }

  public static void setVentanaPrincipal(Stage ventana) {
    ventanaPrincipal = ventana;
  }

  public static <T> T cambiarPantalla(String archivoFxml, String subtitulo) {
    URL ubicacion = Navegacion.class.getResource("/" + archivoFxml);
    if (ubicacion == null) {
      throw new IllegalArgumentException("No se encontró el archivo FXML: " + archivoFxml);
    }

    try {
      FXMLLoader cargador = new FXMLLoader(ubicacion);
      Parent raiz = cargador.load();

      Scene escena = new Scene(raiz);
      URL estilos = Navegacion.class.getResource(HOJA_ESTILOS);
      if (estilos != null) {
        escena.getStylesheets().add(estilos.toExternalForm());
      }

      ventanaPrincipal.setScene(escena);
      ventanaPrincipal.setTitle(TITULO_BASE + " - " + subtitulo);
      return cargador.getController();
    } catch (IOException e) {
      throw new IllegalStateException("No se pudo cargar la pantalla: " + archivoFxml, e);
    }
  }
}