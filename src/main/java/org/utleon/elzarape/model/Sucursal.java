package org.utleon.elzarape.model;

public class Sucursal {
    private int idSucursal;
    private String SucursalNombre;
    private String latitud;
    private String longitud;
    private String foto;
    private String urlWeb;
    private String horarios;
    private String calle;
    private String numCalle;
    private String colonia;
    private boolean activo;
    private Ciudad ciudad;
    private Estado estado;

    public Sucursal() {
    }

    public Sucursal(int idSucursal, String sucursalNombre, String latitud, String longitud, String foto, String urlWeb, String horarios, String calle, String numCalle, String colonia, boolean activo, Ciudad ciudad, Estado estado) {
        this.idSucursal = idSucursal;
        SucursalNombre = sucursalNombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.foto = foto;
        this.urlWeb = urlWeb;
        this.horarios = horarios;
        this.calle = calle;
        this.numCalle = numCalle;
        this.colonia = colonia;
        this.activo = activo;
        this.ciudad = ciudad;
        this.estado = estado;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getSucursalNombre() {
        return SucursalNombre;
    }

    public void setSucursalNombre(String sucursalNombre) {
        SucursalNombre = sucursalNombre;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getUrlWeb() {
        return urlWeb;
    }

    public void setUrlWeb(String urlWeb) {
        this.urlWeb = urlWeb;
    }

    public String getHorarios() {
        return horarios;
    }

    public void setHorarios(String horarios) {
        this.horarios = horarios;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumCalle() {
        return numCalle;
    }

    public void setNumCalle(String numCalle) {
        this.numCalle = numCalle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
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
