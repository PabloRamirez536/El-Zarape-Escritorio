package org.utleon.elzarape.model;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private String contrasenia;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String contrasenia) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
