package com.els.facturacion.controlador;

import com.els.facturacion.dao.CajaMovimientoDAO;
import com.els.facturacion.dao.CategoriaGastoDAO;
import com.els.facturacion.dao.GastoDAO;
import com.els.facturacion.modelo.CajaMovimientoDTO;
import com.els.facturacion.modelo.CategoriaGastoDTO;
import com.els.facturacion.modelo.GastoDTO;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MigracionExcelController {

    private CajaMovimientoDAO cajaDAO;
    private CategoriaGastoDAO categoriaDAO;
    private GastoDAO gastoDAO;

    public MigracionExcelController() {
        this.cajaDAO = new CajaMovimientoDAO();
        this.categoriaDAO = new CategoriaGastoDAO();
        this.gastoDAO = new GastoDAO();
    }

    public void mostrarCabecerasExcel(String rutaArchivo) {
        try (Workbook workbook = WorkbookFactory.create(new File(rutaArchivo))) {
            System.out.println("=== HOJAS DISPONIBLES ===");
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                System.out.println("Hoja " + i + ": " + workbook.getSheetAt(i).getSheetName());
            }
            
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                System.out.println("\n=== CABECERAS HOJA: " + sheet.getSheetName() + " ===");
                Row headerRow = sheet.getRow(0);
                if (headerRow != null) {
                    for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                        Cell cell = headerRow.getCell(c);
                        String valor = getTexto(cell);
                        System.out.println("Columna " + c + ": " + valor);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCabecerasExcel(String rutaArchivo) {
        StringBuilder sb = new StringBuilder();
        try (Workbook workbook = WorkbookFactory.create(new File(rutaArchivo))) {
            sb.append("HOJAS DISPONIBLES:\n");
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sb.append("- ").append(workbook.getSheetAt(i).getSheetName()).append("\n");
            }
            
            for (int s = 0; s < workbook.getNumberOfSheets(); s++) {
                Sheet sheet = workbook.getSheetAt(s);
                sb.append("\n--- HOJA: ").append(sheet.getSheetName()).append(" ---\n");
                Row headerRow = sheet.getRow(0);
                if (headerRow != null) {
                    for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                        Cell cell = headerRow.getCell(c);
                        String valor = getTexto(cell);
                        sb.append("Col ").append(c).append(": ").append(valor).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public int migrarCajaBRC(String rutaArchivo) {
        return migrarCajaBRC(rutaArchivo, "2026");
    }

    public int migrarCajaBRC(String rutaArchivo, int numeroHoja) {
        int migrados = 0;
        try (Workbook workbook = WorkbookFactory.create(new File(rutaArchivo))) {
            Sheet sheet = workbook.getSheetAt(numeroHoja);
            if (sheet == null) {
                System.err.println("Hoja no encontrada: " + numeroHoja);
                return 0;
            }
            migrados = procesarHojaCaja(sheet);
        } catch (Exception e) {
            System.err.println("Error migrando Caja BRC: " + e.getMessage());
            e.printStackTrace();
        }
        return migrados;
    }

    public int migrarCajaBRC(String rutaArchivo, String anio) {
        int migrados = 0;
        try (Workbook workbook = WorkbookFactory.create(new File(rutaArchivo))) {
            Sheet sheet = null;
            
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetAt(i).getSheetName();
                if (sheetName.equals(anio) || sheetName.contains(anio)) {
                    sheet = workbook.getSheetAt(i);
                    System.out.println("Encontrada hoja: " + sheetName + " para año " + anio);
                    break;
                }
            }

            if (sheet == null) {
                System.err.println("Hoja no encontrada para el año: " + anio);
                JOptionPane.showMessageDialog(null, "No se encontró la hoja para el año " + anio, "Error", JOptionPane.ERROR_MESSAGE);
                return 0;
            }

            migrados = procesarHojaCaja(sheet);
            System.out.println("✓ Migrados " + migrados + " movimientos de caja del año " + anio);

        } catch (Exception e) {
            System.err.println("Error migrando Caja BRC: " + e.getMessage());
            e.printStackTrace();
        }
        return migrados;
    }

    private int procesarHojaCaja(Sheet sheet) {
        int migrados = 0;
        List<CajaMovimientoDTO> movimientos = new ArrayList<>();

        System.out.println("Procesando hoja: " + sheet.getSheetName() + " con " + sheet.getLastRowNum() + " filas");

        Row rowSaldos = sheet.getRow(1);
        if (rowSaldos != null) {
            BigDecimal saldoCuentaPatagonia = getMonto(rowSaldos.getCell(9));
            BigDecimal saldoEfectivo = getMonto(rowSaldos.getCell(10));
            BigDecimal dolares = getMonto(rowSaldos.getCell(11));
            BigDecimal saldoEfectivoBanco = getMonto(rowSaldos.getCell(12));

            System.out.println("=== SALDOS INICIALES DEL AÑO (Fila 2 - datos del año anterior) ===");
            System.out.println("Saldo Cuenta Patagonia: " + saldoCuentaPatagonia);
            System.out.println("Saldo Efectivo: " + saldoEfectivo);
            System.out.println("Dólares: " + dolares);
            System.out.println("Saldo Efectivo + Banco: " + saldoEfectivoBanco);
            
            if (saldoEfectivo != null && saldoEfectivo.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("→ Estos saldos se usarán para calcular el saldo inicial del año");
            }
        }

        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            try {
                LocalDate fecha = getFecha(row.getCell(0));
                String cliente = getTexto(row.getCell(1));
                String formaPagoCompleta = getTexto(row.getCell(2));
                String els = getTexto(row.getCell(3));
                
                String formaPago = formaPagoCompleta;
                if (formaPagoCompleta != null) {
                    formaPago = formaPagoCompleta.replaceAll("\\s*ELS\\s*\\d+$", "").trim();
                }
                
                String elsTexto = (els != null && !els.isEmpty()) ? " ELS " + els : "";
                
                BigDecimal cobroEfectivo = getMonto(row.getCell(4));
                BigDecimal cobroCuentaPatagonia = getMonto(row.getCell(5));
                BigDecimal pagoEfectivo = getMonto(row.getCell(6));
                BigDecimal pagoCuentaPatagonia = getMonto(row.getCell(7));

                boolean tieneCobro = (cobroEfectivo != null && cobroEfectivo.compareTo(BigDecimal.ZERO) > 0) 
                                   || (cobroCuentaPatagonia != null && cobroCuentaPatagonia.compareTo(BigDecimal.ZERO) > 0);
                boolean tienePago = (pagoEfectivo != null && pagoEfectivo.compareTo(BigDecimal.ZERO) > 0) 
                                 || (pagoCuentaPatagonia != null && pagoCuentaPatagonia.compareTo(BigDecimal.ZERO) > 0);

                if (!tieneCobro && !tienePago) {
                    continue;
                }

                String descripcion = cliente;
                if (formaPago != null && !formaPago.isEmpty()) {
                    descripcion = cliente + " - " + formaPago;
                }

                if (tieneCobro) {
                    BigDecimal montoCobro = BigDecimal.ZERO;
                    if (cobroEfectivo != null && cobroEfectivo.compareTo(BigDecimal.ZERO) > 0) {
                        montoCobro = montoCobro.add(cobroEfectivo);
                    }
                    if (cobroCuentaPatagonia != null && cobroCuentaPatagonia.compareTo(BigDecimal.ZERO) > 0) {
                        montoCobro = montoCobro.add(cobroCuentaPatagonia);
                    }
                    
                    CajaMovimientoDTO mov = new CajaMovimientoDTO(fecha, "cobro", descripcion + elsTexto, montoCobro);
                    movimientos.add(mov);
                    String fechaMostrar = fecha != null ? fecha.toString() : "(sin fecha)";
                    System.out.println("Cobro: " + fechaMostrar + " - " + descripcion + elsTexto + " - $" + montoCobro);
                }

                if (tienePago) {
                    BigDecimal montoPago = BigDecimal.ZERO;
                    if (pagoEfectivo != null && pagoEfectivo.compareTo(BigDecimal.ZERO) > 0) {
                        montoPago = montoPago.add(pagoEfectivo);
                    }
                    if (pagoCuentaPatagonia != null && pagoCuentaPatagonia.compareTo(BigDecimal.ZERO) > 0) {
                        montoPago = montoPago.add(pagoCuentaPatagonia);
                    }
                    
                    CajaMovimientoDTO mov = new CajaMovimientoDTO(fecha, "pago", descripcion + elsTexto, montoPago);
                    movimientos.add(mov);
                    String fechaMostrar = fecha != null ? fecha.toString() : "(sin fecha)";
                    System.out.println("Pago: " + fechaMostrar + " - " + descripcion + elsTexto + " - $" + montoPago);
                }

            } catch (Exception e) {
                System.err.println("Error en fila " + i + ": " + e.getMessage());
            }
        }

        System.out.println("Total movimientos a migrar: " + movimientos.size());

        for (CajaMovimientoDTO mov : movimientos) {
            int id = cajaDAO.insertar(mov);
            if (id > 0) migrados++;
        }

        System.out.println("✓ Migrados " + migrados + " movimientos de caja");
        return migrados;
    }

    public int migrarGastos(String rutaArchivo) {
        return migrarGastos(rutaArchivo, 0);
    }

    public int migrarGastos(String rutaArchivo, int numeroHoja) {
        int migrados = 0;
        try (Workbook workbook = WorkbookFactory.create(new File(rutaArchivo))) {
            Sheet sheet = workbook.getSheetAt(numeroHoja);
            if (sheet == null) {
                System.err.println("Hoja no encontrada: " + numeroHoja);
                return 0;
            }

            inicializarCategorias();

            List<GastoDTO> gastos = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    LocalDate fecha = getFecha(row.getCell(0));
                    String categoriaNombre = getTexto(row.getCell(1));
                    String descripcion = getTexto(row.getCell(2));
                    BigDecimal monto = getMonto(row.getCell(3));

                    if (fecha != null && categoriaNombre != null && !categoriaNombre.isEmpty() && monto != null) {
                        Integer categoriaId = getCategoriaId(categoriaNombre);
                        if (categoriaId != null) {
                            GastoDTO gasto = new GastoDTO();
                            gasto.setFecha(fecha);
                            gasto.setCategoriaId(categoriaId);
                            gasto.setDescripcion(descripcion);
                            gasto.setMonto(monto);
                            gasto.setMes(fecha.getMonthValue());
                            gasto.setAnio(fecha.getYear());
                            gastos.add(gasto);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error en fila " + i + ": " + e.getMessage());
                }
            }

            for (GastoDTO gasto : gastos) {
                int id = gastoDAO.insertar(gasto);
                if (id > 0) migrados++;
            }

            System.out.println("✓ Migrados " + migrados + " gastos");

        } catch (Exception e) {
            System.err.println("Error migrando gastos: " + e.getMessage());
            e.printStackTrace();
        }
        return migrados;
    }

    private void inicializarCategorias() {
        String[] categorias = {
            "Alquiler", "Servicios", "Insumos", "Mantenimiento",
            "Software", "Transporte", "Impuestos", "Otros"
        };

        List<CategoriaGastoDTO> existentes = categoriaDAO.listarTodos();
        if (existentes.isEmpty()) {
            for (String nombre : categorias) {
                CategoriaGastoDTO cat = new CategoriaGastoDTO(nombre, "");
                categoriaDAO.insertar(cat);
            }
        }
    }

    private Integer getCategoriaId(String nombre) {
        List<CategoriaGastoDTO> categorias = categoriaDAO.listarActivas();
        for (CategoriaGastoDTO cat : categorias) {
            if (cat.getNombre().equalsIgnoreCase(nombre.trim())) {
                return cat.getId();
            }
        }
        CategoriaGastoDTO nueva = new CategoriaGastoDTO(nombre.trim(), "");
        int id = categoriaDAO.insertar(nueva);
        return id > 0 ? id : null;
    }

    private LocalDate getFecha(Cell cell) {
        if (cell == null) return null;
        
        if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.BLANK) {
            return null;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
                try {
                    return LocalDate.ofEpochDay((long) cell.getNumericCellValue());
                } catch (Exception e) {
                    return null;
                }
            case STRING:
                String texto = cell.getStringCellValue().trim();
                if (texto.isEmpty()) return null;
                try {
                    return LocalDate.parse(texto);
                } catch (Exception e) {
                    try {
                        return LocalDate.parse(texto, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (Exception ex) {
                        return null;
                    }
                }
            default:
                return null;
        }
    }

    private String getTexto(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (cell.getNumericCellValue() % 1 == 0) {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
                return String.valueOf(cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private BigDecimal getMonto(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).setScale(2);
            case STRING:
                try {
                    String texto = cell.getStringCellValue().replace(",", ".").replace("$", "").replace(" ", "").trim();
                    if (texto.isEmpty()) return null;
                    return new BigDecimal(texto);
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        MigracionExcelController ctrl = new MigracionExcelController();
        String ruta = "F:\\Users\\Diego\\git\\SistemaGestion\\ReparsoftCliente\\Excels\\Caja BRC.xlsx";
        System.out.println("Analizando archivo: " + ruta);
        ctrl.mostrarCabecerasExcel(ruta);
    }
}