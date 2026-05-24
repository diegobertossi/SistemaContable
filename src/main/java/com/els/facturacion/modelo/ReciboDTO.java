package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReciboDTO {
    private Integer id;
    private String numeroRecibo;
    private LocalDate fechaCobro;
    private Integer clienteId;
    private String cuitCliente;
    private String razonSocialCliente;
    private BigDecimal montoTotal;
    private String observaciones;
    private List<ReciboPagoDTO> formasPago;
    private List<ReciboFacturaDTO> facturas;

    public ReciboDTO() {
        this.fechaCobro = LocalDate.now();
        this.montoTotal = BigDecimal.ZERO;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroRecibo() { return numeroRecibo; }
    public void setNumeroRecibo(String numeroRecibo) { this.numeroRecibo = numeroRecibo; }
    public LocalDate getFechaCobro() { return fechaCobro; }
    public void setFechaCobro(LocalDate fechaCobro) { this.fechaCobro = fechaCobro; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getCuitCliente() { return cuitCliente; }
    public void setCuitCliente(String cuitCliente) { this.cuitCliente = cuitCliente; }
    public String getRazonSocialCliente() { return razonSocialCliente; }
    public void setRazonSocialCliente(String razonSocialCliente) { this.razonSocialCliente = razonSocialCliente; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<ReciboPagoDTO> getFormasPago() { return formasPago; }
    public void setFormasPago(List<ReciboPagoDTO> formasPago) { this.formasPago = formasPago; }
    public List<ReciboFacturaDTO> getFacturas() { return facturas; }
    public void setFacturas(List<ReciboFacturaDTO> facturas) { this.facturas = facturas; }
}
