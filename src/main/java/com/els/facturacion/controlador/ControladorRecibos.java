package com.els.facturacion.controlador;

import com.els.facturacion.dao.FacturaPagoDAO;
import com.els.facturacion.dao.ReciboDAO;
import com.els.facturacion.modelo.ReciboDTO;
import com.els.facturacion.modelo.ReciboFacturaDTO;
import java.math.BigDecimal;
import java.util.List;

public class ControladorRecibos {

    private ReciboDAO reciboDAO;
    private FacturaPagoDAO facturaPagoDAO;

    public ControladorRecibos() {
        this.reciboDAO = new ReciboDAO();
        this.facturaPagoDAO = new FacturaPagoDAO();
    }

    public int guardarRecibo(ReciboDTO recibo) {
        if (recibo.getNumeroRecibo() == null || recibo.getNumeroRecibo().isEmpty()) {
            recibo.setNumeroRecibo(generarNumeroRecibo());
        }
        int id = reciboDAO.insertar(recibo);
        if (id > 0 && recibo.getFacturas() != null) {
            for (ReciboFacturaDTO rf : recibo.getFacturas()) {
                actualizarEstadoFactura(rf.getComprobanteId());
            }
        }
        return id;
    }

    public ReciboDTO buscarPorId(int id) {
        return reciboDAO.buscarPorId(id);
    }

    public List<ReciboDTO> listarTodos() {
        return reciboDAO.listarTodos();
    }

    public List<ReciboDTO> buscarPorCliente(String cuit) {
        return reciboDAO.buscarPorCliente(cuit);
    }

    public BigDecimal getSaldoPendiente(int comprobanteId) {
        return facturaPagoDAO.getTotalPagado(comprobanteId);
    }

    private void actualizarEstadoFactura(int comprobanteId) {
        com.els.facturacion.dao.ComprobanteDAO compDAO = new com.els.facturacion.dao.ComprobanteDAO();
        com.els.facturacion.modelo.ComprobanteDTO comp = compDAO.buscarPorId(comprobanteId);
        if (comp == null) return;

        BigDecimal totalPagado = facturaPagoDAO.getTotalPagado(comprobanteId);
        BigDecimal total = comp.getImporteTotal() != null ? comp.getImporteTotal() : BigDecimal.ZERO;

        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            comp.setEstadoPago("pendiente");
        } else if (totalPagado.compareTo(total) >= 0) {
            comp.setEstadoPago("pagada_total");
        } else {
            comp.setEstadoPago("pagada_parcial");
        }

        String sql = "UPDATE comprobantes SET estado_pago = ? WHERE id = ?";
        try (java.sql.Connection conn = com.els.facturacion.conexion.ConexionFacturacion.getInstancia().getConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, comp.getEstadoPago());
            ps.setInt(2, comprobanteId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error actualizando estado de pago: " + e.getMessage());
        }
    }

    public String generarNumeroRecibo() {
        String ultimo = reciboDAO.getUltimoNumero();
        int num = 1;
        if (ultimo != null && ultimo.contains("-")) {
            try {
                num = Integer.parseInt(ultimo.substring(ultimo.lastIndexOf('-') + 1)) + 1;
            } catch (NumberFormatException e) {}
        }
        return String.format("RE %04d-%08d", 1, num);
    }
}
