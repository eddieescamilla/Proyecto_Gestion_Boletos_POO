package patrones.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Pruebas para las estrategias {@link DescuentoFijo} y {@link DescuentoPorcentaje}. */
class DescuentoTest {

  private static final double DELTA = 0.001;

  @Test
  void descuentoPorcentajeRestaElPorcentajeIndicado() {
    Descuento diez = new DescuentoPorcentaje(10.0);
    assertEquals(90.0, diez.aplicar(100.0), DELTA);
  }

  @Test
  void descuentoPorcentajeCon100EsCero() {
    Descuento total = new DescuentoPorcentaje(100.0);
    assertEquals(0.0, total.aplicar(50.0), DELTA);
  }

  @Test
  void descuentoPorcentajeCon0NoCambiaElMonto() {
    Descuento cero = new DescuentoPorcentaje(0.0);
    assertEquals(75.5, cero.aplicar(75.5), DELTA);
  }

  @Test
  void descuentoFijoRestaElMonto() {
    Descuento cinco = new DescuentoFijo(5.0);
    assertEquals(15.0, cinco.aplicar(20.0), DELTA);
  }

  @Test
  void descuentoFijoNoDejaElTotalNegativo() {
    Descuento gigante = new DescuentoFijo(1000.0);
    assertEquals(0.0, gigante.aplicar(30.0), DELTA);
  }

  @Test
  void descuentoFijoIgualAlMontoDejaCero() {
    Descuento exacto = new DescuentoFijo(25.0);
    assertEquals(0.0, exacto.aplicar(25.0), DELTA);
  }
}
