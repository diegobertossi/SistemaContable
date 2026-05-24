package com.els.facturacion.modelo;

import java.math.BigDecimal;

public class ReciboFacturaDTO {
    private Integer id;
    private Integer reciboId;
    private Integer comprobanteId;
    private BigDecimal montoAplicado;
    private String numeroFactura;
    private String tipoComprobanteStr;

    public ReciboFacturaDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getReciboId() { return reciboId; }
    public void setReciboId(Integer reciboId) { this.reciboId = reciboId; }
    public Integer getComprobanteId() { return comprobanteId; }
    public void setComprobanteId(Integer comprobanteId) { this.comprobanteId = comprobanteId; }
    public BigDecimal getMontoAplicado() { return montoAplicado; }
    public void setMontoAplicado(BigDecimal montoAplicado) { this.montoAplicado = montoAplicado; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getTipoComprobanteStr() { return tipoComprobanteStr; }
    public void setTipoComprobanteStr(String tipoComprobanteStr) { this.tipoComprobanteStr = tipoComprobanteStr; }
}
