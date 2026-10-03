package persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Datos de conexión a la base de datos.
 *
 * <p>Cada parámetro se resuelve en este orden: primero el archivo {@code .env}
 * en la raíz del proyecto (si existe), luego la variable de entorno del
 * sistema, y por último el valor por defecto para desarrollo local.
 *
 * <p>El archivo {@code .env} está en {@code .gitignore} y sirve para que
 * cada quien tenga sus propias credenciales sin filtrarlas al repositorio.
 * Como referencia queda {@code .env.example}.
 */
public class ConfigBD {

  private static final String URL_POR_DEFECTO = "jdbc:postgresql://localhost:5432/boletos";
  private static final String USUARIO_POR_DEFECTO = "boletos";
  private static final String CLAVE_POR_DEFECTO = "boletos123";

  private static final Map<String, String> archivoEnv = cargarArchivoEnv();

  /**
   * Devuelve la URL de conexión JDBC.
   *
   * @return la URL leída de {@code BOLETOS_DB_URL} o el valor por defecto de desarrollo
   */
  public static String url() {
    return leer("BOLETOS_DB_URL", URL_POR_DEFECTO);
  }

  /**
   * Devuelve el usuario de la base de datos.
   *
   * @return el usuario leído de {@code BOLETOS_DB_USER} o el valor por defecto de desarrollo
   */
  public static String usuario() {
    return leer("BOLETOS_DB_USER", USUARIO_POR_DEFECTO);
  }

  /**
   * Devuelve la contraseña de la base de datos.
   *
   * @return la contraseña leída de {@code BOLETOS_DB_PASSWORD} o el valor por defecto
   *     de desarrollo
   */
  public static String clave() {
    return leer("BOLETOS_DB_PASSWORD", CLAVE_POR_DEFECTO);
  }

  private static String leer(String clave, String porDefecto) {
    String desdeArchivo = archivoEnv.get(clave);
    if (desdeArchivo != null && !desdeArchivo.isBlank()) {
      return desdeArchivo;
    }
    String desdeEntorno = System.getenv(clave);
    if (desdeEntorno != null && !desdeEntorno.isBlank()) {
      return desdeEntorno;
    }
    return porDefecto;
  }

  private static Map<String, String> cargarArchivoEnv() {
    Path ruta = Paths.get(".env");
    if (!Files.exists(ruta)) {
      return Map.of();
    }
    Map<String, String> valores = new HashMap<>();
    try {
      for (String linea : Files.readAllLines(ruta)) {
        String limpia = linea.trim();
        if (limpia.isEmpty() || limpia.startsWith("#")) {
          continue;
        }
        int separador = limpia.indexOf('=');
        if (separador <= 0) {
          continue;
        }
        String clave = limpia.substring(0, separador).trim();
        String valor = limpia.substring(separador + 1).trim();
        if (valor.startsWith("\"") && valor.endsWith("\"") && valor.length() >= 2) {
          valor = valor.substring(1, valor.length() - 1);
        }
        valores.put(clave, valor);
      }
    } catch (IOException e) {
      return Map.of();
    }
    return valores;
  }
}
