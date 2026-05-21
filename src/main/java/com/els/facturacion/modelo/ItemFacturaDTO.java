package com.els.facturacion.modelo;

import java.math.BigDecimal;

public class ItemFacturaDTO {
    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private String unidadMedida;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public ItemFacturaDTO() {
    }

    public ItemFacturaDTO(String codigo, String descripcion, BigDecimal cantidad,
                          String unidadMedida, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

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
}
