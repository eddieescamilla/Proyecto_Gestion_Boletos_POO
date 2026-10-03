package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import util.PasswordHasher;

/** Administra la conexión única a PostgreSQL y prepara las tablas y los datos iniciales. */
public class ConexionBD {

  private static Connection conexion;

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

      statement.execute("CREATE TABLE IF NOT EXISTS descuento_config (" +
          "codigo TEXT PRIMARY KEY, " +
          "tipo TEXT NOT NULL, " +
          "valor REAL NOT NULL, " +
          "activo INTEGER NOT NULL)");
    }
  }

  private static void sembrarDatosIniciales(Connection conexion) throws SQLException {
    try (Statement statement = conexion.createStatement()) {

      try (ResultSet resultadoUsuarios = statement.executeQuery(
          "SELECT COUNT(*) AS total FROM usuarios")) {
        resultadoUsuarios.next();
        if (resultadoUsuarios.getInt("total") == 0) {
          String sqlAdmin = "INSERT INTO usuarios (correo, nombre, clave, rol, activo) " +
              "VALUES (?, ?, ?, ?, ?)";
          try (PreparedStatement insertAdmin = conexion.prepareStatement(sqlAdmin)) {
            insertAdmin.setString(1, "admin@boletos.com");
            insertAdmin.setString(2, "Administrador General");
            insertAdmin.setString(3, PasswordHasher.hash("admin123"));
            insertAdmin.setString(4, "ADMINISTRADOR");
            insertAdmin.setInt(5, 1);
            insertAdmin.executeUpdate();
          }
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

      try (ResultSet resultadoDescuentos = statement.executeQuery(
          "SELECT COUNT(*) AS total FROM descuento_config")) {
        resultadoDescuentos.next();
        if (resultadoDescuentos.getInt("total") == 0) {
          statement.executeUpdate("INSERT INTO descuento_config (codigo, tipo, valor, activo) VALUES " +
              "('DESC10', 'PORCENTAJE', 10.0, 1)");
          statement.executeUpdate("INSERT INTO descuento_config (codigo, tipo, valor, activo) VALUES " +
              "('DESC5', 'PORCENTAJE', 5.0, 1)");
        }
      }
    }
  }
}
