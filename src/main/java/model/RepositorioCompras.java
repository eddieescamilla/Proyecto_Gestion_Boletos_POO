package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

public class RepositorioCompras {

    private String archivoCompras;

    public RepositorioCompras(String archivoCompras) {
        this.archivoCompras = archivoCompras;
    }

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
