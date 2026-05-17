package com.els.facturacion.modelo;

public class CuitConfigDTO {

    private Integer id;
    private String cuit;
    private String razonSocial;
    private String condicionIva;
    private Integer puntoVenta;
    private String rutaCertificado;
    private String passwordCert;
    private Boolean activo;

    public CuitConfigDTO() {
    }

    public CuitConfigDTO(String cuit, String razonSocial, String condicionIva,
                         Integer puntoVenta, String rutaCertificado, String passwordCert) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.condicionIva = condicionIva;
        this.puntoVenta = puntoVenta;
        this.rutaCertificado = rutaCertificado;
        this.passwordCert = passwordCert;
        this.activo = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(String condicionIva) {
        this.condicionIva = condicionIva;
    }

    public Integer getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(Integer puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getRutaCertificado() {
        return rutaCertificado;
    }

    public void setRutaCertificado(String rutaCertificado) {
        this.rutaCertificado = rutaCertificado;
    }

    public String getPasswordCert() {
        return passwordCert;
    }

    public void setPasswordCert(String passwordCert) {
        this.passwordCert = passwordCert;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}