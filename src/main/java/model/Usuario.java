package model;

import catalogo.RolUsuario;

public abstract class Usuario {

  protected String nombre;
  protected String correo;
  protected String clave;
  protected RolUsuario rol;
  protected boolean activo;

  public Usuario(String nombre, String correo, String clave, RolUsuario rol) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre no puede estar vacio.");
    }
    if (correo == null || correo.isBlank()) {
      throw new IllegalArgumentException("El correo no puede estar vacio.");
    }
    if (clave == null || clave.isBlank()) {
      throw new IllegalArgumentException("La clave no puede estar vacia.");
    }
    this.nombre = nombre;
    this.correo = correo;
    this.clave = clave;
    this.rol = rol;
    this.activo = true;
  }

  public String getNombre() {
    return nombre;
  }

  public String getCorreo() {
    return correo;
  }

  public String getClave() {
    return clave;
  }

  public RolUsuario getRol() {
    return rol;
  }

  public boolean isActivo() {
    return activo;
  }

  public void setActivo(boolean activo) {
    this.activo = activo;
  }

  public double obtenerDescuentoAdicional() {
    return 0.0;
  }

  @Override
  public String toString() {
    return nombre + " (" + rol + ")";
  }
}