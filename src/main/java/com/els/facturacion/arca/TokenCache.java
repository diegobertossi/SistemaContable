package com.els.facturacion.arca;

import com.els.facturacion.conexion.ConexionFacturacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TokenCache {

    private static final String LOGIN_TICKET_REQUEST = "<loginTicketRequest><header><service>wsfe</service><generationTime>%s</generationTime><expirationTime>%s</expirationTime></header><credentials>%s</credentials></loginTicketRequest>";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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
        return expiracion.isAfter(ahora.plusMinutes(10));
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
                expiracion = LocalDateTime.parse(expStr, FORMATO_FECHA);
                ultimoCuit = cuit;

                LocalDateTime ahora = LocalDateTime.now();
                if (expiracion.isAfter(ahora.plusMinutes(10))) {
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

    public String generarLoginTicketRequest(String credentials) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime generationTime = ahora.minusMinutes(5);
        LocalDateTime expirationTime = ahora.plusHours(12);

        return String.format(LOGIN_TICKET_REQUEST,
                generationTime.format(FORMATO_FECHA),
                expirationTime.format(FORMATO_FECHA),
                credentials);
    }
}