package com.els.facturacion.pdf;

import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.json.simple.JSONObject;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class GestorPDF {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FECHA_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String generarFacturaA4(ComprobanteDTO comprobante, CuitConfigDTO emitidor, String rutaSalida) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("RAZON_SOCIAL_EMISOR", emitidor.getRazonSocial());
            parametros.put("CUIT_EMISOR", emitidor.getCuit());
            parametros.put("PUNTO_VENTA", emitidor.getPuntoVenta());
            parametros.put("TIPO_COMPROBANTE", "FACTURA A");
            parametros.put("LETRA_COMPROBANTE", "A");

            parametros.put("NUMERO_COMPROBANTE", String.format("%08d", comprobante.getNumero()));
            parametros.put("FECHA_EMISION", comprobante.getFechaEmision().format(FECHA_DISPLAY));

            parametros.put("CUIT_RECEPTOR", comprobante.getCuitReceptor());
            parametros.put("RAZON_SOCIAL_RECEPTOR", comprobante.getRazonSocialRec());
            parametros.put("CONDICION_IVA", emitidor.getCondicionIva());

            parametros.put("IMPORTE_NETO", comprobante.getImporteNeto().setScale(2));
            parametros.put("IMPORTE_IVA", comprobante.getImporteIva() != null ? comprobante.getImporteIva().setScale(2) : BigDecimal.ZERO);
            parametros.put("IMPORTE_TOTAL", comprobante.getImporteTotal().setScale(2));

            parametros.put("CAE", comprobante.getCae());
            parametros.put("VENCIMIENTO_CAE", comprobante.getVencimientoCae().format(FECHA_DISPLAY));

            String qrARCA = generarQR_ARCA(comprobante, emitidor);
            parametros.put("QR_BASE64", qrARCA);
            parametros.put("URL_QR", "https://www.afip.gob.ar/fe/qr/?p=" + qrARCA);

            String jrxmlPath = "src/main/resources/reportes/factura_a.jrxml";
            File jrxmlFile = new File(jrxmlPath);

            if (!jrxmlFile.exists()) {
                System.err.println("Plantilla no encontrada, usando generación por código");
                return generarPDFDirecto(comprobante, emitidor, rutaSalida);
            }

            JRDataSource dataSource = new JRDataSource() {
                private boolean first = true;
                @Override
                public boolean next() {
                    if (first) {
                        first = false;
                        return true;
                    }
                    return false;
                }
                @Override
                public Object getFieldValue(JRField field) {
                    return null;
                }
            };

            JasperPrint jasperPrint = JasperFillManager.fillReport(jrxmlPath, parametros, dataSource);

            if (rutaSalida != null && !rutaSalida.isEmpty()) {
                FileOutputStream fos = new FileOutputStream(rutaSalida);
                net.sf.jasperreports.engine.export.JRPdfExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();
                exporter.setParameter(net.sf.jasperreports.engine.JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(net.sf.jasperreports.engine.JRExporterParameter.OUTPUT_STREAM, fos);
                exporter.exportReport();
                fos.close();
                System.out.println("✓ PDF generado: " + rutaSalida);
                return rutaSalida;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            net.sf.jasperreports.engine.export.JRPdfExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();
            exporter.setParameter(net.sf.jasperreports.engine.JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(net.sf.jasperreports.engine.JRExporterParameter.OUTPUT_STREAM, baos);
            exporter.exportReport();
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (Exception e) {
            System.err.println("Error generando PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private String generarQR_ARCA(ComprobanteDTO comp, CuitConfigDTO emit) {
        try {
            JSONObject qrJson = new JSONObject();
            qrJson.put("ver", 1);
            qrJson.put("fecha", comp.getFechaEmision().format(FECHA_FMT));
            qrJson.put("cuit", Long.parseLong(emit.getCuit()));
            qrJson.put("ptoVta", emit.getPuntoVenta());
            qrJson.put("tipoCmp", comp.getTipoComprobante());
            qrJson.put("nroCmp", comp.getNumero());
            qrJson.put("importe", comp.getImporteTotal().doubleValue());
            qrJson.put("moneda", "PES");
            qrJson.put("ctz", 1.0);
            qrJson.put("tipoDocRec", 80);
            qrJson.put("nroDocRec", Long.parseLong(comp.getCuitReceptor()));
            qrJson.put("tipoCodAut", "E");
            qrJson.put("codAut", Long.parseLong(comp.getCae()));

            String jsonString = qrJson.toJSONString();
            return Base64.getEncoder().encodeToString(jsonString.getBytes("UTF-8"));
        } catch (Exception e) {
            System.err.println("Error generando QR: " + e.getMessage());
            return "";
        }
    }

    private String generarPDFDirecto(ComprobanteDTO comprobante, CuitConfigDTO emitidor, String rutaSalida) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           FACTURA A\n");
        sb.append("========================================\n");
        sb.append("Razón Social: ").append(emitidor.getRazonSocial()).append("\n");
        sb.append("CUIT: ").append(emitidor.getCuit()).append("\n");
        sb.append("Punto de Venta: ").append(emitidor.getPuntoVenta()).append("\n");
        sb.append("Comprobante Nro: ").append(String.format("%08d", comprobante.getNumero())).append("\n");
        sb.append("Fecha: ").append(comprobante.getFechaEmision().format(FECHA_DISPLAY)).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Receptor:\n");
        sb.append("CUIT: ").append(comprobante.getCuitReceptor()).append("\n");
        sb.append("Razón Social: ").append(comprobante.getRazonSocialRec()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Importe Neto: $").append(comprobante.getImporteNeto()).append("\n");
        if (comprobante.getImporteIva() != null) {
            sb.append("IVA: $").append(comprobante.getImporteIva()).append("\n");
        }
        sb.append("TOTAL: $").append(comprobante.getImporteTotal()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("CAE: ").append(comprobante.getCae()).append("\n");
        sb.append("Vencimiento: ").append(comprobante.getVencimientoCae().format(FECHA_DISPLAY)).append("\n");
        sb.append("========================================\n");

        try {
            if (rutaSalida != null) {
                Files.write(Paths.get(rutaSalida), sb.toString().getBytes());
                return rutaSalida;
            }
        } catch (Exception e) {
            System.err.println("Error guardando PDF texto: " + e.getMessage());
        }
        return null;
    }

    public void verPreview(ComprobanteDTO comprobante, CuitConfigDTO emitidor) {
        String rutaTemporal = "temp_factura_" + System.currentTimeMillis() + ".txt";
        String ruta = generarFacturaA4(comprobante, emitidor, rutaTemporal);
        if (ruta != null) {
            try {
                Desktop.getDesktop().open(new File(ruta));
            } catch (Exception e) {
                System.err.println("No se puede abrir preview: " + e.getMessage());
            }
        }
    }

    public byte[] generarPDFBytes(ComprobanteDTO comprobante, CuitConfigDTO emitidor) {
        String base64 = generarFacturaA4(comprobante, emitidor, null);
        if (base64 != null) {
            return Base64.getDecoder().decode(base64);
        }
        return null;
    }
}