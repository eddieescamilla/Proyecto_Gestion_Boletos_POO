package model;

import catalogo.RolUsuario;

public class Administrador extends Usuario {

  public Administrador(String nombre, String correo, String clave) {
    super(nombre, correo, clave, RolUsuario.ADMINISTRADOR);
  }
}