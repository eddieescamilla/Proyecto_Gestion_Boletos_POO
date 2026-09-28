package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/** Guarda el registro de las compras en un archivo de texto. */
public class RepositorioCompras {

  private String archivoCompras;

  /**
   * Crea el repositorio para un archivo de compras.
   *
   * @param archivoCompras ruta del archivo donde se guardan las compras
   */
  public RepositorioCompras(String archivoCompras) {
    this.archivoCompras = archivoCompras;
  }

  /**
   * Agrega una compra al final del archivo.
   *
   * @param compra compra a registrar
   * @return {@code true} si se guardó correctamente
   * @throws RuntimeException si no se puede escribir en el archivo
   */
  public boolean guardarCompra(Compra compra) {
    try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivoCompras, true))) {
      escritor.write(compra.getComprador().getCorreo() + "|"
          + compra.getEvento().getNombreEvento() + "|"
          + compra.getCantidadBoletos() + "|"
          + compra.getTotal() + "|"
          + LocalDate.now());
      escritor.newLine();
      return true;
    } catch (IOException e) {
      throw new RuntimeException("No se pudo guardar el registro de la compra.");
    }
  }
}
