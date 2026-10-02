package patrones.strategy;

/** Descuento de un porcentaje sobre el total. */
public class DescuentoPorcentaje implements Descuento {

  private double porcentaje;

  /**
   * Crea un descuento por porcentaje.
   *
   * @param porcentaje porcentaje de descuento, por ejemplo 10 para 10%
   */
  public DescuentoPorcentaje(double porcentaje) {
    this.porcentaje = porcentaje;
  }

  /**
   * Resta el porcentaje de descuento al monto.
   *
   * @param montoOriginal monto antes del descuento
   * @return el monto con el descuento aplicado
   */
  @Override
  public double aplicar(double montoOriginal) {
    return montoOriginal - (montoOriginal * (porcentaje / 100.0));
  }
}
