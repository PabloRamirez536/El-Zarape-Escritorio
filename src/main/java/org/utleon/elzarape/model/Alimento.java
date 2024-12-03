package org.utleon.elzarape.model;

public class Alimento {
    private int idAlimento;
    private int idProducto;
    private Producto producto;
    private Categoria categoria;

    public Alimento() {
    }

    public int getIdAlimento() {
        return idAlimento;
    }

    public void setIdAlimento(int idAlimento) {
        this.idAlimento = idAlimento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Alimento{" +
                "idAlimento=" + idAlimento +
                ", idProducto=" + idProducto +
                ", producto=" + producto +
                ", categoria=" + categoria +
                '}';
    }
}
