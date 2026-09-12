package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorUsuarios {

    private List<Usuario> listaUsuarios;
    private String archivoUsuarios;

    public GestorUsuarios(String archivoUsuarios) {
        this.archivoUsuarios = archivoUsuarios;
        this.listaUsuarios = new ArrayList<>();
    }

    public boolean cargarUsuarios() {
        listaUsuarios.clear();
        File archivo = new File(archivoUsuarios);

        if (!archivo.exists()) {
            throw new RuntimeException("No se pudo abrir el archivo de usuarios.");
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) continue;

                try {
                    String[] partes = linea.split("\\|");
                    if (partes.length != 5) {
                        throw new IllegalArgumentException("Formato incorrecto en la linea.");
                    }

                    String nombre = partes[0];
                    String correo = partes[1];
                    String clave = partes[2];
                    RolUsuario rol = RolUsuario.valueOf(partes[3].trim().toUpperCase());
                    boolean activo = Boolean.parseBoolean(partes[4]);

                    Usuario usuario = (rol == RolUsuario.ADMINISTRADOR)
                            ? new Administrador(nombre, correo, clave)
                            : new Comprador(nombre, correo, clave);
                    usuario.setActivo(activo);
                    listaUsuarios.add(usuario);

                } catch (Exception e) {
                    System.out.println("Advertencia: se omitio la linea " + numeroLinea
                            + " del archivo de usuarios. Motivo: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("No se pudo abrir el archivo de usuarios.");
        }

        return true;
    }

    public boolean guardarUsuarios() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoUsuarios))) {
            for (Usuario usuario : listaUsuarios) {
                escritor.write(usuario.getNombre() + "|"
                        + usuario.getCorreo() + "|"
                        + usuario.getClave() + "|"
                        + usuario.getRol() + "|"
                        + usuario.isActivo());
                escritor.newLine();
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de usuarios.");
        }
    }

    public boolean registrar(String nombre, String correo, String clave) {
        if (nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank()
                || clave == null || clave.isBlank()) {
            return false;
        }
        if (buscarPorCorreo(correo) != null) {
            return false;
        }
        Comprador nuevo = new Comprador(nombre, correo, clave);
        listaUsuarios.add(nuevo);
        guardarUsuarios();
        return true;
    }

    public Usuario iniciarSesion(String correo, String clave) {
        Usuario usuario = buscarPorCorreo(correo);
        if (usuario == null || !usuario.isActivo() || !usuario.getClave().equals(clave)) {
            return null;
        }
        return usuario;
    }

    private Usuario buscarPorCorreo(String correo) {
        for (Usuario usuario : listaUsuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                return usuario;
            }
        }
        return null;
    }
}