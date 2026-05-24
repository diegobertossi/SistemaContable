package com.els.facturacion.controlador;

import com.els.facturacion.conexion.ConexionReparsoft;
import com.els.facturacion.dao.ReparacionLecturaDAO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ControladorReparsoft {

    private ReparacionLecturaDAO reparacionDAO;

    public ControladorReparsoft() {
        this.reparacionDAO = new ReparacionLecturaDAO();
    }

    public boolean verificarELS(int els) {
        return reparacionDAO.verificarELS(els);
    }

    public Map<String, Object> obtenerDatosOrden(int els) {
        return reparacionDAO.getDatosParaFacturacion(els);
    }

    public ComprobanteDTO crearComprobanteDesdeELS(int els) {
        Map<String, Object> datos = reparacionDAO.getDatosParaFacturacion(els);
        if (datos == null) {
            return null;
        }

        ComprobanteDTO comprobante = new ComprobanteDTO();
        comprobante.setElsAsociado(els);

        String razonSocial = (String) datos.get("razonSocial");
        String descripcion = (String) datos.get("descripcion");
        String cuit = (String) datos.get("cuit");

        String cuitLimpio = limpiarCuit(cuit);
        if (cuitLimpio != null && esCuitValido(cuitLimpio)) {
            comprobante.setCuitReceptor(cuitLimpio);
        } else {
            String mensaje = "ELS " + els + " no tiene CUIT válido - usando consumidor final";
            System.out.println(mensaje);
        }

        if (razonSocial != null && !razonSocial.isEmpty()) {
            comprobante.setRazonSocialRec(razonSocial);
        } else {
            comprobante.setRazonSocialRec("Consumidor Final");
        }

        Object importeTotalObj = datos.get("importeTotal");
        if (importeTotalObj != null) {
            double importeTotal = ((Number) importeTotalObj).doubleValue();
            if (importeTotal > 0) {
                double iva = importeTotal / 1.21;
                double montoIva = importeTotal - iva;
                comprobante.setImporteNeto(BigDecimal.valueOf(iva).setScale(2));
                comprobante.setImporteIva(BigDecimal.valueOf(montoIva).setScale(2));
                comprobante.setImporteTotal(BigDecimal.valueOf(importeTotal).setScale(2));
            } else {
                comprobante.setImporteNeto(BigDecimal.ZERO);
                comprobante.setImporteTotal(BigDecimal.ZERO);
            }
        } else {
            comprobante.setImporteNeto(BigDecimal.ZERO);
            comprobante.setImporteTotal(BigDecimal.ZERO);
        }

        comprobante.setFechaEmision(LocalDate.now());
        comprobante.setDescripcion(descripcion);

        return comprobante;
    }

    public boolean escribirNumeroFactura(int els, String numeroFactura) {
        String base = reparacionDAO.getBaseDatosELS(els);
        if (base == null) {
            System.err.println("ELS no encontrada en ninguna base de datos");
            return false;
        }

        Connection conn = ConexionReparsoft.getInstancia().getConexion(base);
        if (conn == null) {
            System.err.println("No hay conexión a base de datos: " + base);
            return false;
        }

        String sql = "UPDATE reparaciones SET numero_factura = ? WHERE els = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroFactura);
            ps.setInt(2, els);
            int updated = ps.executeUpdate();

            if (updated > 0) {
                System.out.println("✓ Número de factura " + numeroFactura + " escrito en ELS " + els);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error escribiendo número de factura en ReparSoft: " + e.getMessage());
        }
        return false;
    }

    public String getBaseDatosELS(int els) {
        return reparacionDAO.getBaseDatosELS(els);
    }

    public List<RemitoReparsoftDTO> listarRemitos(String baseDatos) {
        return reparacionDAO.listarRemitos(baseDatos);
    }

    private String limpiarCuit(String cuit) {
        if (cuit == null || cuit.isEmpty()) {
            return null;
        }
        return cuit.replaceAll("[^0-9]", "");
    }

    private boolean esCuitValido(String cuit) {
        if (cuit == null || cuit.length() != 11) {
            return false;
        }
        try {
            Long.parseLong(cuit);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}