package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class Alertas {

    private Alertas() {
    }

    public static void mostrarInformacion(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void mostrarError(String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void mostrarAdvertencia(String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, titulo, mensaje);
    }

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