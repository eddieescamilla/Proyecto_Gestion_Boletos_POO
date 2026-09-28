package persistencia;

import dao.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Compra;

public class CompraPersistencia implements DAO<RegistroCompra> {

  private Connection conexion;

  public CompraPersistencia() {
    this.conexion = ConexionBD.obtenerConexion();
  }

  public boolean guardarCompra(Compra compra) {
    RegistroCompra registro = new RegistroCompra(
        compra.getComprador().getCorreo(),
        compra.getEvento().getNombreEvento(),
        compra.getEvento().getCategoria(),
        compra.getCantidadBoletos(),
        compra.getTotal(),
        LocalDate.now()
    );
    return guardar(registro);
  }

  @Override
  public boolean guardar(RegistroCompra registro) {
    String sql = "INSERT INTO compras (correo_comprador, nombre_evento, categoria_evento, " +
        "cantidad_boletos, total, fecha) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, registro.getCorreoComprador());
      statement.setString(2, registro.getNombreEvento());
      statement.setString(3, registro.getCategoriaEvento());
      statement.setInt(4, registro.getCantidadBoletos());
      statement.setDouble(5, registro.getTotal());
      statement.setString(6, registro.getFecha().toString());
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo guardar el registro de la compra.", e);
    }
  }

  @Override
  public RegistroCompra buscarPorId(String id) {
    String sql = "SELECT * FROM compras WHERE id = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setInt(1, Integer.parseInt(id));
      try (ResultSet resultado = statement.executeQuery()) {
        return resultado.next() ? mapearRegistro(resultado) : null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo buscar la compra.", e);
    }
  }

  @Override
  public List<RegistroCompra> listarTodos() {
    List<RegistroCompra> registros = new ArrayList<>();
    String sql = "SELECT * FROM compras";
    try (PreparedStatement statement = conexion.prepareStatement(sql);
        ResultSet resultado = statement.executeQuery()) {
      while (resultado.next()) {
        registros.add(mapearRegistro(resultado));
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo listar las compras.", e);
    }
    return registros;
  }

  @Override
  public boolean eliminar(String id) {
    String sql = "DELETE FROM compras WHERE id = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setInt(1, Integer.parseInt(id));
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo eliminar la compra.", e);
    }
  }

  public String generarReportePorCategoria(String categoria) {
    if (categoria == null || categoria.isBlank()) {
      return "Debe seleccionar una categoria antes de generar el reporte.";
    }
    String sql = "SELECT SUM(cantidad_boletos) AS total_boletos, SUM(total) AS ingreso_total " +
        "FROM compras WHERE categoria_evento = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, categoria);
      try (ResultSet resultado = statement.executeQuery()) {
        if (resultado.next() && resultado.getInt("total_boletos") > 0) {
          int totalBoletos = resultado.getInt("total_boletos");
          double ingresoTotal = resultado.getDouble("ingreso_total");
          return "Reporte - Categoria: " + categoria
              + "\nBoletos vendidos: " + totalBoletos
              + "\nIngreso total: $" + ingresoTotal;
        }
        return "No hay datos disponibles para la categoria '" + categoria + "'.";
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo generar el reporte.", e);
    }
  }

  private RegistroCompra mapearRegistro(ResultSet resultado) throws SQLException {
    String correo = resultado.getString("correo_comprador");
    String nombreEvento = resultado.getString("nombre_evento");
    String categoria = resultado.getString("categoria_evento");
    int cantidad = resultado.getInt("cantidad_boletos");
    double total = resultado.getDouble("total");
    LocalDate fecha = LocalDate.parse(resultado.getString("fecha"));
    return new RegistroCompra(correo, nombreEvento, categoria, cantidad, total, fecha);
  }
}
