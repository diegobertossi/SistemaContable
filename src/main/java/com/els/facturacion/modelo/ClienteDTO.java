package com.els.facturacion.modelo;

public class ClienteDTO {
    private Integer id;
    private String tipoDocumento;
    private String nroDocumento;
    private String razonSocial;
    private String condicionIva;
    private String domicilio;
    private String telefono;
    private String email;
    private String origen;
    private Integer elsReferencia;
    private Boolean activo;
    private String tipoPersona;
    private String sucursal;

    public ClienteDTO() {
        this.tipoDocumento = "CUIT";
        this.activo = true;
        this.origen = "manual";
        this.tipoPersona = "empresa";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getCondicionIva() { return condicionIva; }
    public void setCondicionIva(String condicionIva) { this.condicionIva = condicionIva; }
    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public Integer getElsReferencia() { return elsReferencia; }
    public void setElsReferencia(Integer elsReferencia) { this.elsReferencia = elsReferencia; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public String getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(String tipoPersona) { this.tipoPersona = tipoPersona; }
    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
}
