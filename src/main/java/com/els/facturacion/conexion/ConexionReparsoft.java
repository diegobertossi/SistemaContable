package com.els.facturacion.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConexionReparsoft {

    private static ConexionReparsoft instancia;
    private Map<String, Connection> conexiones;
    private final String HOST = "localhost";
    private final String PORT = "3306";
    private final String USER = "root";
    private final String PASS = "root";

    private ConexionReparsoft() {
        conexiones = new HashMap<>();
    }

    public static ConexionReparsoft getInstancia() {
        if (instancia == null) {
            synchronized (ConexionReparsoft.class) {
                if (instancia == null) {
                    instancia = new ConexionReparsoft();
                }
            }
        }
        return instancia;
    }

    public Connection getConexion(String baseDatos) {
        if (conexiones.containsKey(baseDatos) && isConnected(conexiones.get(baseDatos))) {
            return conexiones.get(baseDatos);
        }

        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + baseDatos
                + "?useUnicode=true&characterEncoding=UTF-8"
                + "&connectionCollation=utf8mb4_unicode_ci"
                + "&serverTimezone=UTC&useSSL=false"
                + "&allowPublicKeyRetrieval=true";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, USER, PASS);
            conexiones.put(baseDatos, conn);
            System.out.println("✓ Conexión a " + baseDatos + " establecida");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Error conectando a " + baseDatos + ": " + e.getMessage());
        }
        return null;
    }

    public Connection getOrdenesBRC() {
        return getConexion("ordenesbrc");
    }

    public Connection getOrdenesBSAS() {
        return getConexion("ordenesbsas");
    }

    private boolean isConnected(Connection conn) {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void cerrarTodo() {
        for (Connection conn : conexiones.values()) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("✗ Error cerrando conexión: " + e.getMessage());
                }
            }
        }
        conexiones.clear();
        System.out.println("✓ Todas las conexiones a ReparSoft cerradas");
    }
}