package com.els.facturacion.arca;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.util.Store;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ServicioWSAA {

    private static final String WSAA_URL_HOMO =
        "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";
    private static final String WSAA_URL_PROD =
        "https://wsaa.afip.gov.ar/ws/services/LoginCms";

    private TokenCache tokenCache;
    private String entorno;
    private X509Certificate cert;

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
            return tokenCache.getToken();
        }

        if (tokenCache.cargarDeBD(cuit)) {
            return tokenCache.getToken();
        }

        return solicitarNuevoToken(cuit, rutaP12, passwordP12);
    }

    private String solicitarNuevoToken(String cuit, String rutaP12, String passwordP12) throws Exception {

        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream is = new ByteArrayInputStream(
                java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(rutaP12)))) {
            ks.load(is, passwordP12.toCharArray());
        }

        String alias = ks.aliases().nextElement();

        this.cert = (X509Certificate) ks.getCertificate(alias);
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, passwordP12.toCharArray());

        String source = cert.getSubjectX500Principal().getName();
        String destination = "homo".equals(entorno) ? "CN=wsaahomo, O=AFIP, C=AR, SERIALNUMBER=CUIT 33693450239"
                                                     : "CN=wsaa, O=AFIP, C=AR";

        String tra = tokenCache.generarLoginTicketRequest(source, destination, "wsfe");
        System.out.println("=== TRA ===");
        System.out.println(tra);
        System.out.println("=== END TRA ===");

        String cms = firmarCMS(tra, privateKey);

        String response = enviarWSAA(cms);

        String innerXml = extraerTag(response, "loginCmsReturn");
        if (innerXml == null) {
            throw new Exception("WSAA no devolvió loginCmsReturn: " + response);
        }
        innerXml = innerXml.replace("&lt;", "<").replace("&gt;", ">")
                           .replace("&quot;", "\"").replace("&apos;", "'")
                           .replace("&amp;", "&");

        String token = extraerTag(innerXml, "token");
        String sign  = extraerTag(innerXml, "sign");

        if (token == null) {
            throw new Exception("WSAA no devolvió token: " + innerXml);
        }

        java.time.LocalDateTime exp =
            java.time.LocalDateTime.now().plusHours(12);

        tokenCache.guardarToken(token, sign, exp, cuit);

        return token;
    }

    private String firmarCMS(String tra, PrivateKey pk) throws Exception {

        JcaDigestCalculatorProviderBuilder digest =
            new JcaDigestCalculatorProviderBuilder().setProvider("BC");

        JcaContentSignerBuilder signer =
            new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC");

        java.util.List<X509Certificate> list = new java.util.ArrayList<>();
        list.add(cert);

        Store<X509CertificateHolder> store = new JcaCertStore(list);

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();

        gen.addSignerInfoGenerator(
            new org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder(digest.build())
                .build(signer.build(pk), cert)
        );

        gen.addCertificates(store);

        byte[] data = tra.getBytes(StandardCharsets.UTF_8);

        CMSTypedData cmsData = new CMSProcessableByteArray(data);
        CMSSignedData signed = gen.generate(cmsData, true);

        byte[] encoded = signed.getEncoded();

        // DEBUG: verify CMS signer info and signature locally
        CMSSignedData parsed = new CMSSignedData(encoded);
        org.bouncycastle.util.Store stores = parsed.getCertificates();
        java.util.Collection<?> certs = stores.getMatches(null);
        System.out.println("CMS certs count: " + certs.size());
        for (org.bouncycastle.cms.SignerInformation si : parsed.getSignerInfos().getSigners()) {
            System.out.println("CMS signer digestAlg: " + si.getDigestAlgorithmID().getAlgorithm().getId());
            System.out.println("CMS signer encAlg: " + si.getEncryptionAlgOID());
            byte[] recoveredContent = (byte[]) parsed.getSignedContent().getContent();
            System.out.println("CMS content length: " + recoveredContent.length + " bytes");
            String recoveredStr = new String(recoveredContent, StandardCharsets.UTF_8);
            System.out.println("CMS content matches TRA: " + tra.equals(recoveredStr));
            // Verify signature
            X509CertificateHolder holder = (X509CertificateHolder) certs.iterator().next();
            org.bouncycastle.cms.SignerInformationVerifier verifier =
                new org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder()
                    .setProvider("BC").build(holder);
            boolean verified = si.verify(verifier);
            System.out.println("CMS signature VERIFIED: " + verified);
        }

        return Base64.getEncoder().encodeToString(encoded);
    }

    private String enviarWSAA(String cmsFirmado) throws Exception {

        String xmlRequest =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
          + "xmlns:wsaa=\"http://wsaa.view.sua.dvadac.desein.afip.gov\">"
          + "<soapenv:Header/>"
          + "<soapenv:Body>"
          + "<wsaa:loginCms>"
          + "<wsaa:in0>" + cmsFirmado + "</wsaa:in0>"
          + "</wsaa:loginCms>"
          + "</soapenv:Body>"
          + "</soapenv:Envelope>";

        URL url = new URL(getUrlWSAA());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(xmlRequest.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();

        BufferedReader reader;
        if (responseCode != 200) {
            reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        if (responseCode != 200) {
            throw new Exception("WSAA error " + responseCode + ": " + response);
        }

        return response.toString();
    }

    
    private String escapeXml(String input) {
        if (input == null) return null;

        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;");
    }
    
    private String extraerTag(String xml, String tag) {

        String open = "<" + tag + ">";
        String close = "</" + tag + ">";

        int i = xml.indexOf(open);
        int j = xml.indexOf(close);

        if (i == -1 || j == -1) return null;

        return xml.substring(i + open.length(), j);
    }

    public String getToken() {
        return tokenCache.getToken();
    }

    public String getSign() {
        return tokenCache.getSign();
    }
}