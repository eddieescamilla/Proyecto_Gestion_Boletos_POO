package persistencia;

import catalogo.RolUsuario;
import dao.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Administrador;
import model.Comprador;
import model.Usuario;

/** Persistencia de los usuarios en PostgreSQL mediante el patrón DAO. */
public class UsuarioPersistencia implements DAO<Usuario> {

  private Connection conexion;

  /** Crea la persistencia usando la conexión compartida a la base de datos. */
  public UsuarioPersistencia() {
    this.conexion = ConexionBD.obtenerConexion();
  }

  /**
   * Guarda un usuario nuevo.
   *
   * @param usuario usuario a guardar
   * @return {@code true} si se guardó correctamente
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public boolean guardar(Usuario usuario) {
    String sql = "INSERT INTO usuarios (correo, nombre, clave, rol, activo) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, usuario.getCorreo());
      statement.setString(2, usuario.getNombre());
      statement.setString(3, usuario.getClave());
      statement.setString(4, usuario.getRol().toString());
      statement.setInt(5, usuario.isActivo() ? 1 : 0);
      statement.executeUpdate();
      return true;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo guardar el usuario.", e);
    }
  }

  /**
   * Busca un usuario por su correo.
   *
   * @param correo correo del usuario
   * @return el usuario encontrado, o {@code null} si no existe
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public Usuario buscarPorId(String correo) {
    String sql = "SELECT * FROM usuarios WHERE correo = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, correo);
      try (ResultSet resultado = statement.executeQuery()) {
        return resultado.next() ? mapearUsuario(resultado) : null;
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo buscar el usuario.", e);
    }
  }

  /**
   * Devuelve todos los usuarios registrados.
   *
   * @return lista con todos los usuarios
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public List<Usuario> listarTodos() {
    List<Usuario> usuarios = new ArrayList<>();
    String sql = "SELECT * FROM usuarios";
    try (PreparedStatement statement = conexion.prepareStatement(sql);
        ResultSet resultado = statement.executeQuery()) {
      while (resultado.next()) {
        usuarios.add(mapearUsuario(resultado));
      }
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo listar los usuarios.", e);
    }
    return usuarios;
  }

  /**
   * Elimina un usuario por su correo.
   *
   * @param correo correo del usuario
   * @return {@code true} si se eliminó el usuario
   * @throws RuntimeException si ocurre un error de base de datos
   */
  @Override
  public boolean eliminar(String correo) {
    String sql = "DELETE FROM usuarios WHERE correo = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, correo);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo eliminar el usuario.", e);
    }
  }

  /**
   * Actualiza el estado (activo o inactivo) de un usuario.
   *
   * @param correo correo del usuario
   * @param activo nuevo estado del usuario
   * @return {@code true} si se actualizó el estado
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public boolean actualizarEstado(String correo, boolean activo) {
    String sql = "UPDATE usuarios SET activo = ? WHERE correo = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setInt(1, activo ? 1 : 0);
      statement.setString(2, correo);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo actualizar el estado del usuario.", e);
    }
  }

  /**
   * Actualiza la contraseña almacenada de un usuario.
   *
   * <p>Se usa para reemplazar contraseñas heredadas en texto plano por su hash BCrypt en el
   * primer inicio de sesión exitoso, y también será el hook para el flujo de recuperación
   * de contraseña.
   *
   * @param correo correo del usuario
   * @param clave nueva contraseña (ya hasheada)
   * @return {@code true} si la fila se actualizó
   * @throws RuntimeException si ocurre un error de base de datos
   */
  public boolean actualizarClave(String correo, String clave) {
    String sql = "UPDATE usuarios SET clave = ? WHERE correo = ?";
    try (PreparedStatement statement = conexion.prepareStatement(sql)) {
      statement.setString(1, clave);
      statement.setString(2, correo);
      return statement.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("No se pudo actualizar la contrasena del usuario.", e);
    }
  }

  private Usuario mapearUsuario(ResultSet resultado) throws SQLException {
    String nombre = resultado.getString("nombre");
    String correo = resultado.getString("correo");
    String clave = resultado.getString("clave");
    RolUsuario rol = RolUsuario.valueOf(resultado.getString("rol"));
    boolean activo = resultado.getInt("activo") == 1;

    Usuario usuario = (rol == RolUsuario.ADMINISTRADOR)
        ? new Administrador(nombre, correo, clave)
        : new Comprador(nombre, correo, clave);
    usuario.setActivo(activo);
    return usuario;
  }
}
