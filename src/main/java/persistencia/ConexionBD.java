package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
