package com.els.facturacion.email;

import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.pdf.GestorPDF;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Properties;

public class GestorEmail {

    private String host;
    private int port;
    private String usuario;
    private String password;
    private boolean configured;

    public GestorEmail() {
        this.configured = false;
        cargarConfiguracion();
    }

    private void cargarConfiguracion() {
        try {
            Properties prop = new Properties();
            String configPath = "src/main/resources/config/email.properties";

            File configFile = new File(configPath);
            if (configFile.exists()) {
                prop.load(new FileInputStream(configFile));
            } else {
                System.out.println("Archivo de configuración de email no encontrado, usando configuración por defecto");
            }

            this.host = prop.getProperty("smtp.host", "smtp.gmail.com");
            this.port = Integer.parseInt(prop.getProperty("smtp.port", "587"));
            this.usuario = prop.getProperty("smtp.user", "");
            this.password = prop.getProperty("smtp.pass", "");

            this.configured = !usuario.isEmpty() && !password.isEmpty();

            if (configured) {
                System.out.println("✓ GestorEmail configurado con host: " + host + ":" + port);
            } else {
                System.err.println("✗ GestorEmail no configurado - SMTP no disponible");
            }

        } catch (Exception e) {
            System.err.println("Error cargando configuración de email: " + e.getMessage());
        }
    }

    public boolean isConfigured() {
        return configured;
    }

    public boolean enviarFactura(ComprobanteDTO comprobante, String emailDestino, byte[] pdfBytes) {
        if (!configured) {
            System.err.println("Email no configurado");
            return false;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", host);

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(usuario, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(usuario, "FacturaSoft", "UTF-8"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));

            String tipoComp = comprobante.getTipoComprobanteStr();
            message.setSubject("Comprobante Electrónico - " + tipoComp + " Nro " + comprobante.getNumero());

            MimeMultipart multipart = new MimeMultipart();

            String cuerpoHtml = generarCuerpoEmail(comprobante);
            BodyPart cuerpoPart = new MimeBodyPart();
            cuerpoPart.setContent(cuerpoHtml, "text/html; charset=UTF-8");
            multipart.addBodyPart(cuerpoPart);

            if (pdfBytes != null && pdfBytes.length > 0) {
                BodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(pdfBytes, "application/pdf");
                attachmentPart.setDataHandler(new DataHandler(source));

                String nombreArchivo = "factura_" + comprobante.getNumero() + ".pdf";
                attachmentPart.setFileName(nombreArchivo);
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✓ Email enviado a: " + emailDestino);
            return true;

        } catch (MessagingException e) {
            System.err.println("✗ Error enviando email: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("✗ Error inesperado enviando email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean enviarFacturaConAdjunto(ComprobanteDTO comprobante, String emailDestino, String rutaPDF) {
        try {
            byte[] pdfBytes;
            if (rutaPDF != null && new File(rutaPDF).exists()) {
                FileInputStream fis = new FileInputStream(rutaPDF);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                fis.close();
                pdfBytes = baos.toByteArray();
            } else {
                pdfBytes = null;
            }

            return enviarFactura(comprobante, emailDestino, pdfBytes);

        } catch (Exception e) {
            System.err.println("Error leyendo archivo PDF: " + e.getMessage());
            return false;
        }
    }

    private String generarCuerpoEmail(ComprobanteDTO comp) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head><style>");
        html.append("body { font-family: Arial, sans-serif; }");
        html.append(".header { background: #2c3e50; color: white; padding: 20px; }");
        html.append(".content { padding: 20px; }");
        html.append(".footer { background: #ecf0f1; padding: 10px; font-size: 12px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 10px 0; }");
        html.append("th, td { padding: 8px; border: 1px solid #ddd; text-align: left; }");
        html.append("th { background: #3498db; color: white; }");
        html.append(".total { font-weight: bold; font-size: 18px; color: #27ae60; }");
        html.append("</style></head>");
        html.append("<body>");
        html.append("<div class='header'>");
        html.append("<h2>Comprobante Electrónico</h2>");
        html.append("</div>");
        html.append("<div class='content'>");
        html.append("<h3>").append(comp.getTipoComprobanteStr()).append("</h3>");
        html.append("<table>");
        html.append("<tr><th>Número:</th><td>").append(comp.getNumero()).append("</td></tr>");
        html.append("<tr><th>Fecha:</th><td>").append(comp.getFechaEmision()).append("</td></tr>");
        html.append("<tr><th>CUIT Receptor:</th><td>").append(comp.getCuitReceptor()).append("</td></tr>");
        html.append("<tr><th>Razón Social:</th><td>").append(comp.getRazonSocialRec()).append("</td></tr>");
        html.append("<tr><th>Importe Total:</th><td class='total'>$").append(comp.getImporteTotal()).append("</td></tr>");
        html.append("</table>");
        html.append("<p><strong>CAE:</strong> ").append(comp.getCae()).append("</p>");
        html.append("<p><strong>Vencimiento CAE:</strong> ").append(comp.getVencimientoCae()).append("</p>");
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>Este es un comprobante electrónico válido según normativa ARCA.</p>");
        html.append("<p>FacturaSoft v1.0 - Sistema de Facturación</p>");
        html.append("</div>");
        html.append("</body></html>");
        return html.toString();
    }

    public boolean probarConexion() {
        if (!configured) {
            System.err.println("Email no configurado");
            return false;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(usuario, password);
                }
            });

            Transport transport = session.getTransport("smtp");
            transport.connect(host, usuario, password);
            transport.close();
            System.out.println("✓ Conexión SMTP exitosa");
            return true;

        } catch (Exception e) {
            System.err.println("✗ Error probando conexión SMTP: " + e.getMessage());
            return false;
        }
    }

    public void setConfiguracion(String host, int port, String usuario, String password) {
        this.host = host;
        this.port = port;
        this.usuario = usuario;
        this.password = password;
        this.configured = !usuario.isEmpty() && !password.isEmpty();
    }
}