package persistencia;

import dao.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import model.DescuentoConfig;

/** Persistencia de los descuentos configurables en PostgreSQL mediante el patrón DAO. */
public class DescuentoPersistencia implements DAO<DescuentoConfig> {

  private final DataSource dataSource;

  /** Crea la persistencia usando el pool compartido de conexiones. */
  public DescuentoPersistencia() {
    this.dataSource = ConexionBD.obtenerDataSource();
  }

  /**
   * Guarda una configuración de descuento nueva.
   *
   * @param descuento descuento a guardar
   * @return {@code true} si se guardó correctamente
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public boolean guardar(DescuentoConfig descuento) {
    String sql = "INSERT INTO descuento_config (codigo, tipo, valor, activo) VALUES (?, ?, ?, ?)";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, descuento.getCodigo());
      statement.setString(2, descuento.getTipo());
      statement.setDouble(3, descuento.getValor());
      statement.setInt(4, descuento.isActivo() ? 1 : 0);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo guardar el descuento.", e);
    }
  }

  /**
   * Busca un descuento activo por su código.
   *
   * @param codigo código del descuento, ya normalizado por el caller
   * @return el descuento encontrado y activo, o {@code null} si no existe o está deshabilitado
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public DescuentoConfig buscarPorId(String codigo) {
    String sql = "SELECT * FROM descuento_config WHERE codigo = ? AND activo = 1";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, codigo);
      try (ResultSet resultado = statement.executeQuery()) {
        return resultado.next() ? mapearDescuento(resultado) : null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo buscar el descuento.", e);
    }
  }

  /**
   * Devuelve todos los descuentos configurados, activos e inactivos.
   *
   * @return lista con todas las configuraciones de descuento
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public List<DescuentoConfig> listarTodos() {
    List<DescuentoConfig> descuentos = new ArrayList<>();
    String sql = "SELECT * FROM descuento_config";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql);
        ResultSet resultado = statement.executeQuery()) {
      while (resultado.next()) {
        descuentos.add(mapearDescuento(resultado));
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo listar los descuentos.", e);
    }
    return descuentos;
  }

  /**
   * Elimina una configuración de descuento por su código.
   *
   * @param codigo código del descuento
   * @return {@code true} si se eliminó el descuento
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public boolean eliminar(String codigo) {
    String sql = "DELETE FROM descuento_config WHERE codigo = ?";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, codigo);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo eliminar el descuento.", e);
    }
  }

  private DescuentoConfig mapearDescuento(ResultSet resultado) throws SQLException {
    return new DescuentoConfig(
        resultado.getString("codigo"),
        resultado.getString("tipo"),
        resultado.getDouble("valor"),
        resultado.getInt("activo") == 1);
  }
}
