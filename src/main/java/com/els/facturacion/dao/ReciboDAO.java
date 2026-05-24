package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.ReciboDTO;
import com.els.facturacion.modelo.ReciboFacturaDTO;
import com.els.facturacion.modelo.ReciboPagoDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReciboDAO {

    public ReciboDAO() {
    }

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(ReciboDTO recibo) {
        String sql = "INSERT INTO recibos (numero_recibo, fecha_cobro, cliente_id, "
                + "cuit_cliente, razon_social_cliente, monto_total, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, recibo.getNumeroRecibo());
            ps.setDate(2, java.sql.Date.valueOf(recibo.getFechaCobro()));
            if (recibo.getClienteId() != null) {
                ps.setInt(3, recibo.getClienteId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, recibo.getCuitCliente());
            ps.setString(5, recibo.getRazonSocialCliente());
            ps.setBigDecimal(6, recibo.getMontoTotal());
            ps.setString(7, recibo.getObservaciones());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    if (recibo.getFormasPago() != null) {
                        insertarFormasPago(id, recibo.getFormasPago());
                    }
                    if (recibo.getFacturas() != null) {
                        insertarFacturas(id, recibo.getFacturas());
                    }
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando recibo: " + e.getMessage());
        }
        return -1;
    }

    private void insertarFormasPago(int reciboId, List<ReciboPagoDTO> pagos) {
        String sql = "INSERT INTO recibo_pagos (recibo_id, forma_pago, monto, referencia, datos_adicionales) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            for (ReciboPagoDTO p : pagos) {
                ps.setInt(1, reciboId);
                ps.setString(2, p.getFormaPago());
                ps.setBigDecimal(3, p.getMonto());
                ps.setString(4, p.getReferencia());
                ps.setString(5, p.getDatosAdicionales());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error insertando formas de pago: " + e.getMessage());
        }
    }

    private void insertarFacturas(int reciboId, List<ReciboFacturaDTO> facturas) {
        String sql = "INSERT INTO recibo_facturas (recibo_id, comprobante_id, monto_aplicado) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            for (ReciboFacturaDTO f : facturas) {
                ps.setInt(1, reciboId);
                ps.setInt(2, f.getComprobanteId());
                ps.setBigDecimal(3, f.getMontoAplicado());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error insertando facturas del recibo: " + e.getMessage());
        }
    }

    public ReciboDTO buscarPorId(int id) {
        String sql = "SELECT * FROM recibos WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ReciboDTO dto = mapear(rs);
                dto.setFormasPago(buscarFormasPago(id));
                dto.setFacturas(buscarFacturas(id));
                return dto;
            }
        } catch (SQLException e) {
            System.err.println("Error buscando recibo: " + e.getMessage());
        }
        return null;
    }

    public List<ReciboDTO> listarTodos() {
        String sql = "SELECT * FROM recibos ORDER BY fecha_cobro DESC";
        List<ReciboDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ReciboDTO dto = mapear(rs);
                dto.setFormasPago(buscarFormasPago(dto.getId()));
                lista.add(dto);
            }
        } catch (SQLException e) {
            System.err.println("Error listando recibos: " + e.getMessage());
        }
        return lista;
    }

    public List<ReciboDTO> buscarPorCliente(String cuit) {
        String sql = "SELECT * FROM recibos WHERE cuit_cliente = ? ORDER BY fecha_cobro DESC";
        List<ReciboDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, cuit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error buscando recibos por cliente: " + e.getMessage());
        }
        return lista;
    }

    public String getUltimoNumero() {
        String sql = "SELECT numero_recibo FROM recibos ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("numero_recibo");
        } catch (SQLException e) {
            System.err.println("Error obteniendo ultimo numero recibo: " + e.getMessage());
        }
        return null;
    }

    private List<ReciboPagoDTO> buscarFormasPago(int reciboId) {
        List<ReciboPagoDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM recibo_pagos WHERE recibo_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, reciboId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReciboPagoDTO p = new ReciboPagoDTO();
                p.setId(rs.getInt("id"));
                p.setReciboId(rs.getInt("recibo_id"));
                p.setFormaPago(rs.getString("forma_pago"));
                p.setMonto(rs.getBigDecimal("monto"));
                p.setReferencia(rs.getString("referencia"));
                p.setDatosAdicionales(rs.getString("datos_adicionales"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando formas de pago: " + e.getMessage());
        }
        return lista;
    }

    private List<ReciboFacturaDTO> buscarFacturas(int reciboId) {
        List<ReciboFacturaDTO> lista = new ArrayList<>();
        String sql = "SELECT rf.*, c.tipo_comprobante, c.punto_venta, c.numero "
                + "FROM recibo_facturas rf "
                + "LEFT JOIN comprobantes c ON c.id = rf.comprobante_id "
                + "WHERE rf.recibo_id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, reciboId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReciboFacturaDTO f = new ReciboFacturaDTO();
                f.setId(rs.getInt("id"));
                f.setReciboId(rs.getInt("recibo_id"));
                f.setComprobanteId(rs.getInt("comprobante_id"));
                f.setMontoAplicado(rs.getBigDecimal("monto_aplicado"));
                String tipo = rs.getString("tipo_comprobante");
                int pv = rs.getInt("punto_venta");
                long num = rs.getLong("numero");
                if (tipo != null) {
                    f.setNumeroFactura(String.format("%04d-%08d", pv, num));
                }
                lista.add(f);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando facturas del recibo: " + e.getMessage());
        }
        return lista;
    }

    private ReciboDTO mapear(ResultSet rs) throws SQLException {
        ReciboDTO dto = new ReciboDTO();
        dto.setId(rs.getInt("id"));
        dto.setNumeroRecibo(rs.getString("numero_recibo"));
        dto.setFechaCobro(rs.getDate("fecha_cobro").toLocalDate());
        dto.setClienteId(rs.getObject("cliente_id") != null ? rs.getInt("cliente_id") : null);
        dto.setCuitCliente(rs.getString("cuit_cliente"));
        dto.setRazonSocialCliente(rs.getString("razon_social_cliente"));
        dto.setMontoTotal(rs.getBigDecimal("monto_total"));
        dto.setObservaciones(rs.getString("observaciones"));
        return dto;
    }
}
