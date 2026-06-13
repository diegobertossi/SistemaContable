package com.els.facturacion.pdf;

import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.modelo.RemitoItemDTO;
import com.els.facturacion.util.UbicacionSistema;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestorRemitoPDF {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final List<String> PREIMPRESO_PREFIXES = Arrays.asList("0002", "0005", "0006", "0007");

    public String generarPDFRemito(RemitoDTO remito, String codigoUbicacion, int cantBultos) {
        try {
            String templateName = determinarTemplate(codigoUbicacion);
            String outputDir = determinarDirectorioSalida(templateName);
            Files.createDirectories(Paths.get(outputDir));

            String fechaStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String nroClean = remito.getNumeroRemito().replaceAll("[^0-9-]", "");
            String filename = "Remito-" + nroClean + "-" + fechaStr + ".pdf";
            String outputPath = outputDir + File.separator + filename;

            Map<String, Object> params = new HashMap<>();
            params.put("cliente", remito.getRazonSocialReceptor() != null ? remito.getRazonSocialReceptor() : "");
            params.put("remitoConformado", remito.getNumeroRemito() != null ? remito.getNumeroRemito() : "");
            params.put("cantBultos", cantBultos);
            params.put("cuit", remito.getCuitEmisor() != null ? remito.getCuitEmisor() : "");
            params.put("domicilio", remito.getDomicilioEmisor() != null ? remito.getDomicilioEmisor() : "");
            params.put("fecha_Entrada", remito.getFechaEmision() != null ? remito.getFechaEmision().format(FMT) : "");

            String descripcionTexto = remito.getItems() != null
                ? remito.getItems().stream().map(RemitoItemDTO::getDescripcion).collect(Collectors.joining("\n\n"))
                : "";
            params.put("descripcion", descripcionTexto);

            InputStream jasperStream = getClass().getClassLoader()
                .getResourceAsStream("reportes/" + templateName);
            if (jasperStream == null) {
                System.err.println("No se encontro " + templateName);
                return null;
            }

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperStream, params, new JREmptyDataSource(1));

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

    private String determinarTemplate(String codigoUbicacion) {
        if (codigoUbicacion != null && PREIMPRESO_PREFIXES.contains(codigoUbicacion.trim())) {
            return "RemitoPreImpreso.jasper";
        }
        return "RemitoComun.jasper";
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
