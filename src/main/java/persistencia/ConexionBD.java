package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PasswordHasher;

/**
 * Administra la conexión única a PostgreSQL y delega la creación de las
 * tablas y los datos iniciales a Flyway.
 */
public class ConexionBD {

  private static final Logger log = LoggerFactory.getLogger(ConexionBD.class);

  private static Connection conexion;

  /**
   * Devuelve la conexión a la base de datos, creándola la primera vez.
   *
   * <p>Al crearla, corre las migraciones pendientes ({@code src/main/resources/db/migration})
   * para dejar el esquema y los datos semilla al día.
   *
   * @return la conexión a la base de datos
   * @throws RuntimeException si no se puede conectar o migrar la base de datos
   */
  public static Connection obtenerConexion() {
    if (conexion == null) {
      try {
        log.info("Aplicando migraciones de base de datos");
        aplicarMigraciones();
        log.info("Abriendo conexion a la base de datos");
        conexion = DriverManager.getConnection(
            ConfigBD.url(), ConfigBD.usuario(), ConfigBD.clave());
        log.info("Conexion lista y esquema aplicado");
        sembrarAdminSiCorresponde();
      } catch (SQLException e) {
        log.error("Fallo la conexion a la base de datos", e);
        throw new RuntimeException("No se pudo conectar a la base de datos.", e);
      }
    }
    return conexion;
  }

  private static void aplicarMigraciones() {
    Flyway.configure()
        .dataSource(ConfigBD.url(), ConfigBD.usuario(), ConfigBD.clave())
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .baselineVersion("0")
        .load()
        .migrate();
  }

  // Siembra el administrador por defecto solo si la clave se provee via la variable
  // BOLETOS_ADMIN_PASSWORD (sistema o .env). La migracion V5 borra el admin plano que
  // sembraba V2, para que una instalacion limpia no quede con credenciales conocidas.
  private static void sembrarAdminSiCorresponde() {
    String clavePlana = ConfigBD.claveAdmin();
    if (clavePlana == null || clavePlana.isBlank()) {
      log.info("BOLETOS_ADMIN_PASSWORD no definida; no se siembra el administrador por defecto.");
      return;
    }
    String correo = ConfigBD.correoAdmin();
    String sql = "INSERT INTO usuarios (correo, nombre, clave, rol, activo) "
        + "VALUES (?, ?, ?, 'ADMINISTRADOR', 1) "
        + "ON CONFLICT (correo) DO NOTHING";
    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
      ps.setString(1, correo);
      ps.setString(2, "Administrador General");
      ps.setString(3, PasswordHasher.hash(clavePlana));
      int filas = ps.executeUpdate();
      if (filas > 0) {
        log.info("Administrador por defecto sembrado desde BOLETOS_ADMIN_PASSWORD ({}).", correo);
      } else {
        log.info("Administrador por defecto ya existia ({}); no se toca su clave.", correo);
      }
    } catch (SQLException e) {
      log.warn("No se pudo sembrar el administrador por defecto", e);
    }
  }
}
