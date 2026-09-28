package model;

import java.util.List;
import persistencia.EventoPersistencia;

public class SistemaGestionBoletos {

  private EventoPersistencia persistencia;

  public SistemaGestionBoletos() {
    this.persistencia = new EventoPersistencia();
  }

  public boolean cargarEventos() {
    List<Evento> eventos = persistencia.listarTodos();
    if (eventos.isEmpty()) {
      throw new RuntimeException("No hay eventos validos para cargar.");
    }
    return true;
  }

  public void mostrarEventos() {
    List<Evento> eventos = persistencia.listarTodos();
    for (int i = 0; i < eventos.size(); i++) {
      System.out.println((i + 1) + ". " + eventos.get(i));
    }
  }

  public Evento seleccionarEvento(int opcion) {
    List<Evento> eventos = persistencia.listarTodos();
    if (opcion < 1 || opcion > eventos.size()) {
      throw new IllegalArgumentException("Opcion de evento invalida.");
    }
    return eventos.get(opcion - 1);
  }

  public boolean actualizarInventario(Evento evento) {
    return persistencia.actualizarInventario(evento);
  }

  public List<Evento> getListaEventos() {
    return persistencia.listarTodos();
  }
}