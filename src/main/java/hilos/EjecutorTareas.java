package hilos;

import javafx.concurrent.Task;

/**
 * Ejecuta tareas de JavaFX ({@link Task}) en hilos en segundo plano.
 *
 * <p>Los controladores la usan para consultar y guardar datos sin bloquear la interfaz: el
 * trabajo pesado corre en {@link Task#call()} y la pantalla se actualiza en los manejadores
 * {@code setOnSucceeded} y {@code setOnFailed}, que JavaFX ejecuta en su propio hilo.
 */
public final class EjecutorTareas {

  private EjecutorTareas() {
  }

  /**
   * Ejecuta una tarea en un hilo demonio, que se cierra junto con la aplicación.
   *
   * @param tarea tarea a ejecutar
   */
  public static void ejecutar(Task<?> tarea) {
    Thread hilo = new Thread(tarea, "boletos-tarea");
    hilo.setDaemon(true);
    hilo.start();
  }
}
