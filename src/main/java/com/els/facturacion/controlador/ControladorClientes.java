package com.els.facturacion.controlador;

import com.els.facturacion.dao.ClienteDAO;
import com.els.facturacion.modelo.ClienteDTO;
import java.util.List;

public class ControladorClientes {

    private ClienteDAO clienteDAO;

    public ControladorClientes() {
        this.clienteDAO = new ClienteDAO();
    }

    public int guardarCliente(ClienteDTO cliente) {
        if (cliente.getId() != null) {
            clienteDAO.actualizar(cliente);
            return cliente.getId();
        }
        return clienteDAO.insertar(cliente);
    }

    public boolean eliminarCliente(int id) {
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
