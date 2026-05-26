package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.GastoDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GastoDAO {

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(GastoDTO gasto) {
        String sql = "INSERT INTO gastos (fecha, categoria_id, descripcion, monto, mes, anio) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, java.sql.Date.valueOf(gasto.getFecha()));
            ps.setInt(2, gasto.getCategoriaId());
            ps.setString(3, gasto.getDescripcion());
            ps.setBigDecimal(4, gasto.getMonto());
            ps.setInt(5, gasto.getMes());
            ps.setInt(6, gasto.getAnio());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando gasto: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(GastoDTO gasto) {
        String sql = "UPDATE gastos SET fecha = ?, categoria_id = ?, descripcion = ?, "
                + "monto = ?, mes = ?, anio = ? WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(gasto.getFecha()));
            ps.setInt(2, gasto.getCategoriaId());
            ps.setString(3, gasto.getDescripcion());
            ps.setBigDecimal(4, gasto.getMonto());
            ps.setInt(5, gasto.getMes());
            ps.setInt(6, gasto.getAnio());
            ps.setInt(7, gasto.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando gasto: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM gastos WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando gasto: " + e.getMessage());
        }
        return false;
    }

    public GastoDTO buscarPorId(int id) {
        String sql = "SELECT g.*, c.nombre as categoria_nombre FROM gastos g "
                + "LEFT JOIN categorias_gastos c ON g.categoria_id = c.id WHERE g.id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando gasto: " + e.getMessage());
        }
        return null;
    }

    public List<GastoDTO> listarTodos() {
        String sql = "SELECT g.*, c.nombre as categoria_nombre FROM gastos g "
                + "LEFT JOIN categorias_gastos c ON g.categoria_id = c.id ORDER BY g.fecha DESC";
        List<GastoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando gastos: " + e.getMessage());
        }
        return lista;
    }

    public List<GastoDTO> buscarPorMes(int mes, int anio) {
        String sql = "SELECT g.*, c.nombre as categoria_nombre FROM gastos g "
                + "LEFT JOIN categorias_gastos c ON g.categoria_id = c.id "
                + "WHERE g.mes = ? AND g.anio = ? ORDER BY g.fecha DESC";
        List<GastoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, anio);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error buscando gastos por mes: " + e.getMessage());
        }
        return lista;
    }

    public List<GastoDTO> buscarPorAnio(int anio) {
        String sql = "SELECT g.*, c.nombre as categoria_nombre FROM gastos g "
                + "LEFT JOIN categorias_gastos c ON g.categoria_id = c.id "
                + "WHERE g.anio = ? ORDER BY g.fecha DESC";
        List<GastoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, anio);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error buscando gastos por año: " + e.getMessage());
        }
        return lista;
    }

    public java.math.BigDecimal getTotalGastosMes(int mes, int anio) {
        String sql = "SELECT COALESCE(SUM(monto), 0) as total FROM gastos WHERE mes = ? AND anio = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, anio);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculando total de gastos: " + e.getMessage());
        }
        return java.math.BigDecimal.ZERO;
    }

    private GastoDTO mapearResultado(ResultSet rs) throws SQLException {
        GastoDTO dto = new GastoDTO();
        dto.setId(rs.getInt("id"));

        java.sql.Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            dto.setFecha(fecha.toLocalDate());
        }

        dto.setCategoriaId(rs.getInt("categoria_id"));
        dto.setCategoriaNombre(rs.getString("categoria_nombre"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setMonto(rs.getBigDecimal("monto"));
        dto.setMes(rs.getInt("mes"));
        dto.setAnio(rs.getInt("anio"));
        return dto;
    }
}