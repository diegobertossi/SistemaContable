package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoDTO {

    private Integer id;
    private LocalDate fecha;
    private Integer categoriaId;
    private String categoriaNombre;
    private String descripcion;
    private BigDecimal monto;
    private Integer mes;
    private Integer anio;

    public GastoDTO() {
    }

    public GastoDTO(LocalDate fecha, Integer categoriaId, String descripcion, BigDecimal monto) {
        this.fecha = fecha;
        this.categoriaId = categoriaId;
        this.descripcion = descripcion;
        this.monto = monto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }
}