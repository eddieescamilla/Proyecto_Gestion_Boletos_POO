package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** Pruebas para la lógica de negocio de {@link Evento}. */
class EventoTest {

  private static final LocalDate FECHA_FUTURA = LocalDate.now().plusMonths(3);

  private Evento eventoConInventario(int inventario) {
    return new Evento("Concierto Test", "Musica", FECHA_FUTURA, "Sala Test", inventario, 20.0);
  }

  @Test
  void verificarDisponibilidadDevuelveTrueSiAlcanza() {
    Evento evento = eventoConInventario(10);
    assertTrue(evento.verificarDisponibilidad(5));
    assertTrue(evento.verificarDisponibilidad(10));
  }

  @Test
  void verificarDisponibilidadDevuelveFalseSiNoAlcanza() {
    Evento evento = eventoConInventario(3);
    assertFalse(evento.verificarDisponibilidad(4));
  }

  @Test
  void verificarDisponibilidadDevuelveFalseConCantidadNoPositiva() {
    Evento evento = eventoConInventario(10);
    assertFalse(evento.verificarDisponibilidad(0));
    assertFalse(evento.verificarDisponibilidad(-1));
  }

  @Test
  void actualizarInventarioDescuentaLaCantidadPedida() {
    Evento evento = eventoConInventario(10);
    evento.actualizarInventario(4);
    assertEquals(6, evento.getInventarioDisponible());
  }

  @Test
  void actualizarInventarioRechazaCantidadNoPositiva() {
    Evento evento = eventoConInventario(10);
    assertThrows(IllegalArgumentException.class, () -> evento.actualizarInventario(0));
    assertThrows(IllegalArgumentException.class, () -> evento.actualizarInventario(-2));
  }

  @Test
  void actualizarInventarioRechazaCantidadMayorAlInventario() {
    Evento evento = eventoConInventario(3);
    assertThrows(IllegalStateException.class, () -> evento.actualizarInventario(4));
  }

  @Test
  void estaAgotadoEsFalsoSiHayInventario() {
    Evento evento = eventoConInventario(1);
    assertFalse(evento.estaAgotado());
  }

  @Test
  void estaAgotadoEsCiertoSiInventarioLlegaACero() {
    Evento evento = eventoConInventario(2);
    evento.actualizarInventario(2);
    assertTrue(evento.estaAgotado());
  }
}
