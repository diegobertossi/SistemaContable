package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionReparsoft;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReparacionLecturaDAO {

    public Map<String, Object> buscarOrdenPorELS(int els, String baseDatos) {
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) {
            System.err.println("No hay conexión a base de datos: " + baseDatos);
            return null;
        }

        String sql = "SELECT * FROM reparaciones WHERE els = ? LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, els);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearOrden(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando orden ELS " + els + ": " + e.getMessage());
        }
        return null;
    }

    public Map<String, Object> buscarOrdenPorELS_BRC(int els) {
        return buscarOrdenPorELS(els, "ordenesbrc");
    }

    public Map<String, Object> buscarOrdenPorELS_BSAS(int els) {
        return buscarOrdenPorELS(els, "ordenesbsas");
    }

    public Map<String, Object> buscarOrdenEnAmbas(int els) {
        Map<String, Object> orden = buscarOrdenPorELS_BRC(els);
        if (orden == null) {
            orden = buscarOrdenPorELS_BSAS(els);
        }
        return orden;
    }

    public boolean verificarELS(int els) {
        return buscarOrdenEnAmbas(els) != null;
    }

    public String getBaseDatosELS(int els) {
        if (buscarOrdenPorELS_BRC(els) != null) {
            return "ordenesbrc";
        }
        if (buscarOrdenPorELS_BSAS(els) != null) {
            return "ordenesbsas";
        }
        return null;
    }

    private Map<String, Object> mapearOrden(ResultSet rs) throws SQLException {
        Map<String, Object> orden = new HashMap<>();

        try {
            orden.put("els", rs.getInt("els"));
            orden.put("fecha", rs.getDate("fecha"));
            orden.put("cliente", rs.getString("cliente"));
            orden.put("domicilio", rs.getString("domicilio"));
            orden.put("telefono", rs.getString("telefono"));
            orden.put("vehiculo", rs.getString("vehiculo"));
            orden.put("patente", rs.getString("patente"));
            orden.put("modelo", rs.getString("modelo"));
            orden.put("kilometros", rs.getInt("kilometros"));
            orden.put("motor", rs.getString("motor"));
            orden.put("chasis", rs.getString("chasis"));

            try { orden.put("cuit", rs.getString("cuit")); } catch (Exception e) {}
            try { orden.put("iva", rs.getString("iva")); } catch (Exception e) {}
            try { orden.put("total", rs.getDouble("total")); } catch (Exception e) {}

            orden.put("estado", rs.getString("estado"));
            orden.put("observaciones", rs.getString("observaciones"));

        } catch (SQLException e) {
            System.err.println("Error mapeando orden: " + e.getMessage());
        }

        return orden;
    }

    public Map<String, Object> getDatosParaFacturacion(int els) {
        Map<String, Object> orden = buscarOrdenEnAmbas(els);
        if (orden == null) {
            return null;
        }

        Map<String, Object> datos = new HashMap<>();

        String razonSocial = (String) orden.get("cliente");
        String domicilio = (String) orden.get("domicilio");
        String telefono = (String) orden.get("telefono");
        String vehiculo = (String) orden.get("vehiculo");
        String patente = (String) orden.get("patente");
        String modelo = (String) orden.get("modelo");
        Object totalObj = orden.get("total");

        datos.put("razonSocial", razonSocial != null ? razonSocial.trim() : "");
        datos.put("domicilio", domicilio != null ? domicilio.trim() : "");
        datos.put("telefono", telefono != null ? telefono.trim() : "");
        datos.put("vehiculo", vehiculo != null ? vehiculo.trim() : "");
        datos.put("patente", patente != null ? patente.trim() : "");
        datos.put("modelo", modelo != null ? modelo.trim() : "");

        try {
            datos.put("cuit", orden.get("cuit"));
        } catch (Exception e) {
            datos.put("cuit", "");
        }

        try {
            if (totalObj != null) {
                datos.put("importeTotal", ((Number) totalObj).doubleValue());
            }
        } catch (Exception e) {
            datos.put("importeTotal", 0.0);
        }

        datos.put("els", els);
        datos.put("descripcion", String.format("Servicio de reparación - Vehículo: %s - Patente: %s",
                vehiculo != null ? vehiculo : "",
                patente != null ? patente : ""));

        return datos;
    }
}