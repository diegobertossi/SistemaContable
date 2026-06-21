package com.els.facturacion.arca;

import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.RespuestaCAE;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.net.ssl.HttpsURLConnection;

public class ServicioWSFEv1 {

    private static final String WSFE_URL_HOMO = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
    private static final String WSFE_URL_PROD = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd");

    private ServicioWSAA servicioWSAA;
    private String entorno;

    public ServicioWSFEv1() {
        this.servicioWSAA = new ServicioWSAA();
        this.entorno = "homo";
    }

    public void setEntorno(String entorno) {
        this.entorno = entorno;
        this.servicioWSAA.setEntorno(entorno);
    }

    public String getUrlWSFE() {
        return "homo".equals(entorno) ? WSFE_URL_HOMO : WSFE_URL_PROD;
    }

    public RespuestaCAE emitirComprobante(ComprobanteDTO comprobante, String rutaP12, String passwordP12)
            throws Exception {

        String token = servicioWSAA.obtenerToken(comprobante.getCuitEmisor(), rutaP12, passwordP12);
        String sign = servicioWSAA.getSign();

        String xmlRequest = construirFECAESolicitar(comprobante, token, sign);
        String xmlResponse = enviarWSFE(xmlRequest, "http://ar.gov.afip.dif.FEV1/FECAESolicitar");

        System.out.println("=== FECAESolicitar RESPONSE ===");
        System.out.println(xmlResponse);
        System.out.println("=== END RESPONSE ===");

        return parsearRespuestaFECA(xmlResponse);
    }

    private int mapDocTipo(ComprobanteDTO comp) {
        String tipoDoc = comp.getTipoDocumento();
        String condIva = comp.getCondicionIvaReceptor();
        if ("Consumidor Final".equals(condIva)) {
            return 99;
        }
        if (tipoDoc == null || tipoDoc.isEmpty() || "CUIT".equals(tipoDoc)) {
            return 80;
        }
        if ("DNI".equals(tipoDoc)) {
            return 96;
        }
        return 80;
    }

    private String getDocNro(ComprobanteDTO comp, int docTipo) {
        if (docTipo == 99) {
            return "0";
        }
        String nroDoc = comp.getNroDocumento();
        if (nroDoc != null && !nroDoc.isEmpty()) {
            return nroDoc;
        }
        String cuit = comp.getCuitReceptor();
        return cuit != null && !cuit.isEmpty() ? cuit : "0";
    }

    private String mapCondicionIva(String condicion) {
        if (condicion == null || condicion.isEmpty()) return null;
        switch (condicion) {
            case "IVA Responsable Inscripto": return "1";
            case "IVA Sujeto Exento":        return "4";
            case "Consumidor Final":         return "5";
            case "Responsable Monotributo":  return "6";
            case "Proveedor del Exterior":   return "8";
            case "Cliente del Exterior":     return "9";
            case "IVA Liberado - Ley 19.640": return "10";
            case "Monotributista Social":    return "13";
            case "IVA No Alcanzado":         return "15";
            default:                         return null;
        }
    }

    private String construirFECAESolicitar(ComprobanteDTO comp, String token, String sign) {
        String fecha = comp.getFechaEmision().format(FORMATO_FECHA);
        String importeNeto = comp.getImporteNeto().toPlainString();
        String importeIva = comp.getImporteIva() != null ? comp.getImporteIva().toPlainString() : "0";
        String importeTotal = comp.getImporteTotal().toPlainString();
        int docTipo = mapDocTipo(comp);
        String docNro = getDocNro(comp, docTipo);
        String condIva = mapCondicionIva(comp.getCondicionIvaReceptor());

        StringBuilder det = new StringBuilder();
        det.append("<Concepto>1</Concepto>")
           .append("<DocTipo>").append(docTipo).append("</DocTipo>")
           .append("<DocNro>").append(docNro).append("</DocNro>")
           .append("<CbteDesde>").append(comp.getNumero()).append("</CbteDesde>")
           .append("<CbteHasta>").append(comp.getNumero()).append("</CbteHasta>")
           .append("<CbteFch>").append(fecha).append("</CbteFch>")
           .append("<ImpTotal>").append(importeTotal).append("</ImpTotal>")
           .append("<ImpTotConc>0</ImpTotConc>")
           .append("<ImpNeto>").append(importeNeto).append("</ImpNeto>")
           .append("<ImpOpEx>0</ImpOpEx>")
           .append("<ImpTrib>0</ImpTrib>")
           .append("<ImpIVA>").append(importeIva).append("</ImpIVA>")
           .append("<MonId>PES</MonId>")
           .append("<MonCotiz>1</MonCotiz>");
        if (condIva != null) {
            det.append("<CondicionIVAReceptorId>").append(condIva).append("</CondicionIVAReceptorId>");
        }

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FECAESolicitar xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + comp.getCuitEmisor() + "</Cuit>"
                + "</Auth>"
                + "<FeCAEReq>"
                + "<FeCabReq>"
                + "<CantReg>1</CantReg>"
                + "<PtoVta>" + comp.getPuntoVenta() + "</PtoVta>"
                + "<CbteTipo>" + comp.getTipoComprobante() + "</CbteTipo>"
                + "</FeCabReq>"
                + "<FeDetReq>"
                + "<FECAEDetRequest>"
                + det.toString()
                + "</FECAEDetRequest>"
                + "</FeDetReq>"
                + "</FeCAEReq>"
                + "</FECAESolicitar>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        System.out.println("=== FECAESolicitar REQUEST ===");
        System.out.println(xml);
        System.out.println("=== END REQUEST ===");

        return xml;
    }

    private String enviarWSFE(String xmlRequest, String soapAction) throws Exception {
        URL url = new URL(getUrlWSFE());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", soapAction);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(xmlRequest.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Error en WSFEv1 - Código: " + responseCode);
        }

        java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }

    private RespuestaCAE parsearRespuestaFECA(String xmlResponse) {
        RespuestaCAE respuesta = new RespuestaCAE();

        if (xmlResponse.contains("CAE")) {
            String cae = extraerValor(xmlResponse, "<CAEFmt>", "</CAEFmt>");
            if (cae == null) {
                cae = extraerValor(xmlResponse, "<CAE>", "</CAE>");
            }

            String fechaVto = extraerValor(xmlResponse, "<CAEFchVto>", "</CAEFchVto>");
            String numero = extraerValor(xmlResponse, "<CbteNum>", "</CbteNum>");

            if (cae != null && !cae.isEmpty()) {
                respuesta.setCae(cae);
                respuesta.setExitosa(true);

                if (fechaVto != null) {
                    respuesta.setVencimiento(LocalDate.parse(fechaVto, FORMATO_FECHA));
                }
                if (numero != null) {
                    respuesta.setNumeroComprobante(Long.parseLong(numero));
                }

                System.out.println("✓ Comprobante emitido con CAE: " + cae);
                return respuesta;
            }
        }

        String codigoError = extraerValor(xmlResponse, "<Code>", "</Code>");
        if (codigoError != null) {
            respuesta.setCodigoError(codigoError);
        }

        String mensajeError = extraerValor(xmlResponse, "<Msg>", "</Msg>");
        if (mensajeError == null) {
            mensajeError = extraerValor(xmlResponse, "<Errors>", "</Errors>");
        }
        respuesta.setError(mensajeError != null ? mensajeError : "Error desconocido en ARCA");
        System.err.println("✗ Error ARCA: [" + (codigoError != null ? codigoError : "?") + "] " + respuesta.getMensaje());
        return respuesta;
    }

    private String extraerValor(String xml, String tagInicio, String tagFin) {
        int start = xml.indexOf(tagInicio);
        int end = xml.indexOf(tagFin);
        if (start != -1 && end != -1 && start < end) {
            return xml.substring(start + tagInicio.length(), end).trim();
        }
        return null;
    }

    public long consultarUltimoAutorizado(String cuitEmisor, int puntoVenta, int tipoComprobante,
                                          String rutaP12, String passwordP12) throws Exception {
        String token = servicioWSAA.obtenerToken(cuitEmisor, rutaP12, passwordP12);
        String sign = servicioWSAA.getSign();

        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FECompUltimoAutorizado xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuitEmisor + "</Cuit>"
                + "</Auth>"
                + "<FeCompUltimoAutorizadoReq>"
                + "<PtoVta>" + puntoVenta + "</PtoVta>"
                + "<CbteTipo>" + tipoComprobante + "</CbteTipo>"
                + "</FeCompUltimoAutorizadoReq>"
                + "</FECompUltimoAutorizado>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        System.out.println("=== FECompUltimoAutorizado REQUEST ===");
        System.out.println(xmlRequest);
        System.out.println("=== END REQUEST ===");

        String xmlResponse = enviarWSFE(xmlRequest, "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado");

        System.out.println("=== FECompUltimoAutorizado RESPONSE ===");
        System.out.println(xmlResponse);
        System.out.println("=== END RESPONSE ===");

        String errors = extraerValor(xmlResponse, "<Errors>", "</Errors>");
        if (errors != null && !errors.isEmpty() && errors.contains("<Err>")) {
            System.out.println("FECompUltimoAutorizado: ARCA devolvió errores, ignorando CbteNro");
            return 0;
        }

        String nroStr = extraerValor(xmlResponse, "<CbteNro>", "</CbteNro>");
        System.out.println("CbteNro extraído: '" + nroStr + "'");
        if (nroStr != null) {
            long nro = Long.parseLong(nroStr);
            System.out.println("FECompUltimoAutorizado return: " + nro);
            return nro;
        }
        System.out.println("FECompUltimoAutorizado: no se encontró CbteNro, retornando 0");
        return 0;
    }

    public String consultarComprobante(String cuit, int puntoVenta, int tipoComprobante,
                                       long numero, String token, String sign) throws Exception {

        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FECompConsultar xmlns=\"http://ar.gov.afip.dif.FEV1/\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + cuit + "</Cuit>"
                + "</Auth>"
                + "<FeCompConsReq>"
                + "<PtoVta>" + puntoVenta + "</PtoVta>"
                + "<CbteTipo>" + tipoComprobante + "</CbteTipo>"
                + "<CbteNro>" + numero + "</CbteNro>"
                + "</FeCompConsReq>"
                + "</FECompConsultar>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        return enviarWSFE(xmlRequest, "http://ar.gov.afip.dif.FEV1/FECompConsultar");
    }
}