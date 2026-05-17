package com.els.facturacion.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionFacturacion {

    private static ConexionFacturacion instancia;
    private Connection conexion;

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DB = "facturacion_db";
    private static final String USER = "root";
    private static final String PASS = "root";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB
            + "?useUnicode=true&characterEncoding=UTF-8"
            + "&connectionCollation=utf8mb4_unicode_ci"
            + "&serverTimezone=UTC&useSSL=false"
            + "&allowPublicKeyRetrieval=true";

    private ConexionFacturacion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✓ Conexión a facturacion_db establecida");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Error conectando a facturacion_db: " + e.getMessage());
        }
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
        return conexion;
    }

    public void cerrar() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("✓ Conexión facturacion_db cerrada");
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