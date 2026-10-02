package model;

import catalogo.RolUsuario;

/** Usuario del sistema con sus credenciales, rol y estado. */
public abstract class Usuario {

  /** Nombre completo del usuario. */
  protected String nombre;
  /** Correo electrónico, que funciona como identificador. */
  protected String correo;
  /** Contraseña del usuario. */
  protected String clave;
  /** Rol del usuario en el sistema. */
  protected RolUsuario rol;
  /** Indica si la cuenta está activa. */
  protected boolean activo;

  /**
   * Crea un usuario activo.
   *
   * @param nombre nombre completo
   * @param correo correo electrónico, que funciona como identificador
   * @param clave contraseña
   * @param rol rol del usuario
   * @throws IllegalArgumentException si el nombre, el correo o la clave están vacíos
   */
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

  /**
   * Devuelve el nombre del usuario.
   *
   * @return el nombre completo
   */
  public String getNombre() {
    return nombre;
  }

  /**
   * Devuelve el correo del usuario.
   *
   * @return el correo electrónico
   */
  public String getCorreo() {
    return correo;
  }

  /**
   * Devuelve la contraseña del usuario.
   *
   * @return la contraseña
   */
  public String getClave() {
    return clave;
  }

  /**
   * Devuelve el rol del usuario.
   *
   * @return el rol
   */
  public RolUsuario getRol() {
    return rol;
  }

  /**
   * Indica si la cuenta del usuario está activa.
   *
   * @return {@code true} si la cuenta está activa
   */
  public boolean isActivo() {
    return activo;
  }

  /**
   * Activa o desactiva la cuenta del usuario.
   *
   * @param activo {@code true} para activar la cuenta
   */
  public void setActivo(boolean activo) {
    this.activo = activo;
  }

  /**
   * Devuelve el porcentaje de descuento adicional del usuario.
   *
   * @return el porcentaje de descuento; 0 para usuarios sin beneficio
   */
  public double obtenerDescuentoAdicional() {
    return 0.0;
  }

  /**
   * Devuelve el nombre y el rol del usuario.
   *
   * @return el nombre y el rol
   */
  @Override
  public String toString() {
    return nombre + " (" + rol + ")";
  }
}