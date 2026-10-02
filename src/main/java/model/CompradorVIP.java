package model;

/** Comprador VIP que recibe un descuento adicional del 10% en cada compra. */
public class CompradorVIP extends Comprador {

  private static final double DESCUENTO_VIP_PORCENTAJE = 10.0;

  /**
   * Crea un comprador VIP activo.
   *
   * @param nombre nombre completo
   * @param correo correo electrónico, que funciona como identificador
   * @param clave contraseña
   */
  public CompradorVIP(String nombre, String correo, String clave) {
    super(nombre, correo, clave);
  }

  /**
   * Devuelve el descuento adicional del comprador VIP.
   *
   * @return el porcentaje de descuento VIP
   */
  @Override
  public double obtenerDescuentoAdicional() {
    return DESCUENTO_VIP_PORCENTAJE;
  }

  /**
   * Devuelve el comprador con la marca VIP.
   *
   * @return el nombre, el rol y la marca VIP
   */
  @Override
  public String toString() {
    return super.toString() + " (VIP)";
  }
}