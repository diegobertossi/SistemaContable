package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.FacturaPagoDTO;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaPagoDAO {

    public FacturaPagoDAO() {
    }

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(FacturaPagoDTO pago) {
        String sql = "INSERT INTO factura_pagos (comprobante_id, monto, fecha_pago, forma_pago, recibo_id, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pago.getComprobanteId());
            ps.setBigDecimal(2, pago.getMonto());
            ps.setDate(3, java.sql.Date.valueOf(pago.getFechaPago()));
            ps.setString(4, pago.getFormaPago());
            if (pago.getReciboId() != null) {
                ps.setInt(5, pago.getReciboId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setString(6, pago.getObservaciones());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error insertando pago de factura: " + e.getMessage());
        }
        return -1;
    }

    public BigDecimal getTotalPagado(int comprobanteId) {
        String sql = "SELECT COALESCE(SUM(monto), 0) FROM factura_pagos WHERE comprobante_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, comprobanteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBigDecimal(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo total pagado: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public FacturaPagoDTO buscarPorId(int id) {
        String sql = "SELECT * FROM factura_pagos WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                FacturaPagoDTO dto = new FacturaPagoDTO();
                dto.setId(rs.getInt("id"));
                dto.setComprobanteId(rs.getInt("comprobante_id"));
                dto.setMonto(rs.getBigDecimal("monto"));
                dto.setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                dto.setFormaPago(rs.getString("forma_pago"));
                dto.setReciboId(rs.getObject("recibo_id") != null ? rs.getInt("recibo_id") : null);
                dto.setObservaciones(rs.getString("observaciones"));
                return dto;
            }
        } catch (SQLException e) {
            System.err.println("Error buscando pago por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarReciboId(int pagoId, int reciboId) {
        String sql = "UPDATE factura_pagos SET recibo_id = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, reciboId);
            ps.setInt(2, pagoId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando recibo_id en pago: " + e.getMessage());
        }
        return false;
    }

    public List<FacturaPagoDTO> buscarPorComprobante(int comprobanteId) {
        String sql = "SELECT fp.*, r.numero_recibo "
                + "FROM factura_pagos fp "
                + "LEFT JOIN recibos r ON r.id = fp.recibo_id "
                + "WHERE fp.comprobante_id = ? ORDER BY fp.fecha_pago DESC";
        List<FacturaPagoDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, comprobanteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FacturaPagoDTO dto = new FacturaPagoDTO();
                dto.setId(rs.getInt("fp.id"));
                dto.setComprobanteId(rs.getInt("fp.comprobante_id"));
                dto.setMonto(rs.getBigDecimal("fp.monto"));
                dto.setFechaPago(rs.getDate("fp.fecha_pago").toLocalDate());
                dto.setFormaPago(rs.getString("fp.forma_pago"));
                dto.setReciboId(rs.getObject("fp.recibo_id") != null ? rs.getInt("fp.recibo_id") : null);
                dto.setReciboNumero(rs.getString("r.numero_recibo"));
                dto.setObservaciones(rs.getString("fp.observaciones"));
                lista.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando pagos de factura: " + e.getMessage());
        }
        return lista;
    }
}
