package com.els.facturacion.arca;

import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.RespuestaCAE;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ServicioWSFEv1 {

    private static final Logger LOG = Logger.getLogger(ServicioWSFEv1.class.getName());
    private static final String WSFE_URL_HOMO = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
    private static final String WSFE_URL_PROD = "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
    private static final String NS_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String NS_FE = "http://ar.gov.afip.dif.FEV1/";

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int TIMEOUT_CONNECT_MS = 15000;
    private static final int TIMEOUT_READ_MS = 30000;

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

        String cuitEmisor = comprobante.getCuitEmisor();
        String token = servicioWSAA.obtenerToken(cuitEmisor, rutaP12, passwordP12);
        String sign = servicioWSAA.getSign(cuitEmisor);

        String xmlRequest = construirFECAESolicitar(comprobante, token, sign);
        String xmlResponse = enviarWSFE(xmlRequest, "http://ar.gov.afip.dif.FEV1/FECAESolicitar");

        LOG.log(Level.FINE, "FECAESolicitar RESPONSE:\n{0}", xmlResponse);

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

        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"" + NS_SOAP + "\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FECAESolicitar xmlns=\"" + NS_FE + "\">"
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
    }

    private String enviarWSFE(String xmlRequest, String soapAction) throws Exception {
        URL url = new URL(getUrlWSFE());
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", soapAction);
        conn.setDoOutput(true);
        conn.setConnectTimeout(TIMEOUT_CONNECT_MS);
        conn.setReadTimeout(TIMEOUT_READ_MS);

        try {
            try (OutputStream os = conn.getOutputStream()) {
                os.write(xmlRequest.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                LOG.log(Level.WARNING, "WSFEv1 HTTP {0}", responseCode);
                throw new Exception("Error en ARCA - HTTP " + responseCode);
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
        } catch (SocketTimeoutException e) {
            LOG.log(Level.SEVERE, "Timeout de conexion a ARCA (read timeout {0}ms)", TIMEOUT_READ_MS);
            throw new Exception("ARCA no responde (timeout). Verifique su conexion a internet.", e);
        } catch (UnknownHostException e) {
            LOG.log(Level.SEVERE, "Host ARCA no encontrado: {0}", getUrlWSFE());
            throw new Exception("Sin conexion a internet. No se pudo contactar ARCA.", e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error de E/S hacia ARCA: {0}", e.getMessage());
            throw new Exception("Error de conexion con ARCA: " + e.getMessage(), e);
        }
    }

    private RespuestaCAE parsearRespuestaFECA(String xmlResponse) {
        RespuestaCAE respuesta = new RespuestaCAE();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
            XPath xpath = XPathFactory.newInstance().newXPath();

            String cae = evaluarXPath(doc, xpath, "//*[local-name()='CAE']");
            if (cae == null) {
                cae = evaluarXPath(doc, xpath, "//*[local-name()='CAEFmt']");
            }

            if (cae != null && !cae.isEmpty()) {
                respuesta.setCae(cae);
                respuesta.setExitosa(true);

                String fechaVto = evaluarXPath(doc, xpath, "//*[local-name()='CAEFchVto']");
                if (fechaVto != null) {
                    try {
                        respuesta.setVencimiento(LocalDate.parse(fechaVto, FORMATO_FECHA));
                    } catch (Exception e) {
                        LOG.log(Level.FINE, "No se pudo parsear CAEFchVto: {0}", fechaVto);
                    }
                }

                String numero = evaluarXPath(doc, xpath, "//*[local-name()='CbteNro']");
                String numeroDesde = evaluarXPath(doc, xpath, "//*[local-name()='CbteDesde']");
                if (numero != null) {
                    try {
                        respuesta.setNumeroComprobante(Long.parseLong(numero));
                    } catch (Exception e) {
                        LOG.log(Level.FINE, "No se pudo parsear CbteNro: {0}", numero);
                    }
                } else if (numeroDesde != null) {
                    try {
                        respuesta.setNumeroComprobante(Long.parseLong(numeroDesde));
                    } catch (Exception e) {
                        LOG.log(Level.FINE, "No se pudo parsear CbteDesde: {0}", numeroDesde);
                    }
                }

                LOG.log(Level.INFO, "Comprobante emitido con CAE: {0}", cae);
                return respuesta;
            }

            String codigoError = evaluarXPath(doc, xpath, "//*[local-name()='Code']");
            if (codigoError != null) {
                respuesta.setCodigoError(codigoError);
            }

            NodeList msgNodes = (NodeList) xpath.evaluate("//*[local-name()='Msg']", doc, XPathConstants.NODESET);
            StringBuilder mensajeError = new StringBuilder();
            if (msgNodes != null) {
                for (int i = 0; i < msgNodes.getLength(); i++) {
                    if (mensajeError.length() > 0) mensajeError.append("; ");
                    mensajeError.append(msgNodes.item(i).getTextContent().trim());
                }
            }
            if (mensajeError.length() == 0) {
                String errors = evaluarXPath(doc, xpath, "//*[local-name()='Errors']");
                if (errors != null) {
                    mensajeError.append(errors);
                }
            }
            respuesta.setError(mensajeError.length() > 0
                ? mensajeError.toString() : "Error desconocido en ARCA");
            LOG.log(Level.WARNING, "Error ARCA: [{0}] {1}",
                new Object[]{codigoError != null ? codigoError : "?", respuesta.getMensaje()});
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error parseando respuesta ARCA", e);
            respuesta.setError("Error interno parseando respuesta ARCA: " + e.getMessage());
        }
        return respuesta;
    }

    private String evaluarXPath(Document doc, XPath xpath, String expression) {
        try {
            String val = xpath.evaluate(expression, doc);
            if (val == null || val.trim().isEmpty()) return null;
            return val;
        } catch (Exception e) {
            LOG.log(Level.FINE, "XPath evaluacion fallo: {0}", expression);
            return null;
        }
    }

    public long consultarUltimoAutorizado(String cuitEmisor, int puntoVenta, int tipoComprobante,
                                          String rutaP12, String passwordP12) throws Exception {
        String token = servicioWSAA.obtenerToken(cuitEmisor, rutaP12, passwordP12);
        String sign = servicioWSAA.getSign(cuitEmisor);

        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"" + NS_SOAP + "\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FECompUltimoAutorizado xmlns=\"" + NS_FE + "\">"
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

        LOG.log(Level.FINE, "FECompUltimoAutorizado REQUEST para CUIT {0}", cuitEmisor);

        String xmlResponse = enviarWSFE(xmlRequest, "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado");

        LOG.log(Level.FINE, "FECompUltimoAutorizado RESPONSE:\n{0}", xmlResponse);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));
            XPath xpath = XPathFactory.newInstance().newXPath();

            String errors = evaluarXPath(doc, xpath, "//*[local-name()='Errors']");
            if (errors != null && !errors.isEmpty()) {
                LOG.log(Level.FINE, "FECompUltimoAutorizado: ARCA devolvio errores, ignorando CbteNro");
                return 0;
            }

            String nroStr = evaluarXPath(doc, xpath, "//*[local-name()='CbteNro']");
            LOG.log(Level.FINE, "CbteNro extraido: ''{0}''", nroStr);
            if (nroStr != null) {
                long nro = Long.parseLong(nroStr);
                LOG.log(Level.INFO, "FECompUltimoAutorizado return: {0}", nro);
                return nro;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Error parseando FECompUltimoAutorizado, retornando 0", e);
        }
        LOG.info("FECompUltimoAutorizado: no se encontro CbteNro, retornando 0");
        return 0;
    }

    public String consultarComprobante(String cuit, int puntoVenta, int tipoComprobante,
                                       long numero, String token, String sign) throws Exception {

        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"" + NS_SOAP + "\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<FECompConsultar xmlns=\"" + NS_FE + "\">"
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
