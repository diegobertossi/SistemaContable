package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.modelo.RemitoItemDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RemitoDAO {

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(RemitoDTO remito) {
        String sql = "INSERT INTO remitos (numero_remito, fecha_emision, fecha_entrega, "
                + "cuit_emisor, razon_social_emisor, domicilio_emisor, "
                + "cuit_receptor, razon_social_receptor, domicilio_receptor, "
                + "comprobante_id, estado, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, remito.getNumeroRemito());
            ps.setDate(2, java.sql.Date.valueOf(remito.getFechaEmision()));
            ps.setDate(3, remito.getFechaEntrega() != null ? java.sql.Date.valueOf(remito.getFechaEntrega()) : null);
            ps.setString(4, remito.getCuitEmisor());
            ps.setString(5, remito.getRazonSocialEmisor());
            ps.setString(6, remito.getDomicilioEmisor());
            ps.setString(7, remito.getCuitReceptor());
            ps.setString(8, remito.getRazonSocialReceptor());
            ps.setString(9, remito.getDomicilioReceptor());
            if (remito.getComprobanteId() != null) {
                ps.setInt(10, remito.getComprobanteId());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }
            ps.setString(11, remito.getEstado());
            ps.setString(12, remito.getObservaciones());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    if (remito.getItems() != null) {
                        insertarItems(id, remito.getItems());
                    }
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando remito: " + e.getMessage());
        }
        return -1;
    }

    private void insertarItems(int remitoId, List<RemitoItemDTO> items) {
        String sql = "INSERT INTO remito_items (remito_id, codigo, descripcion, cantidad, "
                + "unidad_medida, els_referencia, orden) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            int orden = 0;
            for (RemitoItemDTO item : items) {
                ps.setInt(1, remitoId);
                ps.setString(2, item.getCodigo());
                ps.setString(3, item.getDescripcion());
                ps.setBigDecimal(4, item.getCantidad());
                ps.setString(5, item.getUnidadMedida());
                if (item.getElsReferencia() != null) {
                    ps.setInt(6, item.getElsReferencia());
                } else {
                    ps.setNull(6, java.sql.Types.INTEGER);
                }
                ps.setInt(7, orden++);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error insertando items de remito: " + e.getMessage());
        }
    }

    public boolean actualizarEstado(int id, String estado) {
        String sql = "UPDATE remitos SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando estado remito: " + e.getMessage());
        }
        return false;
    }

    public RemitoDTO buscarPorId(int id) {
        String sql = "SELECT * FROM remitos WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error buscando remito: " + e.getMessage());
        }
        return null;
    }

    public List<RemitoDTO> listarTodos() {
        String sql = "SELECT * FROM remitos ORDER BY fecha_emision DESC";
        List<RemitoDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listando remitos: " + e.getMessage());
        }
        return lista;
    }

    public List<RemitoDTO> buscarPorReceptor(String cuit) {
        String sql = "SELECT * FROM remitos WHERE cuit_receptor = ? ORDER BY fecha_emision DESC";
        List<RemitoDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, cuit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error buscando remitos por receptor: " + e.getMessage());
        }
        return lista;
    }

    public String getUltimoNumero() {
        String sql = "SELECT numero_remito FROM remitos ORDER BY id DESC LIMIT 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString("numero_remito");
        } catch (SQLException e) {
            System.err.println("Error obteniendo ultimo numero remito: " + e.getMessage());
        }
        return null;
    }

    private RemitoDTO mapear(ResultSet rs) throws SQLException {
        RemitoDTO dto = new RemitoDTO();
        dto.setId(rs.getInt("id"));
        dto.setNumeroRemito(rs.getString("numero_remito"));
        dto.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        java.sql.Date fe = rs.getDate("fecha_entrega");
        if (fe != null) dto.setFechaEntrega(fe.toLocalDate());
        dto.setCuitEmisor(rs.getString("cuit_emisor"));
        dto.setRazonSocialEmisor(rs.getString("razon_social_emisor"));
        dto.setDomicilioEmisor(rs.getString("domicilio_emisor"));
        dto.setCuitReceptor(rs.getString("cuit_receptor"));
        dto.setRazonSocialReceptor(rs.getString("razon_social_receptor"));
        dto.setDomicilioReceptor(rs.getString("domicilio_receptor"));
        dto.setComprobanteId(rs.getObject("comprobante_id") != null ? rs.getInt("comprobante_id") : null);
        dto.setEstado(rs.getString("estado"));
        dto.setObservaciones(rs.getString("observaciones"));
        return dto;
    }
}
