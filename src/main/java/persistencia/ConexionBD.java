package persistencia;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.PasswordHasher;

/**
 * Administra el pool de conexiones a PostgreSQL y delega la creación de las
 * tablas y los datos iniciales a Flyway.
 *
 * <p>Antes de 1.8.0 esta clase guardaba una única {@code Connection} estática
 * compartida entre todos los DAO. Con las pantallas pasando a correr en
 * segundo plano con {@link javafx.concurrent.Task}, dos tareas concurrentes
 * podían terminar ejecutando SQL sobre la misma conexión; la spec de JDBC
 * no garantiza que {@code Connection} sea thread-safe (CWE-662).
 *
 * <p>Ahora exponemos un {@link DataSource} (HikariCP) y cada DAO pide una
 * conexión por operación con try-with-resources, para que el pool las reparta
 * entre los hilos.
 */
public class ConexionBD {

  private static final Logger log = LoggerFactory.getLogger(ConexionBD.class);

  private static HikariDataSource dataSource;

  /**
   * Devuelve el {@link DataSource} compartido. Lo crea la primera vez junto
   * con la aplicación de migraciones y el sembrado del administrador por
   * defecto.
   *
   * @return pool HikariCP listo para repartir conexiones
   * @throws RuntimeException si no se puede inicializar el pool o migrar la base
   */
  public static synchronized DataSource obtenerDataSource() {
    if (dataSource == null) {
      log.info("Aplicando migraciones de base de datos");
      aplicarMigraciones();
      log.info("Inicializando el pool de conexiones HikariCP");
      dataSource = construirPool();
      log.info("Pool listo (maximumPoolSize={})", dataSource.getMaximumPoolSize());
      sembrarAdminSiCorresponde();
    }
    return dataSource;
  }

  private static HikariDataSource construirPool() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(ConfigBD.url());
    config.setUsername(ConfigBD.usuario());
    config.setPassword(ConfigBD.clave());
    config.setPoolName("boletos-pool");
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(2);
    config.setConnectionTimeout(10_000L);
    return new HikariDataSource(config);
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
    try (Connection conexion = dataSource.getConnection();
        PreparedStatement ps = conexion.prepareStatement(sql)) {
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
