package com.els.facturacion.pdf;

import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorPDF {

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FECHA_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String DIRECTORIO_FACTURAS = "F:\\Trabajo\\Monotributo\\Facturas";

    private static final Map<Integer, String> TIPO_FILENAME = new HashMap<>();
    private static final Map<Integer, String> TIPO_TITULO = new HashMap<>();
    static {
        TIPO_FILENAME.put(1, "00001");
        TIPO_FILENAME.put(6, "00006");
        TIPO_FILENAME.put(11, "00011");
        TIPO_FILENAME.put(3, "00003");
        TIPO_FILENAME.put(8, "00008");
        TIPO_FILENAME.put(13, "00013");
        TIPO_FILENAME.put(2, "00002");
        TIPO_FILENAME.put(7, "00007");
        TIPO_FILENAME.put(12, "00012");
        TIPO_FILENAME.put(4, "00004");
        TIPO_FILENAME.put(9, "00009");
        TIPO_FILENAME.put(10, "00010");
        TIPO_FILENAME.put(331, "00331");

        TIPO_TITULO.put(1, "FACTURA A");
        TIPO_TITULO.put(6, "FACTURA B");
        TIPO_TITULO.put(11, "FACTURA C");
        TIPO_TITULO.put(3, "NOTA DE CREDITO A");
        TIPO_TITULO.put(8, "NOTA DE CREDITO B");
        TIPO_TITULO.put(13, "NOTA DE CREDITO C");
        TIPO_TITULO.put(2, "NOTA DE DEBITO A");
        TIPO_TITULO.put(7, "NOTA DE DEBITO B");
        TIPO_TITULO.put(12, "NOTA DE DEBITO C");
        TIPO_TITULO.put(4, "RECIBO A");
        TIPO_TITULO.put(9, "RECIBO B");
        TIPO_TITULO.put(10, "RECIBO C");
        TIPO_TITULO.put(331, "FACTURA DE CREDITO ELECTRONICA MIPYMES");
    }

    private static final Map<Integer, String> LETRA = new HashMap<>();
    static {
        LETRA.put(1, "A"); LETRA.put(6, "B"); LETRA.put(11, "C");
        LETRA.put(3, "A"); LETRA.put(8, "B"); LETRA.put(13, "C");
        LETRA.put(2, "A"); LETRA.put(7, "B"); LETRA.put(12, "C");
        LETRA.put(4, "A"); LETRA.put(9, "B"); LETRA.put(10, "C");
        LETRA.put(331, "FCE");
    }

    public String generarFactura(ComprobanteDTO comprobante, CuitConfigDTO emitidor, List<ItemFacturaDTO> items) {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_FACTURAS));

            String cuit = emitidor.getCuit();
            String pv = String.format("%03d", emitidor.getPuntoVenta());
            String tipo = TIPO_FILENAME.getOrDefault(comprobante.getTipoComprobante(), "00001");
            String numero = String.format("%08d", comprobante.getNumero());
            String nombreArchivo = cuit + "_" + pv + "_" + tipo + "_" + numero + ".pdf";
            String rutaSalida = DIRECTORIO_FACTURAS + File.separator + nombreArchivo;

            String ruta = generarFacturaA4(comprobante, emitidor, rutaSalida, items);
            return ruta;
        } catch (Exception e) {
            System.err.println("Error generando PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String generarFactura(ComprobanteDTO comprobante, CuitConfigDTO emitidor) {
        List<ItemFacturaDTO> items = new ArrayList<>();
        if (comprobante.getDescripcion() != null && !comprobante.getDescripcion().isEmpty()) {
            items.add(new ItemFacturaDTO("", comprobante.getDescripcion(), BigDecimal.ONE,
                "Unidad", comprobante.getImporteNeto(), comprobante.getImporteNeto()));
        }
        return generarFactura(comprobante, emitidor, items);
    }

    public String generarFacturaA4(ComprobanteDTO comprobante, CuitConfigDTO emitidor, String rutaSalida) {
        List<ItemFacturaDTO> items = new ArrayList<>();
        if (comprobante.getDescripcion() != null && !comprobante.getDescripcion().isEmpty()) {
            items.add(new ItemFacturaDTO("", comprobante.getDescripcion(), BigDecimal.ONE,
                "Unidad", comprobante.getImporteNeto(), comprobante.getImporteNeto()));
        }
        return generarFacturaA4(comprobante, emitidor, rutaSalida, items);
    }

    public String generarFacturaA4(ComprobanteDTO comprobante, CuitConfigDTO emitidor, String rutaSalida, List<ItemFacturaDTO> items) {
        try {
            String titulo = TIPO_TITULO.getOrDefault(comprobante.getTipoComprobante(), "FACTURA");
            String letra = LETRA.getOrDefault(comprobante.getTipoComprobante(), "A");
            String tipoStr = titulo + " (Tipo " + comprobante.getTipoComprobante() + ")";

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("RAZON_SOCIAL_EMISOR", emitidor.getRazonSocial());
            parametros.put("CUIT_EMISOR", emitidor.getCuit());
            parametros.put("CONDICION_IVA_EMISOR", emitidor.getCondicionIva());
            parametros.put("DOMICILIO_EMISOR", "");
            parametros.put("PUNTO_VENTA", String.format("%03d", emitidor.getPuntoVenta()));
            parametros.put("TIPO_COMPROBANTE", tipoStr);
            parametros.put("NUMERO_COMPROBANTE", String.format("%08d", comprobante.getNumero()));
            parametros.put("FECHA_EMISION", comprobante.getFechaEmision().format(FECHA_DISPLAY));
            parametros.put("CONCEPTO", comprobante.getConcepto() != null ? comprobante.getConcepto() : "");
            parametros.put("PERIODO_DESDE", comprobante.getPeriodoDesde() != null ? comprobante.getPeriodoDesde().format(FECHA_DISPLAY) : "");
            parametros.put("PERIODO_HASTA", comprobante.getPeriodoHasta() != null ? comprobante.getPeriodoHasta().format(FECHA_DISPLAY) : "");
            parametros.put("PERIODO_VTO", comprobante.getPeriodoVto() != null ? comprobante.getPeriodoVto().format(FECHA_DISPLAY) : "");

            parametros.put("CUIT_RECEPTOR", comprobante.getCuitReceptor());
            parametros.put("RAZON_SOCIAL_RECEPTOR", comprobante.getRazonSocialRec());
            parametros.put("CONDICION_IVA_RECEPTOR", comprobante.getCondicionIvaReceptor() != null ? comprobante.getCondicionIvaReceptor() : "");
            parametros.put("DOMICILIO_RECEPTOR", comprobante.getDomicilioReceptor() != null ? comprobante.getDomicilioReceptor() : "");

            parametros.put("IMPORTE_NETO", comprobante.getImporteNeto().setScale(2, RoundingMode.HALF_UP));
            parametros.put("IMPORTE_IVA", comprobante.getImporteIva() != null ? comprobante.getImporteIva().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            parametros.put("IMPORTE_TOTAL", comprobante.getImporteTotal().setScale(2, RoundingMode.HALF_UP));
            parametros.put("TOTAL_LETRAS", totalEnLetras(comprobante.getImporteTotal()));

            parametros.put("CAE", comprobante.getCae());
            parametros.put("VENCIMIENTO_CAE", comprobante.getVencimientoCae().format(FECHA_DISPLAY));

            String qrARCA = generarQR_ARCA(comprobante, emitidor);
            parametros.put("QR_URL", "https://www.afip.gob.ar/fe/qr/?p=" + qrARCA);

            JasperReport jasperReport;
            InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("reportes/factura_a.jrxml");
            if (jrxmlStream == null) {
                jrxmlStream = new FileInputStream("src/main/resources/reportes/factura_a.jrxml");
            }
            try {
                jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            } finally {
                jrxmlStream.close();
            }

            JRDataSource dataSource = (items != null && !items.isEmpty())
                ? new JRBeanCollectionDataSource(items)
                : new JRBeanCollectionDataSource(java.util.Collections.singletonList(
                    new ItemFacturaDTO("", comprobante.getDescripcion() != null ? comprobante.getDescripcion() : "",
                        BigDecimal.ONE, "Unidad", comprobante.getImporteNeto(), comprobante.getImporteNeto())));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            if (rutaSalida != null && !rutaSalida.isEmpty()) {
                FileOutputStream fos = new FileOutputStream(rutaSalida);
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
                exporter.exportReport();
                fos.close();
                System.out.println("PDF generado: " + rutaSalida);
                return rutaSalida;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
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
        String titulo = TIPO_TITULO.getOrDefault(comprobante.getTipoComprobante(), "FACTURA");
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           ").append(titulo).append("\n");
        sb.append("========================================\n");
        sb.append("Razon Social: ").append(emitidor.getRazonSocial()).append("\n");
        sb.append("CUIT: ").append(emitidor.getCuit()).append("\n");
        sb.append("Condicion IVA: ").append(emitidor.getCondicionIva()).append("\n");
        sb.append("Punto de Venta: ").append(emitidor.getPuntoVenta()).append("\n");
        sb.append("Comprobante Nro: ").append(String.format("%08d", comprobante.getNumero())).append("\n");
        sb.append("Fecha: ").append(comprobante.getFechaEmision().format(FECHA_DISPLAY)).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Receptor:\n");
        sb.append("CUIT: ").append(comprobante.getCuitReceptor()).append("\n");
        sb.append("Razon Social: ").append(comprobante.getRazonSocialRec()).append("\n");
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

    public byte[] generarPDFBytes(ComprobanteDTO comprobante, CuitConfigDTO emitidor) {
        String base64 = generarFacturaA4(comprobante, emitidor, null, null);
        if (base64 != null) {
            return Base64.getDecoder().decode(base64);
        }
        return null;
    }

    private String totalEnLetras(BigDecimal monto) {
        if (monto == null) return "CERO PESOS";
        long parteEntera = monto.longValue();
        int centavos = monto.remainder(BigDecimal.ONE).multiply(BigDecimal.valueOf(100)).intValue();
        String pesos = numeroEnLetras(parteEntera);
        return "SON " + pesos + " PESOS CON " + String.format("%02d", centavos) + "/100";
    }

    private String numeroEnLetras(long n) {
        if (n == 0) return "CERO";
        String[] unidades = {"", "UN", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE"};
        String[] especiales = {"DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"};
        String[] decenas = {"", "", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"};
        String[] centenas = {"", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"};

        StringBuilder sb = new StringBuilder();

        if (n >= 1000000000) {
            long milesMills = n / 1000000000;
            sb.append(numeroEnLetras(milesMills)).append(" MIL MILLONES ");
            n %= 1000000000;
            if (n > 0) sb.append(" ");
        }
        if (n >= 1000000) {
            long mills = n / 1000000;
            if (mills == 1) sb.append("UN MILLON");
            else sb.append(numeroEnLetras(mills)).append(" MILLONES");
            n %= 1000000;
            if (n > 0) sb.append(" ");
        }
        if (n >= 1000) {
            long mil = n / 1000;
            if (mil == 1) sb.append("MIL");
            else sb.append(numeroEnLetras(mil)).append(" MIL");
            n %= 1000;
            if (n > 0) sb.append(" ");
        }
        if (n >= 100) {
            long c = n / 100;
            if (c == 1 && n % 100 == 0) sb.append("CIEN");
            else sb.append(centenas[(int)c]);
            n %= 100;
            if (n > 0) sb.append(" ");
        }
        if (n >= 20) {
            long d = n / 10;
            sb.append(decenas[(int)d]);
            n %= 10;
            if (n > 0) sb.append(" Y ");
        } else if (n >= 10) {
            sb.append(especiales[(int)(n - 10)]);
            n = 0;
        }
        if (n > 0) {
            sb.append(unidades[(int)n]);
        }
        return sb.toString().trim();
    }
}
