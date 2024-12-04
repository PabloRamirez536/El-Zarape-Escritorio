package org.utleon.elzarape.model;

public class Empleado {
    private int idEmpleado;
    private boolean activo;
    private Sucursal sucursal;
    private Persona persona;
    private Usuario usuario;
    private Estado estado;
    private Ciudad ciudad;

    public Empleado(int idEmpleado, boolean activo, Sucursal sucursal, Persona persona, Usuario usuario, Estado estado, Ciudad ciudad) {
        this.idEmpleado = idEmpleado;
        this.activo = activo;
        this.sucursal = sucursal;
        this.persona = persona;
        this.usuario = usuario;
        this.estado = estado;
        this.ciudad = ciudad;
    }

    public Empleado() {
    }



    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public boolean getActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {

        this.activo = activo;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }
}
