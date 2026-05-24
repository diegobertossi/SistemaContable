package com.els.facturacion.modelo;

import java.time.LocalDate;
import java.util.List;

public class RemitoDTO {
    private Integer id;
    private String numeroRemito;
    private LocalDate fechaEmision;
    private LocalDate fechaEntrega;
    private String cuitEmisor;
    private String razonSocialEmisor;
    private String domicilioEmisor;
    private String cuitReceptor;
    private String razonSocialReceptor;
    private String domicilioReceptor;
    private Integer comprobanteId;
    private String estado;
    private String observaciones;
    private List<RemitoItemDTO> items;

    public RemitoDTO() {
        this.fechaEmision = LocalDate.now();
        this.estado = "pendiente";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroRemito() { return numeroRemito; }
    public void setNumeroRemito(String numeroRemito) { this.numeroRemito = numeroRemito; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getCuitEmisor() { return cuitEmisor; }
    public void setCuitEmisor(String cuitEmisor) { this.cuitEmisor = cuitEmisor; }
    public String getRazonSocialEmisor() { return razonSocialEmisor; }
    public void setRazonSocialEmisor(String razonSocialEmisor) { this.razonSocialEmisor = razonSocialEmisor; }
    public String getDomicilioEmisor() { return domicilioEmisor; }
    public void setDomicilioEmisor(String domicilioEmisor) { this.domicilioEmisor = domicilioEmisor; }
    public String getCuitReceptor() { return cuitReceptor; }
    public void setCuitReceptor(String cuitReceptor) { this.cuitReceptor = cuitReceptor; }
    public String getRazonSocialReceptor() { return razonSocialReceptor; }
    public void setRazonSocialReceptor(String razonSocialReceptor) { this.razonSocialReceptor = razonSocialReceptor; }
    public String getDomicilioReceptor() { return domicilioReceptor; }
    public void setDomicilioReceptor(String domicilioReceptor) { this.domicilioReceptor = domicilioReceptor; }
    public Integer getComprobanteId() { return comprobanteId; }
    public void setComprobanteId(Integer comprobanteId) { this.comprobanteId = comprobanteId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<RemitoItemDTO> getItems() { return items; }
    public void setItems(List<RemitoItemDTO> items) { this.items = items; }
}
