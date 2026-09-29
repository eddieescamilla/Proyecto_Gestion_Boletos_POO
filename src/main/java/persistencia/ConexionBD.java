package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;

/**
 * Administra la conexión única a PostgreSQL y delega la creación de las
 * tablas y los datos iniciales a Flyway.
 */
public class ConexionBD {

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
        aplicarMigraciones();
        conexion = DriverManager.getConnection(
            ConfigBD.url(), ConfigBD.usuario(), ConfigBD.clave());
      } catch (SQLException e) {
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
