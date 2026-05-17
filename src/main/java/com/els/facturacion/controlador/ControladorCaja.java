package com.els.facturacion.controlador;

import com.els.facturacion.dao.CajaMovimientoDAO;
import com.els.facturacion.modelo.CajaMovimientoDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ControladorCaja {

    private CajaMovimientoDAO cajaDAO;

    public ControladorCaja() {
        this.cajaDAO = new CajaMovimientoDAO();
    }

    public int registrarMovimiento(LocalDate fecha, String tipo, String descripcion, BigDecimal monto) {
        if (tipo == null || (!tipo.equalsIgnoreCase("cobro") && !tipo.equalsIgnoreCase("pago"))) {
            System.err.println("Tipo debe ser 'cobro' o 'pago'");
            return -1;
        }

        CajaMovimientoDTO movimiento = new CajaMovimientoDTO(fecha, tipo.toLowerCase(), descripcion, monto);
        return cajaDAO.insertar(movimiento);
    }

    public int registrarCobro(LocalDate fecha, String descripcion, BigDecimal monto) {
        return registrarMovimiento(fecha, "cobro", descripcion, monto);
    }

    public int registrarPago(LocalDate fecha, String descripcion, BigDecimal monto) {
        return registrarMovimiento(fecha, "pago", descripcion, monto);
    }

    public boolean eliminarMovimiento(int id) {
        return cajaDAO.eliminar(id);
    }

    public CajaMovimientoDTO buscarMovimiento(int id) {
        return cajaDAO.buscarPorId(id);
    }

    public List<CajaMovimientoDTO> listarMovimientos() {
        return cajaDAO.listarTodos();
    }

    public List<CajaMovimientoDTO> listarMovimientos(LocalDate desde, LocalDate hasta) {
        return cajaDAO.buscarPorFecha(desde, hasta);
    }

    public List<CajaMovimientoDTO> listarCobros() {
        return cajaDAO.buscarPorTipo("cobro");
    }

    public List<CajaMovimientoDTO> listarPagos() {
        return cajaDAO.buscarPorTipo("pago");
    }

    public BigDecimal getSaldoCaja() {
        return getSaldoCaja(LocalDate.now());
    }

    public BigDecimal getSaldoCaja(LocalDate hasta) {
        return cajaDAO.getSaldoCaja(hasta);
    }

    public BigDecimal getTotalCobros() {
        BigDecimal saldo = BigDecimal.ZERO;
        for (CajaMovimientoDTO mov : listarCobros()) {
            saldo = saldo.add(mov.getMonto());
        }
        return saldo;
    }

    public BigDecimal getTotalPagos() {
        BigDecimal saldo = BigDecimal.ZERO;
        for (CajaMovimientoDTO mov : listarPagos()) {
            saldo = saldo.add(mov.getMonto());
        }
        return saldo;
    }
}