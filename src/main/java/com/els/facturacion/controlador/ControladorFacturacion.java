package com.els.facturacion.controlador;

import com.els.facturacion.arca.ServicioWSFEv1;
import com.els.facturacion.dao.ComprobanteDAO;
import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.modelo.ClienteDTO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem;
import com.els.facturacion.modelo.RespuestaCAE;
import com.els.facturacion.pdf.GestorPDF;
import com.els.facturacion.vista.VentanaFacturacion;
import com.els.facturacion.vista.VentanaCaja;
import com.els.facturacion.vista.VentanaClientes;
import com.els.facturacion.vista.VentanaComprobantes;
import com.els.facturacion.vista.VentanaConfigCertificados;
import com.els.facturacion.vista.VentanaGastos;
import com.els.facturacion.vista.VentanaImportarRemito;
import com.els.facturacion.vista.VentanaMigracion;
import com.els.facturacion.vista.VentanaPagos;
import com.els.facturacion.vista.VentanaRecibos;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ControladorFacturacion {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    private final VentanaFacturacion view;
    private CuitDAO cuitDAO;
    private ComprobanteDAO comprobanteDAO;
    private ServicioWSFEv1 servicioWSFEv1;
    private ControladorClientes controladorClientes;
    private ControladorReparsoft controladorReparsoft;
    private boolean modoPrueba;
    private boolean recalculando;

    public ControladorFacturacion() {
        this(null);
    }

    public ControladorFacturacion(VentanaFacturacion view) {
        this.view = view;
        this.cuitDAO = new CuitDAO();
        this.comprobanteDAO = new ComprobanteDAO();
        this.servicioWSFEv1 = new ServicioWSFEv1();
        this.controladorClientes = new ControladorClientes();
        this.controladorReparsoft = new ControladorReparsoft();
        this.modoPrueba = false;
        this.recalculando = false;
    }

    // ===================== INIT =====================

    public void inicializar() {
        if (view == null) throw new IllegalStateException("ControladorFacturacion sin vista. Use constructor con VentanaFacturacion.");
        cargarEmisorActivo();
        cargarClientes();

        // Navigation
        view.getChkModoPrueba().addActionListener(e -> modoPrueba = view.getChkModoPrueba().isSelected());
        view.getBtnSiguiente().addActionListener(e -> {
            if (validarDatosReceptor()) {
                view.getCardLayout().show(view.getPanelPrincipal(), "operacion");
            }
        });
        view.getBtnAnterior().addActionListener(e -> view.getCardLayout().show(view.getPanelPrincipal(), "datos"));

        // Emission
        view.getBtnEmitir().addActionListener(e -> btnEmitirAction());
        view.getBtnLimpiar().addActionListener(e -> limpiarTodo());
        view.getBtnImportarRemito().addActionListener(e -> importarRemitoReparsoft());

        // Items table
        view.getBtnAgregarItem().addActionListener(e -> agregarItem());
        view.getBtnEliminarItem().addActionListener(e -> eliminarItem());

        // Auto-recalculate on cell edits (checkbox, cantidad, precio)
        view.getModeloItems().addTableModelListener(e -> {
            int col = e.getColumn();
            if ((col == 2 || col == 4 || col == 6) && !recalculando) {
                recalcularTotales();
            }
        });

        // Auto-recalculate on IVA combobox change
        view.getCmbAlicuotaIva().addActionListener(e -> recalcularTotales());

        // Autocomplete
        JTextField editorRS = (JTextField) view.getCmbRazonSocial().getEditor().getEditorComponent();
        editorRS.addActionListener(e -> autocompletarPorRazonSocial());
        JTextField editorND = (JTextField) view.getCmbNroDoc().getEditor().getEditorComponent();
        editorND.addActionListener(e -> autocompletarPorDocumento());

        // Menu actions
        view.getItemSalir().addActionListener(e -> System.exit(0));
        view.getItemConfig().addActionListener(e -> {
            VentanaConfigCertificados configWindow = new VentanaConfigCertificados();
            configWindow.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    cargarEmisorActivo();
                }
            });
            configWindow.setVisible(true);
        });
        view.getItemHistorial().addActionListener(e -> new VentanaComprobantes().setVisible(true));
        view.getItemCaja().addActionListener(e -> new VentanaCaja().setVisible(true));
        view.getItemGastos().addActionListener(e -> new VentanaGastos().setVisible(true));
        view.getItemMigrar().addActionListener(e -> new VentanaMigracion().setVisible(true));
        view.getItemClientes().addActionListener(e -> {
            VentanaClientes vc = new VentanaClientes();
            vc.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent ev) {
                    cargarClientes();
                }
            });
            vc.setVisible(true);
        });
        view.getItemRemitos().addActionListener(e -> JOptionPane.showMessageDialog(view, "Funcionalidad en desarrollo"));
        view.getItemRecibos().addActionListener(e -> new VentanaRecibos().setVisible(true));
        view.getItemPagos().addActionListener(e -> new VentanaPagos().setVisible(true));
    }

    private void cargarEmisorActivo() {
        List<CuitConfigDTO> activos = cuitDAO.listarActivos();
        if (!activos.isEmpty()) {
            CuitConfigDTO emisor = activos.get(0);
            view.actualizarEmisor(emisor.getRazonSocial(), emisor.getCuit(), emisor.getCondicionIva());
            view.actualizarTiposComprobante(emisor.getCondicionIva());
        } else {
            view.actualizarEmisor("", "", "");
        }
    }

    private void cargarClientes() {
        List<ClienteDTO> clientes = listarClientes();
        List<String> razones = new ArrayList<>();
        List<String> docs = new ArrayList<>();
        for (ClienteDTO c : clientes) {
            razones.add(c.getRazonSocial());
            docs.add(c.getNroDocumento());
        }
        view.getCmbRazonSocial().setData(razones);
        view.getCmbNroDoc().setData(docs);
    }

    // ===================== AUTOCOMPLETE =====================

    private void autocompletarPorRazonSocial() {
        String texto = view.getCmbRazonSocial().getEditorText().trim();
        if (texto.isEmpty()) return;

        List<ClienteDTO> resultados = buscarClientesPorRazonSocial(texto);
        ClienteDTO match = null;
        for (ClienteDTO c : resultados) {
            if (c.getRazonSocial().equalsIgnoreCase(texto)) {
                match = c;
                break;
            }
        }
        if (match == null && resultados.size() == 1) {
            match = resultados.get(0);
        }

        if (match != null) {
            autocompletarCamposCliente(match);
        } else {
            int opcion = JOptionPane.showConfirmDialog(view,
                "No existe un cliente con razon social \"" + texto + "\". Desea darlo de alta?",
                "Cliente no encontrado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opcion == JOptionPane.YES_OPTION) {
                VentanaClientes ventana = new VentanaClientes();
                ventana.setVisible(true);
                ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        cargarClientes();
                    }
                });
            }
        }
    }

    private void autocompletarPorDocumento() {
        String texto = view.getCmbNroDoc().getEditorText().trim();
        if (texto.isEmpty()) return;

        String tipoDoc = (String) view.getCmbTipoDoc().getSelectedItem();
        ClienteDTO cli = buscarClientePorDocumento(tipoDoc, texto);
        if (cli != null) {
            autocompletarCamposCliente(cli);
            return;
        }

        List<ClienteDTO> todos = listarClientes();
        for (ClienteDTO c : todos) {
            if (c.getNroDocumento() != null && c.getNroDocumento().contains(texto)) {
                autocompletarCamposCliente(c);
                return;
            }
        }

        int opcion = JOptionPane.showConfirmDialog(view,
            "No existe un cliente con documento \"" + texto + "\". Desea darlo de alta?",
            "Cliente no encontrado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            VentanaClientes ventana = new VentanaClientes();
            ventana.setVisible(true);
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosed(java.awt.event.WindowEvent e) {
                    cargarClientes();
                }
            });
        }
    }

    private void autocompletarCamposCliente(ClienteDTO cli) {
        if (cli.getRazonSocial() != null) view.setRazonSocial(cli.getRazonSocial());
        if (cli.getNroDocumento() != null) view.setNroDoc(cli.getNroDocumento());
        if (cli.getCondicionIva() != null) view.setCmbCondicionIva(cli.getCondicionIva());
        if (cli.getTipoDocumento() != null) view.setTipoDoc(cli.getTipoDocumento());
        view.getTxtDomicilio().setText(cli.getDomicilio() != null ? cli.getDomicilio() : "");
        view.getTxtEmail().setText(cli.getEmail() != null ? cli.getEmail() : "");
    }

    // ===================== IMPORTAR REMITO =====================

    private void importarRemitoReparsoft() {
        RemitoReparsoftDTO remito = VentanaImportarRemito.mostrarDialog(view);
        if (remito == null) return;

        view.getTxtComprobanteAsoc().setText(remito.getNumeroRemitoDisplay());

        if (remito.getRazonSocialCliente() != null && !remito.getRazonSocialCliente().isEmpty()) {
            boolean encontrado = false;
            List<ClienteDTO> clientes = buscarClientesPorRazonSocial(remito.getRazonSocialCliente());
            for (ClienteDTO c : clientes) {
                if (c.getRazonSocial().equalsIgnoreCase(remito.getRazonSocialCliente())) {
                    autocompletarCamposCliente(c);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado && remito.getCuitCliente() != null && !remito.getCuitCliente().isEmpty()) {
                String cuitLimpio = remito.getCuitCliente().replaceAll("[^0-9]", "");
                for (ClienteDTO c : listarClientes()) {
                    if (c.getNroDocumento() != null && c.getNroDocumento().replaceAll("[^0-9]", "").equals(cuitLimpio)) {
                        autocompletarCamposCliente(c);
                        encontrado = true;
                        break;
                    }
                }
            }
            if (!encontrado) {
                view.setRazonSocial(remito.getRazonSocialCliente());
                if (remito.getCuitCliente() != null && !remito.getCuitCliente().isEmpty()) {
                    view.setNroDoc(remito.getCuitCliente());
                    view.setTipoDoc("CUIT");
                }
            }
        }

        view.getCardLayout().show(view.getPanelPrincipal(), "operacion");
        DefaultTableModel model = view.getModeloItems();
        model.setRowCount(0);
        if (remito.getItems() != null) {
            for (RemitoReparsoftItem item : remito.getItems()) {
                String descripcion = item.getDescripcion();
                BigDecimal precio = item.getPrecioPeso() != null ? item.getPrecioPeso() : BigDecimal.ZERO;
                String precioStr = precio.compareTo(BigDecimal.ZERO) > 0
                    ? DF.format(precio)
                    : "0,00";
                String codigo = String.valueOf(item.getEls());
                model.addRow(new Object[]{codigo, descripcion, "1", "Unidad", precioStr, "0,00", true});
            }
        }
        recalcularTotales();
    }

    // ===================== EMITIR =====================

    private void btnEmitirAction() {
        DefaultTableModel model = view.getModeloItems();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Agregue al menos un item", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean algunSeleccionado = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Boolean.TRUE.equals(model.getValueAt(i, 6))) {
                algunSeleccionado = true;
                break;
            }
        }
        if (!algunSeleccionado) {
            JOptionPane.showMessageDialog(view, "Seleccione al menos un item (marque la casilla Sel.)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        recalcularTotales();

        try {
            CuitConfigDTO cuiSelected = obtenerCuitSeleccionado();
            if (cuiSelected == null) {
                JOptionPane.showMessageDialog(view, "Configure un CUIT emisor en Herramientas > Configurar Certificados", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ComprobanteDTO comprobante = new ComprobanteDTO();
            comprobante.setCuitEmisor(cuiSelected.getCuit());
            comprobante.setTipoComprobante(obtenerTipoCodigo());
            comprobante.setPuntoVenta(Integer.parseInt((String) view.getCmbPuntoVenta().getSelectedItem()));
            comprobante.setCuitReceptor(obtenerDocReceptor());
            comprobante.setRazonSocialRec(view.getCmbRazonSocial().getEditorText().trim());
            comprobante.setFechaEmision(parseFechaChooser(view.getDateFecha()));
            comprobante.setPeriodoDesde(parseFechaChooser(view.getDatePeriodoDesde()));
            comprobante.setPeriodoHasta(parseFechaChooser(view.getDatePeriodoHasta()));
            comprobante.setPeriodoVto(parseFechaChooser(view.getDatePeriodoVto()));
            comprobante.setCondicionIvaReceptor((String) view.getCmbCondicionIva().getSelectedItem());
            comprobante.setTipoDocumento((String) view.getCmbTipoDoc().getSelectedItem());
            comprobante.setNroDocumento(view.getCmbNroDoc().getEditorText().trim());
            comprobante.setDomicilioReceptor(view.getTxtDomicilio().getText().trim());
            comprobante.setEmailReceptor(view.getTxtEmail().getText().trim());
            comprobante.setCondicionesVenta(obtenerCondicionesVenta());
            comprobante.setComprobanteAsociado(view.getTxtComprobanteAsoc().getText().trim());
            comprobante.setDescripcion(obtenerDescripcionItems());
            comprobante.setEstadoPago("pendiente");
            try {
                comprobante.setOtrosImpuestos(parseBigDecimal(view.getTxtOtrosImpuestos().getText()));
            } catch (Exception e) {
                comprobante.setOtrosImpuestos(BigDecimal.ZERO);
            }

            List<ItemFacturaDTO> items = obtenerItems();
            try {
                comprobante.setImporteNeto(parseBigDecimal(view.getTxtImporteNeto().getText().replace("$", "").trim()));
                comprobante.setImporteIva(parseBigDecimal(view.getTxtImporteIva().getText().replace("$", "").trim()));
                comprobante.setImporteTotal(parseBigDecimal(view.getTxtImporteTotal().getText().replace("$", "").trim()));
            } catch (Exception e) {
            }
            RespuestaCAE respuesta = emitirFactura(comprobante, items);

            if (respuesta.isExitosa()) {
                Long nroComp = respuesta.getNumeroComprobante();
                if (nroComp != null) {
                    String tipoStr = (String) view.getCmbTipoComprobante().getSelectedItem();
                    String abrev = "FC";
                    if (tipoStr.startsWith("Nota de Debito")) abrev = "ND";
                    else if (tipoStr.startsWith("Nota de Credito")) abrev = "NC";
                    else if (tipoStr.startsWith("Recibo")) abrev = "RE";
                    else if (tipoStr.contains("FCE")) abrev = "FCE";
                    String numeroFactura = String.format("%05d-%08d",
                        Integer.parseInt((String) view.getCmbPuntoVenta().getSelectedItem()), nroComp);

                    int elsActualizados = 0;
                    int elsConError = 0;
                    int elsEncontrados = 0;
                    String baseReparsoft = null;
                    for (ItemFacturaDTO item : items) {
                        if (item.getElsReferencia() != null) {
                            elsEncontrados++;
                            if (baseReparsoft == null) {
                                String[] opciones = {"Bariloche (ordenesbrc)", "Buenos Aires (ordenesbsas)", "Cancelar"};
                                int resp = JOptionPane.showOptionDialog(view,
                                    "\u00bfA qu\u00e9 base de ReparSoft pertenece(n) el/los ELS?",
                                    "Seleccionar base ReparSoft",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null, opciones, opciones[0]);
                                if (resp == 0) baseReparsoft = "ordenesbrc";
                                else if (resp == 1) baseReparsoft = "ordenesbsas";
                                else break;
                            }
                            boolean ok = controladorReparsoft.escribirNumeroFactura(
                                item.getElsReferencia(), numeroFactura, baseReparsoft);
                            if (ok) elsActualizados++;
                            else elsConError++;
                        }
                    }
                    if (elsEncontrados == 0) {
                        JOptionPane.showMessageDialog(view,
                            "Ning\u00fan item de la factura tiene un n\u00famero de ELS.\n"
                            + "No se actualiz\u00f3 el n\u00famero de factura en ReparSoft.",
                            "ReparSoft", JOptionPane.WARNING_MESSAGE);
                    } else if (elsActualizados > 0) {
                        JOptionPane.showMessageDialog(view,
                            "N\u00famero de factura registrado en " + elsActualizados
                            + " ELS de " + baseReparsoft + " correctamente."
                            + (elsConError > 0 ? "\n" + elsConError + " ELS no se pudieron actualizar." : ""),
                            "ReparSoft", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(view,
                            "No se pudo actualizar ning\u00fan ELS en " + baseReparsoft + ".\n"
                            + "Verifique que los n\u00fameros de ELS existan en la base de datos seleccionada.",
                            "ReparSoft", JOptionPane.ERROR_MESSAGE);
                    }
                }

                String modo = isModoPrueba() ? " (MODO PRUEBA)" : "";
                String msg = "Factura emitida exitosamente!\n"
                    + (respuesta.getCae() != null ? "CAE: " + respuesta.getCae() + "\n" : "")
                    + (respuesta.getVencimiento() != null ? "Vto CAE: " + respuesta.getVencimiento() : "")
                    + modo;
                JOptionPane.showMessageDialog(view, msg, "Exito", JOptionPane.INFORMATION_MESSAGE);
                view.getLblEstadoPago().setText("Estado: Emitida" + modo);
                limpiarTodo();
            } else {
                JOptionPane.showMessageDialog(view, "Error: " + respuesta.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // ===================== HELPERS =====================

    private boolean validarDatosReceptor() {
        if (view.getCmbNroDoc().getEditorText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese numero de documento", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (view.getCmbRazonSocial().getEditorText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Ingrese razon social", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private CuitConfigDTO obtenerCuitSeleccionado() {
        List<CuitConfigDTO> cuits = getCuitsActivos();
        if (cuits.isEmpty()) return null;
        return cuits.get(0);
    }

    private int obtenerTipoCodigo() {
        String tipo = (String) view.getCmbTipoComprobante().getSelectedItem();
        if (tipo.contains("(FCE)")) return 331;
        if (tipo.endsWith(" A")) {
            if (tipo.startsWith("Factura")) return 1;
            if (tipo.startsWith("Nota de Debito")) return 2;
            if (tipo.startsWith("Nota de Credito")) return 3;
            if (tipo.startsWith("Recibo")) return 4;
        }
        if (tipo.endsWith(" B")) {
            if (tipo.startsWith("Factura")) return 6;
            if (tipo.startsWith("Nota de Debito")) return 7;
            if (tipo.startsWith("Nota de Credito")) return 8;
            if (tipo.startsWith("Recibo")) return 9;
        }
        if (tipo.endsWith(" C")) {
            if (tipo.startsWith("Factura")) return 11;
            if (tipo.startsWith("Nota de Debito")) return 12;
            if (tipo.startsWith("Nota de Credito")) return 13;
            if (tipo.startsWith("Recibo")) return 10;
        }
        return 6;
    }

    private String obtenerDocReceptor() {
        String doc = view.getCmbNroDoc().getEditorText().trim();
        if (doc.isEmpty()) doc = "0";
        return doc;
    }

    private String obtenerCondicionesVenta() {
        StringBuilder sb = new StringBuilder();
        javax.swing.JCheckBox[] checks = {
            view.getChkContado(), view.getChkTarjetaDeb(), view.getChkTarjetaCred(),
            view.getChkCC(), view.getChkCheque(), view.getChkTransf(), view.getChkOtra()
        };
        String[] labels = {"Contado", "Tarjeta Debito", "Tarjeta Credito", "Cuenta Corriente", "Cheque", "Transf. Bancaria", "Otra"};
        for (int i = 0; i < checks.length; i++) {
            if (checks[i].isSelected()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(labels[i]);
            }
        }
        return sb.toString();
    }

    private LocalDate parseFechaChooser(JComponent comp) {
        if (comp instanceof JTextField) {
            try {
                return LocalDate.parse(((JTextField) comp).getText(), FMT);
            } catch (Exception e) {
                return LocalDate.now();
            }
        }
        try {
            Date date = (Date) comp.getClass().getMethod("getDate").invoke(comp);
            if (date != null) {
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } catch (Exception e) {
        }
        return LocalDate.now();
    }

    private void limpiarTodo() {
        Date hoy = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        for (JComponent dc : new JComponent[]{view.getDateFecha(), view.getDatePeriodoDesde(), view.getDatePeriodoHasta(), view.getDatePeriodoVto()}) {
            try {
                dc.getClass().getMethod("setDate", Date.class).invoke(dc, hoy);
            } catch (Exception e) {
            }
        }
        view.getCmbRazonSocial().setSelectedItem(null);
        view.setRazonSocial("");
        view.getCmbNroDoc().setSelectedItem(null);
        view.setNroDoc("");
        view.getCmbCondicionIva().setSelectedIndex(0);
        view.getTxtDomicilio().setText("");
        view.getTxtEmail().setText("");
        view.getTxtComprobanteAsoc().setText("");
        limpiarItems();
        view.getCardLayout().show(view.getPanelPrincipal(), "datos");
    }

    // ===================== ITEMS TABLE =====================

    private void agregarItem() {
        view.getModeloItems().addRow(new Object[]{"", "", "1", "Unidad", "0,00", "0,00", true});
    }

    private void eliminarItem() {
        int row = view.getTablaItems().getSelectedRow();
        if (row >= 0) {
            view.getModeloItems().removeRow(row);
            recalcularTotales();
        }
    }

    private void recalcularTotales() {
        recalculando = true;
        DefaultTableModel model = view.getModeloItems();
        BigDecimal totalNeto = BigDecimal.ZERO;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (!Boolean.TRUE.equals(model.getValueAt(i, 6))) continue;
            try {
                Object cantObj = model.getValueAt(i, 2);
                Object precioObj = model.getValueAt(i, 4);
                BigDecimal cantidad = parseBigDecimal(cantObj.toString());
                BigDecimal precio = parseBigDecimal(precioObj.toString().replace("$", "").trim());
                BigDecimal subtotal = redondear(cantidad.multiply(precio));
                model.setValueAt(DF.format(subtotal), i, 5);
                totalNeto = totalNeto.add(subtotal);
            } catch (Exception e) {
                model.setValueAt("0,00", i, 5);
            }
        }
        BigDecimal alicuota = BigDecimal.ZERO;
        try {
            String ivaStr = view.getCmbAlicuotaIva().getSelectedItem().toString().replace("%", "");
            alicuota = new BigDecimal(ivaStr);
        } catch (Exception ignored) {}
        BigDecimal ivaTotal = redondear(totalNeto.multiply(alicuota).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
        BigDecimal otros = BigDecimal.ZERO;
        try {
            otros = parseBigDecimal(view.getTxtOtrosImpuestos().getText());
        } catch (Exception e) {}
        BigDecimal totalConIva = totalNeto.add(ivaTotal).add(otros);
        view.getTxtImporteNeto().setText("$ " + DF.format(totalNeto));
        view.getTxtImporteIva().setText("$ " + DF.format(ivaTotal));
        view.getTxtImporteTotal().setText("$ " + DF.format(totalConIva));
        view.getLblTotal().setText("$ " + DF.format(totalConIva));
        recalculando = false;
    }

    private String obtenerDescripcionItems() {
        DefaultTableModel model = view.getModeloItems();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (sb.length() > 0) sb.append("; ");
            sb.append(model.getValueAt(i, 0)).append(" ").append(model.getValueAt(i, 1));
        }
        return sb.toString();
    }

    private List<ItemFacturaDTO> obtenerItems() {
        DefaultTableModel model = view.getModeloItems();
        List<ItemFacturaDTO> items = new ArrayList<>();
        BigDecimal alicuotaGlobal = BigDecimal.ZERO;
        try {
            String ivaStr = view.getCmbAlicuotaIva().getSelectedItem().toString().replace("%", "");
            alicuotaGlobal = new BigDecimal(ivaStr);
        } catch (Exception ignored) {}
        for (int i = 0; i < model.getRowCount(); i++) {
            if (!Boolean.TRUE.equals(model.getValueAt(i, 6))) continue;
            try {
                String codigo = (String) model.getValueAt(i, 0);
                String descripcion = (String) model.getValueAt(i, 1);
                BigDecimal cantidad = parseBigDecimal(model.getValueAt(i, 2).toString());
                String unidad = (String) model.getValueAt(i, 3);
                BigDecimal precio = parseBigDecimal(model.getValueAt(i, 4).toString().replace("$", "").trim());
                BigDecimal subtotal = redondear(cantidad.multiply(precio));
                ItemFacturaDTO item = new ItemFacturaDTO(codigo, descripcion, cantidad, unidad, precio, subtotal);
                item.setAlicuotaIva(alicuotaGlobal);
                item.setElsReferencia(codigo.matches("\\d+") ? Integer.parseInt(codigo) : null);
                items.add(item);
            } catch (Exception e) {
                System.err.println("Error leyendo item " + i + ": " + e.getMessage());
            }
        }
        return items;
    }

    private void limpiarItems() {
        view.getModeloItems().setRowCount(0);
        view.getTxtImporteNeto().setText("");
        view.getTxtImporteIva().setText("");
        view.getTxtImporteTotal().setText("");
        view.getTxtOtrosImpuestos().setText("0,00");
        view.getLblTotal().setText("$ 0,00");
        view.getLblEstadoPago().setText("Estado: Pendiente de pago");
    }

    // ===================== EMITIR FACTURA (existing) =====================

    public RespuestaCAE emitirFactura(ComprobanteDTO comprobante) {
        return emitirFactura(comprobante, null);
    }

    public RespuestaCAE emitirFactura(ComprobanteDTO comprobante, List<ItemFacturaDTO> items) {
        try {
            CuitConfigDTO cuit = cuitDAO.buscarPorCuit(comprobante.getCuitEmisor());
            if (cuit == null) {
                RespuestaCAE error = new RespuestaCAE();
                error.setError("CUIT no encontrado en la configuración");
                return error;
            }

            if (cuit.getRutaCertificado() == null || cuit.getRutaCertificado().isEmpty()) {
                RespuestaCAE error = new RespuestaCAE();
                error.setError("Certificado no configurado para este CUIT");
                return error;
            }

            long ultimoNumero = comprobanteDAO.getUltimoNumero(
                comprobante.getCuitEmisor(),
                comprobante.getPuntoVenta(),
                comprobante.getTipoComprobante()
            );
            comprobante.setNumero(ultimoNumero + 1);

            if (comprobante.getFechaEmision() == null) {
                comprobante.setFechaEmision(LocalDate.now());
            }

            if (comprobante.getImporteTotal() == null) {
                BigDecimal total = comprobante.getImporteNeto();
                if (comprobante.getImporteIva() != null) {
                    total = total.add(comprobante.getImporteIva());
                }
                comprobante.setImporteTotal(total);
            }

            RespuestaCAE respuesta;
            if (modoPrueba) {
                respuesta = new RespuestaCAE(
                    String.format("%011d", comprobante.hashCode() % 100000000000L),
                    LocalDate.now().plusDays(15),
                    comprobante.getNumero()
                );
                respuesta.setMensaje("MODO PRUEBA - CAE ficticio generado");
            } else {
                respuesta = servicioWSFEv1.emitirComprobante(
                    comprobante,
                    cuit.getRutaCertificado(),
                    cuit.getPasswordCert()
                );
            }

            if (respuesta.isExitosa()) {
                comprobante.setCae(respuesta.getCae());
                comprobante.setVencimientoCae(respuesta.getVencimiento());
                if (respuesta.getNumeroComprobante() != null) {
                    comprobante.setNumero(respuesta.getNumeroComprobante());
                }

                int id = comprobanteDAO.insertar(comprobante);
                if (id > 0) {
                    comprobante.setId(id);

                    if (items != null && !items.isEmpty()) {
                        guardarItemsFactura(id, items);
                    }

                    String rutaPDF = new GestorPDF().generarFactura(comprobante, cuit, items);
                    if (rutaPDF != null) {
                        comprobante.setRutaPdf(rutaPDF);
                        comprobanteDAO.actualizar(comprobante);
                    }
                } else {
                    respuesta.setError("Error al guardar comprobante en base de datos");
                }
            }

            return respuesta;

        } catch (Exception e) {
            RespuestaCAE error = new RespuestaCAE();
            error.setError("Error emitiendo factura: " + e.getMessage());
            System.err.println("Error en ControladorFacturacion: " + e.getMessage());
            e.printStackTrace();
            return error;
        }
    }

    public void escribirNumeroFacturaEnReparsoft(Integer els, String numeroFactura) {
        if (els != null && numeroFactura != null) {
            controladorReparsoft.escribirNumeroFactura(els, numeroFactura);
        }
    }

    public void escribirNumeroFacturaEnReparsoft(Integer els, String numeroFactura, String baseDatos) {
        if (els != null && numeroFactura != null && baseDatos != null) {
            controladorReparsoft.escribirNumeroFactura(els, numeroFactura, baseDatos);
        }
    }

    public List<RemitoReparsoftDTO> listarRemitos(String baseDatos) {
        return controladorReparsoft.listarRemitos(baseDatos);
    }

    public String getBaseDatosELS(int els) {
        return controladorReparsoft.getBaseDatosELS(els);
    }

    public boolean verificarELS(int els) {
        return controladorReparsoft.verificarELS(els);
    }

    public ComprobanteDTO crearComprobanteDesdeELS(int els) {
        return controladorReparsoft.crearComprobanteDesdeELS(els);
    }

    public List<ClienteDTO> listarClientes() {
        return controladorClientes.listarTodos();
    }

    public List<ClienteDTO> buscarClientesPorRazonSocial(String termino) {
        return controladorClientes.buscarPorRazonSocial(termino);
    }

    public ClienteDTO buscarClientePorDocumento(String tipo, String nro) {
        return controladorClientes.buscarPorDocumento(tipo, nro);
    }

    public int guardarCliente(ClienteDTO cliente) {
        return controladorClientes.guardarCliente(cliente);
    }

    public ComprobanteDTO buscarComprobante(int id) {
        return comprobanteDAO.buscarPorId(id);
    }

    public ComprobanteDTO buscarComprobante(String cae) {
        return comprobanteDAO.buscarPorCAE(cae);
    }

    public List<ComprobanteDTO> listarComprobantes() {
        return comprobanteDAO.listarTodos();
    }

    public List<ComprobanteDTO> listarComprobantes(String cuit) {
        return comprobanteDAO.listarPorCuit(cuit);
    }

    public List<ComprobanteDTO> buscarComprobantes(LocalDate desde, LocalDate hasta) {
        return comprobanteDAO.buscarPorFecha(desde, hasta);
    }

    public List<ComprobanteDTO> listarComprobantesSinPDF() {
        return comprobanteDAO.listarSinPDF();
    }

    public boolean actualizarComprobante(ComprobanteDTO comprobante) {
        return comprobanteDAO.actualizar(comprobante);
    }

    public CuitConfigDTO getCuitActivo(String cuit) {
        return cuitDAO.buscarPorCuit(cuit);
    }

    public List<CuitConfigDTO> getCuitsActivos() {
        return cuitDAO.listarActivos();
    }

    public void setEntorno(String entorno) {
        servicioWSFEv1.setEntorno(entorno);
    }

    public String regenerarPDF(ComprobanteDTO comprobante) {
        return regenerarPDF(comprobante, null);
    }

    public String regenerarPDF(ComprobanteDTO comprobante, List<ItemFacturaDTO> items) {
        CuitConfigDTO cuit = cuitDAO.buscarPorCuit(comprobante.getCuitEmisor());
        if (cuit == null) return null;
        String rutaPDF = new GestorPDF().generarFactura(comprobante, cuit, items);
        if (rutaPDF != null) {
            comprobante.setRutaPdf(rutaPDF);
            comprobanteDAO.actualizar(comprobante);
        }
        return rutaPDF;
    }

    public void setModoPrueba(boolean modoPrueba) {
        this.modoPrueba = modoPrueba;
    }

    public boolean isModoPrueba() {
        return modoPrueba;
    }

    public void guardarItemsFactura(int comprobanteId, List<ItemFacturaDTO> items) {
        new com.els.facturacion.dao.FacturaItemDAO().insertarItems(comprobanteId, items);
    }

    public List<ItemFacturaDTO> getItemsFactura(int comprobanteId) {
        return new com.els.facturacion.dao.FacturaItemDAO().buscarPorComprobante(comprobanteId);
    }

    public List<ComprobanteDTO> listarFacturasPorEstado(String estado) {
        List<ComprobanteDTO> todas = comprobanteDAO.listarTodos();
        List<ComprobanteDTO> filtradas = new ArrayList<>();
        for (ComprobanteDTO c : todas) {
            if (estado == null || estado.isEmpty() || estado.equals(c.getEstadoPago())) {
                filtradas.add(c);
            }
        }
        return filtradas;
    }

    public void actualizarEstadoPago(int comprobanteId, String estado) {
        ComprobanteDTO comp = comprobanteDAO.buscarPorId(comprobanteId);
        if (comp != null) {
            comp.setEstadoPago(estado);
            String sql = "UPDATE comprobantes SET estado_pago = ? WHERE id = ?";
            try (Connection conn = com.els.facturacion.conexion.ConexionFacturacion.getInstancia().getConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, estado);
                ps.setInt(2, comprobanteId);
                ps.executeUpdate();
            } catch (Exception e) {
                System.err.println("Error actualizando estado pago: " + e.getMessage());
            }
        }
    }

    public List<ComprobanteDTO> buscarComprobantesPorReceptor(String cuitReceptor) {
        List<ComprobanteDTO> todas = comprobanteDAO.listarTodos();
        List<ComprobanteDTO> filtradas = new ArrayList<>();
        for (ComprobanteDTO c : todas) {
            if (cuitReceptor.equals(c.getCuitReceptor())) {
                filtradas.add(c);
            }
        }
        return filtradas;
    }

    // ===================== STATIC UTILITIES =====================

    public static BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        return new BigDecimal(value.trim().replace(".", "").replace(",", "."));
    }

    public static BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    public ItemCalculoDTO calcularItem(String cantidadStr, String precioStr, String ivaStr) {
        try {
            BigDecimal cantidad = parseBigDecimal(cantidadStr);
            BigDecimal precio = parseBigDecimal(precioStr.replace("$", "").trim());
            BigDecimal subtotal = redondear(cantidad.multiply(precio));
            String subtotalStr = DF.format(subtotal);

            BigDecimal alicuota = new BigDecimal(ivaStr.replace("%", ""));
            BigDecimal iva = redondear(subtotal.multiply(alicuota).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));

            return new ItemCalculoDTO(subtotal, iva, subtotalStr);
        } catch (Exception e) {
            return new ItemCalculoDTO();
        }
    }

    public static class TotalesDTO {
        private BigDecimal neto;
        private BigDecimal iva;
        private BigDecimal total;
        private List<String> errores;

        public TotalesDTO(BigDecimal neto, BigDecimal iva, BigDecimal total) {
            this.neto = neto;
            this.iva = iva;
            this.total = total;
            this.errores = new ArrayList<>();
        }

        public BigDecimal getNeto() { return neto; }
        public BigDecimal getIva() { return iva; }
        public BigDecimal getTotal() { return total; }
        public List<String> getErrores() { return errores; }
        public boolean hayError() { return !errores.isEmpty(); }
        public void addError(String e) { errores.add(e); }
    }

    public static class ItemCalculoDTO {
        private BigDecimal subtotal;
        private BigDecimal iva;
        private String subtotalStr;
        private boolean ok;

        public ItemCalculoDTO(BigDecimal subtotal, BigDecimal iva, String subtotalStr) {
            this.subtotal = subtotal;
            this.iva = iva;
            this.subtotalStr = subtotalStr;
            this.ok = true;
        }

        public ItemCalculoDTO() {
            this.ok = false;
        }

        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getIva() { return iva; }
        public String getSubtotalStr() { return subtotalStr; }
        public boolean isOk() { return ok; }
    }
}
