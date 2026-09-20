package model;

import persistencia.CompraPersistencia;
import persistencia.RegistroCompra;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistorialCompras {

    private CompraPersistencia compraPersistencia;

    public HistorialCompras(CompraPersistencia compraPersistencia) {
        this.compraPersistencia = compraPersistencia;
    }

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

    private List<RegistroCompra> buscarPorCliente(String correoCliente) {
        List<RegistroCompra> resultado = new ArrayList<>();
        for (RegistroCompra registro : compraPersistencia.listarTodos()) {
            if (registro.getCorreoComprador().equalsIgnoreCase(correoCliente)) {
                resultado.add(registro);
            }
        }
        return resultado;
    }
}
