package com.els.facturacion.modelo;

public class CategoriaGastoDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Boolean activa;

    public CategoriaGastoDTO() {
    }

    public CategoriaGastoDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activa = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}