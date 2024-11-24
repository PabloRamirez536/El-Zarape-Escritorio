package org.utleon.elzarape.model;

public class Cliente {
    private int idCliente;
    private boolean activo;
    private Persona persona;
    private Usuario usuario;
    private Ciudad ciudad;
    private Estado estado;

    public Cliente() {
    }

    public Cliente(int idCliente, boolean activo, Persona persona, Usuario usuario, Ciudad ciudad, Estado estado) {
        this.idCliente = idCliente;
        this.activo = activo;
        this.persona = persona;
        this.usuario = usuario;
        this.ciudad = ciudad;
        this.estado = estado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
