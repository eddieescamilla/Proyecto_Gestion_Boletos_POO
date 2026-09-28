package model;

import catalogo.RolUsuario;

public class Comprador extends Usuario {

  public Comprador(String nombre, String correo, String clave) {
    super(nombre, correo, clave, RolUsuario.CLIENTE);
  }
}