package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import model.DescuentoConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Pruebas de integración de {@link DescuentoPersistencia} contra un PostgreSQL real.
 *
 * <p>Cubre el fix de #73: la columna {@code activo} es {@code INTEGER} y la consulta
 * {@code WHERE codigo = ? AND activo = 1} tiene que resolver correctamente códigos
 * activos y descartar los inactivos.
 */
@EnabledIfEnvironmentVariable(named = "BOLETOS_INTEGRATION_TESTS", matches = "1")
class DescuentoPersistenciaIT {

  private static String nuevoCodigo() {
    return ("IT-" + UUID.randomUUID()).substring(0, 20).toUpperCase();
  }

  @Test
  void guardarYBuscarPorIdDevuelveElDescuentoActivo() {
    DescuentoPersistencia persistencia = new DescuentoPersistencia();
    String codigo = nuevoCodigo();
    persistencia.guardar(new DescuentoConfig(codigo, "PORCENTAJE", 15.0, true));

    DescuentoConfig encontrado = persistencia.buscarPorId(codigo);
    assertNotNull(encontrado);
    assertEquals("PORCENTAJE", encontrado.getTipo());
    assertEquals(15.0, encontrado.getValor());
    assertTrue(encontrado.isActivo());
  }

  @Test
  void buscarPorIdNoDevuelveDescuentosInactivos() {
    DescuentoPersistencia persistencia = new DescuentoPersistencia();
    String codigo = nuevoCodigo();
    persistencia.guardar(new DescuentoConfig(codigo, "FIJO", 5.0, false));

    assertNull(persistencia.buscarPorId(codigo));
  }

  @Test
  void seedTraeLosCodigosConocidos() {
    DescuentoPersistencia persistencia = new DescuentoPersistencia();

    DescuentoConfig desc10 = persistencia.buscarPorId("DESC10");
    assertNotNull(desc10, "El seed de ConexionBD debe incluir DESC10.");
    assertEquals("PORCENTAJE", desc10.getTipo());
    assertEquals(10.0, desc10.getValor());
  }
}
