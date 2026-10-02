package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Administra la conexión única a PostgreSQL y prepara las tablas y los datos iniciales. */
public class ConexionBD {

  private static Connection conexion;

  /**
   * Devuelve la conexión a la base de datos, creándola la primera vez.
   *
   * <p>Al crearla, también crea las tablas y carga los datos iniciales si no existen.
   *
   * @return la conexión a la base de datos
   * @throws RuntimeException si no se puede conectar a la base de datos
   */
  public static Connection obtenerConexion() {
    if (conexion == null) {
      try {
        conexion = DriverManager.getConnection(
            ConfigBD.url(), ConfigBD.usuario(), ConfigBD.clave());
        crearTablas(conexion);
        sembrarDatosIniciales(conexion);
      } catch (SQLException e) {
        throw new RuntimeException("No se pudo conectar a la base de datos.", e);
      }
    }
    return conexion;
  }

  private static void crearTablas(Connection conexion) throws SQLException {
    try (Statement statement = conexion.createStatement()) {
      statement.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
          "correo TEXT PRIMARY KEY, " +
          "nombre TEXT NOT NULL, " +
          "clave TEXT NOT NULL, " +
          "rol TEXT NOT NULL, " +
          "activo INTEGER NOT NULL)");

      statement.execute("CREATE TABLE IF NOT EXISTS eventos (" +
          "nombre_evento TEXT PRIMARY KEY, " +
          "categoria TEXT NOT NULL, " +