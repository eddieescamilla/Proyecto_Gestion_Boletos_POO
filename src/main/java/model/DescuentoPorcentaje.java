package model;

public class DescuentoPorcentaje implements Descuento {

    private double porcentaje;

    public DescuentoPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public double aplicar(double montoOriginal) {
        return montoOriginal - (montoOriginal * (porcentaje / 100.0));
    }
}
