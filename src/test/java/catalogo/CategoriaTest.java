package catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Pruebas para el enum {@link Categoria}. */
class CategoriaTest {

  @Test
  void etiquetaConservaTildes() {
    assertEquals("Música", Categoria.MUSICA.getEtiqueta());
    assertEquals("Teatro", Categoria.TEATRO.getEtiqueta());
  }

  @Test
  void valorBdEsSinTildes() {
    assertEquals("Musica", Categoria.MUSICA.getValorBD());
    assertEquals("Teatro", Categoria.TEATRO.getValorBD());
  }

  @Test
  void toStringDevuelveLaEtiqueta() {
    assertEquals("Música", Categoria.MUSICA.toString());
  }

  @Test
  void desdeValorBdEncuentraLaCategoria() {
    assertEquals(Categoria.MUSICA, Categoria.desdeValorBD("Musica"));
    assertEquals(Categoria.TEATRO, Categoria.desdeValorBD("Teatro"));
  }

  @Test
  void desdeValorBdIgnoraMayusculas() {
    assertEquals(Categoria.MUSICA, Categoria.desdeValorBD("MUSICA"));
    assertEquals(Categoria.TEATRO, Categoria.desdeValorBD("teatro"));
  }

  @Test
  void desdeValorBdDevuelveNullSiNoExiste() {
    assertNull(Categoria.desdeValorBD("Cine"));
    assertNull(Categoria.desdeValorBD(""));
  }
}
