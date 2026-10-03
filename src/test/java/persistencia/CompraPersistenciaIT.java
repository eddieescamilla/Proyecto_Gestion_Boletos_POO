package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Pruebas de integración de {@link CompraPersistencia} contra un PostgreSQL real.
 *
 * <p>Verifica que las compras se guardan, se listan y se cuentan en el reporte por
 * categoría, usando correos y nombres de evento únicos para aislar cada prueba.
 */
@EnabledIfEnvironmentVariable(named = "BOLETOS_INTEGRATION_TESTS", matches = "1")
class CompraPersistenciaIT {

  private static String correoAleatorio() {
    return "it-" + UUID.randomUUID() + "@test.local";
  }

  private static String nombreEventoAleatorio() {
    return "IT Evento " + UUID.randomUUID();
  }

  @Test
  void guardarYListarEncuentraLaCompraQueSeAcabaDeGuardar() {
    CompraPersistencia persistencia = new CompraPersistencia();
    RegistroCompra registro = new RegistroCompra(
        correoAleatorio(),
        nombreEventoAleatorio(),
        "Musica",
        3,
        75.0,
        LocalDate.now());

    assertTrue(persistencia.guardar(registro));

    List<RegistroCompra> todas = persistencia.listarTodos();
    boolean encontrada = todas.stream()
        .anyMatch(r -> r.getCorreoComprador().equals(registro.getCorreoComprador())
            && r.getNombreEvento().equals(registro.getNombreEvento()));
    assertTrue(encontrada, "La compra recien guardada debe aparecer en listarTodos().");
  }

  @Test
  void generarReportePorCategoriaIncluyeLasComprasRegistradas() {
    CompraPersistencia persistencia = new CompraPersistencia();
    String nombreEvento = nombreEventoAleatorio();
    persistencia.guardar(new RegistroCompra(
        correoAleatorio(), nombreEvento, "Teatro", 2, 31.0, LocalDate.now()));

    String reporte = persistencia.generarReportePorCategoria("Teatro");
    assertNotNull(reporte);
    assertTrue(reporte.contains("Categoria: Teatro"),
        "El reporte debe mencionar la categoria consultada.");
    assertTrue(reporte.contains("Boletos vendidos"),
        "El reporte debe incluir el total de boletos vendidos.");
  }

  @Test
  void generarReportePorCategoriaInexistenteDevuelveMensajeCero() {
    CompraPersistencia persistencia = new CompraPersistencia();
    String categoriaFantasma = "NoExiste-" + UUID.randomUUID();

    String reporte = persistencia.generarReportePorCategoria(categoriaFantasma);
    assertNotNull(reporte);
    assertTrue(reporte.contains("No hay datos disponibles"),
        "El reporte vacio debe decir que no hay datos.");
  }

  @Test
  void generarReportePorCategoriaVaciaDevuelveAdvertencia() {
    CompraPersistencia persistencia = new CompraPersistencia();
    String reporte = persistencia.generarReportePorCategoria("");
    assertEquals("Debe seleccionar una categoria antes de generar el reporte.", reporte);
  }
}
