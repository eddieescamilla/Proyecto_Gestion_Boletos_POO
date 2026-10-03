package util;

import model.Usuario;

/**
 * Guarda el usuario autenticado durante la vida de la aplicación.
 *
 * <p>Las pantallas que necesitan saber quién está en sesión (compra, historial, panel de
 * administración) leen de aquí en lugar de recibir el usuario como parámetro en cada
 * navegación. Al cerrar sesión se limpia el valor para que no quede filtración entre
 * cuentas si otro usuario inicia sesión en la misma ejecución.
 */
public final class Sesion {

  private static Usuario usuarioActual;

  private Sesion() {}

  /**
   * Guarda el usuario autenticado como la sesión activa.
   *
   * @param usuario usuario que inició sesión
   */
  public static void setUsuarioActual(Usuario usuario) {
    usuarioActual = usuario;
  }

  /**
   * Devuelve el usuario autenticado.
   *
   * @return el usuario en sesión, o {@code null} si nadie ha iniciado sesión
   */
  public static Usuario getUsuarioActual() {
    return usuarioActual;
  }

  /** Cierra la sesión actual limpiando el usuario autenticado. */
  public static void cerrar() {
    usuarioActual = null;
  }

  /**
   * Devuelve el saludo que se muestra en el encabezado de las pantallas del cliente.
   *
   * @return "Bienvenido, " seguido del nombre del usuario, o solo "Bienvenido" si no hay sesión
   */
  public static String textoBienvenida() {
    return usuarioActual == null ? "Bienvenido" : "Bienvenido, " + usuarioActual.getNombre();
  }
}
