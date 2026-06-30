package com.els.facturacion.modelo;

public class RemitoPreimpresoConfigDTO {

    private Integer id;
    private Integer puntoVenta;
    private Long cai;
    private String fechaVencimiento;
    private Integer desde;
    private Integer hasta;

    public RemitoPreimpresoConfigDTO() {
    }

    public RemitoPreimpresoConfigDTO(Integer puntoVenta, Long cai, String fechaVencimiento,
                                     Integer desde, Integer hasta) {
        this.puntoVenta = puntoVenta;
        this.cai = cai;
        this.fechaVencimiento = fechaVencimiento;
        this.desde = desde;
        this.hasta = hasta;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getPuntoVenta() { return puntoVenta; }
    public void setPuntoVenta(Integer puntoVenta) { this.puntoVenta = puntoVenta; }

    public Long getCai() { return cai; }
    public void setCai(Long cai) { this.cai = cai; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Integer getDesde() { return desde; }
    public void setDesde(Integer desde) { this.desde = desde; }

    public Integer getHasta() { return hasta; }
    public void setHasta(Integer hasta) { this.hasta = hasta; }
}
