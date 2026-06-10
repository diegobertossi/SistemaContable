package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionReparsoft;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RemitoReparsoftLecturaDAO {

    public List<String> listarClientes(String baseDatos) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT c.nombre FROM " + baseDatos + ".cliente c "
                   + "WHERE c.nombre IS NOT NULL AND c.nombre != '' "
                   + "ORDER BY c.nombre";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return lista;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(rs.getString("nombre").trim());
        } catch (SQLException e) {
            System.err.println("Error listando clientes desde " + baseDatos + ": " + e.getMessage());
        }
        return lista;
    }

    public Map<Integer, String> listarSucursalesPorCliente(String baseDatos, int idCliente) {
        Map<Integer, String> mapa = new LinkedHashMap<>();
        String sql = "SELECT s.IdSucursal, s.NombreSucursal FROM " + baseDatos + ".sucursal s "
                   + "WHERE s.idCliente = ? AND s.NombreSucursal IS NOT NULL "
                   + "ORDER BY s.NombreSucursal";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return mapa;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mapa.put(rs.getInt("IdSucursal"), rs.getString("NombreSucursal").trim());
            }
        } catch (SQLException e) {
            System.err.println("Error listando sucursales desde " + baseDatos + ": " + e.getMessage());
        }
        return mapa;
    }

    public List<Map<String, Object>> listarEquiposParaRemito(String baseDatos, int idCliente) {
        return listarEquiposParaRemito(baseDatos, idCliente, -1);
    }

    public List<Map<String, Object>> listarEquiposParaRemito(String baseDatos, int idCliente, int idSucursal) {
        List<Map<String, Object>> lista = new ArrayList<>();
        boolean filtrarSucursal = idSucursal > 0;
        String sql = "SELECT r.ELS, e.Nombre AS equipo_nombre, e.Marca, e.Modelo, "
                   + "e.NumeroDeSerie, e.Aviso, r.EstadoTecnico, r.EstadoComercial "
                   + "FROM " + baseDatos + ".reparaciones r "
                   + "JOIN " + baseDatos + ".equipos e ON r.idEquipo = e.IdEquipo "
                   + "WHERE e.idCliente = ? "
                   + (filtrarSucursal ? "AND e.idSucursal = ? " : "")
                   + "AND LOWER(r.EstadoFisico) != 'enviado' "
                   + "AND (r.RemitoGenerado IS NULL OR r.RemitoGenerado != 1) "
                   + "ORDER BY r.ELS DESC";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return lista;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            if (filtrarSucursal) ps.setInt(2, idSucursal);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("els", rs.getInt("ELS"));
                item.put("equipo", rs.getString("equipo_nombre") != null ? rs.getString("equipo_nombre").trim() : "");
                item.put("marca", rs.getString("Marca") != null ? rs.getString("Marca").trim() : "");
                item.put("modelo", rs.getString("Modelo") != null ? rs.getString("Modelo").trim() : "");
                item.put("serie", rs.getString("NumeroDeSerie") != null ? rs.getString("NumeroDeSerie").trim() : "");
                item.put("aviso", rs.getString("Aviso") != null ? rs.getString("Aviso").trim() : "");
                item.put("estadoTec", rs.getString("EstadoTecnico") != null ? rs.getString("EstadoTecnico").trim() : "");
                item.put("estadoCom", rs.getString("EstadoComercial") != null ? rs.getString("EstadoComercial").trim() : "");
                lista.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error listando equipos para remito desde " + baseDatos + ": " + e.getMessage());
        }
        return lista;
    }

    public List<String> listarUbicaciones(String baseDatos) {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT Codigo, Ubicacion FROM " + baseDatos + ".UbicacionRemitos ORDER BY Codigo";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return lista;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                if (rs.getString("Codigo") != null) {
                    int codigo = rs.getInt("Codigo");
                    String label;
                    if (codigo == 2 || codigo == 5 || codigo == 6 || codigo == 7) {
                        label = "000" + codigo + " - " + rs.getString("Ubicacion");
                    } else {
                        label = codigo + " - " + rs.getString("Ubicacion");
                    }
                    lista.add(label);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error listando ubicaciones desde " + baseDatos + ": " + e.getMessage());
        }
        return lista;
    }

    public int obtenerNumeroRemito(String baseDatos, int codigoUbicacion) {
        String sql = "SELECT COALESCE(MAX(r.NumeroRemitoSalida), 0) + 1 AS proximo "
                   + "FROM " + baseDatos + ".remitos r "
                   + "JOIN " + baseDatos + ".UbicacionRemitos u ON r.IdUbicacion = u.IdUbicacion "
                   + "WHERE u.Codigo = ?";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return 1;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigoUbicacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("proximo");
        } catch (SQLException e) {
            System.err.println("Error obteniendo numero remito: " + e.getMessage());
        }
        return 1;
    }

    public int idClienteporNombre(String baseDatos, String nombre) {
        String sql = "SELECT idCliente FROM " + baseDatos + ".cliente WHERE nombre = ? LIMIT 1";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return -1;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("idCliente");
        } catch (SQLException e) {
            System.err.println("Error buscando idCliente: " + e.getMessage());
        }
        return -1;
    }

    public int idSucursalporNombre(String baseDatos, String nombre, int idCliente) {
        String sql = "SELECT IdSucursal FROM " + baseDatos + ".sucursal WHERE NombreSucursal = ? AND idCliente = ? LIMIT 1";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return -1;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idCliente);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("IdSucursal");
        } catch (SQLException e) {
            System.err.println("Error buscando idSucursal: " + e.getMessage());
        }
        return -1;
    }
}
