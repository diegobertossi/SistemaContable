package com.els.facturacion.controlador;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.conexion.ConexionReparsoft;
import com.els.facturacion.dao.ReparacionLecturaDAO;
import com.els.facturacion.modelo.ClienteDTO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem;
import com.els.facturacion.util.UbicacionSistema;
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
import java.util.stream.Collectors;

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

    public List<RemitoReparsoftItem> listarTodosEquiposConPrecio() {
        return listarTodosEquiposConPrecio(null);
    }

    public List<RemitoReparsoftItem> listarTodosEquiposConPrecio(String baseDatos) {
        List<RemitoReparsoftItem> items = new ArrayList<>();
        Set<Integer> visto = new HashSet<>();

        String[] bases = baseDatos != null && !baseDatos.isEmpty()
            ? new String[]{baseDatos}
            : new String[]{"ordenesbrc", "ordenesbsas"};
        for (String base : bases) {
            List<RemitoReparsoftItem> resultado = reparacionDAO.listarTodosEquiposConPrecio(base);
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

    // ─── Sincronización bidireccional de clientes ────────────────────────

    public Integer insertarClienteEnReparsoft(ClienteDTO cliente) {
        String base = UbicacionSistema.getNombreDbReparsoft();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(base);
        if (conn == null) return null;

        int nextId;
        try (PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(MAX(idCliente), 0) + 1 FROM cliente");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            nextId = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo próximo idCliente: " + e.getMessage());
            return null;
        }

        String sql = "INSERT INTO cliente (idCliente, nombre, CUIT, Domicilio, TelefonoEmpresa, "
                + "CorreoElectronico, tipo_documento, condicion_iva, tipo_persona) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nextId);
            ps.setString(2, cliente.getRazonSocial());
            ps.setString(3, cliente.getNroDocumento());
            ps.setString(4, cliente.getDomicilio());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.setString(7, cliente.getTipoDocumento());
            ps.setString(8, cliente.getCondicionIva());
            ps.setString(9, cliente.getTipoPersona() != null ? cliente.getTipoPersona() : "empresa");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                System.out.println("\u2713 Cliente insertado en ReparSoft con idCliente=" + nextId);
                return nextId;
            }
        } catch (SQLException e) {
            System.err.println("Error insertando cliente en ReparSoft: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarClienteEnReparsoft(ClienteDTO cliente) {
        if (cliente.getElsReferencia() == null) return false;
        String base = UbicacionSistema.getNombreDbReparsoft();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(base);
        if (conn == null) return false;

        String sql = "UPDATE cliente SET nombre = ?, CUIT = ?, Domicilio = ?, TelefonoEmpresa = ?, "
                + "CorreoElectronico = ?, tipo_documento = ?, condicion_iva = ?, tipo_persona = ? "
                + "WHERE idCliente = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getRazonSocial());
            ps.setString(2, cliente.getNroDocumento());
            ps.setString(3, cliente.getDomicilio());
            ps.setString(4, cliente.getTelefono());
            ps.setString(5, cliente.getEmail());
            ps.setString(6, cliente.getTipoDocumento());
            ps.setString(7, cliente.getCondicionIva());
            ps.setString(8, cliente.getTipoPersona() != null ? cliente.getTipoPersona() : "empresa");
            ps.setInt(9, cliente.getElsReferencia());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                System.out.println("\u2713 Cliente actualizado en ReparSoft (idCliente=" + cliente.getElsReferencia() + ")");
            }
            return ok;
        } catch (SQLException e) {
            System.err.println("Error actualizando cliente en ReparSoft: " + e.getMessage());
        }
        return false;
    }

    // ─── Sincronización bidireccional de remitos ────────────────────────

    public Integer insertarRemitoEnReparsoft(String numeroRemito, List<Integer> elsList, int codigoUbicacion) {
        String base = UbicacionSistema.getNombreDbReparsoft();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(base);
        if (conn == null) return null;

        // 1. obtener IdUbicacion desde el Codigo
        int idUbicacion = -1;
        String sqlUbic = "SELECT IdUbicacion FROM " + base + ".UbicacionRemitos WHERE Codigo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlUbic)) {
            ps.setInt(1, codigoUbicacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) idUbicacion = rs.getInt("IdUbicacion");
        } catch (SQLException e) {
            System.err.println("Error obteniendo IdUbicacion: " + e.getMessage());
            return null;
        }
        if (idUbicacion < 0) {
            System.err.println("No se encontró IdUbicacion para Codigo=" + codigoUbicacion);
            return null;
        }

        // 2. calcular próximo idRemito
        int nextId;
        try (PreparedStatement ps = conn.prepareStatement("SELECT COALESCE(MAX(idRemito), 0) + 1 FROM " + base + ".remitos");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            nextId = rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo próximo idRemito: " + e.getMessage());
            return null;
        }

        // 3. extraer NumeroRemitoSalida del numeroRemito FacturaSoft (formato "XXXX - YYYYYYYY")
        int numeroSalida;
        try {
            int idx = numeroRemito.lastIndexOf('-');
            if (idx < 0) {
                System.err.println("Formato de numeroRemito invalido (sin guion): " + numeroRemito);
                return null;
            }
            String parteNumerica = numeroRemito.substring(idx + 1).trim();
            numeroSalida = Integer.parseInt(parteNumerica);
        } catch (Exception e) {
            System.err.println("Error parseando numeroRemito: " + numeroRemito + " - " + e.getMessage());
            return null;
        }

        // 4. INSERT en remitos
        String sqlInsert = "INSERT INTO " + base + ".remitos (idRemito, NumeroRemitoSalida, IdUbicacion) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
            ps.setInt(1, nextId);
            ps.setInt(2, numeroSalida);
            ps.setInt(3, idUbicacion);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                System.err.println("Error insertando remito en ReparSoft");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error insertando remito en ReparSoft: " + e.getMessage());
            return null;
        }

        // 5. UPDATE reparaciones SET idRemito, RemitoGenerado, RemitoCliente, Agregadoaremito
        String elsPlaceholders = elsList.stream().map(e -> "?").collect(Collectors.joining(","));
        String sqlUpdRep = "UPDATE " + base + ".reparaciones SET idRemito = ?, RemitoGenerado = 1, "
                + "Agregadoaremito = 1 WHERE els IN (" + elsPlaceholders + ")";
        try (PreparedStatement ps = conn.prepareStatement(sqlUpdRep)) {
            ps.setInt(1, nextId);
            for (int i = 0; i < elsList.size(); i++) {
                ps.setInt(i + 2, elsList.get(i));
            }
            int updated = ps.executeUpdate();
            System.out.println("✓ Remito " + numeroRemito + " sincronizado a ReparSoft (idRemito=" + nextId
                    + ", " + updated + " reparaciones actualizadas)");
        } catch (SQLException e) {
            System.err.println("Error actualizando reparaciones en ReparSoft: " + e.getMessage());
            return null;
        }

        return nextId;
    }

    public boolean eliminarRemitoEnReparsoft(int reparsoftRemitoId) {
        String base = UbicacionSistema.getNombreDbReparsoft();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(base);
        if (conn == null) return false;

        try {
            // desvincular reparaciones
            String sqlUpd = "UPDATE " + base + ".reparaciones SET idRemito = 0, RemitoGenerado = 0, "
                    + "Agregadoaremito = 0 WHERE idRemito = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setInt(1, reparsoftRemitoId);
                ps.executeUpdate();
            }

            // eliminar remito
            String sqlDel = "DELETE FROM " + base + ".remitos WHERE idRemito = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDel)) {
                ps.setInt(1, reparsoftRemitoId);
                boolean ok = ps.executeUpdate() > 0;
                if (ok) {
                    System.out.println("✓ Remito eliminado de ReparSoft (idRemito=" + reparsoftRemitoId + ")");
                }
                return ok;
            }
        } catch (SQLException e) {
            System.err.println("Error eliminando remito de ReparSoft: " + e.getMessage());
        }
        return false;
    }

    public List<Integer> verificarRemitosExistentes(List<Integer> idsReparsoft) {
        if (idsReparsoft == null || idsReparsoft.isEmpty()) return new ArrayList<>();
        String base = UbicacionSistema.getNombreDbReparsoft();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(base);
        if (conn == null) return null;

        String placeholders = idsReparsoft.stream().map(e -> "?").collect(Collectors.joining(","));
        String sql = "SELECT idRemito FROM " + base + ".remitos WHERE idRemito IN (" + placeholders + ")";
        List<Integer> existentes = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < idsReparsoft.size(); i++) {
                ps.setInt(i + 1, idsReparsoft.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                existentes.add(rs.getInt("idRemito"));
            }
        } catch (SQLException e) {
            System.err.println("Error verificando remitos en ReparSoft: " + e.getMessage());
            return null;
        }
        return existentes;
    }

    public boolean eliminarClienteEnReparsoft(int elsReferencia) {
        String base = UbicacionSistema.getNombreDbReparsoft();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(base);
        if (conn == null) return false;

        String sql = "DELETE FROM cliente WHERE idCliente = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, elsReferencia);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                System.out.println("\u2713 Cliente eliminado de ReparSoft (idCliente=" + elsReferencia + ")");
            }
            return ok;
        } catch (SQLException e) {
            System.err.println("Error eliminando cliente de ReparSoft: " + e.getMessage());
        }
        return false;
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