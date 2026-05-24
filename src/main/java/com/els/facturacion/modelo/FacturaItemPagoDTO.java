package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaItemPagoDTO {
    private Integer id;
    private Integer facturaItemId;
    private Integer comprobanteId;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private Integer reciboId;
    private String estado;

    public FacturaItemPagoDTO() {
        this.estado = "pendiente";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getFacturaItemId() { return facturaItemId; }
    public void setFacturaItemId(Integer facturaItemId) { this.facturaItemId = facturaItemId; }
    public Integer getComprobanteId() { return comprobanteId; }
    public void setComprobanteId(Integer comprobanteId) { this.comprobanteId = comprobanteId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public Integer getReciboId() { return reciboId; }
    public void setReciboId(Integer reciboId) { this.reciboId = reciboId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
