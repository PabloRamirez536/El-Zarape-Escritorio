package org.utleon.elzarape.model;

public class Categoria {
    private int idCategoria;
    private String descripcion;
    private String tipo;
    private boolean activo;

    public Categoria() {
    }

    public Categoria(int idCategoria, String descripcion, String tipo, boolean activo) {
        this.idCategoria = idCategoria;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.activo = activo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
