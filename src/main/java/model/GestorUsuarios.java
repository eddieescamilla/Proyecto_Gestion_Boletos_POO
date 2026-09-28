package model;

import persistencia.UsuarioPersistencia;

/** Gestiona el registro y el inicio de sesión de los usuarios. */
public class GestorUsuarios {

  private UsuarioPersistencia persistencia;

  /** Crea el gestor con acceso a la persistencia de usuarios. */
  public GestorUsuarios() {
    this.persistencia = new UsuarioPersistencia();
  }

  /**
   * Verifica que los usuarios estén disponibles.
   *
   * <p>Con la persistencia en base de datos no requiere carga previa.
   *
   * @return siempre {@code true}
   */
  public boolean cargarUsuarios() {
    return true;
  }

  /**
   * Registra un nuevo usuario con rol de cliente.
   *
   * @param nombre nombre completo
   * @param correo correo electrónico, que no debe estar registrado
   * @param clave contraseña
   * @return {@code true} si se registró; {@code false} si faltan datos o el correo ya existe
   */
  public boolean registrar(String nombre, String correo, String clave) {
    if (nombre == null || nombre.isBlank()
        || correo == null || correo.isBlank()
        || clave == null || clave.isBlank()) {
      return false;
    }
    if (persistencia.buscarPorId(correo) != null) {
      return false;
    }
    Comprador nuevo = new Comprador(nombre, correo, clave);
    return persistencia.guardar(nuevo);
  }

  /**
   * Valida las credenciales de un usuario.
   *
   * @param correo correo electrónico
   * @param clave contraseña
   * @return el usuario autenticado, o {@code null} si las credenciales no son válidas o la
   *     cuenta está inactiva
   */
  public Usuario iniciarSesion(String correo, String clave) {
    Usuario usuario = persistencia.buscarPorId(correo);
    if (usuario == null || !usuario.isActivo() || !usuario.getClave().equals(clave)) {
      return null;
    }
    return usuario;
  }
}