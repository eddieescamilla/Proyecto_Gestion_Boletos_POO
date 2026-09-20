package persistencia;

import dao.DAO;
import model.Evento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventoPersistencia implements DAO<Evento> {

    private Connection conexion;

    public EventoPersistencia() {
        this.conexion = ConexionBD.obtenerConexion();
    }

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

    @Override
    public List<Evento> listarTodos() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos";
        try (PreparedStatement statement = conexion.prepareStatement(sql);
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
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, nombreEvento);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar el evento.", e);
        }
    }

    public boolean actualizarInventario(Evento evento) {
        String sql = "UPDATE eventos SET inventario_disponible = ? WHERE nombre_evento = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, evento.getInventarioDisponible());
            statement.setString(2, evento.getNombreEvento());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar el inventario.", e);
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