package hilos;

public class HiloMensaje extends Thread {

  private final String nombreHilo;
  private final String mensaje;

  public HiloMensaje(String nombreHilo, String mensaje) {
    super(nombreHilo);
    this.nombreHilo = nombreHilo;
    this.mensaje = mensaje;
  }

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
