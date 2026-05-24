package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.util.List;

public class RemitoReparsoftDTO {
    private int idRemito;
    private Integer numeroRemitoSalida;
    private String razonSocialCliente;
    private String cuitCliente;
    private List<RemitoReparsoftItem> items;

    public static class RemitoReparsoftItem {
        private int els;
        private String equipoNombre;
        private String numeroSerie;
        private String falla;
        private BigDecimal precioPeso;

        public RemitoReparsoftItem(int els, String equipoNombre, String numeroSerie, String falla, BigDecimal precioPeso) {
            this.els = els;
            this.equipoNombre = equipoNombre;
            this.numeroSerie = numeroSerie;
            this.falla = falla;
            this.precioPeso = precioPeso;
        }

        public int getEls() { return els; }
        public String getEquipoNombre() { return equipoNombre; }
        public String getNumeroSerie() { return numeroSerie; }
        public String getFalla() { return falla; }
        public BigDecimal getPrecioPeso() { return precioPeso; }

        public String getDescripcion() {
            String desc = equipoNombre != null ? equipoNombre : "Sin equipo";
            if (numeroSerie != null && !numeroSerie.trim().isEmpty()) {
                desc += " (S/N: " + numeroSerie + ")";
            }
            if (falla != null && !falla.trim().isEmpty()) {
                desc += " - " + falla;
            }
            return desc;
        }
    }

    public RemitoReparsoftDTO(int idRemito, Integer numeroRemitoSalida, String razonSocialCliente, String cuitCliente) {
        this.idRemito = idRemito;
        this.numeroRemitoSalida = numeroRemitoSalida;
        this.razonSocialCliente = razonSocialCliente;
        this.cuitCliente = cuitCliente;
    }

    public int getIdRemito() { return idRemito; }
    public Integer getNumeroRemitoSalida() { return numeroRemitoSalida; }
    public String getRazonSocialCliente() { return razonSocialCliente; }
    public String getCuitCliente() { return cuitCliente; }
    public List<RemitoReparsoftItem> getItems() { return items; }
    public void setItems(List<RemitoReparsoftItem> items) { this.items = items; }

    public String getNumeroRemitoDisplay() {
        return numeroRemitoSalida != null ? "R-" + numeroRemitoSalida : "R-" + idRemito;
    }

    public String getClienteDisplay() {
        return (razonSocialCliente != null ? razonSocialCliente : "Sin cliente")
            + (cuitCliente != null ? " (" + cuitCliente + ")" : "");
    }
}
