package model;

public class CompradorVIP extends Comprador {

    private static final double DESCUENTO_VIP_PORCENTAJE = 10.0;

    public CompradorVIP(String nombre) {
        super(nombre);
    }

    @Override
    public double obtenerDescuentoAdicional() {
        return DESCUENTO_VIP_PORCENTAJE;
    }

    @Override
    public String toString() {
        return super.toString() + " (VIP)";
    }
}
