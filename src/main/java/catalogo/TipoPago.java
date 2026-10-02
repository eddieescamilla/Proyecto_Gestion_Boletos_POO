package catalogo;

/** Métodos de pago disponibles para la compra de boletos. */
public enum TipoPago {

  /** Pago con tarjeta de crédito. */
  TARJETA_CREDITO("Tarjeta de crédito"),
  /** Pago con tarjeta de débito. */
  TARJETA_DEBITO("Tarjeta de débito"),
  /** Pago en efectivo. */
  EFECTIVO("Efectivo");

  private final String etiqueta;

  TipoPago(String etiqueta) {
    this.etiqueta = etiqueta;
  }

  /**
   * Devuelve el nombre del método de pago para mostrar en pantalla.
   *
   * @return la etiqueta del método de pago
   */
  public String getEtiqueta() {
    return etiqueta;
  }

  /**
   * Devuelve la etiqueta del método de pago, para que el ComboBox la muestre legible.
   *
   * @return la etiqueta del método de pago
   */
  @Override
  public String toString() {
    return etiqueta;
  }
}
