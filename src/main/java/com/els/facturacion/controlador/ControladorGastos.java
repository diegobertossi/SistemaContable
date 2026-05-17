package com.els.facturacion.controlador;

import com.els.facturacion.dao.CategoriaGastoDAO;
import com.els.facturacion.dao.GastoDAO;
import com.els.facturacion.modelo.CategoriaGastoDTO;
import com.els.facturacion.modelo.GastoDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ControladorGastos {

    private GastoDAO gastoDAO;
    private CategoriaGastoDAO categoriaDAO;

    public ControladorGastos() {
        this.gastoDAO = new GastoDAO();
        this.categoriaDAO = new CategoriaGastoDAO();
    }

    public int registrarGasto(LocalDate fecha, int categoriaId, String descripcion, BigDecimal monto) {
        if (categoriaId <= 0) {
            System.err.println("Categoría inválida");
            return -1;
        }

        GastoDTO gasto = new GastoDTO();
        gasto.setFecha(fecha);
        gasto.setCategoriaId(categoriaId);
        gasto.setDescripcion(descripcion);
        gasto.setMonto(monto);
        gasto.setMes(fecha.getMonthValue());
        gasto.setAnio(fecha.getYear());

        return gastoDAO.insertar(gasto);
    }

    public boolean eliminarGasto(int id) {
        return gastoDAO.eliminar(id);
    }

    public GastoDTO buscarGasto(int id) {
        return gastoDAO.buscarPorId(id);
    }

    public List<GastoDTO> listarGastos() {
        return gastoDAO.listarTodos();
    }

    public List<GastoDTO> listarGastos(int mes, int anio) {
        return gastoDAO.buscarPorMes(mes, anio);
    }

    public List<GastoDTO> listarGastos(int anio) {
        return gastoDAO.buscarPorAnio(anio);
    }

    public BigDecimal getTotalGastos(int mes, int anio) {
        return gastoDAO.getTotalGastosMes(mes, anio);
    }

    public List<CategoriaGastoDTO> listarCategorias() {
        return categoriaDAO.listarTodos();
    }

    public List<CategoriaGastoDTO> listarCategoriasActivas() {
        return categoriaDAO.listarActivas();
    }

    public int crearCategoria(String nombre, String descripcion) {
        CategoriaGastoDTO cat = new CategoriaGastoDTO(nombre, descripcion);
        return categoriaDAO.insertar(cat);
    }

    public boolean eliminarCategoria(int id) {
        return categoriaDAO.eliminar(id);
    }
}