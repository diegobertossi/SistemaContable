package com.els.facturacion.modelo;

import java.time.LocalDate;

public class RespuestaCAE {

    private String cae;
    private LocalDate vencimiento;
    private Long numeroComprobante;
    private String mensaje;
    private boolean exitosa;
    private String codigoError;

    public RespuestaCAE() {
        this.exitosa = false;
    }

    public RespuestaCAE(String cae, LocalDate vencimiento, Long numeroComprobante) {
        this.cae = cae;
        this.vencimiento = vencimiento;
        this.numeroComprobante = numeroComprobante;
        this.exitosa = true;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public LocalDate getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(LocalDate vencimiento) {
        this.vencimiento = vencimiento;
    }

    public Long getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(Long numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isExitosa() {
        return exitosa;
    }

    public void setExitosa(boolean exitosa) {
        this.exitosa = exitosa;
    }

    public void setError(String mensaje) {
        this.mensaje = mensaje;
        this.exitosa = false;
    }

    public String getCodigoError() {
        return codigoError;
    }

    public void setCodigoError(String codigoError) {
        this.codigoError = codigoError;
    }
}