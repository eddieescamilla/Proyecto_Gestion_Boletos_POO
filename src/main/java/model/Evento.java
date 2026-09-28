package model;

import java.time.LocalDate;

/** Evento con boletos a la venta. */
public class Evento {

  private String nombreEvento;
  private String categoria;
  private LocalDate fecha;
  private String lugar;
  private int inventarioDisponible;
  private double precioBoleto;

  /**
   * Crea un evento con su inventario de boletos.
   *
   * @param nombreEvento nombre del evento, que funciona como identificador
   * @param categoria categoría del evento
   * @param fecha fecha del evento, que no puede ser pasada
   * @param lugar lugar donde se realiza
   * @param inventarioDisponible cantidad de boletos disponibles
   * @param precioBoleto precio de cada boleto
   * @throws IllegalArgumentException si la fecha es nula o pasada
   */
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

  /**
   * Verifica si hay boletos suficientes para una cantidad.
   *
   * @param cantidad cantidad de boletos solicitada
   * @return {@code true} si la cantidad es mayor a cero y hay inventario suficiente
   */
  public boolean verificarDisponibilidad(int cantidad) {
    return cantidad > 0 && cantidad <= inventarioDisponible;
  }

  /**
   * Descuenta boletos del inventario disponible.
   *
   * @param cantidad cantidad de boletos a descontar
   * @throws IllegalArgumentException si la cantidad no es mayor a cero
   * @throws IllegalStateException si la cantidad supera el inventario disponible
   */
  public void actualizarInventario(int cantidad) {
    if (cantidad <= 0) {
      throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
    }
    if (cantidad > inventarioDisponible) {
      throw new IllegalStateException("No se puede descontar mas boletos que el inventario.");
    }
    inventarioDisponible -= cantidad;
  }

  /**
   * Indica si el evento ya no tiene boletos disponibles.
   *
   * @return {@code true} si el inventario está en cero
   */
  public boolean estaAgotado() {
    return inventarioDisponible <= 0;
  }

  /**
   * Devuelve el nombre del evento.
   *
   * @return el nombre del evento
   */
  public String getNombreEvento() {
    return nombreEvento;
  }

  /**
   * Devuelve la categoría del evento, tal como está guardada en la base de datos.
   *
   * @return la categoría del evento
   */
  public String getCategoria() {
    return categoria;
  }

  /**
   * Devuelve la fecha del evento.
   *
   * @return la fecha del evento
   */
  public LocalDate getFecha() {
    return fecha;
  }

  /**
   * Devuelve el lugar del evento.
   *
   * @return el lugar del evento
   */
  public String getLugar() {
    return lugar;
  }

  /**
   * Devuelve la cantidad de boletos disponibles.
   *
   * @return el inventario disponible
   */
  public int getInventarioDisponible() {
    return inventarioDisponible;
  }

  /**
   * Devuelve el precio de cada boleto.
   *
   * @return el precio del boleto
   */
  public double getPrecioBoleto() {
    return precioBoleto;
  }

  /**
   * Devuelve un resumen del evento con su estado de inventario y precio.
   *
   * @return el resumen del evento
   */
  @Override
  public String toString() {
    String estado = estaAgotado() ? "AGOTADO" : ("Stock: " + inventarioDisponible);
    return nombreEvento + " [" + categoria + "] - " + fecha + " en " + lugar
        + " (" + estado + ", Precio: $" + precioBoleto + ")";
  }
}