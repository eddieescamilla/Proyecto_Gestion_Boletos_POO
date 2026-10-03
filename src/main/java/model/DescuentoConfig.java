package model;

public class DescuentoConfig {

    private String codigo;
    private String tipo;
    private double valor;
    private boolean activo;

    public DescuentoConfig(String codigo, String tipo, double valor, boolean activo) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.valor = valor;
        this.activo = activo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public boolean isActivo() {
        return activo;
    }
}
