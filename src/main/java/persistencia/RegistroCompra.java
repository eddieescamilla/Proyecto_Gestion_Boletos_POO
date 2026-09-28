package persistencia;

import java.time.LocalDate;

/** Registro de una compra tal como se guarda en la base de datos. */
public class RegistroCompra {

  private String correoComprador;
  private String nombreEvento;
  private String categoriaEvento;
  private int cantidadBoletos;
  private double total;
  private LocalDate fecha;

  /**
   * Crea un registro de compra.
   *
   * @param correoComprador correo del comprador
   * @param nombreEvento nombre del evento
   * @param categoriaEvento categoría del evento
   * @param cantidadBoletos cantidad de boletos comprados
   * @param total total pagado
   * @param fecha fecha de la compra
   */
  public RegistroCompra(String correoComprador, String nombreEvento, String categoriaEvento,
      int cantidadBoletos, double total, LocalDate fecha) {
    this.correoComprador = correoComprador;
    this.nombreEvento = nombreEvento;
    this.categoriaEvento = categoriaEvento;
    this.cantidadBoletos = cantidadBoletos;
    this.total = total;
    this.fecha = fecha;
  }

  /**
   * Devuelve el correo del comprador.
   *
   * @return el correo del comprador
   */
  public String getCorreoComprador() {
    return correoComprador;
  }

  /**
   * Devuelve el nombre del evento comprado.
   *
   * @return el nombre del evento
   */
  public String getNombreEvento() {
    return nombreEvento;
  }

  /**
   * Devuelve la categoría del evento comprado.
   *
   * @return la categoría del evento
   */
  public String getCategoriaEvento() {
    return categoriaEvento;
  }

  /**
   * Devuelve la cantidad de boletos comprados.
   *
   * @return la cantidad de boletos
   */
  public int getCantidadBoletos() {
    return cantidadBoletos;
  }

  /**
   * Devuelve el total pagado.
   *
   * @return el total de la compra
   */
  public double getTotal() {
    return total;
  }

  /**
   * Devuelve la fecha de la compra.
   *
   * @return la fecha de la compra
   */
  public LocalDate getFecha() {
    return fecha;
  }
}
