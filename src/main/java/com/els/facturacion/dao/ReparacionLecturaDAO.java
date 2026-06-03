package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionReparsoft;
import com.els.facturacion.util.UbicacionSistema;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReparacionLecturaDAO {

    public Map<String, Object> buscarOrdenPorELS(int els, String baseDatos) {
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) {
            System.err.println("No hay conexi�n a base de datos: " + baseDatos);
            return null;
        }

        String sql = "SELECT r.*, e.IdEquipo, e.Nombre as equipo_nombre, e.Modelo, e.Marca, "
                + "e.NumeroDeSerie, e.idCliente, "
                + "c.nombre as cliente_nombre, c.CUIT, c.Domicilio, "
                + "c.TelefonoEmpresa, c.CorreoElectronico "
                + "FROM reparaciones r "
                + "LEFT JOIN equipos e ON r.idEquipo = e.IdEquipo "
                + "LEFT JOIN cliente c ON e.idCliente = c.idCliente "
                + "WHERE r.ELS = ? LIMIT 1";

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

    public Map<String, Object> buscarOrdenPorELS_Actual(int els) {
        return buscarOrdenPorELS(els, UbicacionSistema.getNombreDbReparsoft());
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

        try { orden.put("els", rs.getInt("ELS")); } catch (Exception e) {}
        try { orden.put("fecha", rs.getDate("FechaEntrada")); } catch (Exception e) {}
        try { orden.put("cliente", rs.getString("cliente_nombre")); } catch (Exception e) {}
        try { orden.put("domicilio", rs.getString("Domicilio")); } catch (Exception e) {}
        try { orden.put("telefono", rs.getString("TelefonoEmpresa")); } catch (Exception e) {}
        try { orden.put("email", rs.getString("CorreoElectronico")); } catch (Exception e) {}
        try { orden.put("vehiculo", rs.getString("equipo_nombre")); } catch (Exception e) {}
        try { orden.put("patente", rs.getString("NumeroDeSerie")); } catch (Exception e) {}
        try { orden.put("modelo", rs.getString("Modelo")); } catch (Exception e) {}
        try { orden.put("marca", rs.getString("Marca")); } catch (Exception e) {}
        try { orden.put("falla", rs.getString("Falla")); } catch (Exception e) {}
        try { orden.put("solucion", rs.getString("Solucion")); } catch (Exception e) {}
        try { orden.put("cuit", rs.getString("CUIT")); } catch (Exception e) {}
        try { orden.put("total", rs.getDouble("PrecioPeso")); } catch (Exception e) {}
        try { orden.put("iva", rs.getString("iva")); } catch (Exception e) {}
        try { orden.put("estado", rs.getString("EstadoComercial")); } catch (Exception e) {}
        try { orden.put("observaciones", rs.getString("Informecliente")); } catch (Exception e) {}
        try { orden.put("idEquipo", rs.getInt("idEquipo")); } catch (Exception e) {}
        try { orden.put("idCliente", rs.getInt("idCliente")); } catch (Exception e) {}

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
        String falla = (String) orden.get("falla");
        Object totalObj = orden.get("total");

        datos.put("razonSocial", razonSocial != null ? razonSocial.trim() : "");
        datos.put("domicilio", domicilio != null ? domicilio.trim() : "");
        datos.put("telefono", telefono != null ? telefono.trim() : "");
        datos.put("vehiculo", vehiculo != null ? vehiculo.trim() : "");
        datos.put("patente", patente != null ? patente.trim() : "");
        datos.put("modelo", modelo != null ? modelo.trim() : "");

        try {
            String cuit = (String) orden.get("cuit");
            if (cuit != null) {
                datos.put("cuit", cuit.replaceAll("[^0-9]", ""));
            } else {
                datos.put("cuit", "");
            }
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
        String descripcion = "Servicio de reparaci�n";
        if (vehiculo != null && !vehiculo.trim().isEmpty()) {
            descripcion += " - Equipo: " + vehiculo;
        }
        if (falla != null && !falla.trim().isEmpty()) {
            descripcion += " - Falla: " + falla;
        }
        datos.put("descripcion", descripcion);

        return datos;
    }

    public List<com.els.facturacion.modelo.RemitoReparsoftDTO> listarRemitosActual() {
        return listarRemitos(UbicacionSistema.getNombreDbReparsoft());
    }

    public List<com.els.facturacion.modelo.RemitoReparsoftDTO> listarRemitos(String baseDatos) {
        List<com.els.facturacion.modelo.RemitoReparsoftDTO> lista = new ArrayList<>();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return lista;

        String sql = "SELECT r.idRemito, r.NumeroRemitoSalida, r.IdUbicacion, "
                + "rep.ELS, rep.RemitoCliente, rep.idEquipo, "
                + "e.Nombre as equipo_nombre, e.NumeroDeSerie, e.Modelo, e.Marca, "
                + "c.nombre as cliente_nombre, c.CUIT, "
                + "rep.Falla, rep.PrecioPeso "
                + "FROM " + baseDatos + ".remitos r "
                + "JOIN " + baseDatos + ".reparaciones rep ON r.idRemito = rep.idRemito "
                + "LEFT JOIN " + baseDatos + ".equipos e ON rep.idEquipo = e.IdEquipo "
                + "LEFT JOIN " + baseDatos + ".cliente c ON e.idCliente = c.idCliente "
                + "WHERE r.NumeroRemitoSalida IS NOT NULL "
                + "ORDER BY r.NumeroRemitoSalida DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            java.util.Map<Integer, com.els.facturacion.modelo.RemitoReparsoftDTO> map = new java.util.LinkedHashMap<>();

            while (rs.next()) {
                int idRemito = rs.getInt("idRemito");
                Integer numSalida = rs.getObject("NumeroRemitoSalida") != null ? rs.getInt("NumeroRemitoSalida") : null;
                int idUbicacion = 0;
                try { idUbicacion = rs.getInt("IdUbicacion"); if (rs.wasNull()) idUbicacion = 0; } catch (Exception e) {}
                String clienteNombre = "";
                String cuit = "";
                try { clienteNombre = rs.getString("cliente_nombre"); } catch (Exception e) {}
                try { cuit = rs.getString("CUIT"); } catch (Exception e) {}

                com.els.facturacion.modelo.RemitoReparsoftDTO dto = map.get(idRemito);
                if (dto == null) {
                    dto = new com.els.facturacion.modelo.RemitoReparsoftDTO(idRemito, numSalida, clienteNombre, cuit, idUbicacion);
                    dto.setItems(new ArrayList<>());
                    map.put(idRemito, dto);
                }

                int els = 0;
                String equipoNombre = "";
                String serie = "";
                String falla = "";
                String modelo = "";
                String marca = "";
                double precio = 0;
                boolean facturado = false;
                try { els = rs.getInt("ELS"); } catch (Exception e) {}
                try { equipoNombre = rs.getString("equipo_nombre"); } catch (Exception e) {}
                try { serie = rs.getString("NumeroDeSerie"); } catch (Exception e) {}
                try { falla = rs.getString("Falla"); } catch (Exception e) {}
                try { modelo = rs.getString("Modelo"); } catch (Exception e) {}
                try { marca = rs.getString("Marca"); } catch (Exception e) {}
                try { precio = rs.getDouble("PrecioPeso"); } catch (Exception e) {}
                facturado = false;

                dto.getItems().add(new com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem(
                    els, equipoNombre, serie, falla, modelo, marca, java.math.BigDecimal.valueOf(precio), facturado));
            }

            lista.addAll(map.values());
        } catch (SQLException e) {
            System.err.println("Error listando remitos desde " + baseDatos + ": " + e.getMessage());
        }
        return lista;
    }

    public List<com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem> listarEquiposPorCliente(String baseDatos, String nombreCliente) {
        List<com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem> lista = new ArrayList<>();
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return lista;

        String sql = "SELECT rep.ELS, e.Nombre as equipo_nombre, e.NumeroDeSerie, e.Modelo, e.Marca, "
                + "rep.Falla, rep.PrecioPeso, "
                + "r.IdUbicacion, r.NumeroRemitoSalida "
                + "FROM " + baseDatos + ".reparaciones rep "
                + "JOIN " + baseDatos + ".equipos e ON rep.idEquipo = e.IdEquipo "
                + "JOIN " + baseDatos + ".cliente c ON e.idCliente = c.idCliente "
                + "LEFT JOIN " + baseDatos + ".remitos r ON rep.idRemito = r.idRemito "
                + "WHERE c.nombre = ? AND rep.PrecioPeso > 0 "
                + "ORDER BY rep.ELS DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreCliente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int els = 0;
                String equipoNombre = "";
                String serie = "";
                String falla = "";
                String modelo = "";
                String marca = "";
                double precio = 0;
                String numeroRemito = "";
                try { els = rs.getInt("ELS"); } catch (Exception e) {}
                try { equipoNombre = rs.getString("equipo_nombre"); } catch (Exception e) {}
                try { serie = rs.getString("NumeroDeSerie"); } catch (Exception e) {}
                try { falla = rs.getString("Falla"); } catch (Exception e) {}
                try { modelo = rs.getString("Modelo"); } catch (Exception e) {}
                try { marca = rs.getString("Marca"); } catch (Exception e) {}
                try { precio = rs.getDouble("PrecioPeso"); } catch (Exception e) {}
                try {
                    int idUbicacion = rs.getInt("IdUbicacion");
                    if (!rs.wasNull()) {
                        int nroSalida = rs.getInt("NumeroRemitoSalida");
                        if (!rs.wasNull()) {
                            int displayUbicacion = idUbicacionADisplay(idUbicacion);
                            numeroRemito = String.format("%04d-%08d", displayUbicacion, nroSalida);
                        }
                    }
                } catch (Exception e) {}

                lista.add(new com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem(
                    els, equipoNombre, serie, falla, modelo, marca, java.math.BigDecimal.valueOf(precio), false, numeroRemito));
            }
        } catch (SQLException e) {
            System.err.println("Error listando equipos por cliente desde " + baseDatos + ": " + e.getMessage());
        }
        return lista;
    }

    private static int idUbicacionADisplay(int idUbicacion) {
        switch (idUbicacion) {
            case 1: return 5;
            case 2: return 2;
            case 3: return 1000;
            case 4: return 2000;
            case 5: return 3000;
            case 7: return 6;
            case 8: return 7;
            default: return idUbicacion;
        }
    }
}
