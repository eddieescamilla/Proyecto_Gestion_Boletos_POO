package persistencia;

import dao.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Evento;

/** Persistencia de los eventos en PostgreSQL mediante el patrón DAO. */
public class EventoPersistencia implements DAO<Evento> {

  private Connection conexion;

  /** Crea la persistencia usando la conexión compartida a la base de datos. */
  public EventoPersistencia() {
    this.conexion = ConexionBD.obtenerConexion();
  }

  /**
   * Guarda un evento nuevo.
   *
   * @param evento evento a guardar
   * @return {@code true} si se guardó correctamente
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public boolean guardar(Evento evento) {
    String sql = "INSERT INTO eventos (nombre_evento, categoria, fecha, lugar, " +
        "inventario_disponible, precio_boleto) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
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

  /**
   * Busca un evento por su nombre.
   *
   * @param nombreEvento nombre del evento
   * @return el evento encontrado, o {@code null} si no existe
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public Evento buscarPorId(String nombreEvento) {
    String sql = "SELECT * FROM eventos WHERE nombre_evento = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, nombreEvento);
      try (ResultSet resultado = statement.executeQuery()) {
        return resultado.next() ? mapearEvento(resultado) : null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo buscar el evento.", e);
    }
  }

  /**
   * Devuelve todos los eventos registrados.
   *
   * @return lista con todos los eventos
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public List<Evento> listarTodos() {
    List<Evento> eventos = new ArrayList<>();
    String sql = "SELECT * FROM eventos";
    try (PreparedStatement