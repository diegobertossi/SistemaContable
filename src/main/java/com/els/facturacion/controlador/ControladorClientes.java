package com.els.facturacion.controlador;

import com.els.facturacion.dao.ClienteDAO;
import com.els.facturacion.modelo.ClienteDTO;
import java.util.List;

public class ControladorClientes {

    private ClienteDAO clienteDAO;
    private ControladorReparsoft controladorReparsoft;

    public ControladorClientes() {
        this.clienteDAO = new ClienteDAO();
        this.controladorReparsoft = new ControladorReparsoft();
    }

    public int guardarCliente(ClienteDTO cliente) {
        if (cliente.getId() != null) {
            clienteDAO.actualizar(cliente);
            controladorReparsoft.actualizarClienteEnReparsoft(cliente);
            return cliente.getId();
        }
        int id = clienteDAO.insertar(cliente);
        if (id > 0) {
            cliente.setId(id);
            Integer reparsoftId = controladorReparsoft.insertarClienteEnReparsoft(cliente);
            if (reparsoftId != null) {
                cliente.setElsReferencia(reparsoftId);
                clienteDAO.actualizar(cliente);
            }
        }
        return id;
    }

    public boolean eliminarCliente(int id) {
        ClienteDTO cliente = clienteDAO.buscarPorId(id);
        if (cliente == null) return false;
        if (cliente.getElsReferencia() != null) {
            controladorReparsoft.eliminarClienteEnReparsoft(cliente.getElsReferencia());
        }
        return clienteDAO.eliminar(id);
    }

    public ClienteDTO buscarPorId(int id) {
        return clienteDAO.buscarPorId(id);
    }

    public ClienteDTO buscarPorDocumento(String tipo, String nro) {
        return clienteDAO.buscarPorDocumento(tipo, nro);
    }

    public List<ClienteDTO> buscarPorRazonSocial(String termino) {
        return clienteDAO.buscarPorRazonSocial(termino);
    }

    public List<ClienteDTO> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public List<ClienteDTO> listarActivos() {
        return clienteDAO.listarActivos();
    }

    public List<ClienteDTO> importarDesdeReparsoft() {
        return clienteDAO.importarDesdeReparsoft();
    }
}
