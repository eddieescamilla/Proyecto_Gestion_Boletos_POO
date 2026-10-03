package model;

/**
 * Configuración de un descuento aplicable a una compra.
 *
 * <p>Representa una fila de la tabla {@code descuento_config}. El código es el
 * identificador que el cliente ingresa en la pantalla de compra; el tipo indica si el valor
 * se interpreta como porcentaje (0-100) o como monto fijo en dólares; el flag {@code
 * activo} permite dejar un descuento registrado pero deshabilitado sin borrarlo.
 */
public class DescuentoConfig {

  private String codigo;
  private String tipo;
  private double valor;
  private boolean activo;

  /**
   * Crea una configuración de descuento.
   *
   * @param codigo código que el cliente ingresa (p. ej. {@code DESC10})
   * @param tipo tipo del descuento: {@code PORCENTAJE} o {@code FIJO}
   * @param valor magnitud del descuento (porcentaje 0-100 o monto en dólares)
   * @param activo {@code true} si el descuento está vigente
   */
  public DescuentoConfig(String codigo, String tipo, double valor, boolean activo) {
    this.codigo = codigo;
    this.tipo = tipo;
    this.valor = valor;
    this.activo = activo;
  }

  /**
   * Devuelve el código del descuento.
   *
   * @return el código que el cliente ingresa
   */
  public String getCodigo() {
    return codigo;
  }

  /**
   * Devuelve el tipo del descuento.
   *
   * @return {@code PORCENTAJE} o {@code FIJO}
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * Devuelve la magnitud del descuento.
   *
   * @return porcentaje (0-100) o monto fijo según el tipo
   */
  public double getValor() {
    return valor;
  }

  /**
   * Indica si el descuento está vigente.
   *
   * @return {@code true} si está activo
   */
  public boolean isActivo() {
    return activo;
  }
}
