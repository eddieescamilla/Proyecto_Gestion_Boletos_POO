package model;

import catalogo.RolUsuario;

/** Usuario con rol de administrador del sistema. */
public class Administrador extends Usuario {

  /**
   * Crea un administrador activo.
   *
   * @param nombre nombre completo
   * @param correo correo electrónico, que funciona como identificador
   * @param clave contraseña
   */
  public Administrador(String nombre, String correo, String clave) {
    super(nombre, correo, clave, RolUsuario.ADMINISTRADOR);
  }
}