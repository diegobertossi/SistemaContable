package com.els.facturacion.arca;

import com.els.facturacion.conexion.ConexionFacturacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TokenCache {

    private static final String LOGIN_TICKET_REQUEST = "<loginTicketRequest version=\"1.0\">"
            + "<header>"
            + "<source>%s</source>"
            + "<destination>%s</destination>"
            + "<uniqueId>%s</uniqueId>"
            + "<generationTime>%s</generationTime>"
            + "<expirationTime>%s</expirationTime>"
            + "</header>"
            + "<service>%s</service>"
            + "</loginTicketRequest>";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter FORMATO_FECHA_ARCA = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private String token;
    private String sign;
    private LocalDateTime expiracion;
    private String ultimoCuit;

    private static TokenCache instancia;

    private TokenCache() {
    }

    public static TokenCache getInstancia() {
        if (instancia == null) {
            synchronized (TokenCache.class) {
                if (instancia == null) {
                    instancia = new TokenCache();
                }
            }
        }
        return instancia;
    }

    public boolean tieneTokenValido(String cuit) {
        if (token == null || sign == null || expiracion == null) {
            return false;
        }
        if (!cuit.equals(ultimoCuit)) {
            return false;
        }
        LocalDateTime ahora = LocalDateTime.now();
        return expiracion.isAfter(ahora.plusMinutes(1));
    }

    public void guardarToken(String token, String sign, LocalDateTime expiracion, String cuit) {
        this.token = token;
        this.sign = sign;
        this.expiracion = expiracion;
        this.ultimoCuit = cuit;
        guardarEnBD(token, sign, expiracion, cuit);
    }

    public String getToken() {
        return token;
    }

    public String getSign() {
        return sign;
    }

    public LocalDateTime getExpiracion() {
        return expiracion;
    }

    private void guardarEnBD(String token, String sign, LocalDateTime expiracion, String cuit) {
        Connection conn = ConexionFacturacion.getInstancia().getConexion();
        String sql = "INSERT INTO token_cache (cuit, token, sign, expiracion) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE token = VALUES(token), sign = VALUES(sign), expiracion = VALUES(expiracion)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cuit);
            ps.setString(2, token);
            ps.setString(3, sign);
            ps.setString(4, expiracion.format(FORMATO_FECHA));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error guardando token en BD: " + e.getMessage());
        }
    }

    public boolean cargarDeBD(String cuit) {
        Connection conn = ConexionFacturacion.getInstancia().getConexion();
        String sql = "SELECT token, sign, expiracion FROM token_cache WHERE cuit = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cuit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                token = rs.getString("token");
                sign = rs.getString("sign");
                String expStr = rs.getString("expiracion");
                expiracion = LocalDateTime.parse(expStr.replace(" ", "T"), FORMATO_FECHA);
                ultimoCuit = cuit;

                LocalDateTime ahora = LocalDateTime.now();
                if (expiracion.isAfter(ahora.plusMinutes(1))) {
                    System.out.println("✓ Token cargado desde BD válido hasta: " + expiracion);
                    return true;
                } else {
                    System.out.println("Token en BD expirado, se renovará");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error cargando token desde BD: " + e.getMessage());
        }
        return false;
    }

    public String generarLoginTicketRequest(String source, String destination, String service) {

        int uniqueId = (int)(System.currentTimeMillis() % 1_000_000_000);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime genTime = now.minusMinutes(5);
        OffsetDateTime expTime = now.plusMinutes(10);

        return String.format(LOGIN_TICKET_REQUEST,
                source,
                destination,
                uniqueId,
                genTime.format(FORMATO_FECHA_ARCA),
                expTime.format(FORMATO_FECHA_ARCA),
                service);
    }
}