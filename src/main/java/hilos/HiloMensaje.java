package hilos;

/** Hilo que envía un mensaje de forma concurrente al iniciar el sistema. */
public class HiloMensaje extends Thread {

  private final String nombreHilo;
  private final String mensaje;

  /**
   * Crea un hilo con su nombre y el mensaje que va a enviar.
   *
   * @param nombreHilo nombre del hilo
   * @param mensaje mensaje que se envía al ejecutar el hilo
   */
  public HiloMensaje(String nombreHilo, String mensaje) {
    super(nombreHilo);
    this.nombreHilo = nombreHilo;
    this.mensaje = mensaje;
  }

  /** Envía el mensaje, espera medio segundo y confirma el envío. */
  @Override
  public void run() {
    System.out.println("[" + nombreHilo + "] enviando mensaje: " + mensaje);
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    System.out.println("[" + nombreHilo + "] mensaje enviado.");
  }
}
