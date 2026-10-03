package persistencia;

import dao.DAO;
import model.DescuentoConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DescuentoPersistencia implements DAO<DescuentoConfig> {

    private Connection conexion;

    public DescuentoPersistencia() {
        this.conexion = ConexionBD.obtenerConexion();
    }

    @Override
    public boolean guardar(DescuentoConfig descuento) {
        String sql = "INSERT INTO descuento_config (codigo, tipo, valor, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
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

    @Override
    public DescuentoConfig buscarPorId(String codigo) {
        String sql = "SELECT * FROM descuento_config WHERE codigo = ? AND activo = 1";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, codigo);
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next() ? mapearDescuento(resultado) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo buscar el descuento.", e);
        }
    }

    @Override
    public List<DescuentoConfig> listarTodos() {
        List<DescuentoConfig> descuentos = new ArrayList<>();
        String sql = "SELECT * FROM descuento_config";
        try (PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                descuentos.add(mapearDescuento(resultado));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo listar los descuentos.", e);
        }
        return descuentos;
    }

    @Override
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM descuento_config WHERE codigo = ?";
        try (PreparedStatement statement = conexion.prepareStatement(sql)) {
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
                resultado.getInt("activo") == 1
        );
    }
}
