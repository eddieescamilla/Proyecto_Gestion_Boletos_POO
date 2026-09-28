package model;

import java.util.List;
import persistencia.EventoPersistencia;

/** Gestiona la consulta, selección e inventario de los eventos. */
public class SistemaGestionBoletos {

  private EventoPersistencia persistencia;

  /** Crea el sistema con acceso a la persistencia de eventos. */
  public SistemaGestionBoletos() {
    this.persistencia = new EventoPersistencia();
  }

  /**
   * Verifica que existan eventos en la base de datos.
   *
   * @return {@code true} si hay eventos disponibles
   * @throws RuntimeException si no hay eventos para cargar
   */
  public boolean cargarEventos() {
    List<Evento> eventos = persistencia.listarTodos();
    if (eventos.isEmpty()) {
      throw new RuntimeException("No hay eventos validos para cargar.");
    }
    return true;
  }

  /** Muestra en consola la lista numerada de eventos. */
  public void mostrarEventos() {
    List<Evento> eventos = persistencia.listarTodos();
    for (int i = 0; i < eventos.size(); i++) {
      System.out.println((i + 1) + ". " + eventos.get(i));
    }
  }

  /**
   * Devuelve el evento que corresponde a una opción de la lista.
   *
   * @param opcion número del evento en la lista, empezando en 1
   * @return el evento seleccionado
   * @throws IllegalArgumentException si la opción está fuera de rango
   */
  public Evento seleccionarEvento(int opcion) {
    List<Evento> eventos = persistencia.listarTodos();
    if (opcion < 1 || opcion > eventos.size()) {
      throw new IllegalArgumentException("Opcion de evento invalida.");
    }
    return eventos.get(opcion - 1);
  }

  /**
   * Guarda en la base de datos el inventario actual de un evento.
   *
   * @param evento evento con el inventario actualizado
   * @return {@code true} si se actualizó
   */
  public boolean actualizarInventario(Evento evento) {
    return persistencia.actualizarInventario(evento);
  }

  /**
   * Devuelve todos los eventos de la base de datos.
   *
   * @return la lista de eventos
   */
  public List<Evento> getListaEventos() {
    return persistencia.listarTodos();
  }
}