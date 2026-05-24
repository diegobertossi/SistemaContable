package com.els.facturacion.modelo;

import java.math.BigDecimal;

public class ReciboPagoDTO {
    private Integer id;
    private Integer reciboId;
    private String formaPago;
    private BigDecimal monto;
    private String referencia;
    private String datosAdicionales;

    public ReciboPagoDTO() {}

    public ReciboPagoDTO(String formaPago, BigDecimal monto, String referencia) {
        this.formaPago = formaPago;
        this.monto = monto;
        this.referencia = referencia;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getReciboId() { return reciboId; }
    public void setReciboId(Integer reciboId) { this.reciboId = reciboId; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    public String getDatosAdicionales() { return datosAdicionales; }
    public void setDatosAdicionales(String datosAdicionales) { this.datosAdicionales = datosAdicionales; }
}
