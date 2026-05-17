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

    private static final String WSFE_URL_HOMO = "https://wswhomo.afip.gov.ar/WSFE/Factura";
    private static final String WSFE_URL_PROD = "https://wsfe.afip.gov.ar/WSFE/Factura";

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        String xmlResponse = enviarWSFE(xmlRequest);

        return parsearRespuestaFECA(xmlResponse);
    }

    private String construirFECAESolicitar(ComprobanteDTO comp, String token, String sign) {
        String fecha = comp.getFechaEmision().format(FORMATO_FECHA);
        String importeNeto = comp.getImporteNeto().toPlainString();
        String importeIva = comp.getImporteIva() != null ? comp.getImporteIva().toPlainString() : "0";
        String importeTotal = comp.getImporteTotal().toPlainString();

        String cmp = "<Concepto>1</Concepto>"
                + "<Moneda>PES</Moneda>"
                + "<Ctz>1</Ctz>"
                + "<DocTipo>80</DocTipo>"
                + "<DocNro>" + comp.getCuitReceptor() + "</DocNro>"
                + "<PtoVta>" + comp.getPuntoVenta() + "</PtoVta>"
                + "<CbteTipo>" + comp.getTipoComprobante() + "</CbteTipo>"
                + "<CbteFch>" + fecha + "</CbteFch>"
                + "<ImpTotal>" + importeTotal + "</ImpTotal>"
                + "<ImpNeto>" + importeNeto + "</ImpNeto>"
                + "<ImpIVA>" + importeIva + "</ImpIVA>"
                + "<MonId>1</MonId>";

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FEArGEtCAERequest xmlns=\"http://ar.gov.afip.dif.fex.v1\">"
                + "<Auth>"
                + "<Token>" + token + "</Token>"
                + "<Sign>" + sign + "</Sign>"
                + "<Cuit>" + comp.getCuitEmisor() + "</Cuit>"
                + "</Auth>"
                + "<FeCAEReq>"
                + "<FedHeader>"
                + "<CantReg>1</CantReg>"
                + "<PtoVta>" + comp.getPuntoVenta() + "</PtoVta>"
                + "<CbteTipo>" + comp.getTipoComprobante() + "</CbteTipo>"
                + "</FedHeader>"
                + "<FeDetRequest>"
                + "<FECAEDetRequest>"
                + cmp
                + "</FECAEDetRequest>"
                + "</FeDetRequest>"
                + "</FeCAEReq>"
                + "</FEArGEtCAERequest>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        return xml;
    }

    private String enviarWSFE(String xmlRequest) throws Exception {
        URL url = new URL(getUrlWSFE());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
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

        String mensajeError = extraerValor(xmlResponse, "<Msg>", "</Msg>");
        if (mensajeError == null) {
            mensajeError = extraerValor(xmlResponse, "<Errors>", "</Errors>");
        }
        respuesta.setError(mensajeError != null ? mensajeError : "Error desconocido en ARCA");
        System.err.println("✗ Error ARCA: " + respuesta.getMensaje());
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

    public String consultarComprobante(String cuit, int puntoVenta, int tipoComprobante,
                                       long numero, String token, String sign) throws Exception {

        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FECompConsultar xmlns=\"http://ar.gov.afip.dif.fex.v1\">"
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

        return enviarWSFE(xmlRequest);
    }
}