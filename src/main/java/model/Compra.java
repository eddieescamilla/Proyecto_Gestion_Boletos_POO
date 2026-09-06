package model;

import model.patrones.strategy.Descuento;
import model.patrones.strategy.DescuentoFijo;
import model.patrones.strategy.DescuentoPorcentaje;

public class Compra {

    private Evento evento;
    private Comprador comprador;
    private int cantidadBoletos;
    private double total;
    private boolean estadoTransaccion;

    public Compra(Evento evento, Comprador comprador, int cantidadBoletos) {
        if (cantidadBoletos <= 0) {
            throw new IllegalArgumentException("La cantidad de boletos debe ser mayor a cero.");
        }
        this.evento = evento;
        this.comprador = comprador;
        this.cantidadBoletos = cantidadBoletos;
        this.estadoTransaccion = false;
    }

    public double calcularTotal() {
        double subtotal = cantidadBoletos * evento.getPrecioBoleto();
        total = subtotal - (subtotal * (comprador.obtenerDescuentoAdicional() / 100.0));
        return total;
    }

    public boolean aplicarDescuento(String codigo) {
        Descuento descuento = obtenerDescuentoPorCodigo(codigo);
        if (descuento == null) {
            return false;
        }
        return aplicarDescuento(descuento);
    }

    public boolean aplicarDescuento(Descuento descuento) {
        if (descuento == null) {
            return false;
        }
        total = descuento.aplicar(total);
        return true;
    }

    private Descuento obtenerDescuentoPorCodigo(String codigo) {
        switch (codigo) {
            case "DESC10":
                return new DescuentoPorcentaje(10.0);
            case "DESC5":
                return new DescuentoPorcentaje(5.0);
            default:
                return null;
        }
    }

    public boolean confirmarPago() {
        if (!evento.verificarDisponibilidad(cantidadBoletos)) {
            estadoTransaccion = false;
            return false;
        }
        evento.actualizarInventario(cantidadBoletos);
        estadoTransaccion = true;
        return true;
    }

    public double getTotal() {
        return total;
    }

    public int getCantidadBoletos() {
        return cantidadBoletos;
    }

    public Evento getEvento() {
        return evento;
    }

    public Comprador getComprador() {
        return comprador;
    }

    public boolean isEstadoTransaccion() {
        return estadoTransaccion;
    }
}