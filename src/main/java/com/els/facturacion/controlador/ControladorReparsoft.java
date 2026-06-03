package com.els.facturacion.controlador;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.conexion.ConexionReparsoft;
import com.els.facturacion.dao.ReparacionLecturaDAO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public boolean escribirNumeroFactura(int els, String numeroFactura, String baseDatos) {
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) {
            System.err.println("No hay conexi\u00f3n a base de datos: " + baseDatos);
            return false;
        }

        String sql = "UPDATE reparaciones SET NroFactura = ? WHERE els = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numeroFactura);
            ps.setInt(2, els);
            int updated = ps.executeUpdate();

            if (updated > 0) {
                System.out.println("\u2713 N\u00famero de factura " + numeroFactura + " escrito en ELS " + els + " (" + baseDatos + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error escribiendo n\u00famero de factura en ReparSoft: " + e.getMessage());
        }
        return false;
    }

    public boolean escribirNumeroFactura(int els, String numeroFactura) {
        String base = reparacionDAO.getBaseDatosELS(els);
        if (base == null) {
            System.err.println("ELS no encontrada en ninguna base de datos");
            return false;
        }
        return escribirNumeroFactura(els, numeroFactura, base);
    }

    public String getBaseDatosELS(int els) {
        return reparacionDAO.getBaseDatosELS(els);
    }

    public boolean actualizarPagoReparsoft(int els, BigDecimal monto, String baseDatos) {
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) {
            System.err.println("No hay conexi\u00f3n a base de datos: " + baseDatos);
            return false;
        }
        String sql = "UPDATE reparaciones SET Pago = ? WHERE els = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, monto);
            ps.setInt(2, els);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                System.out.println("\u2713 Pago $" + monto + " registrado en ELS " + els + " (" + baseDatos + ")");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error actualizando pago en ReparSoft: " + e.getMessage());
        }
        return false;
    }

    public List<RemitoReparsoftItem> listarEquiposPorCliente(String baseDatos, String nombreCliente) {
        List<RemitoReparsoftItem> items = new ArrayList<>();
        Set<Integer> visto = new HashSet<>();

        String[] bases = {"ordenesbrc", "ordenesbsas"};
        for (String base : bases) {
            List<RemitoReparsoftItem> resultado = reparacionDAO.listarEquiposPorCliente(base, nombreCliente);
            for (RemitoReparsoftItem item : resultado) {
                if (visto.add(item.getEls())) {
                    items.add(item);
                }
            }
        }

        Set<Integer> elsFacturados = obtenerELSFacturados();
        if (!elsFacturados.isEmpty()) {
            for (RemitoReparsoftItem item : items) {
                if (elsFacturados.contains(item.getEls())) {
                    item.setFacturado(true);
                }
            }
        }
        return items;
    }

    public List<RemitoReparsoftDTO> listarRemitos(String baseDatos) {
        List<RemitoReparsoftDTO> remitos = reparacionDAO.listarRemitos(baseDatos);
        Set<Integer> elsFacturados = obtenerELSFacturados();
        if (!elsFacturados.isEmpty()) {
            for (RemitoReparsoftDTO r : remitos) {
                if (r.getItems() != null) {
                    for (RemitoReparsoftItem item : r.getItems()) {
                        if (elsFacturados.contains(item.getEls())) {
                            item.setFacturado(true);
                        }
                    }
                }
            }
        }
        return remitos;
    }

    private Set<Integer> obtenerELSFacturados() {
        Set<Integer> set = new HashSet<>();
        String sql = "SELECT DISTINCT els_referencia FROM factura_items WHERE els_referencia IS NOT NULL";
        try {
            Connection conn = ConexionFacturacion.getInstancia().getConexion();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                set.add(rs.getInt("els_referencia"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error consultando ELS facturados: " + e.getMessage());
        }
        return set;
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