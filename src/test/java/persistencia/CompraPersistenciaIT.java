package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import model.Comprador;
import model.Evento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Pruebas de integración de {@link CompraPersistencia} contra un PostgreSQL real.
 *
 * <p>Como el esquema impone llaves foráneas sobre {@code compras.correo_comprador} y
 * {@code compras.nombre_evento} (migración V3), cada prueba inserta antes el usuario y
 * el evento al que apuntará la compra.
 */
@EnabledIfEnvironmentVariable(named = "BOLETOS_INTEGRATION_TESTS", matches = "1")
class CompraPersistenciaIT {

  private static String correoAleatorio() {
    return "it-" + UUID.randomUUID() + "@test.local";
  }

  private static String nombreEventoAleatorio() {
    return "IT Evento " + UUID.randomUUID();
  }

  private static String crearUsuario() {
    UsuarioPersistencia persistencia = new UsuarioPersistencia();
    String correo = correoAleatorio();
    persistencia.guardar(new Comprador("IT Comprador", correo, "clave-prueba"));
    return correo;
  }

  private static String crearEvento(String categoria) {
    EventoPersistencia persistencia = new EventoPersistencia();
    String nombre = nombreEventoAleatorio();
    persistencia.guardar(new Evento(
        nombre, categoria, LocalDate.now().plusMonths(1),
        "Lugar IT", 100, 25.0));
    return nombre;
  }

  @Test
  void guardarYListarEncuentraLaCompraQueSeAcabaDeGuardar() {
    CompraPersistencia persistencia = new CompraPersistencia();
    String correo = crearUsuario();
    String nombreEvento = crearEvento("Musica");
    RegistroCompra registro = new RegistroCompra(
        correo, nombreEvento, "Musica", 3, 75.0, LocalDate.now());

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
    String correo = crearUsuario();
    String nombreEvento = crearEvento("Teatro");
    persistencia.guardar(new RegistroCompra(
        correo, nombreEvento, "Teatro", 2, 31.0, LocalDate.now()));

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
