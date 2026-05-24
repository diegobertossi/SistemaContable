package com.els.facturacion.modelo;

import java.math.BigDecimal;

public class RemitoItemDTO {
    private Integer id;
    private Integer remitoId;
    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private String unidadMedida;
    private Integer elsReferencia;
    private Integer orden;

    public RemitoItemDTO() {
        this.unidadMedida = "Unidad";
        this.cantidad = BigDecimal.ONE;
    }

    public RemitoItemDTO(String codigo, String descripcion, BigDecimal cantidad, String unidadMedida, Integer elsReferencia) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.elsReferencia = elsReferencia;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getRemitoId() { return remitoId; }
    public void setRemitoId(Integer remitoId) { this.remitoId = remitoId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public Integer getElsReferencia() { return elsReferencia; }
    public void setElsReferencia(Integer elsReferencia) { this.elsReferencia = elsReferencia; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
