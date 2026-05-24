package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.ItemFacturaDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaItemDAO {

    public FacturaItemDAO() {
    }

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public void insertarItems(int comprobanteId, List<ItemFacturaDTO> items) {
        String sql = "INSERT INTO factura_items (comprobante_id, codigo, descripcion, cantidad, "
                + "unidad_medida, precio_unitario, subtotal, alicuota_iva, orden) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            int orden = 0;
            for (ItemFacturaDTO item : items) {
                ps.setInt(1, comprobanteId);
                ps.setString(2, item.getCodigo());
                ps.setString(3, item.getDescripcion());
                ps.setBigDecimal(4, item.getCantidad());
                ps.setString(5, item.getUnidadMedida());
                ps.setBigDecimal(6, item.getPrecioUnitario());
                ps.setBigDecimal(7, item.getSubtotal());
                ps.setBigDecimal(8, item.getAlicuotaIva());
                ps.setInt(9, orden++);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error insertando items de factura: " + e.getMessage());
        }
    }

    public List<ItemFacturaDTO> buscarPorComprobante(int comprobanteId) {
        String sql = "SELECT * FROM factura_items WHERE comprobante_id = ? ORDER BY orden";
        List<ItemFacturaDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, comprobanteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ItemFacturaDTO item = new ItemFacturaDTO();
                item.setId(rs.getInt("id"));
                item.setCodigo(rs.getString("codigo"));
                item.setDescripcion(rs.getString("descripcion"));
                item.setCantidad(rs.getBigDecimal("cantidad"));
                item.setUnidadMedida(rs.getString("unidad_medida"));
                item.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                item.setSubtotal(rs.getBigDecimal("subtotal"));
                item.setAlicuotaIva(rs.getBigDecimal("alicuota_iva"));
                item.setElsReferencia(rs.getObject("els_referencia") != null ? rs.getInt("els_referencia") : null);
                try { item.setEstadoPago(rs.getString("estado_pago")); } catch (SQLException e) {}
                lista.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando items por comprobante: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstadoPagoItem(int itemId, String estado) {
        String sql = "UPDATE factura_items SET estado_pago = ? WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando estado pago item: " + e.getMessage());
        }
        return false;
    }

    public int[] contarItemsPorEstado(int comprobanteId) {
        int total = 0;
        int pagados = 0;
        String sql = "SELECT estado_pago, COUNT(*) as cnt FROM factura_items WHERE comprobante_id = ? GROUP BY estado_pago";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, comprobanteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int cnt = rs.getInt("cnt");
                total += cnt;
                if ("pagado".equals(rs.getString("estado_pago"))) {
                    pagados = cnt;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error contando items por estado: " + e.getMessage());
        }
        return new int[]{total, pagados};
    }
}
