package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ComprobanteDTO {

    private Integer id;
    private String cuitEmisor;
    private Integer tipoComprobante;
    private Integer puntoVenta;
    private Long numero;
    private String cuitReceptor;
    private String razonSocialRec;
    private LocalDate fechaEmision;
    private BigDecimal importeNeto;
    private BigDecimal importeIva;
    private BigDecimal importeTotal;
    private String cae;
    private LocalDate vencimientoCae;
    private Integer elsAsociado;
    private String rutaPdf;
    private Boolean emailEnviado;
    private String descripcion;

    private String concepto;
    private LocalDate periodoDesde;
    private LocalDate periodoHasta;
    private LocalDate periodoVto;
    private String condicionIvaReceptor;
    private String tipoDocumento;
    private String nroDocumento;
    private String domicilioReceptor;
    private String emailReceptor;
    private String condicionesVenta;
    private String comprobanteAsociado;
    private String estadoPago;
    private BigDecimal otrosImpuestos;
    private List<ItemFacturaDTO> itemsFactura;

    public ComprobanteDTO() {
        this.estadoPago = "pendiente";
        this.otrosImpuestos = BigDecimal.ZERO;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuitEmisor() {
        return cuitEmisor;
    }

    public void setCuitEmisor(String cuitEmisor) {
        this.cuitEmisor = cuitEmisor;
    }

    public Integer getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(Integer tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Integer getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(Integer puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getCuitReceptor() {
        return cuitReceptor;
    }

    public void setCuitReceptor(String cuitReceptor) {
        this.cuitReceptor = cuitReceptor;
    }

    public String getRazonSocialRec() {
        return razonSocialRec;
    }

    public void setRazonSocialRec(String razonSocialRec) {
        this.razonSocialRec = razonSocialRec;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(BigDecimal importeNeto) {
        this.importeNeto = importeNeto;
    }

    public BigDecimal getImporteIva() {
        return importeIva;
    }

    public void setImporteIva(BigDecimal importeIva) {
        this.importeIva = importeIva;
    }

    public BigDecimal getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(BigDecimal importeTotal) {
        this.importeTotal = importeTotal;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public LocalDate getVencimientoCae() {
        return vencimientoCae;
    }

    public void setVencimientoCae(LocalDate vencimientoCae) {
        this.vencimientoCae = vencimientoCae;
    }

    public Integer getElsAsociado() {
        return elsAsociado;
    }

    public void setElsAsociado(Integer elsAsociado) {
        this.elsAsociado = elsAsociado;
    }

    public String getRutaPdf() {
        return rutaPdf;
    }

    public void setRutaPdf(String rutaPdf) {
        this.rutaPdf = rutaPdf;
    }

    public Boolean getEmailEnviado() {
        return emailEnviado;
    }

    public void setEmailEnviado(Boolean emailEnviado) {
        this.emailEnviado = emailEnviado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public LocalDate getPeriodoDesde() { return periodoDesde; }
    public void setPeriodoDesde(LocalDate periodoDesde) { this.periodoDesde = periodoDesde; }
    public LocalDate getPeriodoHasta() { return periodoHasta; }
    public void setPeriodoHasta(LocalDate periodoHasta) { this.periodoHasta = periodoHasta; }
    public LocalDate getPeriodoVto() { return periodoVto; }
    public void setPeriodoVto(LocalDate periodoVto) { this.periodoVto = periodoVto; }
    public String getCondicionIvaReceptor() { return condicionIvaReceptor; }
    public void setCondicionIvaReceptor(String condicionIvaReceptor) { this.condicionIvaReceptor = condicionIvaReceptor; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }
    public String getDomicilioReceptor() { return domicilioReceptor; }
    public void setDomicilioReceptor(String domicilioReceptor) { this.domicilioReceptor = domicilioReceptor; }
    public String getEmailReceptor() { return emailReceptor; }
    public void setEmailReceptor(String emailReceptor) { this.emailReceptor = emailReceptor; }
    public String getCondicionesVenta() { return condicionesVenta; }
    public void setCondicionesVenta(String condicionesVenta) { this.condicionesVenta = condicionesVenta; }
    public String getComprobanteAsociado() { return comprobanteAsociado; }
    public void setComprobanteAsociado(String comprobanteAsociado) { this.comprobanteAsociado = comprobanteAsociado; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
    public BigDecimal getOtrosImpuestos() { return otrosImpuestos; }
    public void setOtrosImpuestos(BigDecimal otrosImpuestos) { this.otrosImpuestos = otrosImpuestos; }
    public List<ItemFacturaDTO> getItemsFactura() { return itemsFactura; }
    public void setItemsFactura(List<ItemFacturaDTO> itemsFactura) { this.itemsFactura = itemsFactura; }

    public String getTipoComprobanteStr() {
        switch (tipoComprobante) {
            case 1: return "Factura A";
            case 6: return "Factura B";
            case 11: return "Factura C";
            case 3: return "Nota de Crédito A";
            case 8: return "Nota de Crédito B";
            case 13: return "Nota de Crédito C";
            case 2: return "Nota de Débito A";
            case 7: return "Nota de Débito B";
            case 12: return "Nota de Débito C";
            case 4: return "Recibo A";
            case 9: return "Recibo B";
            case 10: return "Recibo C";
            case 331: return "FCE MiPymes";
            default: return "Tipo " + tipoComprobante;
        }
    }
}