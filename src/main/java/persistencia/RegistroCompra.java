package persistencia;

import java.time.LocalDate;

public class RegistroCompra {

    private String correoComprador;
    private String nombreEvento;
    private String categoriaEvento;
    private int cantidadBoletos;
    private double total;
    private LocalDate fecha;

    public RegistroCompra(String correoComprador, String nombreEvento, String categoriaEvento,
                           int cantidadBoletos, double total, LocalDate fecha) {
        this.correoComprador = correoComprador;
        this.nombreEvento = nombreEvento;
        this.categoriaEvento = categoriaEvento;
        this.cantidadBoletos = cantidadBoletos;
        this.total = total;
        this.fecha = fecha;
    }

    public String getCorreoComprador() { return correoComprador; }
    public String getNombreEvento() { return nombreEvento; }
    public String getCategoriaEvento() { return categoriaEvento; }
    public int getCantidadBoletos() { return cantidadBoletos; }
    public double getTotal() { return total; }
    public LocalDate getFecha() { return fecha; }
}
