package model;

public class Evento {

    private String nombreEvento;
    private int inventarioDisponible;
    private double precioBoleto;

    public Evento(String nombreEvento, int inventarioDisponible, double precioBoleto) {
        this.nombreEvento = nombreEvento;
        this.inventarioDisponible = inventarioDisponible;
        this.precioBoleto = precioBoleto;
    }

    public boolean verificarDisponibilidad(int cantidad) {
        return cantidad > 0 && cantidad <= inventarioDisponible;
    }

    public void actualizarInventario(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
        }
        if (cantidad > inventarioDisponible) {
            throw new IllegalStateException("No se puede descontar mas boletos que el inventario.");
        }
        inventarioDisponible -= cantidad;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public int getInventarioDisponible() {
        return inventarioDisponible;
    }

    public double getPrecioBoleto() {
        return precioBoleto;
    }

    @Override
    public String toString() {
        return nombreEvento + " (Stock: " + inventarioDisponible + ", Precio: $" + precioBoleto + ")";
    }
}