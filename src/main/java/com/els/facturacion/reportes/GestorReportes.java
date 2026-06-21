package com.els.facturacion.reportes;

import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.ReciboDTO;
import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.modelo.RemitoItemDTO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorReportes {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String generarReporteFacturas(List<ComprobanteDTO> facturas, String rutaSalida) {
        try {
            InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("reportes/reporte_facturas.jrxml");
            if (jrxmlStream == null) {
                System.err.println("No se encontro reporte_facturas.jrxml, generando reporte simple");
                return null;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("TITULO", "LISTADO DE FACTURAS");
            params.put("FECHA", java.time.LocalDate.now().format(FMT));

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            JRDataSource dataSource = new JRBeanCollectionDataSource(facturas);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            FileOutputStream fos = new FileOutputStream(rutaSalida);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
            exporter.exportReport();
            fos.close();

            return rutaSalida;
        } catch (Exception e) {
            System.err.println("Error generando reporte de facturas: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String generarReporteRemito(RemitoDTO remito, String rutaSalida) {
        try {
            InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("reportes/RemitoComun.jrxml");
            if (jrxmlStream == null) {
                System.err.println("No se encontro RemitoComun.jrxml");
                return null;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("cliente", remito.getRazonSocialReceptor() != null ? remito.getRazonSocialReceptor() : "");
            params.put("remitoConformado", remito.getNumeroRemito() != null ? remito.getNumeroRemito() : "");
            params.put("cantBultos", 0);
            params.put("cuit", remito.getCuitEmisor() != null ? remito.getCuitEmisor() : "");
            params.put("domicilio", remito.getDomicilioEmisor() != null ? remito.getDomicilioEmisor() : "");

            String descripcionTexto = remito.getItems() != null
                ? remito.getItems().stream().map(RemitoItemDTO::getDescripcion).collect(java.util.stream.Collectors.joining("\n\n"))
                : "";
            params.put("descripcion", descripcionTexto);
            params.put("fecha_Entrada", remito.getFechaEmision() != null ? remito.getFechaEmision().format(FMT) : "");

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport, params, new JREmptyDataSource(1));

            FileOutputStream fos = new FileOutputStream(rutaSalida);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
            exporter.exportReport();
            fos.close();

            return rutaSalida;
        } catch (Exception e) {
            System.err.println("Error generando reporte remito: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String generarReporteRecibo(ReciboDTO recibo, String rutaSalida) {
        try {
            InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("reportes/recibo.jrxml");
            if (jrxmlStream == null) {
                System.err.println("No se encontro reporte recibo.jrxml");
                return null;
            }

            com.els.facturacion.dao.CuitDAO cuitDAO = new com.els.facturacion.dao.CuitDAO();
            List<com.els.facturacion.modelo.CuitConfigDTO> activos = cuitDAO.listarActivos();
            if (activos.isEmpty()) {
                System.err.println("No hay CUIT activo configurado para emitir el recibo");
                return null;
            }
            com.els.facturacion.modelo.CuitConfigDTO emisor = activos.get(0);

            Map<String, Object> params = new HashMap<>();
            params.put("EMISOR_RAZON_SOCIAL", emisor.getRazonSocial());
            params.put("EMISOR_DOMICILIO", emisor.getDomicilio() != null ? emisor.getDomicilio() : "");
            params.put("EMISOR_CUIT", emisor.getCuit());
            params.put("EMISOR_ING_BRUTOS", emisor.getIngresosBrutos() != null ? emisor.getIngresosBrutos() : "");
            params.put("EMISOR_INICIO_ACT", emisor.getFechaInicioActividades() != null ? emisor.getFechaInicioActividades() : "");
            params.put("EMISOR_CONDICION_IVA", emisor.getCondicionIva());
            params.put("PUNTO_VENTA", emisor.getPuntoVenta() != null ? emisor.getPuntoVenta().toString() : "");
            params.put("COMP_NRO", recibo.getNumeroRecibo());
            params.put("FECHA_EMISION", recibo.getFechaCobro() != null ? recibo.getFechaCobro().format(FMT) : "");
            params.put("PERIODO_DESDE", "");
            params.put("PERIODO_HASTA", "");
            params.put("FECHA_VTO_PAGO", "");
            params.put("CLIENTE_RAZON_SOCIAL", recibo.getRazonSocialCliente() != null ? recibo.getRazonSocialCliente() : "");
            params.put("CLIENTE_CUIT", recibo.getCuitCliente() != null ? recibo.getCuitCliente() : "");
            params.put("CLIENTE_DOMICILIO", "");
            params.put("CLIENTE_CONDICION_IVA", "");
            params.put("CLIENTE_CONDICION_VENTA", "");
            String totalStr = recibo.getMontoTotal() != null
                ? new java.text.DecimalFormat("#,##0.00").format(recibo.getMontoTotal().setScale(2, java.math.RoundingMode.HALF_UP)) : "0,00";
            params.put("SUBTOTAL", totalStr);
            params.put("OTROS_TRIBUTOS", "0,00");
            params.put("IMPORTE_TOTAL", totalStr);
            params.put("RECIBO_NUMERO", recibo.getNumeroRecibo());
            params.put("MONTO_TOTAL", recibo.getMontoTotal() != null
                ? recibo.getMontoTotal().setScale(2, java.math.RoundingMode.HALF_UP) : java.math.BigDecimal.ZERO);
            params.put("MONTO_LETRAS", "");
            params.put("FORMAS_PAGO_TEXTO", "");
            params.put("OBSERVACIONES", recibo.getObservaciones() != null ? recibo.getObservaciones() : "");

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            JRDataSource dataSource = recibo.getFacturas() != null
                ? new JRBeanCollectionDataSource(recibo.getFacturas())
                : new JRBeanCollectionDataSource(java.util.Collections.emptyList());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            FileOutputStream fos = new FileOutputStream(rutaSalida);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
            exporter.exportReport();
            fos.close();

            return rutaSalida;
        } catch (Exception e) {
            System.err.println("Error generando reporte recibo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String generarReporteCuentaCorriente(List<Map<String, Object>> movimientos, String cliente, String rutaSalida) {
        try {
            InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("reportes/cuenta_corriente.jrxml");
            if (jrxmlStream == null) {
                System.err.println("No se encontro reporte cuenta_corriente.jrxml");
                return null;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("CLIENTE", cliente);
            params.put("FECHA", java.time.LocalDate.now().format(FMT));

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            JRDataSource dataSource = new JRBeanCollectionDataSource(movimientos);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            FileOutputStream fos = new FileOutputStream(rutaSalida);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
            exporter.exportReport();
            fos.close();

            return rutaSalida;
        } catch (Exception e) {
            System.err.println("Error generando reporte cuenta corriente: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
