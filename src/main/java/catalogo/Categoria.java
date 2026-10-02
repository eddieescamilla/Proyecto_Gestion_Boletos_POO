package catalogo;

/**
 * Categorías de los eventos.
 *
 * <p>Cada categoría guarda la etiqueta que se muestra en pantalla y el valor con el que se
 * almacena en la base de datos.
 */
public enum Categoria {

  /** Conciertos y eventos musicales. */
  MUSICA("Música", "Musica"),
  /** Obras y presentaciones de teatro. */
  TEATRO("Teatro", "Teatro");

  private final String etiqueta;
  private final String valorBD;

  Categoria(String etiqueta, String valorBD) {
    this.etiqueta = etiqueta;
    this.valorBD = valorBD;
  }

  /**
   * Devuelve el nombre de la categoría para mostrar en pantalla.
   *
   * @return la etiqueta de la categoría, con tildes
   */
  public String getEtiqueta() {
    return etiqueta;
  }

  /**
   * Devuelve el valor con el que la categoría se guarda en la base de datos.
   *
   * @return el valor de la categoría en la base de datos
   */
  public String getValorBD() {
    return valorBD;
  }

  /**
   * Busca la categoría que corresponde a un valor de la base de datos.
   *
   * @param valorBD valor de la categoría tal como está guardado en la base de datos
   * @return la categoría encontrada, o {@code null} si ninguna coincide
   */
  public static Categoria desdeValorBD(String valorBD) {
    for (Categoria categoria : values()) {
      if (categoria.valorBD.equalsIgnoreCase(valorBD)) {
        return categoria;
      }
    }
    return null;
  }

  /**
   * Devuelve la etiqueta de la categoría, para que los ComboBox la muestren con tildes.
   *
   * @return la etiqueta de la categoría
   */
  @Override
  public String toString() {
    return etiqueta;
  }
}
