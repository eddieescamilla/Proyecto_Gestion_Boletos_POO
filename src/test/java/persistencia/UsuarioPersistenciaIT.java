package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import model.Comprador;
import model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Pruebas de integración de {@link UsuarioPersistencia} contra un PostgreSQL real.
 *
 * <p>Solo corren cuando la variable de entorno {@code BOLETOS_INTEGRATION_TESTS=1} está
 * presente, lo que ocurre en CI. Localmente se saltan para no ensuciar la base de
 * desarrollo. Cada prueba usa correos únicos con sufijo aleatorio para aislarse del seed
 * y de las otras pruebas.
 */
@EnabledIfEnvironmentVariable(named = "BOLETOS_INTEGRATION_TESTS", matches = "1")
class UsuarioPersistenciaIT {

  private static String nuevoCorreo() {
    return "it-" + UUID.randomUUID() + "@test.local";
  }

  @Test
  void guardarYBuscarPorIdDevuelveElUsuarioQueSeAcabaDeGuardar() {
    UsuarioPersistencia persistencia = new UsuarioPersistencia();
    String correo = nuevoCorreo();
    Comprador comprador = new Comprador("IT Comprador", correo, "clave-de-prueba");

    assertTrue(persistencia.guardar(comprador));

    Usuario encontrado = persistencia.buscarPorId(correo);
    assertNotNull(encontrado);
    assertEquals(correo, encontrado.getCorreo());
    assertEquals("IT Comprador", encontrado.getNombre());
    assertTrue(encontrado.isActivo());
  }

  @Test
  void buscarPorIdDevuelveNullCuandoNoExiste() {
    UsuarioPersistencia persistencia = new UsuarioPersistencia();
    Usuario encontrado = persistencia.buscarPorId("no-existe-" + UUID.randomUUID() + "@test.local");
    assertNull(encontrado);
  }

  @Test
  void actualizarClaveReemplazaElValorAlmacenado() {
    UsuarioPersistencia persistencia = new UsuarioPersistencia();
    String correo = nuevoCorreo();
    persistencia.guardar(new Comprador("IT Clave", correo, "clave-vieja"));

    assertTrue(persistencia.actualizarClave(correo, "clave-nueva"));

    Usuario encontrado = persistencia.buscarPorId(correo);
    assertEquals("clave-nueva", encontrado.getClave());
  }

  @Test
  void actualizarEstadoDesactivaYReactivaLaCuenta() {
    UsuarioPersistencia persistencia = new UsuarioPersistencia();
    String correo = nuevoCorreo();
    persistencia.guardar(new Comprador("IT Estado", correo, "clave"));

    assertTrue(persistencia.actualizarEstado(correo, false));
    assertEquals(false, persistencia.buscarPorId(correo).isActivo());

    assertTrue(persistencia.actualizarEstado(correo, true));
    assertEquals(true, persistencia.buscarPorId(correo).isActivo());
  }
}
