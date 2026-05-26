package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.CategoriaGastoDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaGastoDAO {

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(CategoriaGastoDTO categoria) {
        String sql = "INSERT INTO categorias_gastos (nombre, descripcion, activa) VALUES (?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setBoolean(3, categoria.getActiva() != null ? categoria.getActiva() : true);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando categoría de gasto: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(CategoriaGastoDTO categoria) {
        String sql = "UPDATE categorias_gastos SET nombre = ?, descripcion = ?, activa = ? WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setBoolean(3, categoria.getActiva());
            ps.setInt(4, categoria.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando categoría de gasto: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM categorias_gastos WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando categoría de gasto: " + e.getMessage());
        }
        return false;
    }

    public CategoriaGastoDTO buscarPorId(int id) {
        String sql = "SELECT * FROM categorias_gastos WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando categoría de gasto: " + e.getMessage());
        }
        return null;
    }

    public List<CategoriaGastoDTO> listarTodos() {
        String sql = "SELECT * FROM categorias_gastos ORDER BY nombre";
        List<CategoriaGastoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando categorías de gastos: " + e.getMessage());
        }
        return lista;
    }

    public List<CategoriaGastoDTO> listarActivas() {
        String sql = "SELECT * FROM categorias_gastos WHERE activa = TRUE ORDER BY nombre";
        List<CategoriaGastoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando categorías activas: " + e.getMessage());
        }
        return lista;
    }

    private CategoriaGastoDTO mapearResultado(ResultSet rs) throws SQLException {
        CategoriaGastoDTO dto = new CategoriaGastoDTO();
        dto.setId(rs.getInt("id"));
        dto.setNombre(rs.getString("nombre"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setActiva(rs.getBoolean("activa"));
        return dto;
    }
}