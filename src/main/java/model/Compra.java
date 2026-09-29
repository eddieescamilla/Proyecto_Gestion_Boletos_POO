package model;

import patrones.strategy.Descuento;
import patrones.strategy.DescuentoPorcentaje;

/**
 * Compra de boletos de un evento realizada por un comprador.
 *
 * <p>Calcula el total, aplica descuentos con el patrón Strategy y confirma el pago.
 */
public class Compra {

  private Evento evento;
  private Comprador comprador;
  private int cantidadBoletos;
  private double total;
  private boolean estadoTransaccion;

  /**
   * Crea una compra pendiente de confirmar.
   *
   * @param evento evento del que se compran los boletos
   * @param comprador usuario que realiza la compra
   * @param cantidadBoletos cantidad de boletos, mayor a cero
   * @throws IllegalArgumentException si la cantidad de boletos no es mayor a cero
   */
  public Compra(Evento evento, Comprador comprador, int cantidadBoletos) {
    if (cantidadBoletos <= 0) {
      throw new IllegalArgumentException("La cantidad de boletos debe ser mayor a cero.");
    }
    this.evento = evento;
    this.comprador = comprador;
    this.cantidadBoletos = cantidadBoletos;
    this.estadoTransaccion = false;
  }

  /**
   * Calcula el total de la compra aplicando el descuento adicional del comprador.
   *
   * @return el total calculado
   */
  public double calcularTotal() {
    double subtotal = cantidadBoletos * evento.getPrecioBoleto();
    total = subtotal - (subtotal * (comprador.obtenerDescuentoAdicional() / 100.0));
    return total;
  }

  /**
   * Aplica un descuento a partir de su código ({@code DESC10} o {@code DESC5}).
   *
   * @param codigo código de descuento ingresado por el usuario
   * @return {@code true} si el código es válido y se aplicó el descuento
   */
  public boolean aplicarDescuento(String codigo) {
    if (codigo == null) {
      return false;
    }
    Descuento descuento = obtenerDescuentoPorCodigo(codigo.trim().toUpperCase());
    if (descuento == null) {
      return false;
    }
    return aplicarDescuento(descuento);
  }

  /**
   * Aplica una estrategia de descuento al total de la compra.
   *
   * @param descuento estrategia de descuento a aplicar
   * @return {@code true} si se aplicó el descuento
   */
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

  /**
   * Confirma el pago si hay boletos suficientes y descuenta el inventario del evento.
   *
   * @return {@code true} si la transacción se completó
   */
  public boolean confirmarPago() {
    if (!evento.verificarDisponibilidad(cantidadBoletos)) {
      estadoTransaccion = false;
      return false;
    }
    evento.actualizarInventario(cantidadBoletos);
    estadoTransaccion = true;
    return true;
  }

  /**
   * Devuelve el total de la compra.
   *
   * @return el total, con los descuentos aplicados
   */
  public double getTotal() {
    return total;
  }

  /**
   * Devuelve la cantidad de boletos de la compra.
   *
   * @return la cantidad de boletos
   */
  public int getCantidadBoletos() {
    return cantidadBoletos;
  }

  /**
   * Devuelve el evento de la compra.
   *
   * @return el evento
   */
  public Evento getEvento() {
    return evento;
  }

  /**
   * Devuelve el comprador que realiza la compra.
   *
   * @return el comprador
   */
  public Comprador getComprador() {
    return comprador;
  }

  /**
   * Indica si el pago de la compra fue confirmado.
   *
   * @return {@code true} si la transacción se completó
   */
  public boolean isEstadoTransaccion() {
    return estadoTransaccion;
  }
}