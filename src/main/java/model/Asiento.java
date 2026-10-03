package model;

/**
 * Asiento numerado dentro de un evento.
 *
 * <p>La identidad de un asiento es la combinación {@code (nombreEvento, numero)}; dos
 * eventos pueden tener asientos con el mismo número sin colisionar.
 */
public class Asiento {

  private final String nombreEvento;
  private final int numero;
  private boolean vendido;

  /**
   * Crea un asiento.
   *
   * @param nombreEvento evento al que pertenece
   * @param numero número del asiento, 1..N dentro del evento
   * @param vendido si ya se vendió
   */
  public Asiento(String nombreEvento, int numero, boolean vendido) {
    this.nombreEvento = nombreEvento;
    this.numero = numero;
    this.vendido = vendido;
  }

  public String getNombreEvento() {
    return nombreEvento;
  }

  public int getNumero() {
    return numero;
  }

  public boolean isVendido() {
    return vendido;
  }

  public void marcarVendido() {
    this.vendido = true;
  }

  @Override
  public String toString() {
    return "Asiento#" + numero + (vendido ? " (vendido)" : " (disponible)");
  }
}
