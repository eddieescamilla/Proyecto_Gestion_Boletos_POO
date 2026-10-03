package model;

import persistencia.UsuarioPersistencia;
import util.PasswordHasher;

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
   * <p>La contraseña se almacena hasheada con BCrypt; nunca se guarda en texto plano.
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
    String correoNormalizado = correo.trim().toLowerCase(java.util.Locale.ROOT);
    if (persistencia.buscarPorId(correoNormalizado) != null) {
      return false;
    }
    Comprador nuevo = new Comprador(nombre, correoNormalizado, PasswordHasher.hash(clave));
    return persistencia.guardar(nuevo);
  }

  /**
   * Valida las credenciales de un usuario.
   *
   * <p>La contraseña almacenada se espera hasheada con BCrypt. Si por compatibilidad con
   * cuentas creadas antes del hashing la fila trae texto plano, se compara con {@code
   * equals} y, si coincide, se rehashea y actualiza en la base de forma transparente.
   *
   * @param correo correo electrónico
   * @param clave contraseña
   * @return el usuario autenticado, o {@code null} si las credenciales no son válidas o la
   *     cuenta está inactiva
   */
  public Usuario iniciarSesion(String correo, String clave) {
    if (correo == null || clave == null) {
      return null;
    }
    String correoNormalizado = correo.trim().toLowerCase(java.util.Locale.ROOT);
    Usuario usuario = persistencia.buscarPorId(correoNormalizado);
    if (usuario == null || !usuario.isActivo()) {
      return null;
    }
    String almacenada = usuario.getClave();
    if (PasswordHasher.esHashBCrypt(almacenada)) {
      return PasswordHasher.verificar(clave, almacenada) ? usuario : null;
    }
    if (almacenada.equals(clave)) {
      persistencia.actualizarClave(correoNormalizado, PasswordHasher.hash(clave));
      return usuario;
    }
    return null;
  }
}
