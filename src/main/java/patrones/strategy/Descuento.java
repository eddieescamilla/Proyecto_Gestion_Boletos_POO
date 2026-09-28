package patrones.strategy;

/** Estrategia de descuento aplicable al total de una compra (patrón Strategy). */
public interface Descuento {

  /**
   * Aplica el descuento a un monto.
   *
   * @param montoOriginal monto antes del descuento
   * @return el monto con el descuento aplicado
   */
  double aplicar(double montoOriginal);
}
