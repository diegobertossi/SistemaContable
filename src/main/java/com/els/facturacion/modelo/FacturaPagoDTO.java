package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaPagoDTO {
    private Integer id;
    private Integer comprobanteId;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private String formaPago;
    private Integer reciboId;
    private String reciboNumero;
    private String comprobanteStr;
    private String clienteRazonSocial;
    private String observaciones;

    public FacturaPagoDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getComprobanteId() { return comprobanteId; }
    public void setComprobanteId(Integer comprobanteId) { this.comprobanteId = comprobanteId; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public Integer getReciboId() { return reciboId; }
    public void setReciboId(Integer reciboId) { this.reciboId = reciboId; }
    public String getReciboNumero() { return reciboNumero; }
    public void setReciboNumero(String reciboNumero) { this.reciboNumero = reciboNumero; }
    public String getComprobanteStr() { return comprobanteStr; }
    public void setComprobanteStr(String comprobanteStr) { this.comprobanteStr = comprobanteStr; }
    public String getClienteRazonSocial() { return clienteRazonSocial; }
    public void setClienteRazonSocial(String clienteRazonSocial) { this.clienteRazonSocial = clienteRazonSocial; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
