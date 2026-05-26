package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.CajaMovimientoDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CajaMovimientoDAO {

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(CajaMovimientoDTO movimiento) {
        String sql = "INSERT INTO caja_movimientos (fecha, tipo, descripcion, monto, cuit_asociado, comprobante_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            if (movimiento.getFecha() != null) {
                ps.setDate(1, java.sql.Date.valueOf(movimiento.getFecha()));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }
            ps.setString(2, movimiento.getTipo());
            ps.setString(3, movimiento.getDescripcion());
            ps.setBigDecimal(4, movimiento.getMonto());
            ps.setString(5, movimiento.getCuitAsociado());
            ps.setInt(6, movimiento.getComprobanteId() != null ? movimiento.getComprobanteId() : 0);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando movimiento de caja: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(CajaMovimientoDTO movimiento) {
        String sql = "UPDATE caja_movimientos SET fecha = ?, tipo = ?, descripcion = ?, "
                + "monto = ?, cuit_asociado = ?, comprobante_id = ? WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(movimiento.getFecha()));
            ps.setString(2, movimiento.getTipo());
            ps.setString(3, movimiento.getDescripcion());
            ps.setBigDecimal(4, movimiento.getMonto());
            ps.setString(5, movimiento.getCuitAsociado());
            ps.setInt(6, movimiento.getComprobanteId() != null ? movimiento.getComprobanteId() : 0);
            ps.setInt(7, movimiento.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando movimiento de caja: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM caja_movimientos WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando movimiento de caja: " + e.getMessage());
        }
        return false;
    }

    public CajaMovimientoDTO buscarPorId(int id) {
        String sql = "SELECT * FROM caja_movimientos WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando movimiento de caja: " + e.getMessage());
        }
        return null;
    }

    public List<CajaMovimientoDTO> listarTodos() {
        String sql = "SELECT * FROM caja_movimientos ORDER BY fecha DESC, id DESC";
        List<CajaMovimientoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando movimientos de caja: " + e.getMessage());
        }
        return lista;
    }

    public List<CajaMovimientoDTO> buscarPorFecha(LocalDate desde, LocalDate hasta) {
        String sql = "SELECT * FROM caja_movimientos WHERE (fecha BETWEEN ? AND ?) OR fecha IS NULL ORDER BY fecha DESC, id DESC";
        List<CajaMovimientoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(desde));
            ps.setDate(2, java.sql.Date.valueOf(hasta));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error buscando movimientos por fecha: " + e.getMessage());
        }
        return lista;
    }

    public List<CajaMovimientoDTO> buscarPorTipo(String tipo) {
        String sql = "SELECT * FROM caja_movimientos WHERE tipo = ? ORDER BY fecha DESC";
        List<CajaMovimientoDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error buscando movimientos por tipo: " + e.getMessage());
        }
        return lista;
    }

    public java.math.BigDecimal getSaldoCaja(LocalDate hasta) {
        String sql = "SELECT "
                + "(SELECT COALESCE(SUM(monto), 0) FROM caja_movimientos WHERE tipo = 'cobro' AND fecha <= ?) - "
                + "(SELECT COALESCE(SUM(monto), 0) FROM caja_movimientos WHERE tipo = 'pago' AND fecha <= ?) "
                + "AS saldo";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(hasta));
            ps.setDate(2, java.sql.Date.valueOf(hasta));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("saldo");
            }
        } catch (SQLException e) {
            System.err.println("Error calculando saldo de caja: " + e.getMessage());
        }
        return java.math.BigDecimal.ZERO;
    }

    private CajaMovimientoDTO mapearResultado(ResultSet rs) throws SQLException {
        CajaMovimientoDTO dto = new CajaMovimientoDTO();
        dto.setId(rs.getInt("id"));

        java.sql.Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            dto.setFecha(fecha.toLocalDate());
        }

        dto.setTipo(rs.getString("tipo"));
        dto.setDescripcion(rs.getString("descripcion"));
        dto.setMonto(rs.getBigDecimal("monto"));
        dto.setCuitAsociado(rs.getString("cuit_asociado"));

        int compId = rs.getInt("comprobante_id");
        dto.setComprobanteId(compId > 0 ? compId : null);

        return dto;
    }
}