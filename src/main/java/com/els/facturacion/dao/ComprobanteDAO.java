package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.ComprobanteDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComprobanteDAO {

    private Connection conn;

    public ComprobanteDAO() {
        this.conn = ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(ComprobanteDTO comp) {
        String sql = "INSERT INTO comprobantes (cuit_emisor, tipo_comprobante, punto_venta, numero, "
                + "cuit_receptor, razon_social_rec, fecha_emision, importe_neto, importe_iva, "
                + "importe_total, cae, vencimiento_cae, els_asociado, ruta_pdf, email_enviado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, comp.getCuitEmisor());
            ps.setInt(2, comp.getTipoComprobante());
            ps.setInt(3, comp.getPuntoVenta());
            ps.setLong(4, comp.getNumero());
            ps.setString(5, comp.getCuitReceptor());
            ps.setString(6, comp.getRazonSocialRec());
            ps.setDate(7, java.sql.Date.valueOf(comp.getFechaEmision()));
            ps.setBigDecimal(8, comp.getImporteNeto());
            ps.setBigDecimal(9, comp.getImporteIva());
            ps.setBigDecimal(10, comp.getImporteTotal());
            ps.setString(11, comp.getCae());
            ps.setDate(12, comp.getVencimientoCae() != null ? java.sql.Date.valueOf(comp.getVencimientoCae()) : null);
            ps.setInt(13, comp.getElsAsociado() != null ? comp.getElsAsociado() : 0);
            ps.setString(14, comp.getRutaPdf());
            ps.setBoolean(15, comp.getEmailEnviado() != null ? comp.getEmailEnviado() : false);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando comprobante: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(ComprobanteDTO comp) {
        String sql = "UPDATE comprobantes SET cae = ?, vencimiento_cae = ?, ruta_pdf = ?, "
                + "email_enviado = ? WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comp.getCae());
            ps.setDate(2, comp.getVencimientoCae() != null ? java.sql.Date.valueOf(comp.getVencimientoCae()) : null);
            ps.setString(3, comp.getRutaPdf());
            ps.setBoolean(4, comp.getEmailEnviado() != null ? comp.getEmailEnviado() : false);
            ps.setInt(5, comp.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando comprobante: " + e.getMessage());
        }
        return false;
    }

    public ComprobanteDTO buscarPorId(int id) {
        String sql = "SELECT * FROM comprobantes WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando comprobante por ID: " + e.getMessage());
        }
        return null;
    }

    public ComprobanteDTO buscarPorCAE(String cae) {
        String sql = "SELECT * FROM comprobantes WHERE cae = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cae);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando comprobante por CAE: " + e.getMessage());
        }
        return null;
    }

    public ComprobanteDTO buscarPorCuitYTipo(String cuit, int tipo, int puntoVenta, long numero) {
        String sql = "SELECT * FROM comprobantes WHERE cuit_emisor = ? AND tipo_comprobante = ? "
                + "AND punto_venta = ? AND numero = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cuit);
            ps.setInt(2, tipo);
            ps.setInt(3, puntoVenta);
            ps.setLong(4, numero);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando comprobante: " + e.getMessage());
        }
        return null;
    }

    public List<ComprobanteDTO> listarTodos() {
        String sql = "SELECT * FROM comprobantes ORDER BY fecha_emision DESC, numero DESC";
        List<ComprobanteDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando comprobantes: " + e.getMessage());
        }
        return lista;
    }

    public List<ComprobanteDTO> listarPorCuit(String cuit) {
        String sql = "SELECT * FROM comprobantes WHERE cuit_emisor = ? ORDER BY fecha_emision DESC, numero DESC";
        List<ComprobanteDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cuit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando comprobantes por CUIT: " + e.getMessage());
        }
        return lista;
    }

    public List<ComprobanteDTO> buscarPorFecha(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT * FROM comprobantes WHERE fecha_emision BETWEEN ? AND ? "
                + "ORDER BY fecha_emision DESC, numero DESC";
        List<ComprobanteDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(desde));
            ps.setDate(2, java.sql.Date.valueOf(hasta));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error buscando comprobantes por fecha: " + e.getMessage());
        }
        return lista;
    }

    public long getUltimoNumero(String cuit, int puntoVenta, int tipoComprobante) {
        String sql = "SELECT MAX(numero) as ultimo FROM comprobantes WHERE cuit_emisor = ? "
                + "AND punto_venta = ? AND tipo_comprobante = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cuit);
            ps.setInt(2, puntoVenta);
            ps.setInt(3, tipoComprobante);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Long max = rs.getLong("ultimo");
                return max > 0 ? max : 0;
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo último número: " + e.getMessage());
        }
        return 0;
    }

    public List<ComprobanteDTO> listarSinPDF() {
        String sql = "SELECT * FROM comprobantes WHERE cae IS NOT NULL AND (ruta_pdf IS NULL OR ruta_pdf = '') "
                + "ORDER BY fecha_emision DESC";
        List<ComprobanteDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando comprobantes sin PDF: " + e.getMessage());
        }
        return lista;
    }

    private ComprobanteDTO mapearResultado(ResultSet rs) throws SQLException {
        ComprobanteDTO dto = new ComprobanteDTO();
        dto.setId(rs.getInt("id"));
        dto.setCuitEmisor(rs.getString("cuit_emisor"));
        dto.setTipoComprobante(rs.getInt("tipo_comprobante"));
        dto.setPuntoVenta(rs.getInt("punto_venta"));
        dto.setNumero(rs.getLong("numero"));
        dto.setCuitReceptor(rs.getString("cuit_receptor"));
        dto.setRazonSocialRec(rs.getString("razon_social_rec"));

        java.sql.Date fecha = rs.getDate("fecha_emision");
        if (fecha != null) {
            dto.setFechaEmision(fecha.toLocalDate());
        }

        dto.setImporteNeto(rs.getBigDecimal("importe_neto"));
        dto.setImporteIva(rs.getBigDecimal("importe_iva"));
        dto.setImporteTotal(rs.getBigDecimal("importe_total"));
        dto.setCae(rs.getString("cae"));

        java.sql.Date vto = rs.getDate("vencimiento_cae");
        if (vto != null) {
            dto.setVencimientoCae(vto.toLocalDate());
        }

        int els = rs.getInt("els_asociado");
        dto.setElsAsociado(els > 0 ? els : null);

        dto.setRutaPdf(rs.getString("ruta_pdf"));
        dto.setEmailEnviado(rs.getBoolean("email_enviado"));
        return dto;
    }
}