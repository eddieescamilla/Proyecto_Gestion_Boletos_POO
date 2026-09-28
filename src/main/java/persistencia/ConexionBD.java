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
          "fecha TEXT NOT NULL, " +
          "lugar TEXT NOT NULL, " +
          "inventario_disponible INTEGER NOT NULL, " +
          "precio_boleto REAL NOT NULL)");

      statement.execute("CREATE TABLE IF NOT EXISTS compras (" +
          "id SERIAL PRIMARY KEY, " +
          "correo_comprador TEXT NOT NULL, " +
          "nombre_evento TEXT NOT NULL, " +
          "categoria_evento TEXT NOT NULL, " +
          "cantidad_boletos INTEGER NOT NULL, " +
          "total REAL NOT NULL, " +
          "fecha TEXT NOT NULL)");
    }
  }

  private static void sembrarDatosIniciales(Connection conexion) throws SQLException {
    try (Statement statement = conexion.createStatement()) {

      try (ResultSet resultadoUsuarios = statement.executeQuery(
          "SELECT COUNT(*) AS total FROM usuarios")) {
        resultadoUsuarios.next();
        if (resultadoUsuarios.getInt("total") == 0) {
          statement.executeUpdate(
              "INSERT INTO usuarios (correo, nombre, clave, rol, activo) VALUES " +
                  "('admin@boletos.com', 'Administrador General', 'admin123', 'ADMINISTRADOR', 1)");
        }
      }

      try (ResultSet resultadoEventos = statement.executeQuery(
          "SELECT COUNT(*) AS total FROM eventos")) {
        resultadoEventos.next();
        if (resultadoEventos.getInt("total") == 0) {
          statement.executeUpdate("INSERT INTO eventos VALUES " +
              "('Concierto Rock Nacional', 'Musica', '2026-11-20', 'Estadio Cuscatlan', 45, 25.0)");
          statement.executeUpdate("INSERT INTO eventos VALUES " +
              "('Festival de Jazz', 'Musica', '2026-10-15', 'Teatro Nacional', 26, 40.0)");
          statement.executeUpdate("INSERT INTO eventos VALUES " +
              "('Obra de Teatro', 'Teatro', '2026-12-05', 'Teatro Presidente', 15, 15.5)");
        }
      }
    }
  }
}