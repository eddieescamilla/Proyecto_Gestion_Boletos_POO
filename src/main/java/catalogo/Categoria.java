package catalogo;

public enum Categoria {

  MUSICA("Música", "Musica"),
  TEATRO("Teatro", "Teatro");

  private final String etiqueta;
  private final String valorBD;

  Categoria(String etiqueta, String valorBD) {
    this.etiqueta = etiqueta;
    this.valorBD = valorBD;
  }

  public String getEtiqueta() {
    return etiqueta;
  }

  public String getValorBD() {
    return valorBD;
  }

  public static Categoria desdeValorBD(String valorBD) {
    for (Categoria categoria : values()) {
      if (categoria.valorBD.equalsIgnoreCase(valorBD)) {
        return categoria;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return etiqueta;
  }
}
