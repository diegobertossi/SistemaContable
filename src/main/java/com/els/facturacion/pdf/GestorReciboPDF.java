package com.els.facturacion.pdf;

import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.ReciboDTO;
import com.els.facturacion.modelo.ReciboPagoDTO;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.JRExporterParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorReciboPDF {

    private static final DateTimeFormatter FECHA_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String DIRECTORIO_FACTURAS = "F:\\Trabajo\\Monotributo\\Facturas";

    public static String getRutaPDF(String numeroRecibo) {
        try {
            com.els.facturacion.dao.CuitDAO cuitDAO = new com.els.facturacion.dao.CuitDAO();
            List<com.els.facturacion.modelo.CuitConfigDTO> activos = cuitDAO.listarActivos();
            if (activos.isEmpty()) return null;
            String cuit = activos.get(0).getCuit();
            String nombreArchivo = cuit + "_RECIBO_"
                + numeroRecibo.replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";
            return DIRECTORIO_FACTURAS + File.separator + nombreArchivo;
        } catch (Exception e) {
            System.err.println("Error resolviendo ruta PDF recibo: " + e.getMessage());
            return null;
        }
    }

    public String generarRecibo(ReciboDTO recibo, ComprobanteDTO comprobante) {
        try {
            CuitDAO cuitDAO = new CuitDAO();
            List<CuitConfigDTO> activos = cuitDAO.listarActivos();
            if (activos.isEmpty()) {
                System.err.println("No hay CUIT activo configurado para emitir el recibo");
                return null;
            }
            CuitConfigDTO emisor = activos.get(0);

            String nombreArchivo = emisor.getCuit() + "_RECIBO_"
                + recibo.getNumeroRecibo().replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";
            String rutaSalida = DIRECTORIO_FACTURAS + File.separator + nombreArchivo;

            String ruta = generarReciboPDF(recibo, emisor, comprobante, rutaSalida);
            return ruta;
        } catch (Exception e) {
            System.err.println("Error generando PDF de recibo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String generarReciboPDF(ReciboDTO recibo, CuitConfigDTO emisor,
                                     ComprobanteDTO comprobante, String rutaSalida) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("RECIBO_NUMERO", recibo.getNumeroRecibo());
            parametros.put("FECHA_EMISION", recibo.getFechaCobro() != null
                ? recibo.getFechaCobro().format(FECHA_DISPLAY) : "");
            parametros.put("EMISOR_CUIT", emisor.getCuit());
            parametros.put("EMISOR_RAZON_SOCIAL", emisor.getRazonSocial());
            parametros.put("EMISOR_CONDICION_IVA", emisor.getCondicionIva());
            parametros.put("PAGADOR_CUIT", recibo.getCuitCliente() != null ? recibo.getCuitCliente() : "");
            parametros.put("PAGADOR_RAZON_SOCIAL", recibo.getRazonSocialCliente() != null ? recibo.getRazonSocialCliente() : "");
            parametros.put("MONTO_TOTAL", recibo.getMontoTotal() != null
                ? recibo.getMontoTotal().setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            parametros.put("MONTO_LETRAS", totalEnLetras(recibo.getMontoTotal() != null ? recibo.getMontoTotal() : BigDecimal.ZERO));
            parametros.put("FORMAS_PAGO_TEXTO", formatFormasPago(recibo.getFormasPago()));
            parametros.put("OBSERVACIONES", recibo.getObservaciones() != null ? recibo.getObservaciones() : "");

            JasperReport jasperReport;
            InputStream jrxmlStream = getClass().getClassLoader().getResourceAsStream("reportes/recibo.jrxml");
            if (jrxmlStream == null) {
                jrxmlStream = new FileInputStream("src/main/resources/reportes/recibo.jrxml");
            }
            try {
                jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            } finally {
                jrxmlStream.close();
            }

            List<?> facturas = recibo.getFacturas() != null ? recibo.getFacturas() : java.util.Collections.emptyList();
            JRDataSource dataSource = new JRBeanCollectionDataSource(facturas);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            FileOutputStream fos = new FileOutputStream(rutaSalida);
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
            exporter.exportReport();
            fos.close();

            System.out.println("Recibo PDF generado: " + rutaSalida);
            return rutaSalida;

        } catch (Exception e) {
            System.err.println("Error generando PDF recibo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String formatFormasPago(List<ReciboPagoDTO> formasPago) {
        if (formasPago == null || formasPago.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (ReciboPagoDTO fp : formasPago) {
            if (sb.length() > 0) sb.append("\n");
            String monto = fp.getMonto() != null
                ? String.format("%,.2f", fp.getMonto().setScale(2, RoundingMode.HALF_UP))
                : "0,00";
            sb.append(fp.getFormaPago()).append(": $ ").append(monto);
        }
        return sb.toString();
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
