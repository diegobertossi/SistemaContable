package com.els.facturacion.modelo;

import java.math.BigDecimal;
import java.util.List;

public class RemitoReparsoftDTO {
    private int idRemito;
    private Integer numeroRemitoSalida;
    private String razonSocialCliente;
    private String cuitCliente;
    private int idUbicacion;
    private String sucursalNombre;
    private java.sql.Date fechaEmision;
    private List<RemitoReparsoftItem> items;

    public static class RemitoReparsoftItem {
        private int els;
        private String equipoNombre;
        private String numeroSerie;
        private String falla;
        private String modelo;
        private String marca;
        private BigDecimal precioPeso;
        private boolean facturado;
        private boolean seleccionado;
        private String numeroRemito;
        private String nombreCliente;
        private String baseDatos;
        private int idUbicacion;
        private String sucursalNombre;

        public RemitoReparsoftItem(int els, String equipoNombre, String numeroSerie, String falla, BigDecimal precioPeso) {
            this(els, equipoNombre, numeroSerie, falla, null, null, precioPeso, false, "");
        }

        public RemitoReparsoftItem(int els, String equipoNombre, String numeroSerie, String falla,
                                    String modelo, String marca, BigDecimal precioPeso, boolean facturado) {
            this(els, equipoNombre, numeroSerie, falla, modelo, marca, precioPeso, facturado, "");
        }

        public RemitoReparsoftItem(int els, String equipoNombre, String numeroSerie, String falla,
                                    String modelo, String marca, BigDecimal precioPeso, boolean facturado, String numeroRemito) {
            this(els, equipoNombre, numeroSerie, falla, modelo, marca, precioPeso, facturado, numeroRemito, null, null);
        }

        public RemitoReparsoftItem(int els, String equipoNombre, String numeroSerie, String falla,
                                    String modelo, String marca, BigDecimal precioPeso, boolean facturado,
                                    String numeroRemito, String nombreCliente, String baseDatos) {
            this(els, equipoNombre, numeroSerie, falla, modelo, marca, precioPeso, facturado,
                 numeroRemito, nombreCliente, baseDatos, 0, "");
        }

        public RemitoReparsoftItem(int els, String equipoNombre, String numeroSerie, String falla,
                                    String modelo, String marca, BigDecimal precioPeso, boolean facturado,
                                    String numeroRemito, String nombreCliente, String baseDatos, int idUbicacion) {
            this(els, equipoNombre, numeroSerie, falla, modelo, marca, precioPeso, facturado,
                 numeroRemito, nombreCliente, baseDatos, idUbicacion, "");
        }

        public RemitoReparsoftItem(int els, String equipoNombre, String numeroSerie, String falla,
                                    String modelo, String marca, BigDecimal precioPeso, boolean facturado,
                                    String numeroRemito, String nombreCliente, String baseDatos, int idUbicacion, String sucursalNombre) {
            this.els = els;
            this.equipoNombre = equipoNombre;
            this.numeroSerie = numeroSerie;
            this.falla = falla;
            this.modelo = modelo;
            this.marca = marca;
            this.precioPeso = precioPeso;
            this.facturado = facturado;
            this.seleccionado = false;
            this.numeroRemito = numeroRemito;
            this.nombreCliente = nombreCliente;
            this.baseDatos = baseDatos;
            this.idUbicacion = idUbicacion;
            this.sucursalNombre = sucursalNombre;
        }

        public int getEls() { return els; }
        public String getEquipoNombre() { return equipoNombre; }
        public String getNumeroSerie() { return numeroSerie; }
        public String getFalla() { return falla; }
        public String getModelo() { return modelo; }
        public String getMarca() { return marca; }
        public BigDecimal getPrecioPeso() { return precioPeso; }
        public boolean isFacturado() { return facturado; }
        public void setFacturado(boolean facturado) { this.facturado = facturado; }
        public boolean isSeleccionado() { return seleccionado; }
        public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
        public String getNumeroRemito() { return numeroRemito; }
        public void setNumeroRemito(String numeroRemito) { this.numeroRemito = numeroRemito; }
        public String getNombreCliente() { return nombreCliente; }
        public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
        public String getBaseDatos() { return baseDatos; }
        public void setBaseDatos(String baseDatos) { this.baseDatos = baseDatos; }
        public int getIdUbicacion() { return idUbicacion; }
        public void setIdUbicacion(int idUbicacion) { this.idUbicacion = idUbicacion; }
        public String getSucursalNombre() { return sucursalNombre; }
        public void setSucursalNombre(String sucursalNombre) { this.sucursalNombre = sucursalNombre; }
        public String getSucursalDisplay() {
            if (sucursalNombre != null) return sucursalNombre;
            if (idUbicacion == 0) return baseDatos != null ? baseDatos : "";
            switch (idUbicacion) {
                case 1: return "BRC - Principal";
                case 2: return "BRC - Suc 2";
                case 3: return "BRC - Suc 3";
                case 4: return "BSAS - Suc 4";
                case 5: return "BSAS - Suc 5";
                case 7: return "BRC - Suc 6";
                case 8: return "BRC - Suc 7";
                default: return baseDatos != null ? baseDatos + " - Ubic " + idUbicacion : "";
            }
        }

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
        this(idRemito, numeroRemitoSalida, razonSocialCliente, cuitCliente, 0);
    }

    public RemitoReparsoftDTO(int idRemito, Integer numeroRemitoSalida, String razonSocialCliente, String cuitCliente, int idUbicacion) {
        this.idRemito = idRemito;
        this.numeroRemitoSalida = numeroRemitoSalida;
        this.razonSocialCliente = razonSocialCliente;
        this.cuitCliente = cuitCliente;
        this.idUbicacion = idUbicacion;
    }

    public int getIdRemito() { return idRemito; }
    public Integer getNumeroRemitoSalida() { return numeroRemitoSalida; }
    public String getRazonSocialCliente() { return razonSocialCliente; }
    public String getCuitCliente() { return cuitCliente; }
    public int getIdUbicacion() { return idUbicacion; }
    public void setIdUbicacion(int idUbicacion) { this.idUbicacion = idUbicacion; }
    public String getSucursalNombre() { return sucursalNombre; }
    public void setSucursalNombre(String sucursalNombre) { this.sucursalNombre = sucursalNombre; }
    public java.sql.Date getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(java.sql.Date fechaEmision) { this.fechaEmision = fechaEmision; }
    public List<RemitoReparsoftItem> getItems() { return items; }
    public void setItems(List<RemitoReparsoftItem> items) { this.items = items; }

    public String getNumeroRemitoDisplay() {
        if (numeroRemitoSalida != null && idUbicacion > 0) {
            int display = idUbicacionADisplay(idUbicacion);
            return String.format("%04d-%08d", display, numeroRemitoSalida);
        }
        return numeroRemitoSalida != null ? "R-" + numeroRemitoSalida : "R-" + idRemito;
    }

    private static int idUbicacionADisplay(int idUbicacion) {
        switch (idUbicacion) {
            case 1: return 5;
            case 2: return 2;
            case 3: return 1000;
            case 4: return 2000;
            case 5: return 3000;
            case 7: return 6;
            case 8: return 7;
            default: return idUbicacion;
        }
    }

    public String getClienteDisplay() {
        return (razonSocialCliente != null ? razonSocialCliente : "Sin cliente")
            + (cuitCliente != null ? " (" + cuitCliente + ")" : "");
    }
}
