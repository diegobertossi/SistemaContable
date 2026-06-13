package com.els.facturacion.controlador;

import com.els.facturacion.dao.ComprobanteDAO;
import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.dao.RemitoDAO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.RemitoDTO;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ControladorRemitos {

    private RemitoDAO remitoDAO;
    private CuitDAO cuitDAO;
    private ComprobanteDAO comprobanteDAO;
    private ControladorReparsoft controladorReparsoft;

    public ControladorRemitos() {
        this.remitoDAO = new RemitoDAO();
        this.cuitDAO = new CuitDAO();
        this.comprobanteDAO = new ComprobanteDAO();
        this.controladorReparsoft = new ControladorReparsoft();
    }

    public int guardarRemito(RemitoDTO remito) {
        if (remito.getNumeroRemito() == null || remito.getNumeroRemito().isEmpty()) {
            remito.setNumeroRemito(generarNumeroRemito());
        }
        return remitoDAO.insertar(remito);
    }

    public int guardarRemito(RemitoDTO remito, List<Integer> elsList, int codigoUbicacion) {
        if (remito.getNumeroRemito() == null || remito.getNumeroRemito().isEmpty()) {
            remito.setNumeroRemito(generarNumeroRemito());
        }
        int remitoId = remitoDAO.insertar(remito);
        if (remitoId < 0) return -1;

        List<Integer> elsConReferencia = elsList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!elsConReferencia.isEmpty()) {
            Integer reparsoftId = controladorReparsoft.insertarRemitoEnReparsoft(
                    remito.getNumeroRemito(), elsConReferencia, codigoUbicacion);
            if (reparsoftId != null) {
                remitoDAO.actualizarReparsoftId(remitoId, reparsoftId);
            }
        }
        return remitoId;
    }

    public int limpiarRemitosHuerfanos() {
        List<RemitoDTO> sincronizados = remitoDAO.listarConReparsoftId();
        if (sincronizados.isEmpty()) return 0;

        List<Integer> idsReparsoft = sincronizados.stream()
                .map(RemitoDTO::getReparsoftRemitoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (idsReparsoft.isEmpty()) return 0;

        List<Integer> existentes = controladorReparsoft.verificarRemitosExistentes(idsReparsoft);
        if (existentes == null) return 0;

        int eliminados = 0;
        for (RemitoDTO r : sincronizados) {
            if (r.getReparsoftRemitoId() != null && !existentes.contains(r.getReparsoftRemitoId())) {
                controladorReparsoft.eliminarRemitoEnReparsoft(r.getReparsoftRemitoId());
                if (remitoDAO.eliminar(r.getId())) {
                    System.out.println("Remito huérfano eliminado: " + r.getNumeroRemito());
                    eliminados++;
                }
            }
        }
        return eliminados;
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
