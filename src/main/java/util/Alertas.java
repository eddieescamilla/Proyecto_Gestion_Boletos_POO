package util;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Plantillas de las alertas que muestra la aplicación. */
public final class Alertas {

  private Alertas() {
  }

  /**
   * Muestra una alerta de información y espera a que el usuario la cierre.
   *
   * @param titulo título de la ventana
   * @param mensaje mensaje que se muestra
   */
  public static void mostrarInformacion(String titulo, String mensaje) {
    mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
  }

  /**
   * Muestra una alerta de error y espera a que el usuario la cierre.
   *
   * @param titulo título de la ventana
   * @param mensaje mensaje que se muestra
   */
  public static void mostrarError(String titulo, String mensaje) {
    mostrar(Alert.AlertType.ERROR, titulo, mensaje);
  }

  /**
   * Muestra una alerta de advertencia y espera a que el usuario la cierre.
   *
   * @param titulo título de la ventana
   * @param mensaje mensaje que se muestra
   */
  public static void mostrarAdvertencia(String titulo, String mensaje) {
    mostrar(Alert.AlertType.WARNING, titulo, mensaje);
  }

  /**
   * Muestra una alerta de confirmación y espera la respuesta del usuario.
   *
   * @param titulo título de la ventana
   * @param mensaje pregunta que se muestra
   * @return {@code true} si el usuario presiona Aceptar
   */
  public static boolean confirmar(String titulo, String mensaje) {
    Alert alerta = crear(Alert.AlertType.CONFIRMATION, titulo, mensaje);
    Optional<ButtonType> respuesta = alerta.showAndWait();
    return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
  }

  private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
    crear(tipo, titulo, mensaje).showAndWait();
  }

  private static Alert crear(Alert.AlertType tipo, String titulo, String mensaje) {
    Alert alerta = new Alert(tipo);
    alerta.setTitle(titulo);
    alerta.setHeaderText(null);
    alerta.setContentText(mensaje);
    return alerta;
  }
}