package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SistemaGestionBoletos {

    private List<Evento> listaEventos;
    private String archivoEventos;

    public SistemaGestionBoletos(String archivoEventos) {
        this.archivoEventos = archivoEventos;
        this.listaEventos = new ArrayList<>();
    }

    public boolean cargarEventos() {
        listaEventos.clear();
        File archivo = new File(archivoEventos);

        if (!archivo.exists()) {
            throw new RuntimeException("No se pudo abrir el archivo de eventos.");
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank()) continue;

                try {
                    String[] partes = linea.split("\\|");
                    if (partes.length != 6) {
                        throw new IllegalArgumentException("Formato incorrecto en la linea.");
                    }

                    String nombre = partes[0];
                    String categoria = partes[1];
                    LocalDate fecha = LocalDate.parse(partes[2]);
                    String lugar = partes[3];
                    int inventario = Integer.parseInt(partes[4]);
                    double precio = Double.parseDouble(partes[5]);

                    if (nombre.isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacio.");
                    if (inventario < 0) throw new IllegalArgumentException("El inventario no puede ser negativo.");
                    if (precio <= 0) throw new IllegalArgumentException("El precio debe ser mayor a cero.");

                    listaEventos.add(new Evento(nombre, categoria, fecha, lugar, inventario, precio));

                } catch (Exception e) {
                    System.out.println("Advertencia: se omitio la linea " + numeroLinea
                            + " del archivo. Motivo: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("No se pudo abrir el archivo de eventos.");
        }

        if (listaEventos.isEmpty()) {
            throw new RuntimeException("No hay eventos validos para cargar.");
        }
        return true;
    }

    public boolean guardarEventos() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoEventos))) {
            for (Evento evento : listaEventos) {
                escritor.write(evento.getNombreEvento() + "|"
                        + evento.getCategoria() + "|"
                        + evento.getFecha() + "|"
                        + evento.getLugar() + "|"
                        + evento.getInventarioDisponible() + "|"
                        + evento.getPrecioBoleto());
                escritor.newLine();
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo de eventos.");
        }
    }

    public void mostrarEventos() {
        System.out.println("Seleccione un evento:");
        for (int i = 0; i < listaEventos.size(); i++) {
            System.out.println((i + 1) + ". " + listaEventos.get(i));
        }
    }

    public Evento seleccionarEvento(int opcion) {
        if (opcion < 1 || opcion > listaEventos.size()) {
            throw new IndexOutOfBoundsException("La opcion seleccionada no existe.");
        }
        return listaEventos.get(opcion - 1);
    }

    public List<Evento> getListaEventos() {
        return Collections.unmodifiableList(listaEventos);
    }
}