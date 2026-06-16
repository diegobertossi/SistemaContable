package com.els.facturacion.modelo;

import java.math.BigDecimal;

public class ItemFacturaDTO {
    private Integer id;
    private Integer comprobanteId;
    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private String unidadMedida;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private BigDecimal porcentajeBonif;
    private BigDecimal importeBonif;
    private BigDecimal alicuotaIva;
    private Integer elsReferencia;
    private Integer orden;
    private String estadoPago;

    public ItemFacturaDTO() {
        this.unidadMedida = "Unidad";
        this.cantidad = BigDecimal.ONE;
        this.alicuotaIva = new BigDecimal("21.00");
        this.estadoPago = "pendiente";
    }

    public ItemFacturaDTO(String codigo, String descripcion, BigDecimal cantidad,
                          String unidadMedida, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.alicuotaIva = new BigDecimal("21.00");
        this.estadoPago = "pendiente";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getComprobanteId() { return comprobanteId; }
    public void setComprobanteId(Integer comprobanteId) { this.comprobanteId = comprobanteId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getPorcentajeBonif() { return porcentajeBonif; }
    public void setPorcentajeBonif(BigDecimal porcentajeBonif) { this.porcentajeBonif = porcentajeBonif; }
    public BigDecimal getImporteBonif() { return importeBonif; }
    public void setImporteBonif(BigDecimal importeBonif) { this.importeBonif = importeBonif; }
    public BigDecimal getAlicuotaIva() { return alicuotaIva; }
    public void setAlicuotaIva(BigDecimal alicuotaIva) { this.alicuotaIva = alicuotaIva; }
    public Integer getElsReferencia() { return elsReferencia; }
    public void setElsReferencia(Integer elsReferencia) { this.elsReferencia = elsReferencia; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}
