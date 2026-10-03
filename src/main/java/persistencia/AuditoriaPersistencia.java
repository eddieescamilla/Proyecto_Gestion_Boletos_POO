package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registra las acciones sensibles del sistema en la tabla {@code auditoria}.
 *
 * <p>Cada entrada guarda quién ejecutó la acción, cuándo, sobre qué entidad y opcionalmente
 * una descripción libre. Sirve como pista de auditoría para investigar cambios en eventos,
 * usuarios y reportes.
 */
public class AuditoriaPersistencia {

  private static final Logger log = LoggerFactory.getLogger(AuditoriaPersistencia.class);

  private final DataSource dataSource;

  /** Crea la persistencia usando el pool compartido de conexiones. */
  public AuditoriaPersistencia() {
    this.dataSource = ConexionBD.obtenerDataSource();
  }

  /**
   * Registra una acción en la tabla de auditoría.
   *
   * <p>Nunca lanza excepción hacia el llamador: si el INSERT falla, se deja constancia en el
   * log de la aplicación pero la acción original no se ve afectada.
   *
   * @param actor identificador del usuario que ejecutó la acción
   * @param accion verbo corto (por ejemplo {@code AGREGAR_EVENTO})
   * @param entidad entidad afectada ({@code EVENTO}, {@code USUARIO}, {@code REPORTE})
   * @param referencia identificador natural de la instancia afectada, o {@code null}
   * @param detalle descripción libre, o {@code null}
   */
  public void registrar(String actor, String accion, String entidad,
      String referencia, String detalle) {
    String sql = "INSERT INTO auditoria (fecha_hora, actor, accion, entidad, referencia, detalle)"
        + " VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, LocalDateTime.now().toString());
      statement.setString(2, actor);
      statement.setString(3, accion);
      statement.setString(4, entidad);
      statement.setString(5, referencia);
      statement.setString(6, detalle);
      statement.executeUpdate();
      log.info("auditoria: actor={} accion={} entidad={} ref={}", actor, accion, entidad,
          referencia);
    } catch (SQLException e) {
      log.error("No se pudo registrar la auditoria de {} sobre {}={}", accion, entidad,
          referencia, e);
    }
  }

  /**
   * Devuelve las últimas {@code n} entradas de auditoría, ordenadas de más reciente a más
   * antigua.
   *
   * @param limite cantidad máxima de entradas a devolver
   * @return lista de entradas de auditoría
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public List<EntradaAuditoria> ultimas(int limite) {
    List<EntradaAuditoria> entradas = new ArrayList<>();
    String sql = "SELECT fecha_hora, actor, accion, entidad, referencia, detalle "
        + "FROM auditoria ORDER BY id DESC LIMIT ?";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setInt(1, limite);
      try (ResultSet resultado = statement.executeQuery()) {
        while (resultado.next()) {
          entradas.add(new EntradaAuditoria(
              resultado.getString("fecha_hora"),
              resultado.getString("actor"),
              resultado.getString("accion"),
              resultado.getString("entidad"),
              resultado.getString("referencia"),
              resultado.getString("detalle")));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo consultar la auditoria.", e);
    }
    return entradas;
  }

  /**
   * Entrada individual de auditoría, útil para reportes o inspección desde código.
   *
   * @param fechaHora fecha y hora en que ocurrió la acción
   * @param actor correo del usuario que realizó la acción
   * @param accion acción realizada (por ejemplo {@code CREAR_EVENTO})
   * @param entidad tipo de dato afectado (por ejemplo {@code EVENTO})
   * @param referencia identificador del dato afectado
   * @param detalle información adicional; puede ser {@code null}
   */
  public record EntradaAuditoria(
      String fechaHora,
      String actor,
      String accion,
      String entidad,
      String referencia,
      String detalle) {
  }
}
