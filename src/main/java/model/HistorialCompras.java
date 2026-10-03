package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import persistencia.CompraPersistencia;
import persistencia.RegistroCompra;

/** Consulta el historial de compras de un cliente. */
public class HistorialCompras {

  private CompraPersistencia compraPersistencia;

  /**
   * Crea el historial con acceso a la persistencia de compras.
   *
   * @param compraPersistencia persistencia de las compras
   */
  public HistorialCompras(CompraPersistencia compraPersistencia) {
    this.compraPersistencia = compraPersistencia;
  }

  /**
   * Devuelve todas las compras de un cliente en formato de texto.
   *
   * @param correoCliente correo del cliente
   * @return el historial de compras, o un mensaje si no tiene compras
   */
  public String mostrarHistorial(String correoCliente) {
    List<RegistroCompra> compras = buscarPorCliente(correoCliente);
    if (compras.isEmpty()) {
      return "No tiene compras registradas.";
    }
    StringBuilder resultado = new StringBuilder("=== Historial de compras ===\n");
    for (RegistroCompra registro : compras) {
      resultado.append(registro.getFecha()).append(" | ")
          .append(registro.getNombreEvento()).append(" | ")
          .append(registro.getCantidadBoletos()).append(" boletos | $")
          .append(registro.getTotal()).append("\n");
    }
    return resultado.toString();
  }

  /**
   * Devuelve las compras de un cliente dentro de un rango de fechas, en formato de texto.
   *
   * @param correoCliente correo del cliente
   * @param desde fecha inicial del rango, incluida
   * @param hasta fecha final del rango, incluida
   * @return el historial filtrado, o un mensaje si no hay compras en el rango
   */
  public String mostrarHistorialFiltrado(String correoCliente, LocalDate desde, LocalDate hasta) {
    List<RegistroCompra> comprasEnRango = new ArrayList<>();
    for (RegistroCompra registro : buscarPorCliente(correoCliente)) {
      LocalDate fecha = registro.getFecha();
      if (!fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
        comprasEnRango.add(registro);
      }
    }
    if (comprasEnRango.isEmpty()) {
      return "No tiene compras registradas en ese rango de fechas.";
    }
    StringBuilder resultado = new StringBuilder("=== Historial de compras (filtrado) ===\n");
    for (RegistroCompra registro : comprasEnRango) {
      resultado.append(registro.getFecha()).append(" | ")
          .append(registro.getNombreEvento()).append(" | ")
          .append(registro.getCantidadBoletos()).append(" boletos | $")
          .append(registro.getTotal()).append("\n");
    }
    return resultado.toString();
  }

  /**
   * Devuelve las compras de un cliente como lista de registros.
   *
   * @param correoCliente correo del cliente
   * @return lista con las compras del cliente, posiblemente vacía
   */
  public List<RegistroCompra> buscarPorCliente(String correoCliente) {
    List<RegistroCompra> resultado = new ArrayList<>();
    for (RegistroCompra registro : compraPersistencia.listarTodos()) {
      if (registro.getCorreoComprador().equalsIgnoreCase(correoCliente)) {
        resultado.add(registro);
      }
    }
    return resultado;
  }

  /**
   * Devuelve las compras de un cliente dentro de un rango de fechas como lista de
   * registros.
   *
   * @param correoCliente correo del cliente
   * @param desde fecha inicial del rango, incluida
   * @param hasta fecha final del rango, incluida
   * @return lista filtrada por rango, posiblemente vacía
   */
  public List<RegistroCompra> buscarPorClienteEnRango(
      String correoCliente, LocalDate desde, LocalDate hasta) {
    List<RegistroCompra> enRango = new ArrayList<>();
    for (RegistroCompra registro : buscarPorCliente(correoCliente)) {
      LocalDate fecha = registro.getFecha();
      if (!fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
        enRango.add(registro);
      }
    }
    return enRango;
  }
}
