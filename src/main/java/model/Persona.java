package model;

public abstract class Persona {

    protected String nombre;

    public Persona(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public double obtenerDescuentoAdicional() {
        return 0.0;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
