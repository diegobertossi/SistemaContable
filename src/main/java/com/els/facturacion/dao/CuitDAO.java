package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.modelo.CuitConfigDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CuitDAO {

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(CuitConfigDTO cuit) {
        String sql = "INSERT INTO cuit_certificados (cuit, razon_social, condicion_iva, "
                + "punto_venta, ruta_certificado, password_cert, activo, "
                + "domicilio, ingresos_brutos, fecha_inicio_actividades) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cuit.getCuit());
            ps.setString(2, cuit.getRazonSocial());
            ps.setString(3, cuit.getCondicionIva());
            ps.setInt(4, cuit.getPuntoVenta());
            ps.setString(5, cuit.getRutaCertificado());
            ps.setString(6, cuit.getPasswordCert());
            ps.setBoolean(7, cuit.getActivo() != null ? cuit.getActivo() : true);
            ps.setString(8, cuit.getDomicilio());
            ps.setString(9, cuit.getIngresosBrutos());
            ps.setString(10, cuit.getFechaInicioActividades());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando CUIT: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(CuitConfigDTO cuit) {
        String sql = "UPDATE cuit_certificados SET cuit = ?, razon_social = ?, condicion_iva = ?, "
                + "punto_venta = ?, ruta_certificado = ?, password_cert = ?, activo = ?, "
                + "domicilio = ?, ingresos_brutos = ?, fecha_inicio_actividades = ? "
                + "WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, cuit.getCuit());
            ps.setString(2, cuit.getRazonSocial());
            ps.setString(3, cuit.getCondicionIva());
            ps.setInt(4, cuit.getPuntoVenta());
            ps.setString(5, cuit.getRutaCertificado());
            ps.setString(6, cuit.getPasswordCert());
            ps.setBoolean(7, cuit.getActivo());
            ps.setString(8, cuit.getDomicilio());
            ps.setString(9, cuit.getIngresosBrutos());
            ps.setString(10, cuit.getFechaInicioActividades());
            ps.setInt(11, cuit.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando CUIT: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM cuit_certificados WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando CUIT: " + e.getMessage());
        }
        return false;
    }

    public CuitConfigDTO buscarPorId(int id) {
        String sql = "SELECT * FROM cuit_certificados WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando CUIT por ID: " + e.getMessage());
        }
        return null;
    }

    public CuitConfigDTO buscarPorCuit(String cuit) {
        String sql = "SELECT * FROM cuit_certificados WHERE cuit = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, cuit);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultado(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando CUIT: " + e.getMessage());
        }
        return null;
    }

    public List<CuitConfigDTO> listarTodos() {
        String sql = "SELECT * FROM cuit_certificados ORDER BY razon_social";
        List<CuitConfigDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando CUITs: " + e.getMessage());
        }
        return lista;
    }

    public void activarExclusivo(int id) {
        String sql = "UPDATE cuit_certificados SET activo = (id = ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error activando CUIT exclusivo: " + e.getMessage());
        }
    }

    public int contarActivos() {
        String sql = "SELECT COUNT(*) FROM cuit_certificados WHERE activo = TRUE";
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error contando activos: " + e.getMessage());
        }
        return 0;
    }

    public List<CuitConfigDTO> listarActivos() {
        String sql = "SELECT * FROM cuit_certificados WHERE activo = TRUE ORDER BY razon_social";
        List<CuitConfigDTO> lista = new ArrayList<>();

        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearResultado(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando CUITs activos: " + e.getMessage());
        }
        return lista;
    }

    private CuitConfigDTO mapearResultado(ResultSet rs) throws SQLException {
        CuitConfigDTO dto = new CuitConfigDTO();
        dto.setId(rs.getInt("id"));
        dto.setCuit(rs.getString("cuit"));
        dto.setRazonSocial(rs.getString("razon_social"));
        dto.setCondicionIva(rs.getString("condicion_iva"));
        dto.setPuntoVenta(rs.getInt("punto_venta"));
        dto.setRutaCertificado(rs.getString("ruta_certificado"));
        dto.setPasswordCert(rs.getString("password_cert"));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setDomicilio(rs.getString("domicilio"));
        dto.setIngresosBrutos(rs.getString("ingresos_brutos"));
        dto.setFechaInicioActividades(rs.getString("fecha_inicio_actividades"));
        return dto;
    }
}