package model;

public class Compra {

    private Evento evento;
    private Comprador comprador;
    private int cantidadBoletos;
    private double total;
    private boolean estadoTransaccion;

    public Compra(Evento evento, Comprador comprador, int cantidadBoletos) {
        this.evento = evento;
        this.comprador = comprador;
        this.cantidadBoletos = cantidadBoletos;
        this.estadoTransaccion = false;
    }

    public double calcularTotal() {
        total = cantidadBoletos * evento.getPrecioBoleto();
        return total;
    }

    public void aplicarDescuento(String codigo) {
        double porcentaje;

        switch (codigo) {
            case "DESC10":
                porcentaje = 10.0;
                break;
            case "DESC5":
                porcentaje = 5.0;
                break;
            default:
                return; // codigo no valido, no se aplica descuento
        }

        double monto = total * (porcentaje / 100.0);
        total -= monto;
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

    public boolean getEstadoTransaccion() {
        return estadoTransaccion;
    }
}