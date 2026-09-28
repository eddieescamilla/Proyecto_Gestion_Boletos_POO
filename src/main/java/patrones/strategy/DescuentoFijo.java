package patrones.strategy;

public class DescuentoFijo implements Descuento {

  private double monto;

  public DescuentoFijo(double monto) {
    this.monto = monto;
  }

  @Override
  public double aplicar(double montoOriginal) {
    return Math.max(montoOriginal - monto, 0.0);
  }
}
