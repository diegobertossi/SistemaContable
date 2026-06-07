package com.els.facturacion.dao;

import com.els.facturacion.conexion.ConexionFacturacion;
import com.els.facturacion.conexion.ConexionReparsoft;
import com.els.facturacion.modelo.ClienteDTO;
import com.els.facturacion.util.UbicacionSistema;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClienteDAO {

    private Connection getConn() {
        return ConexionFacturacion.getInstancia().getConexion();
    }

    public int insertar(ClienteDTO cliente) {
        String sql = "INSERT INTO clientes (tipo_documento, nro_documento, razon_social, condicion_iva, "
                + "domicilio, telefono, email, origen, els_referencia, activo, tipo_persona) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = getConn().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getTipoDocumento());
            ps.setString(2, cliente.getNroDocumento());
            ps.setString(3, cliente.getRazonSocial());
            ps.setString(4, cliente.getCondicionIva());
            ps.setString(5, cliente.getDomicilio());
            ps.setString(6, cliente.getTelefono());
            ps.setString(7, cliente.getEmail());
            ps.setString(8, cliente.getOrigen());
            if (cliente.getElsReferencia() != null) {
                ps.setInt(9, cliente.getElsReferencia());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            ps.setBoolean(10, cliente.getActivo() != null ? cliente.getActivo() : true);
            ps.setString(11, cliente.getTipoPersona() != null ? cliente.getTipoPersona() : "empresa");

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando cliente: " + e.getMessage());
        }
        return -1;
    }

    public boolean actualizar(ClienteDTO cliente) {
        String sql = "UPDATE clientes SET tipo_documento = ?, nro_documento = ?, razon_social = ?, "
                + "condicion_iva = ?, domicilio = ?, telefono = ?, email = ?, activo = ?, "
                + "tipo_persona = ?, els_referencia = ? WHERE id = ?";

        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, cliente.getTipoDocumento());
            ps.setString(2, cliente.getNroDocumento());
            ps.setString(3, cliente.getRazonSocial());
            ps.setString(4, cliente.getCondicionIva());
            ps.setString(5, cliente.getDomicilio());
            ps.setString(6, cliente.getTelefono());
            ps.setString(7, cliente.getEmail());
            ps.setBoolean(8, cliente.getActivo() != null ? cliente.getActivo() : true);
            ps.setString(9, cliente.getTipoPersona() != null ? cliente.getTipoPersona() : "empresa");
            if (cliente.getElsReferencia() != null) {
                ps.setInt(10, cliente.getElsReferencia());
            } else {
                ps.setNull(10, java.sql.Types.INTEGER);
            }
            ps.setInt(11, cliente.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando cliente: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando cliente: " + e.getMessage());
        }
        return false;
    }

    public ClienteDTO buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error buscando cliente por ID: " + e.getMessage());
        }
        return null;
    }

    public ClienteDTO buscarPorDocumento(String tipo, String nro) {
        String sql = "SELECT * FROM clientes WHERE tipo_documento = ? AND nro_documento = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, nro);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error buscando cliente por documento: " + e.getMessage());
        }
        return null;
    }

    public ClienteDTO buscarPorElsReferencia(int elsReferencia) {
        String sql = "SELECT * FROM clientes WHERE els_referencia = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, elsReferencia);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            System.err.println("Error buscando cliente por els_referencia: " + e.getMessage());
        }
        return null;
    }

    public List<ClienteDTO> buscarPorRazonSocial(String termino) {
        String sql = "SELECT * FROM clientes WHERE razon_social LIKE ? ORDER BY razon_social";
        List<ClienteDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + termino + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error buscando clientes: " + e.getMessage());
        }
        return lista;
    }

    public List<ClienteDTO> listarTodos() {
        String sql = "SELECT * FROM clientes ORDER BY razon_social";
        List<ClienteDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listando clientes: " + e.getMessage());
        }
        return lista;
    }

    public List<ClienteDTO> listarActivos() {
        String sql = "SELECT * FROM clientes WHERE activo = TRUE ORDER BY razon_social";
        List<ClienteDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Error listando clientes activos: " + e.getMessage());
        }
        return lista;
    }

    public List<ClienteDTO> importarDesdeReparsoft() {
        List<ClienteDTO> importados = new ArrayList<>();
        String base = UbicacionSistema.getNombreDbReparsoft();
        Set<String> nombresImportados = new HashSet<>();
        try {
            ConexionReparsoft cr = ConexionReparsoft.getInstancia();
            java.sql.Connection connRep = cr.getConexion(base);
            if (connRep == null) return importados;

            String sql = "SELECT DISTINCT c.idCliente, c.nombre, c.CUIT, c.Domicilio, "
                    + "c.TelefonoEmpresa, c.CorreoElectronico "
                    + "FROM cliente c "
                    + "ORDER BY c.nombre";
            try (PreparedStatement ps = connRep.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cuit = "";
                    String nombre = "";
                    String domicilio = "";
                    String telefono = "";
                    String email = "";
                    int idCliente = 0;
                    try { idCliente = rs.getInt("idCliente"); } catch (Exception e) {}
                    try { cuit = rs.getString("CUIT").replaceAll("[^0-9]", ""); } catch (Exception e) {}
                    try { nombre = rs.getString("nombre"); } catch (Exception e) {}
                    try { domicilio = rs.getString("Domicilio"); } catch (Exception e) {}
                    try { telefono = rs.getString("TelefonoEmpresa"); } catch (Exception e) {}
                    try { email = rs.getString("CorreoElectronico"); } catch (Exception e) {}

                    if (nombre == null || nombre.trim().isEmpty()) continue;

                    String nombreKey = nombre.trim().toLowerCase();
                    if (nombresImportados.contains(nombreKey)) continue;
                    nombresImportados.add(nombreKey);

                    String tipoDoc;
                    String nroDoc;
                    if (cuit.length() >= 11) {
                        tipoDoc = "CUIT";
                        nroDoc = cuit;
                    } else {
                        tipoDoc = "Otro";
                        nroDoc = String.valueOf(idCliente);
                    }

                    ClienteDTO existente = buscarPorDocumento(tipoDoc, nroDoc);
                    if (existente == null) {
                        existente = buscarPorElsReferencia(idCliente);
                    }
                    if (existente != null) {
                        if (existente.getElsReferencia() == null || existente.getElsReferencia() == 0) {
                            existente.setElsReferencia(idCliente);
                            actualizar(existente);
                        }
                        continue;
                    }

                    ClienteDTO cli = new ClienteDTO();
                    cli.setTipoDocumento(tipoDoc);
                    cli.setNroDocumento(nroDoc);
                    cli.setRazonSocial(nombre.trim());
                    cli.setCondicionIva("IVA Responsable Inscripto");
                    cli.setDomicilio(domicilio != null ? domicilio.trim() : "");
                    cli.setTelefono(telefono != null ? telefono.trim() : "");
                    cli.setEmail(email != null ? email.trim() : "");
                    cli.setElsReferencia(idCliente);
                    cli.setOrigen("reparsoft");
                    int id = insertar(cli);
                    if (id > 0) {
                        cli.setId(id);
                        importados.add(cli);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error importando desde " + base + ": " + e.getMessage());
        }
        return importados;
    }

    private ClienteDTO mapear(ResultSet rs) throws SQLException {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(rs.getInt("id"));
        dto.setTipoDocumento(rs.getString("tipo_documento"));
        dto.setNroDocumento(rs.getString("nro_documento"));
        dto.setRazonSocial(rs.getString("razon_social"));
        dto.setCondicionIva(rs.getString("condicion_iva"));
        dto.setDomicilio(rs.getString("domicilio"));
        dto.setTelefono(rs.getString("telefono"));
        dto.setEmail(rs.getString("email"));
        dto.setOrigen(rs.getString("origen"));
        dto.setElsReferencia(rs.getObject("els_referencia") != null ? rs.getInt("els_referencia") : null);
        dto.setActivo(rs.getBoolean("activo"));
        dto.setTipoPersona(rs.getString("tipo_persona"));
        return dto;
    }
}
