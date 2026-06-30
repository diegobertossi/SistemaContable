package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.RemitoPreimpresoConfigDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RemitoPreimpresoConfigDAO {

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(RemitoPreimpresoConfigDTO dto) {
        String sql = "INSERT INTO remito_preimpreso_config "
                + "(punto_venta, cai, fecha_vencimiento, desde, hasta) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, dto.getPuntoVenta());
            ps.setLong(2, dto.getCai());
            ps.setString(3, dto.getFechaVencimiento());
            ps.setInt(4, dto.getDesde());
            ps.setInt(5, dto.getHasta());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando remito_preimpreso_config: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(RemitoPreimpresoConfigDTO dto) {
        String sql = "UPDATE remito_preimpreso_config SET punto_venta = ?, cai = ?, "
                + "fecha_vencimiento = ?, desde = ?, hasta = ? WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, dto.getPuntoVenta());
            ps.setLong(2, dto.getCai());
            ps.setString(3, dto.getFechaVencimiento());
            ps.setInt(4, dto.getDesde());
            ps.setInt(5, dto.getHasta());
            ps.setInt(6, dto.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando remito_preimpreso_config: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM remito_preimpreso_config WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando remito_preimpreso_config: " + e.getMessage());
        }
        return false;
    }

    public RemitoPreimpresoConfigDTO buscarPorId(int id) {
        String sql = "SELECT * FROM remito_preimpreso_config WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando remito_preimpreso_config por ID: " + e.getMessage());
        }
        return null;
    }

    public List<RemitoPreimpresoConfigDTO> listarTodos() {
        String sql = "SELECT * FROM remito_preimpreso_config ORDER BY punto_venta";
        List<RemitoPreimpresoConfigDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando remito_preimpreso_config: " + e.getMessage());
        }
        return lista;
    }

    public RemitoPreimpresoConfigDTO buscarPorPuntoVenta(int puntoVenta) {
        String sql = "SELECT * FROM remito_preimpreso_config WHERE punto_venta = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, puntoVenta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando por punto_venta: " + e.getMessage());
        }
        return null;
    }

    private RemitoPreimpresoConfigDTO mapearResultado(ResultSet rs) throws SQLException {
        RemitoPreimpresoConfigDTO dto = new RemitoPreimpresoConfigDTO();
        dto.setId(rs.getInt("id"));
        dto.setPuntoVenta(rs.getInt("punto_venta"));
        dto.setCai(rs.getLong("cai"));
        dto.setFechaVencimiento(rs.getString("fecha_vencimiento"));
        dto.setDesde(rs.getInt("desde"));
        dto.setHasta(rs.getInt("hasta"));
        return dto;
    }
}
