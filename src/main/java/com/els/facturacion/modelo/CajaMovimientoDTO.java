package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CajaMovimientoDTO {

    private Integer id;
    private LocalDate fecha;
    private String tipo;
    private String descripcion;
    private BigDecimal monto;
    private String cuitAsociado;
    private Integer comprobanteId;

    public CajaMovimientoDTO() {
    }

    public CajaMovimientoDTO(LocalDate fecha, String tipo, String descripcion, BigDecimal monto) {
        this.fecha = fecha;
        this.tipo = tipo;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getCuitAsociado() {
        return cuitAsociado;
    }

    public void setCuitAsociado(String cuitAsociado) {
        this.cuitAsociado = cuitAsociado;
    }

    public Integer getComprobanteId() {
        return comprobanteId;
    }

    public void setComprobanteId(Integer comprobanteId) {
        this.comprobanteId = comprobanteId;
    }

    public boolean isCobro() {
        return "cobro".equalsIgnoreCase(tipo);
    }

    public boolean isPago() {
        return "pago".equalsIgnoreCase(tipo);
    }
}