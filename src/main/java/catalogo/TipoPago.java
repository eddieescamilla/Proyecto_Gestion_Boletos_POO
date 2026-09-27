package catalogo;

public enum TipoPago {

    TARJETA_CREDITO("Tarjeta de crédito"),
    TARJETA_DEBITO("Tarjeta de débito"),
    EFECTIVO("Efectivo");

    private final String etiqueta;

    TipoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
