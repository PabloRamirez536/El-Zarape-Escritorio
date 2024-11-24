package org.utleon.elzarape.model;

public class Bebida {
    private int idBebida;
    private int idProducto;
    private Producto producto;
    private Categoria categoria;

    public Bebida() {
    }

    public Bebida(int idBebida, int idProducto, Producto producto, Categoria categoria) {
        this.idBebida = idBebida;
        this.idProducto = idProducto;
        this.producto = producto;
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }


    public int getIdBebida() {
        return idBebida;
    }

    public void setIdBebida(int idBebida) {
        this.idBebida = idBebida;
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
}
