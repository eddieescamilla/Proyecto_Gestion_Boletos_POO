package persistencia;

/** Datos de conexión a la base de datos, tomados de variables de entorno o valores por defecto. */
public class ConfigBD {

  /**
   * Devuelve la URL de conexión ({@code BOLETOS_DB_URL}).
   *
   * @return la URL de conexión JDBC
   */
  public static String url() {
    return System.getenv()
        .getOrDefault("BOLETOS_DB_URL", "jdbc:postgresql://localhost:5432/boletos");
  }

  /**
   * Devuelve el usuario de la base de datos ({@code BOLETOS_DB_USER}).
   *
   * @return el usuario de la base de datos
   */
  public static String usuario() {
    return System.getenv().getOrDefault("BOLETOS_DB_USER", "boletos");
  }

  /**
   * Devuelve la contraseña de la base de datos ({@code BOLETOS_DB_PASSWORD}).
   *
   * @return la contraseña de la base de datos
   */
  public static String clave() {
    return System.getenv().getOrDefault("BOLETOS_DB_PASSWORD", "boletos123");
  }
}
