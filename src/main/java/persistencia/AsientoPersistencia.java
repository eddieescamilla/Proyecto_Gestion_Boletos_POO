package persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import model.Asiento;

/**
 * Persistencia de los asientos numerados por evento (#60).
 *
 * <p>Esta clase cubre la capa de datos; la UI de selección de asientos queda como
 * follow-up. Las operaciones expuestas alcanzan para que un seat picker futuro liste
 * los asientos disponibles y los marque vendidos como parte del flujo de compra.
 */
public class AsientoPersistencia {

  private final DataSource dataSource;

  /** Crea la persistencia usando el pool compartido de conexiones. */
  public AsientoPersistencia() {
    this.dataSource = ConexionBD.obtenerDataSource();
  }

  /**
   * Devuelve todos los asientos de un evento ordenados por número.
   *
   * @param nombreEvento evento consultado
   * @return lista completa de asientos (vendidos y disponibles)
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public List<Asiento> listarPorEvento(String nombreEvento) {
    List<Asiento> asientos = new ArrayList<>();
    String sql = "SELECT nombre_evento, numero_asiento, vendido FROM asientos "
        + "WHERE nombre_evento = ? ORDER BY numero_asiento";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, nombreEvento);
      try (ResultSet resultado = statement.executeQuery()) {
        while (resultado.next()) {
          asientos.add(mapear(resultado));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo listar los asientos del evento.", e);
    }
    return asientos;
  }

  /**
   * Devuelve solo los asientos aún disponibles de un evento.
   *
   * @param nombreEvento evento consultado
   * @return lista con los asientos cuyo flag {@code vendido} es 0
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public List<Asiento> listarDisponibles(String nombreEvento) {
    List<Asiento> asientos = new ArrayList<>();
    String sql = "SELECT nombre_evento, numero_asiento, vendido FROM asientos "
        + "WHERE nombre_evento = ? AND vendido = 0 ORDER BY numero_asiento";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, nombreEvento);
      try (ResultSet resultado = statement.executeQuery()) {
        while (resultado.next()) {
          asientos.add(mapear(resultado));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo listar los asientos disponibles.", e);
    }
    return asientos;
  }

  /**
   * Marca un asiento como vendido en una sola sentencia atómica.
   *
   * <p>Devuelve {@code false} si el asiento no existía o ya estaba vendido, con lo cual
   * dos compras simultáneas no pueden quedarse ambas con el mismo asiento.
   *
   * @param nombreEvento evento al que pertenece el asiento
   * @param numero número del asiento
   * @return {@code true} si el asiento era disponible y pasó a vendido
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public boolean marcarVendido(String nombreEvento, int numero) {
    String sql = "UPDATE asientos SET vendido = 1 "
        + "WHERE nombre_evento = ? AND numero_asiento = ? AND vendido = 0";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, nombreEvento);
      statement.setInt(2, numero);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo marcar el asiento como vendido.", e);
    }
  }

  /**
   * Cuenta los asientos aún disponibles de un evento.
   *
   * @param nombreEvento evento consultado
   * @return cantidad de asientos con {@code vendido = 0}
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public int contarDisponibles(String nombreEvento) {
    String sql = "SELECT COUNT(*) AS total FROM asientos "
        + "WHERE nombre_evento = ? AND vendido = 0";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, nombreEvento);
      try (ResultSet resultado = statement.executeQuery()) {
        return resultado.next() ? resultado.getInt("total") : 0;
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo contar los asientos disponibles.", e);
    }
  }

  private Asiento mapear(ResultSet resultado) throws SQLException {
    return new Asiento(
        resultado.getString("nombre_evento"),
        resultado.getInt("numero_asiento"),
        resultado.getInt("vendido") == 1);
  }
}
