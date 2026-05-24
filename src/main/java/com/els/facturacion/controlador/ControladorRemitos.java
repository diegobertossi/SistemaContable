package com.els.facturacion.controlador;

import com.els.facturacion.dao.ComprobanteDAO;
import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.dao.RemitoDAO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.RemitoDTO;
import java.util.List;

public class ControladorRemitos {

    private RemitoDAO remitoDAO;
    private CuitDAO cuitDAO;
    private ComprobanteDAO comprobanteDAO;

    public ControladorRemitos() {
        this.remitoDAO = new RemitoDAO();
        this.cuitDAO = new CuitDAO();
        this.comprobanteDAO = new ComprobanteDAO();
    }

    public int guardarRemito(RemitoDTO remito) {
        if (remito.getNumeroRemito() == null || remito.getNumeroRemito().isEmpty()) {
            remito.setNumeroRemito(generarNumeroRemito());
        }
        return remitoDAO.insertar(remito);
    }

    public boolean actualizarEstado(int id, String estado) {
        return remitoDAO.actualizarEstado(id, estado);
    }

    public RemitoDTO buscarPorId(int id) {
        return remitoDAO.buscarPorId(id);
    }

    public List<RemitoDTO> listarTodos() {
        return remitoDAO.listarTodos();
    }

    public List<RemitoDTO> buscarPorReceptor(String cuit) {
        return remitoDAO.buscarPorReceptor(cuit);
    }

    public CuitConfigDTO getCuitActivo() {
        List<CuitConfigDTO> activos = cuitDAO.listarActivos();
        return activos.isEmpty() ? null : activos.get(0);
    }

    public String generarNumeroRemito() {
        String ultimo = remitoDAO.getUltimoNumero();
        int num = 1;
        if (ultimo != null && ultimo.contains("-")) {
            try {
                num = Integer.parseInt(ultimo.substring(ultimo.lastIndexOf('-') + 1)) + 1;
            } catch (NumberFormatException e) {}
        }
        return String.format("R %04d-%08d", 1, num);
    }
}
