package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import patrones.strategy.DescuentoFijo;
import patrones.strategy.DescuentoPorcentaje;

/** Pruebas para el flujo de compra en {@link Compra}. */
class CompraTest {

  private static final double DELTA = 0.001;
  private static final LocalDate FECHA_FUTURA = LocalDate.now().plusMonths(2);

  private Evento evento(int inventario, double precio) {
    return new Evento("Concierto Test", "Musica", FECHA_FUTURA, "Sala Test", inventario, precio);
  }

  private Comprador cliente() {
    return new Comprador("Cliente Test", "cliente@test.com", "clave");
  }

  private CompradorVIP clienteVip() {
    return new CompradorVIP("VIP Test", "vip@test.com", "clave");
  }

  @Test
  void constructorRechazaCantidadNoPositiva() {
    Evento e = evento(10, 20.0);
    Comprador c = cliente();
    assertThrows(IllegalArgumentException.class, () -> new Compra(e, c, 0));
    assertThrows(IllegalArgumentException.class, () -> new Compra(e, c, -3));
  }

  @Test
  void calcularTotalDeCompradorRegularEsPrecioPorCantidad() {
    Compra compra = new Compra(evento(10, 25.0), cliente(), 3);
    assertEquals(75.0, compra.calcularTotal(), DELTA);
  }

  @Test
  void calcularTotalDeCompradorVipAplicaSuDescuentoAdicional() {
    Compra compra = new Compra(evento(10, 100.0), clienteVip(), 2);
    double subtotal = 200.0;
    double descuentoVip = subtotal * (clienteVip().obtenerDescuentoAdicional() / 100.0);
    assertEquals(subtotal - descuentoVip, compra.calcularTotal(), DELTA);
  }

  @Test
  void aplicarDescuentoPorcentajeReduceElTotal() {
    Compra compra = new Compra(evento(10, 100.0), cliente(), 1);
    compra.calcularTotal();
    assertTrue(compra.aplicarDescuento(new DescuentoPorcentaje(20.0)));
    assertEquals(80.0, compra.getTotal(), DELTA);
  }

  @Test
  void aplicarDescuentoFijoNoDejaTotalNegativo() {
    Compra compra = new Compra(evento(10, 10.0), cliente(), 1);
    compra.calcularTotal();
    assertTrue(compra.aplicarDescuento(new DescuentoFijo(50.0)));
    assertEquals(0.0, compra.getTotal(), DELTA);
  }

  @Test
  void aplicarDescuentoConDescuentoNuloDevuelveFalse() {
    Compra compra = new Compra(evento(10, 20.0), cliente(), 1);
    assertFalse(compra.aplicarDescuento((patrones.strategy.Descuento) null));
  }

  @Test
  void confirmarPagoDescuentaInventarioYQuedaEnTransaccion() {
    Evento e = evento(5, 10.0);
    Compra compra = new Compra(e, cliente(), 3);
    assertTrue(compra.confirmarPago());
    assertTrue(compra.isEstadoTransaccion());
    assertEquals(2, e.getInventarioDisponible());
  }

  @Test
  void confirmarPagoDevuelveFalseSiNoAlcanzaInventario() {
    Evento e = evento(2, 10.0);
    Compra compra = new Compra(e, cliente(), 5);
    assertFalse(compra.confirmarPago());
    assertFalse(compra.isEstadoTransaccion());
    assertEquals(2, e.getInventarioDisponible());
  }
}
