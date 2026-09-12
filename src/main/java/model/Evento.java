package model;

import java.time.LocalDate;

public class Evento {
    private String nombreEvento;
    private String categoria;
    private LocalDate fecha;
    private String lugar;
    private int inventarioDisponible;
    private double precioBoleto;

    public Evento(String nombreEvento, String categoria, LocalDate fecha, String lugar,
                  int inventarioDisponible, double precioBoleto) {
        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha del evento no puede ser pasada.");
        }
        this.nombreEvento = nombreEvento;
        this.categoria = categoria;
        this.fecha = fecha;
        this.lugar = lugar;
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

    public boolean estaAgotado() {
        return inventarioDisponible <= 0;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public String getCategoria() {
        return categoria;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public int getInventarioDisponible() {
        return inventarioDisponible;
    }

    public double getPrecioBoleto() {
        return precioBoleto;
    }

    @Override
    public String toString() {
        String estado = estaAgotado() ? "AGOTADO" : ("Stock: " + inventarioDisponible);
        return nombreEvento + " [" + categoria + "] - " + fecha + " en " + lugar
                + " (" + estado + ", Precio: $" + precioBoleto + ")";
    }
}