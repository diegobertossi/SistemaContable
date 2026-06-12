package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionReparsoft;
import com.els.facturacion.modelo.SucursalDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SucursalDAO {

    public List<SucursalDTO> getSucursalesPorCliente(int idCliente, String baseDatos) {
        List<SucursalDTO> lista = new ArrayList<>();
        String sql = "SELECT IdSucursal, NombreSucursal, DomicilioSucursal, ContactoSucursal, "
                   + "TelefonoSucursal, CorreoElectronico "
                   + "FROM " + baseDatos + ".sucursal "
                   + "WHERE idCliente = ? AND NombreSucursal IS NOT NULL AND NombreSucursal != '' "
                   + "ORDER BY NombreSucursal";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return lista;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SucursalDTO dto = new SucursalDTO();
                    dto.setIdSucursal(rs.getInt("IdSucursal"));
                    dto.setIdCliente(idCliente);
                    dto.setNombre(rs.getString("NombreSucursal") != null ? rs.getString("NombreSucursal").trim() : null);
                    dto.setDireccion(rs.getString("DomicilioSucursal") != null ? rs.getString("DomicilioSucursal").trim() : null);
                    dto.setContacto(rs.getString("ContactoSucursal") != null ? rs.getString("ContactoSucursal").trim() : null);
                    dto.setTelefono(rs.getString("TelefonoSucursal") != null ? rs.getString("TelefonoSucursal").trim() : null);
                    dto.setEmail(rs.getString("CorreoElectronico") != null ? rs.getString("CorreoElectronico").trim() : null);
                    lista.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo sucursales para cliente " + idCliente + ": " + e.getMessage());
        }
        return lista;
    }

    private int getNextId(String baseDatos) {
        String sql = "SELECT COALESCE(MAX(IdSucursal), 0) + 1 FROM " + baseDatos + ".sucursal";
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return 1;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo pr\u00f3ximo IdSucursal: " + e.getMessage());
            return 1;
        }
    }

    public int insertar(SucursalDTO suc, String baseDatos) {
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return -1;

        int nextId = getNextId(baseDatos);
        String sql = "INSERT INTO " + baseDatos + ".sucursal "
                   + "(IdSucursal, NombreSucursal, idCliente, DomicilioSucursal, ContactoSucursal, "
                   + "TelefonoSucursal, CorreoElectronico) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nextId);
            ps.setString(2, suc.getNombre());
            ps.setInt(3, suc.getIdCliente());
            ps.setString(4, suc.getDireccion());
            ps.setString(5, suc.getContacto());
            ps.setString(6, suc.getTelefono());
            ps.setString(7, suc.getEmail());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                System.out.println("\u2713 Sucursal insertada con IdSucursal=" + nextId);
                return nextId;
            }
        } catch (SQLException e) {
            System.err.println("Error insertando sucursal: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(SucursalDTO suc, String baseDatos) {
        if (suc.getIdSucursal() == null) return false;
        Connection conn = ConexionReparsoft.getInstancia().getConexion(baseDatos);
        if (conn == null) return false;

        String sql = "UPDATE " + baseDatos + ".sucursal SET "
                   + "NombreSucursal = ?, DomicilioSucursal = ?, ContactoSucursal = ?, "
                   + "TelefonoSucursal = ?, CorreoElectronico = ? "
                   + "WHERE IdSucursal = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, suc.getNombre());
            ps.setString(2, suc.getDireccion());
            ps.setString(3, suc.getContacto());
            ps.setString(4, suc.getTelefono());
            ps.setString(5, suc.getEmail());
            ps.setInt(6, suc.getIdSucursal());

            boolean ok = ps.executeUpdate() > 0;
            if (ok) {
                System.out.println("\u2713 Sucursal actualizada (IdSucursal=" + suc.getIdSucursal() + ")");
            }
            return ok;
        } catch (SQLException e) {
            System.err.println("Error actualizando sucursal: " + e.getMessage());
        }
        return false;
    }
}
