package model;

import catalogo.RolUsuario;

/** Usuario con rol de cliente que puede comprar boletos. */
public class Comprador extends Usuario {

  /**
   * Crea un comprador activo.
   *
   * @param nombre nombre completo
   * @param correo correo electrónico, que funciona como identificador
   * @param clave contraseña
   */
  public Comprador(String nombre, String correo, String clave) {
    super(nombre, correo, clave, RolUsuario.CLIENTE);
  }
}