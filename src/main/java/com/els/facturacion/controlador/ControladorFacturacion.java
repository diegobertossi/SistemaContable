package com.els.facturacion.controlador;

import com.els.facturacion.arca.ServicioWSFEv1;
import com.els.facturacion.dao.ComprobanteDAO;
import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import com.els.facturacion.modelo.RespuestaCAE;
import com.els.facturacion.pdf.GestorPDF;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ControladorFacturacion {

    private CuitDAO cuitDAO;
    private ComprobanteDAO comprobanteDAO;
    private ServicioWSFEv1 servicioWSFEv1;
    private boolean modoPrueba;

    public ControladorFacturacion() {
        this.cuitDAO = new CuitDAO();
        this.comprobanteDAO = new ComprobanteDAO();
        this.servicioWSFEv1 = new ServicioWSFEv1();
        this.modoPrueba = false;
    }

    public RespuestaCAE emitirFactura(ComprobanteDTO comprobante) {
        return emitirFactura(comprobante, null);
    }

    public RespuestaCAE emitirFactura(ComprobanteDTO comprobante, List<ItemFacturaDTO> items) {
        try {
            CuitConfigDTO cuit = cuitDAO.buscarPorCuit(comprobante.getCuitEmisor());
            if (cuit == null) {
                RespuestaCAE error = new RespuestaCAE();
                error.setError("CUIT no encontrado en la configuración");
                return error;
            }

            if (cuit.getRutaCertificado() == null || cuit.getRutaCertificado().isEmpty()) {
                RespuestaCAE error = new RespuestaCAE();
                error.setError("Certificado no configurado para este CUIT");
                return error;
            }

            long ultimoNumero = comprobanteDAO.getUltimoNumero(
                comprobante.getCuitEmisor(),
                comprobante.getPuntoVenta(),
                comprobante.getTipoComprobante()
            );
            comprobante.setNumero(ultimoNumero + 1);

            if (comprobante.getFechaEmision() == null) {
                comprobante.setFechaEmision(LocalDate.now());
            }

            if (comprobante.getImporteTotal() == null) {
                BigDecimal total = comprobante.getImporteNeto();
                if (comprobante.getImporteIva() != null) {
                    total = total.add(comprobante.getImporteIva());
                }
                comprobante.setImporteTotal(total);
            }

            RespuestaCAE respuesta;
            if (modoPrueba) {
                respuesta = new RespuestaCAE(
                    String.format("%011d", comprobante.hashCode() % 100000000000L),
                    LocalDate.now().plusDays(15),
                    comprobante.getNumero()
                );
                respuesta.setMensaje("MODO PRUEBA - CAE ficticio generado");
            } else {
                respuesta = servicioWSFEv1.emitirComprobante(
                    comprobante,
                    cuit.getRutaCertificado(),
                    cuit.getPasswordCert()
                );
            }

            if (respuesta.isExitosa()) {
                comprobante.setCae(respuesta.getCae());
                comprobante.setVencimientoCae(respuesta.getVencimiento());
                if (respuesta.getNumeroComprobante() != null) {
                    comprobante.setNumero(respuesta.getNumeroComprobante());
                }

                int id = comprobanteDAO.insertar(comprobante);
                if (id > 0) {
                    comprobante.setId(id);
                    System.out.println("Comprobante guardado en BD con ID: " + id);

                    String rutaPDF = new GestorPDF().generarFactura(comprobante, cuit, items);
                    if (rutaPDF != null) {
                        comprobante.setRutaPdf(rutaPDF);
                        comprobanteDAO.actualizar(comprobante);
                        System.out.println("PDF generado: " + rutaPDF);
                    }
                } else {
                    respuesta.setError("Error al guardar comprobante en base de datos");
                }
            }

            return respuesta;

        } catch (Exception e) {
            RespuestaCAE error = new RespuestaCAE();
            error.setError("Error emitiendo factura: " + e.getMessage());
            System.err.println("Error en ControladorFacturacion: " + e.getMessage());
            e.printStackTrace();
            return error;
        }
    }

    public ComprobanteDTO buscarComprobante(int id) {
        return comprobanteDAO.buscarPorId(id);
    }

    public ComprobanteDTO buscarComprobante(String cae) {
        return comprobanteDAO.buscarPorCAE(cae);
    }

    public java.util.List<ComprobanteDTO> listarComprobantes() {
        return comprobanteDAO.listarTodos();
    }

    public java.util.List<ComprobanteDTO> listarComprobantes(String cuit) {
        return comprobanteDAO.listarPorCuit(cuit);
    }

    public java.util.List<ComprobanteDTO> buscarComprobantes(LocalDate desde, LocalDate hasta) {
        return comprobanteDAO.buscarPorFecha(desde, hasta);
    }

    public java.util.List<ComprobanteDTO> listarComprobantesSinPDF() {
        return comprobanteDAO.listarSinPDF();
    }

    public boolean actualizarComprobante(ComprobanteDTO comprobante) {
        return comprobanteDAO.actualizar(comprobante);
    }

    public CuitConfigDTO getCuitActivo(String cuit) {
        return cuitDAO.buscarPorCuit(cuit);
    }

    public java.util.List<CuitConfigDTO> getCuitsActivos() {
        return cuitDAO.listarActivos();
    }

    public void setEntorno(String entorno) {
        servicioWSFEv1.setEntorno(entorno);
    }

    public String regenerarPDF(ComprobanteDTO comprobante) {
        return regenerarPDF(comprobante, null);
    }

    public String regenerarPDF(ComprobanteDTO comprobante, List<ItemFacturaDTO> items) {
        CuitConfigDTO cuit = cuitDAO.buscarPorCuit(comprobante.getCuitEmisor());
        if (cuit == null) return null;
        String rutaPDF = new GestorPDF().generarFactura(comprobante, cuit, items);
        if (rutaPDF != null) {
            comprobante.setRutaPdf(rutaPDF);
            comprobanteDAO.actualizar(comprobante);
        }
        return rutaPDF;
    }

    public void setModoPrueba(boolean modoPrueba) {
        this.modoPrueba = modoPrueba;
    }

    public boolean isModoPrueba() {
        return modoPrueba;
    }
}