package patrones.strategy;

/** Descuento de un monto fijo, sin dejar el total por debajo de cero. */
public class DescuentoFijo implements Descuento {

  private double monto;

  /**
   * Crea un descuento de monto fijo.
   *
   * @param monto cantidad que se resta del total
   */
  public DescuentoFijo(double monto) {
    this.monto = monto;
  }

  /**
   * Resta el monto fijo, sin dejar el resultado por debajo de cero.
   *
   * @param montoOriginal monto antes del descuento
   * @return el monto con el descuento aplicado
   */
  @Override
  public double aplicar(double montoOriginal) {
    return Math.max(montoOriginal - monto, 0.0);
  }
}
