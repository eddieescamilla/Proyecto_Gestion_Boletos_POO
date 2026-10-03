package util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utilidades para hashear y verificar contraseñas con BCrypt.
 *
 * <p>Las contraseñas nunca se almacenan en texto plano. Todos los hashes usan el algoritmo
 * BCrypt con un factor de costo de 12, que es el estándar recomendado a la fecha para
 * aplicaciones interactivas (~250 ms por verificación en hardware moderno).
 */
public final class PasswordHasher {

  private static final int FACTOR_COSTO = 12;
  private static final String PREFIJO_BCRYPT_2A = "$2a$";
  private static final String PREFIJO_BCRYPT_2B = "$2b$";
  private static final String PREFIJO_BCRYPT_2Y = "$2y$";

  private PasswordHasher() {}

  /**
   * Genera un hash BCrypt de la contraseña.
   *
   * @param claveEnClaro contraseña sin hashear
   * @return hash BCrypt listo para almacenar
   * @throws IllegalArgumentException si la contraseña es nula o vacía
   */
  public static String hash(String claveEnClaro) {
    if (claveEnClaro == null || claveEnClaro.isEmpty()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacia.");
    }
    return BCrypt.withDefaults().hashToString(FACTOR_COSTO, claveEnClaro.toCharArray());
  }

  /**
   * Verifica una contraseña contra un hash BCrypt.
   *
   * @param claveEnClaro contraseña ingresada por el usuario
   * @param hashAlmacenado hash guardado en la base
   * @return {@code true} si la contraseña coincide
   */
  public static boolean verificar(String claveEnClaro, String hashAlmacenado) {
    if (claveEnClaro == null || hashAlmacenado == null) {
      return false;
    }
    if (!esHashBCrypt(hashAlmacenado)) {
      return false;
    }
    return BCrypt.verifyer().verify(claveEnClaro.toCharArray(), hashAlmacenado).verified;
  }

  /**
   * Indica si un valor almacenado tiene la forma de un hash BCrypt.
   *
   * <p>Útil para detectar filas heredadas con contraseñas en texto plano y migrarlas al
   * primer inicio de sesión exitoso.
   *
   * @param valor cualquier cadena leída de la columna de contraseña
   * @return {@code true} si el prefijo corresponde a un hash BCrypt válido
   */
  public static boolean esHashBCrypt(String valor) {
    if (valor == null || valor.length() < 60) {
      return false;
    }
    return valor.startsWith(PREFIJO_BCRYPT_2A)
        || valor.startsWith(PREFIJO_BCRYPT_2B)
        || valor.startsWith(PREFIJO_BCRYPT_2Y);
  }
}
