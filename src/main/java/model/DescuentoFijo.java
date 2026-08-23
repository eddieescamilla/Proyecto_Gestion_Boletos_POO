package model;

public class DescuentoFijo extends Descuento {

    private double monto;

    public DescuentoFijo(double monto) {
        this.monto = monto;
    }

    @Override
    public double aplicar(double montoOriginal) {
        return Math.max(montoOriginal - monto, 0.0);
    }
}
