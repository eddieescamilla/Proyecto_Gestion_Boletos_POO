package model;

import persistencia.UsuarioPersistencia;

public class GestorUsuarios {

    private UsuarioPersistencia persistencia;

    public GestorUsuarios(String rutaBD) {
        this.persistencia = new UsuarioPersistencia(rutaBD);
    }

    public boolean cargarUsuarios() {
        return true;
    }

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

    public Usuario iniciarSesion(String correo, String clave) {
        Usuario usuario = persistencia.buscarPorId(correo);
        if (usuario == null || !usuario.isActivo() || !usuario.getClave().equals(clave)) {
            return null;
        }
        return usuario;
    }
}