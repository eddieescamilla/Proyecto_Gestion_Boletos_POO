package util;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Cambia las pantallas de la aplicación dentro de la ventana principal. */
public final class Navegacion {

  private static final String TITULO_BASE = "Sistema de Gestión de Boletos";
  private static final String HOJA_ESTILOS = "/estilos.css";

  private static Stage ventanaPrincipal;

  private Navegacion() {
  }

  /**
   * Guarda la ventana principal donde se mostrarán las pantallas.
   *
   * @param ventana ventana principal de la aplicación
   */
  public static void setVentanaPrincipal(Stage ventana) {
    ventanaPrincipal = ventana;
  }

  /**
   * Carga un archivo FXML, le aplica la hoja de estilos y lo muestra en la ventana principal.
   *
   * @param archivoFxml nombre del archivo FXML dentro de {@code resources}
   * @param subtitulo texto que se agrega al título de la ventana
   * @param <T> tipo del controlador de la pantalla
   * @return el controlador de la pantalla cargada, para pasarle datos
   * @throws IllegalArgumentException si no se encuentra el archivo FXML
   * @throws IllegalStateException si el archivo FXML no se puede cargar
   */
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