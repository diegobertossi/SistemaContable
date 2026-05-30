package com.els.facturacion.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.els.facturacion.util.UbicacionSistema;

public class ConexionFacturacion {

    private static ConexionFacturacion instancia;
    private Connection conexion;
    private String dbConectada;

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String USER = "root";
    private static final String PASS = "root";

    private ConexionFacturacion() {
    }

    public static ConexionFacturacion getInstancia() {
        if (instancia == null) {
            synchronized (ConexionFacturacion.class) {
                if (instancia == null) {
                    instancia = new ConexionFacturacion();
                }
            }
        }
        return instancia;
    }

    public Connection getConexion() {
        String dbActual = UbicacionSistema.getNombreDbFacturacion();
        try {
            if (conexion == null || conexion.isClosed() || !dbActual.equals(dbConectada)) {
                conectar(dbActual);
            }
        } catch (SQLException e) {
            System.err.println("Error verificando conexión: " + e.getMessage());
            conectar(dbActual);
        }
        return conexion;
    }

    private void conectar(String db) {
        try {
            String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + db
                    + "?useUnicode=true&characterEncoding=UTF-8"
                    + "&connectionCollation=utf8mb4_unicode_ci"
                    + "&serverTimezone=UTC&useSSL=false"
                    + "&allowPublicKeyRetrieval=true";
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
            conexion = DriverManager.getConnection(url, USER, PASS);
            dbConectada = db;
            System.out.println("✓ Conexión a " + db + " establecida");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Error conectando a " + db + ": " + e.getMessage());
        }
    }

    public void cerrar() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("✓ Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("✗ Error cerrando conexión: " + e.getMessage());
            }
        }
    }

    public boolean isConnected() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}