package com.els.facturacion.arca;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ServicioWSAA {

    private static final String WSAA_URL_HOMO = "https://wswsaa.afip.gov.ar/WSAA/LoginCMS";
    private static final String WSAA_URL_PROD = "https://wsaa.afip.gov.ar/WSAA/LoginCMS";

    private TokenCache tokenCache;
    private String entorno;

    public ServicioWSAA() {
        this.tokenCache = TokenCache.getInstancia();
        this.entorno = "homo";
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public void setEntorno(String entorno) {
        this.entorno = entorno;
    }

    public String getUrlWSAA() {
        return "homo".equals(entorno) ? WSAA_URL_HOMO : WSAA_URL_PROD;
    }

    public String obtenerToken(String cuit, String rutaP12, String passwordP12) throws Exception {
        if (tokenCache.tieneTokenValido(cuit)) {
            System.out.println("✓ Token válido en caché para CUIT: " + cuit);
            return tokenCache.getToken();
        }

        if (tokenCache.cargarDeBD(cuit)) {
            System.out.println("✓ Token válido cargado desde BD para CUIT: " + cuit);
            return tokenCache.getToken();
        }

        System.out.println("Solicitando nuevo token para CUIT: " + cuit);
        return solicitarNuevoToken(cuit, rutaP12, passwordP12);
    }

    private String solicitarNuevoToken(String cuid, String rutaP12, String passwordP12) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream is = new ByteArrayInputStream(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(rutaP12)));
        keyStore.load(is, passwordP12.toCharArray());
        is.close();

        String alias = keyStore.aliases().nextElement();
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, passwordP12.toCharArray());

        String sourceId = cert.getSubjectX500Principal().getName();
        String credentials = sourceId;

        String loginTicketRequest = tokenCache.generarLoginTicketRequest(credentials);
        String loginTicketRequestBase64 = Base64.getEncoder().encodeToString(
                loginTicketRequest.getBytes(StandardCharsets.UTF_8));

        String cmsFirmado = firmarCMS(loginTicketRequestBase64, privateKey);

        String loginTicketResponse = enviarWSAA(cmsFirmado);

        String token = extraerToken(loginTicketResponse);
        String sign = credentials;

        if (token != null) {
            java.time.LocalDateTime expiracion = java.time.LocalDateTime.now().plusHours(12);
            tokenCache.guardarToken(token, sign, expiracion, cuid);
            System.out.println("✓ Token almacenado en caché");
            return token;
        }

        throw new Exception("No se pudo obtener token de WSAA: " + loginTicketResponse);
    }

    private String firmarCMS(String datos, PrivateKey privateKey) throws Exception {
        try {
            Class<?> jcaSignerClass = Class.forName("org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder");
            Class<?> jcaDigestClass = Class.forName("org.bouncycastle.cms.jcajce.JcaDigestCalculatorProviderBuilder");
            Class<?> jcaContentClass = Class.forName("org.bouncycastle.operator.jcajce.JcaContentSignerBuilder");

            Object digestBuilder = jcaDigestClass.getConstructor().newInstance();
            Object digestProvider = digestBuilder.getClass().getMethod("build").invoke(null);

            Object contentSignerBuilder = jcaContentClass.getConstructor(String.class).newInstance("SHA1withRSA");
            contentSignerBuilder.getClass().getMethod("setProvider", String.class).invoke(contentSignerBuilder, "BC");
            Object contentSigner = contentSignerBuilder.getClass().getMethod("build", PrivateKey.class).invoke(contentSignerBuilder, privateKey);

            Object signerInfoBuilder = jcaSignerClass.getConstructor(digestProvider.getClass()).newInstance(digestProvider);
            Object signerInfoGenerator = signerInfoBuilder.getClass().getMethod("build", contentSigner.getClass()).invoke(signerInfoBuilder, contentSigner);

            Class<?> generatorClass = Class.forName("org.bouncycastle.cms.CMSSignedDataGenerator");
            Object generator = generatorClass.getConstructor().newInstance();
            generatorClass.getMethod("addSignerInfoGenerator", Class.forName("org.bouncycastle.cms.SignerInfoGenerator")).invoke(generator, signerInfoGenerator);

            Class<?> cmsProcessableClass = Class.forName("org.bouncycastle.cms.CMSProcessableByteArray");
            Object cmsProcessable = cmsProcessableClass.getConstructor(byte[].class).newInstance(datos.getBytes(StandardCharsets.UTF_8));

            Object signedData = generatorClass.getMethod("generate", cmsProcessableClass, boolean.class).invoke(generator, cmsProcessable, true);

            byte[] encoded = (byte[]) signedData.getClass().getMethod("getEncoded").invoke(signedData);
            return Base64.getEncoder().encodeToString(encoded);
        } catch (ClassNotFoundException e) {
            System.err.println("Advertencia: BouncyCastle jcajce no disponible, usando método alternativo");
            return Base64.getEncoder().encodeToString(datos.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String enviarWSAA(String cmsFirmado) throws Exception {
        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
                + "xmlns:wsaa=\"http://wsaa.arba.gob.ar\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<wsaa:loginCMS>"
                + "<in0>" + cmsFirmado + "</in0>"
                + "</wsaa:loginCMS>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";

        URL url = new URL(getUrlWSAA());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(xmlRequest.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Error en WSAA - Código: " + responseCode);
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

    private String extraerToken(String xmlResponse) {
        int start = xmlResponse.indexOf("<return>");
        int end = xmlResponse.indexOf("</return>");
        if (start != -1 && end != -1) {
            return xmlResponse.substring(start + 8, end);
        }
        return null;
    }

    public String getToken() {
        return tokenCache.getToken();
    }

    public String getSign() {
        return tokenCache.getSign();
    }
}