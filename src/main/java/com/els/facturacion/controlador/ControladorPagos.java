package com.els.facturacion.controlador;

import com.els.facturacion.dao.ComprobanteDAO;
import com.els.facturacion.dao.FacturaItemDAO;
import com.els.facturacion.dao.FacturaPagoDAO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.FacturaPagoDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import com.els.facturacion.modelo.ReciboDTO;
import com.els.facturacion.modelo.ReciboFacturaDTO;
import com.els.facturacion.modelo.ReciboPagoDTO;
import com.els.facturacion.pdf.GestorReciboPDF;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ControladorPagos {

    private ComprobanteDAO comprobanteDAO;
    private FacturaPagoDAO facturaPagoDAO;
    private FacturaItemDAO facturaItemDAO;

    public ControladorPagos() {
        this.comprobanteDAO = new ComprobanteDAO();
        this.facturaPagoDAO = new FacturaPagoDAO();
        this.facturaItemDAO = new FacturaItemDAO();
    }

    public ComprobanteDTO buscarFactura(int id) {
        return comprobanteDAO.buscarPorId(id);
    }

    public List<ComprobanteDTO> listarFacturasPendientes() {
        List<ComprobanteDTO> todas = comprobanteDAO.listarTodos();
        List<ComprobanteDTO> pendientes = new java.util.ArrayList<>();
        for (ComprobanteDTO c : todas) {
            if (!"pagada_total".equals(c.getEstadoPago())) {
                pendientes.add(c);
            }
        }
        return pendientes;
    }

    public BigDecimal getSaldoPendiente(int comprobanteId) {
        ComprobanteDTO comp = comprobanteDAO.buscarPorId(comprobanteId);
        if (comp == null) return BigDecimal.ZERO;
        BigDecimal totalPagado = facturaPagoDAO.getTotalPagado(comprobanteId);
        BigDecimal total = comp.getImporteTotal() != null ? comp.getImporteTotal() : BigDecimal.ZERO;
        return total.subtract(totalPagado);
    }

    public void registrarPago(int comprobanteId, BigDecimal monto, String formaPago, Integer reciboId) {
        FacturaPagoDTO pago = new FacturaPagoDTO();
        pago.setComprobanteId(comprobanteId);
        pago.setMonto(monto);
        pago.setFechaPago(LocalDate.now());
        pago.setFormaPago(formaPago);
        pago.setReciboId(reciboId);
        facturaPagoDAO.insertar(pago);
        actualizarEstadoFactura(comprobanteId);
    }

    public boolean registrarPagoItem(int itemId, int comprobanteId, BigDecimal monto, String formaPago, Integer reciboId) {
        String sql = "INSERT INTO factura_item_pagos (factura_item_id, comprobante_id, monto, fecha_pago, recibo_id, estado) "
                + "VALUES (?, ?, ?, ?, ?, 'pagado')";
        try (java.sql.Connection conn = com.els.facturacion.conexion.ConexionFacturacion.getInstancia().getConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, comprobanteId);
            ps.setBigDecimal(3, monto);
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            if (reciboId != null) {
                ps.setInt(5, reciboId);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error registrando pago de item: " + e.getMessage());
            return false;
        }
        facturaItemDAO.actualizarEstadoPagoItem(itemId, "pagado");

        FacturaPagoDTO pago = new FacturaPagoDTO();
        pago.setComprobanteId(comprobanteId);
        pago.setMonto(monto);
        pago.setFechaPago(LocalDate.now());
        pago.setFormaPago(formaPago != null ? formaPago : "Pago de Item");
        pago.setReciboId(reciboId);
        facturaPagoDAO.insertar(pago);

        int[] conteo = facturaItemDAO.contarItemsPorEstado(comprobanteId);
        boolean todosPagados = conteo[0] > 0 && conteo[0] == conteo[1];
        return todosPagados;
    }

    public void pagarFacturaCompleta(int comprobanteId, String formaPago) {
        BigDecimal saldo = getSaldoPendiente(comprobanteId);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) return;

        registrarPago(comprobanteId, saldo, formaPago, null);

        List<ItemFacturaDTO> items = getItemsFactura(comprobanteId);
        for (ItemFacturaDTO item : items) {
            if (!"pagado".equals(item.getEstadoPago())) {
                facturaItemDAO.actualizarEstadoPagoItem(item.getId(), "pagado");
            }
        }

        setEstadoFactura(comprobanteId, "pagada_total");
    }

    public void setEstadoFactura(int comprobanteId, String estado) {
        String sql = "UPDATE comprobantes SET estado_pago = ? WHERE id = ?";
        try (java.sql.Connection conn = com.els.facturacion.conexion.ConexionFacturacion.getInstancia().getConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, comprobanteId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error actualizando estado factura: " + e.getMessage());
        }
    }

    public List<ItemFacturaDTO> getItemsFactura(int comprobanteId) {
        return facturaItemDAO.buscarPorComprobante(comprobanteId);
    }

    public List<FacturaPagoDTO> getPagosFactura(int comprobanteId) {
        return facturaPagoDAO.buscarPorComprobante(comprobanteId);
    }

    public List<FacturaPagoDTO> getTodosLosPagos() {
        return facturaPagoDAO.buscarTodos();
    }

    public FacturaPagoDTO getPagoPorId(int pagoId) {
        return facturaPagoDAO.buscarPorId(pagoId);
    }

    public String generarReciboDesdePagos(List<Integer> pagoIds) {
        if (pagoIds == null || pagoIds.isEmpty()) return null;

        List<FacturaPagoDTO> pagos = new ArrayList<>();
        BigDecimal montoTotal = BigDecimal.ZERO;
        int saltados = 0;
        for (int pagoId : pagoIds) {
            FacturaPagoDTO pago = facturaPagoDAO.buscarPorId(pagoId);
            if (pago == null) return null;
            if (pago.getReciboId() != null) {
                saltados++;
                continue;
            }
            pagos.add(pago);
            montoTotal = montoTotal.add(pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO);
        }
        if (pagos.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "Ninguno de los pagos seleccionados puede generar un recibo\n"
                + (saltados > 0 ? "(" + saltados + " ya tienen recibo asociado)" : ""),
                "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (saltados > 0) {
            JOptionPane.showMessageDialog(null,
                saltados + " pago(s) ya ten\u00edan recibo asociado y fueron omitidos",
                "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
        }

        FacturaPagoDTO primerPago = pagos.get(0);
        ComprobanteDTO comp = comprobanteDAO.buscarPorId(primerPago.getComprobanteId());
        ReciboDTO recibo = new ReciboDTO();
        recibo.setFechaCobro(LocalDate.now());
        recibo.setCuitCliente(comp != null ? comp.getCuitReceptor() : "");
        recibo.setRazonSocialCliente(comp != null ? comp.getRazonSocialRec() : "");
        recibo.setMontoTotal(montoTotal);
        recibo.setObservaciones("Recibo generado desde " + pagos.size() + " pago(s)");

        List<ReciboPagoDTO> formasPago = new ArrayList<>();
        for (FacturaPagoDTO pago : pagos) {
            formasPago.add(new ReciboPagoDTO(pago.getFormaPago(), pago.getMonto(), ""));
        }
        recibo.setFormasPago(formasPago);

        List<ReciboFacturaDTO> facturas = new ArrayList<>();
        FacturaItemDAO itemDAO = new FacturaItemDAO();
        for (FacturaPagoDTO pago : pagos) {
            ComprobanteDTO c = comprobanteDAO.buscarPorId(pago.getComprobanteId());
            if (c != null) {
                ReciboFacturaDTO rf = new ReciboFacturaDTO();
                rf.setComprobanteId(c.getId());
                rf.setMontoAplicado(pago.getMonto());
                rf.setNumeroFactura(String.format("%04d-%08d", c.getPuntoVenta(), c.getNumero()));
                rf.setTipoComprobanteStr(c.getTipoComprobanteStr());
                List<ItemFacturaDTO> items = itemDAO.buscarPorComprobante(c.getId());
                StringBuilder elsSb = new StringBuilder();
                if (items != null) {
                    for (ItemFacturaDTO item : items) {
                        if (item.getCodigo() != null && !item.getCodigo().isEmpty()) {
                            if (elsSb.length() > 0) elsSb.append(", ");
                            elsSb.append(item.getCodigo());
                        }
                    }
                }
                rf.setElsAsociados(elsSb.toString());
                facturas.add(rf);
            }
        }
        recibo.setFacturas(facturas);

        ControladorRecibos controladorRecibos = new ControladorRecibos();
        int reciboId = controladorRecibos.guardarRecibo(recibo);
        if (reciboId <= 0) return null;

        for (FacturaPagoDTO pago : pagos) {
            facturaPagoDAO.actualizarReciboId(pago.getId(), reciboId);
        }

        recibo.setId(reciboId);
        try {
            new GestorReciboPDF().generarRecibo(recibo);
        } catch (Exception e) {
            System.err.println("Error generando PDF del recibo: " + e.getMessage());
            e.printStackTrace();
        }

        return recibo.getNumeroRecibo();
    }

    private void actualizarEstadoFactura(int comprobanteId) {
        BigDecimal totalPagado = facturaPagoDAO.getTotalPagado(comprobanteId);
        ComprobanteDTO comp = comprobanteDAO.buscarPorId(comprobanteId);
        if (comp == null) return;

        BigDecimal total = comp.getImporteTotal() != null ? comp.getImporteTotal() : BigDecimal.ZERO;
        String nuevoEstado;

        if (totalPagado.compareTo(BigDecimal.ZERO) == 0) {
            nuevoEstado = "pendiente";
        } else if (totalPagado.compareTo(total) >= 0) {
            nuevoEstado = "pagada_total";
        } else {
            nuevoEstado = "pagada_parcial";
        }

        String sql = "UPDATE comprobantes SET estado_pago = ? WHERE id = ?";
        try (java.sql.Connection conn = com.els.facturacion.conexion.ConexionFacturacion.getInstancia().getConexion();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, comprobanteId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error actualizando estado factura: " + e.getMessage());
        }
    }
}
