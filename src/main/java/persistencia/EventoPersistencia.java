package persistencia;

import dao.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import model.Evento;

/** Persistencia de los eventos en PostgreSQL mediante el patrón DAO. */
public class EventoPersistencia implements DAO<Evento> {

  private final DataSource dataSource;

  /** Crea la persistencia usando el pool compartido de conexiones. */
  public EventoPersistencia() {
    this.dataSource = ConexionBD.obtenerDataSource();
  }

  @Override
  public boolean guardar(Evento evento) {
    String sql = "INSERT INTO eventos (nombre_evento, categoria, fecha, lugar, " +
        "inventario_disponible, precio_boleto) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, evento.getNombreEvento());
      statement.setString(2, evento.getCategoria());
      statement.setString(3, evento.getFecha().toString());
      statement.setString(4, evento.getLugar());
      statement.setInt(5, evento.getInventarioDisponible());
      statement.setDouble(6, evento.getPrecioBoleto());
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo guardar el evento.", e);
    }
  }

  @Override
  public Evento buscarPorId(String nombreEvento) {
    String sql = "SELECT * FROM eventos WHERE nombre_evento = ?";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, nombreEvento);
      try (ResultSet resultado = statement.executeQuery()) {
        return resultado.next() ? mapearEvento(resultado) : null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo buscar el evento.", e);
    }
  }

  @Override
  public List<Evento> listarTodos() {
    List<Evento> eventos = new ArrayList<>();
    String sql = "SELECT * FROM eventos";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql);
        ResultSet resultado = statement.executeQuery()) {
      while (resultado.next()) {
        eventos.add(mapearEvento(resultado));
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo listar los eventos.", e);
    }
    return eventos;
  }

  @Override
  public boolean eliminar(String nombreEvento) {
    String sql = "DELETE FROM eventos WHERE nombre_evento = ?";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, nombreEvento);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo eliminar el evento.", e);
    }
  }

  /**
   * Actualiza en la base de datos el inventario disponible de un evento.
   *
   * @param evento evento con el inventario actualizado
   * @return {@code true} si se actualizó el evento
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public boolean actualizarInventario(Evento evento) {
    String sql = "UPDATE eventos SET inventario_disponible = ? WHERE nombre_evento = ?";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setInt(1, evento.getInventarioDisponible());
      statement.setString(2, evento.getNombreEvento());
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo actualizar el inventario.", e);
    }
  }

  /**
   * Descuenta boletos del inventario en una sola operación, solo si hay suficientes.
   *
   * <p>La condición {@code inventario_disponible >= cantidad} va en la misma sentencia
   * {@code UPDATE}, así que dos compras simultáneas no pueden vender más boletos de los que hay.
   *
   * @param nombreEvento nombre del evento
   * @param cantidad cantidad de boletos a descontar
   * @return {@code true} si había inventario suficiente y se descontó
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public boolean descontarInventarioAtomico(String nombreEvento, int cantidad) {
    String sql = "UPDATE eventos SET inventario_disponible = inventario_disponible - ? " +
        "WHERE nombre_evento = ? AND inventario_disponible >= ?";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setInt(1, cantidad);
      statement.setString(2, nombreEvento);
      statement.setInt(3, cantidad);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo descontar el inventario.", e);
    }
  }

  /**
   * Actualiza la categoría, la fecha, el lugar, el inventario y el precio de un evento.
   *
   * <p>El nombre no se modifica, porque identifica al evento.
   *
   * @param evento evento con los datos actualizados
   * @return {@code true} si se actualizó el evento
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public boolean actualizarEvento(Evento evento) {
    String sql = "UPDATE eventos SET categoria = ?, fecha = ?, lugar = ?, " +
        "inventario_disponible = ?, precio_boleto = ? WHERE nombre_evento = ?";
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, evento.getCategoria());
      statement.setString(2, evento.getFecha().toString());
      statement.setString(3, evento.getLugar());
      statement.setInt(4, evento.getInventarioDisponible());
      statement.setDouble(5, evento.getPrecioBoleto());
      statement.setString(6, evento.getNombreEvento());
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo actualizar el evento.", e);
    }
  }

  private Evento mapearEvento(ResultSet resultado) throws SQLException {
    String nombre = resultado.getString("nombre_evento");
    String categoria = resultado.getString("categoria");
    LocalDate fecha = LocalDate.parse(resultado.getString("fecha"));
    String lugar = resultado.getString("lugar");
    int inventario = resultado.getInt("inventario_disponible");
    double precio = resultado.getDouble("precio_boleto");
    return new Evento(nombre, categoria, fecha, lugar, inventario, precio);
  }
}
