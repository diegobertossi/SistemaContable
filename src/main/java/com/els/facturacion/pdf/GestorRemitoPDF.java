package com.els.facturacion.pdf;

import com.els.facturacion.dao.RemitoPreimpresoConfigDAO;
import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.modelo.RemitoItemDTO;
import com.els.facturacion.modelo.RemitoPreimpresoConfigDTO;
import com.els.facturacion.util.UbicacionSistema;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestorRemitoPDF {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final RemitoPreimpresoConfigDAO configDAO = new RemitoPreimpresoConfigDAO();
    private static final List<String> PREIMPRESO_PREFIXES = Arrays.asList("0002", "0005", "0006", "0007");

    private static final Map<String, String> UBICACION_LABEL = new HashMap<>();
    static {
        UBICACION_LABEL.put("0002", "MDP");
        UBICACION_LABEL.put("0005", "CABA");
        UBICACION_LABEL.put("0006", "BRC");
        UBICACION_LABEL.put("0007", "MDP Avellaneda");
        UBICACION_LABEL.put("1000", "COMUN CABA");
        UBICACION_LABEL.put("2000", "COMUN MDP");
        UBICACION_LABEL.put("3000", "COMUN BRC");
    }

    public String generarPDFRemito(RemitoDTO remito, String codigoUbicacion, int cantBultos) {
        try {
            boolean esPreimpreso = codigoUbicacion != null && PREIMPRESO_PREFIXES.contains(codigoUbicacion.trim());
            String templateName = esPreimpreso ? "RemitoPreImpreso.jasper" : "RemitoComun.jasper";
            String outputDir = determinarDirectorioSalida(templateName);
            Files.createDirectories(Paths.get(outputDir));

            String nroClean = remito.getNumeroRemito().replaceAll("[^0-9-]", "");
            String[] nroParts = nroClean.split("-");
            String nroSecuencia = nroParts.length > 1 ? nroParts[nroParts.length - 1] : nroClean;
            String ubicacionLabel = UBICACION_LABEL.getOrDefault(codigoUbicacion != null ? codigoUbicacion.trim() : "", "DESCONOCIDO");
            String clienteClean = remito.getRazonSocialReceptor() != null
                ? remito.getRazonSocialReceptor().replaceAll("[\\\\/:*?\"<>|]", "").replaceAll("\\s+", "_").trim()
                : "SIN_CLIENTE";
            String filename = nroSecuencia + "-" + ubicacionLabel + " _" + clienteClean + ".pdf";
            String outputPath = outputDir + File.separator + filename;

            InputStream jasperStream = getClass().getClassLoader()
                .getResourceAsStream("reportes/" + templateName);
            if (jasperStream == null) {
                System.err.println("No se encontro " + templateName);
                return null;
            }

            Map<String, Object> params;
            JRDataSource dataSource;

            if (esPreimpreso) {
                params = buildPreimpresoParams(remito, cantBultos, codigoUbicacion);
                List<RemitoItemDTO> items = remito.getItems() != null ? remito.getItems() : java.util.Collections.emptyList();
                dataSource = new JRBeanCollectionDataSource(items);
            } else {
                params = buildComunParams(remito, cantBultos);
                dataSource = new JREmptyDataSource(1);
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperStream, params, dataSource);

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fos);
                exporter.exportReport();
            }

            System.out.println("PDF generado: " + outputPath);
            return outputPath;

        } catch (Exception e) {
            System.err.println("Error generando PDF remito: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String formatDateString(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        if (dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) return dateStr;
        for (DateTimeFormatter fmt : Arrays.asList(FMT_ISO, FMT_COMPACT)) {
            try {
                return LocalDate.parse(dateStr, fmt).format(FMT);
            } catch (DateTimeParseException ignored) {
            }
        }
        return dateStr;
    }

    private Map<String, Object> buildPreimpresoParams(RemitoDTO remito, int cantBultos, String codigoUbicacion) {
        Map<String, Object> p = new HashMap<>();
        p.put("EMISOR_RAZON_SOCIAL", remito.getRazonSocialEmisor() != null ? remito.getRazonSocialEmisor() : "");
        p.put("EMISOR_DOMICILIO", remito.getDomicilioEmisor() != null ? remito.getDomicilioEmisor() : "");
        p.put("EMISOR_CUIT", remito.getCuitEmisor() != null ? remito.getCuitEmisor() : "");
        p.put("EMISOR_ING_BRUTOS", remito.getIngresosBrutosEmisor() != null ? remito.getIngresosBrutosEmisor() : "");
        p.put("EMISOR_INICIO_ACT", formatDateString(remito.getFechaInicioActividadesEmisor()));
        p.put("EMISOR_CONDICION_IVA", remito.getCondicionIvaEmisor() != null ? remito.getCondicionIvaEmisor() : "");
        p.put("PUNTO_VENTA", codigoUbicacion != null ? codigoUbicacion : "");
        p.put("COMP_NRO", remito.getNumeroRemito() != null ? remito.getNumeroRemito() : "");
        p.put("FECHA_EMISION", remito.getFechaEmision() != null ? remito.getFechaEmision().format(FMT) : "");
        p.put("CLIENTE_RAZON_SOCIAL", remito.getRazonSocialReceptor() != null ? remito.getRazonSocialReceptor() : "");
        p.put("CLIENTE_CUIT", remito.getCuitReceptor() != null ? remito.getCuitReceptor() : "");
        p.put("CLIENTE_DOMICILIO", remito.getDomicilioReceptor() != null ? remito.getDomicilioReceptor() : "");
        p.put("CLIENTE_CONDICION_IVA", remito.getCondicionIvaReceptor() != null ? remito.getCondicionIvaReceptor() : "");
        p.put("TRANSPORTISTA_NOMBRE", "");
        p.put("TRANSPORTISTA_DOMICILIO", "");
        p.put("TRANSPORTISTA_CUIT", "");
        p.put("CANTIDAD_BULTOS", String.valueOf(cantBultos));
        p.put("COPIA_LABEL", "ORIGINAL");

        String caiNro = "";
        String caiVenc = "";
        String desdeNro = "";
        String hastaNro = "";
        if (codigoUbicacion != null) {
            try {
                int pv = Integer.parseInt(codigoUbicacion.trim());
                RemitoPreimpresoConfigDTO cfg = configDAO.buscarPorPuntoVenta(pv);
                if (cfg != null) {
                    caiNro = cfg.getCai() != null ? String.valueOf(cfg.getCai()) : "";
                    caiVenc = formatDateString(cfg.getFechaVencimiento());
                    String ubicPadded = String.format("%04d", cfg.getPuntoVenta());
                    desdeNro = ubicPadded + "-" + String.format("%08d", cfg.getDesde());
                    hastaNro = ubicPadded + "-" + String.format("%08d", cfg.getHasta());
                }
            } catch (NumberFormatException e) {
                System.err.println("Error parseando punto de venta: " + codigoUbicacion);
            }
        }
        p.put("CAI_NRO", caiNro);
        p.put("CAI_VENCIMIENTO", caiVenc);
        p.put("DESDE_NRO", desdeNro);
        p.put("HASTA_NRO", hastaNro);

        return p;
    }

    private Map<String, Object> buildComunParams(RemitoDTO remito, int cantBultos) {
        Map<String, Object> p = new HashMap<>();
        p.put("cliente", remito.getRazonSocialReceptor() != null ? remito.getRazonSocialReceptor() : "");
        p.put("remitoConformado", remito.getNumeroRemito() != null ? remito.getNumeroRemito() : "");
        p.put("cantBultos", cantBultos);
        p.put("cuit", remito.getCuitEmisor() != null ? remito.getCuitEmisor() : "");
        p.put("domicilio", remito.getDomicilioEmisor() != null ? remito.getDomicilioEmisor() : "");
        p.put("fecha_Entrada", remito.getFechaEmision() != null ? remito.getFechaEmision().format(FMT) : "");
        String descTexto = remito.getItems() != null
            ? remito.getItems().stream().map(RemitoItemDTO::getDescripcion).collect(Collectors.joining("\n\n"))
            : "";
        p.put("descripcion", descTexto);
        return p;
    }

    private String determinarDirectorioSalida(String templateName) {
        String base;
        if ("BARILOCHE".equals(UbicacionSistema.getUbicacion())) {
            base = "F:\\els\\Bariloche\\Administracion\\Sistema\\Remitos PDF";
        } else {
            base = "F:\\els\\Administracion\\Sistema\\Remitos PDF";
        }
        if (templateName.contains("PreImpreso")) {
            return base + "\\Remitos PreImpresos";
        } else {
            return base + "\\Remitos Comunes";
        }
    }
}
