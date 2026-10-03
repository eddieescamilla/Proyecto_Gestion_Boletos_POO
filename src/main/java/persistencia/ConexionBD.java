package persistencia;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
   * con la aplicación de migraciones.
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
}
