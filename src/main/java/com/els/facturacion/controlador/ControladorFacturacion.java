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
import com.els.facturacion.pdf.GestorFacturaPDF;
import com.els.facturacion.vista.VentanaFacturacion;
import com.els.facturacion.vista.VentanaClientes;
import com.els.facturacion.vista.VentanaImportarRemito;
import com.els.facturacion.vista.VentanaEquiposPresupuestados;
import com.els.facturacion.util.UbicacionSistema;
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
import javax.swing.SwingWorker;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

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
                String tipo = (String) view.getCmbTipoComprobante().getSelectedItem();
                String cliente = view.getCmbRazonSocial().getEditorText().trim();
                view.setSubtituloOp(tipo + " para " + cliente);
                view.getCardLayout().show(view.getPanelPrincipal(), "operacion");
            }
        });
        view.getBtnAnterior().addActionListener(e -> view.getCardLayout().show(view.getPanelPrincipal(), "datos"));

        // Emission
        view.getBtnEmitir().addActionListener(e -> btnEmitirAction());
        view.getBtnLimpiar().addActionListener(e -> limpiarTodo());
        view.getBtnVisualizarFactura().addActionListener(e -> visualizarFacturaAction());
        view.getBtnImportarRemito().addActionListener(e -> {
            if (!view.validarCamposObligatorios()) return;
            importarRemitoReparsoft();
        });
        view.getBtnVerEquipos().addActionListener(e -> {
            if (!view.validarCamposObligatorios()) return;
            verEquiposPresupuestados();
        });
        view.getBtnFacturarPorCliente().addActionListener(e -> {
            if (!view.validarCamposObligatorios()) return;
            view.setReceptorFieldsEnabled(true);
        });

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

        // Autocomplete on Enter
        JTextField editorRS = (JTextField) view.getCmbRazonSocial().getEditor().getEditorComponent();
        editorRS.addActionListener(e -> autocompletarPorRazonSocial());
        JTextField editorND = (JTextField) view.getCmbNroDoc().getEditor().getEditorComponent();
        editorND.addActionListener(e -> autocompletarPorDocumento());

        // Autocomplete on dropdown selection
        PopupMenuListener seleccionDropdown = new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                javax.swing.JComboBox<?> src = (javax.swing.JComboBox<?>) e.getSource();
                if (src.getSelectedItem() != null) {
                    if (src == view.getCmbRazonSocial()) autocompletarPorRazonSocial();
                    else if (src == view.getCmbNroDoc()) autocompletarPorDocumento();
                    else if (src == view.getCmbCondicionIva()) autocompletarPorCondicionIva();
                }
            }
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        };
        view.getCmbRazonSocial().addPopupMenuListener(seleccionDropdown);
        view.getCmbNroDoc().addPopupMenuListener(seleccionDropdown);
        view.getCmbCondicionIva().addPopupMenuListener(seleccionDropdown);

        // Menu actions removed - now handled by VentanaPrincipal
    }

    private void cargarEmisorActivo() {
        List<CuitConfigDTO> activos = cuitDAO.listarActivos();
        if (!activos.isEmpty()) {
            CuitConfigDTO emisor = activos.get(0);
            view.actualizarEmisor(emisor.getRazonSocial(), emisor.getCuit(), emisor.getCondicionIva(),
                emisor.getDomicilio(), emisor.getIngresosBrutos(), emisor.getFechaInicioActividades());
            view.actualizarTiposComprobante(emisor.getCondicionIva());
        } else {
            view.actualizarEmisor("", "", "", "", "", "");
        }
    }

    private void cargarClientes() {
        List<ClienteDTO> clientes = listarClientes();
        List<String> razones = new ArrayList<>();
        List<String> docs = new ArrayList<>();
        razones.add("");
        docs.add("");
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
        view.setTipoDoc(cli.getTipoDocumento() != null ? cli.getTipoDocumento() : "CUIT");
        view.getTxtDomicilio().setText(cli.getDomicilio() != null ? cli.getDomicilio() : "");
        view.getTxtEmail().setText(cli.getEmail() != null ? cli.getEmail() : "");
    }

    private void autocompletarPorCondicionIva() {
        String condIva = (String) view.getCmbCondicionIva().getSelectedItem();
        if (condIva == null) return;

        List<ClienteDTO> todos = listarClientes();
        List<ClienteDTO> matches = new ArrayList<>();
        for (ClienteDTO c : todos) {
            if (condIva.equals(c.getCondicionIva())) {
                matches.add(c);
            }
        }

        if (matches.size() == 1) {
            autocompletarCamposCliente(matches.get(0));
        } else if (matches.size() > 1) {
            String rs = view.getCmbRazonSocial().getEditorText().trim();
            if (!rs.isEmpty()) {
                for (ClienteDTO c : matches) {
                    if (c.getRazonSocial().equalsIgnoreCase(rs)) {
                        autocompletarCamposCliente(c);
                        return;
                    }
                }
            }
        }
    }

    // ===================== IMPORTAR REMITO =====================

    private void importarRemitoReparsoft() {
        RemitoReparsoftDTO remito = VentanaImportarRemito.mostrarDialog(view);
        if (remito == null) return;

        // Enable and populate receptor fields from remito data
        view.setReceptorFieldsEnabled(true);

        if (remito.getRazonSocialCliente() != null && !remito.getRazonSocialCliente().isEmpty()) {
            boolean encontrado = false;
            List<ClienteDTO> clientes = buscarClientesPorRazonSocial(remito.getRazonSocialCliente());
            for (ClienteDTO c : clientes) {
                if (c.getRazonSocial().equalsIgnoreCase(remito.getRazonSocialCliente())) {
                    view.setRazonSocial(c.getRazonSocial());
                    autocompletarPorRazonSocial();
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado && remito.getCuitCliente() != null && !remito.getCuitCliente().isEmpty()) {
                String cuitLimpio = remito.getCuitCliente().replaceAll("[^0-9]", "");
                for (ClienteDTO c : listarClientes()) {
                    if (c.getNroDocumento() != null && c.getNroDocumento().replaceAll("[^0-9]", "").equals(cuitLimpio)) {
                        view.setRazonSocial(c.getRazonSocial());
                        autocompletarPorRazonSocial();
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

        // Populate items table (always, so Siguiente finds them)
        DefaultTableModel model = view.getModeloItems();
        model.setRowCount(0);
        if (remito.getItems() != null) {
            for (RemitoReparsoftItem item : remito.getItems()) {
                String descripcion = "Reparaci\u00f3n de "
                    + (item.getEquipoNombre() != null ? item.getEquipoNombre() : "Sin equipo") + " "
                    + (item.getMarca() != null ? item.getMarca() : "") + " "
                    + (item.getModelo() != null ? item.getModelo() : "")
                    + " s/n: " + (item.getNumeroSerie() != null ? item.getNumeroSerie() : "");
                BigDecimal precio = item.getPrecioPeso() != null ? item.getPrecioPeso() : BigDecimal.ZERO;
                String precioStr = precio.compareTo(BigDecimal.ZERO) > 0
                    ? DF.format(precio)
                    : "0,00";
                String codigo = String.valueOf(item.getEls());
                model.addRow(new Object[]{codigo, descripcion, "1", "Unidad", precioStr, "0,00", true});
            }
        }
        recalcularTotales();

        // Validate receptor fields with red borders — stay on datosCard regardless
        view.validarReceptorConBordes();
    }

    // ===================== VER EQUIPOS PRESUPUESTADOS =====================

    private void verEquiposPresupuestados() {
        String razonSocial = view.getCmbRazonSocial().getEditorText().trim();

        List<RemitoReparsoftItem> equipos = VentanaEquiposPresupuestados.mostrarDialog(view, razonSocial);
        if (equipos == null || equipos.isEmpty()) return;

        // Enable and populate receptor fields from equipos data
        view.setReceptorFieldsEnabled(true);

        // Try to autofill from the first item's client name
        if (!equipos.isEmpty()) {
            String clientName = equipos.get(0).getNombreCliente();
            if (clientName != null && !clientName.isEmpty()) {
                List<ClienteDTO> matches = buscarClientesPorRazonSocial(clientName);
                for (ClienteDTO c : matches) {
                    if (c.getRazonSocial().equalsIgnoreCase(clientName)) {
                        view.setRazonSocial(c.getRazonSocial());
                        autocompletarPorRazonSocial();
                        break;
                    }
                }
            }
        }

        // Populate items table (always, so Siguiente finds them)
        DefaultTableModel model = view.getModeloItems();
        model.setRowCount(0);
        for (RemitoReparsoftItem item : equipos) {
            String descripcion = "Reparaci\u00f3n de "
                + (item.getEquipoNombre() != null ? item.getEquipoNombre() : "Sin equipo") + " "
                + (item.getMarca() != null ? item.getMarca() : "") + " "
                + (item.getModelo() != null ? item.getModelo() : "")
                + " s/n: " + (item.getNumeroSerie() != null ? item.getNumeroSerie() : "");
            BigDecimal precio = item.getPrecioPeso() != null ? item.getPrecioPeso() : BigDecimal.ZERO;
            String precioStr = precio.compareTo(BigDecimal.ZERO) > 0
                ? DF.format(precio)
                : "0,00";
            String codigo = String.valueOf(item.getEls());
            model.addRow(new Object[]{codigo, descripcion, "1", "Unidad", precioStr, "0,00", true});
        }
        recalcularTotales();

        // Validate receptor fields with red borders — stay on datosCard regardless
        view.validarReceptorConBordes();
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
            String errorTipo = validarTipoComprobante(cuiSelected, comprobante);
            if (errorTipo != null) {
                JOptionPane.showMessageDialog(view, errorTipo, "Tipo de comprobante inválido", JOptionPane.WARNING_MESSAGE);
                return;
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
                    String baseReparsoft = UbicacionSistema.getNombreDbReparsoft();
                    for (ItemFacturaDTO item : items) {
                        if (item.getElsReferencia() != null) {
                            elsEncontrados++;
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
               //view.getLblEstadoPago().setText("Estado: Emitida" + modo);
                limpiarTodo();
            } else {
                JOptionPane.showMessageDialog(view, "Error: " + respuesta.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void visualizarFacturaAction() {
        try {
            CuitConfigDTO cuit = obtenerCuitSeleccionado();
            if (cuit == null) {
                JOptionPane.showMessageDialog(view, "Configure un CUIT emisor en Herramientas > Configurar Certificados", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ComprobanteDTO comprobante = new ComprobanteDTO();
            comprobante.setTipoComprobante(obtenerTipoCodigo());
            comprobante.setPuntoVenta(Integer.parseInt((String) view.getCmbPuntoVenta().getSelectedItem()));
            comprobante.setCuitReceptor(obtenerDocReceptor());
            comprobante.setRazonSocialRec(view.getCmbRazonSocial().getEditorText().trim());
            comprobante.setFechaEmision(parseFechaChooser(view.getDateFecha()));
            comprobante.setPeriodoDesde(parseFechaChooser(view.getDatePeriodoDesde()));
            comprobante.setPeriodoHasta(parseFechaChooser(view.getDatePeriodoHasta()));
            comprobante.setPeriodoVto(parseFechaChooser(view.getDatePeriodoVto()));
            comprobante.setCondicionIvaReceptor((String) view.getCmbCondicionIva().getSelectedItem());
            comprobante.setDomicilioReceptor(view.getTxtDomicilio().getText().trim());
            comprobante.setCondicionesVenta(obtenerCondicionesVenta());
            comprobante.setDescripcion(obtenerDescripcionItems());
            try {
                comprobante.setImporteNeto(parseBigDecimal(view.getTxtImporteNeto().getText().replace("$", "").trim()));
                comprobante.setImporteIva(parseBigDecimal(view.getTxtImporteIva().getText().replace("$", "").trim()));
                comprobante.setImporteTotal(parseBigDecimal(view.getTxtImporteTotal().getText().replace("$", "").trim()));
            } catch (Exception e) {
            }
            List<ItemFacturaDTO> items = obtenerItems();

            java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
            Map<String, Object> params = new HashMap<>();
            String errorTipo = validarTipoComprobante(cuit, comprobante);
            if (errorTipo != null) {
                JOptionPane.showMessageDialog(view, errorTipo, "Tipo de comprobante inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            params.put("EMISOR_RAZON_SOCIAL", cuit.getRazonSocial());
            params.put("EMISOR_DOMICILIO", cuit.getDomicilio() != null ? cuit.getDomicilio() : "");
            params.put("EMISOR_CUIT", cuit.getCuit());
            params.put("EMISOR_ING_BRUTOS", cuit.getIngresosBrutos() != null ? cuit.getIngresosBrutos() : "");
            params.put("EMISOR_INICIO_ACT", cuit.getFechaInicioActividades() != null ? cuit.getFechaInicioActividades() : "");
            params.put("EMISOR_CONDICION_IVA", cuit.getCondicionIva());
            params.put("PUNTO_VENTA", String.format("%03d", cuit.getPuntoVenta()));
            params.put("COMP_NRO", String.format("%08d", comprobante.getNumero()));
            params.put("FECHA_EMISION", comprobante.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            params.put("PERIODO_DESDE", comprobante.getPeriodoDesde() != null ? comprobante.getPeriodoDesde().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            params.put("PERIODO_HASTA", comprobante.getPeriodoHasta() != null ? comprobante.getPeriodoHasta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
            params.put("FECHA_VTO_PAGO", comprobante.getPeriodoVto() != null ? comprobante.getPeriodoVto().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");

            params.put("CLIENTE_CUIT", comprobante.getCuitReceptor());
            params.put("CLIENTE_RAZON_SOCIAL", comprobante.getRazonSocialRec());
            params.put("CLIENTE_CONDICION_IVA", comprobante.getCondicionIvaReceptor() != null ? comprobante.getCondicionIvaReceptor() : "");
            params.put("CLIENTE_DOMICILIO", comprobante.getDomicilioReceptor() != null ? comprobante.getDomicilioReceptor() : "");
            params.put("CLIENTE_CONDICION_VENTA", comprobante.getCondicionesVenta() != null ? comprobante.getCondicionesVenta() : "");

            params.put("SUBTOTAL", comprobante.getImporteNeto() != null ? df.format(comprobante.getImporteNeto().setScale(2, RoundingMode.HALF_UP)) : "0,00");
            params.put("OTROS_TRIBUTOS", comprobante.getOtrosImpuestos() != null ? df.format(comprobante.getOtrosImpuestos().setScale(2, RoundingMode.HALF_UP)) : "0,00");
            params.put("IMPORTE_TOTAL", comprobante.getImporteTotal() != null ? df.format(comprobante.getImporteTotal().setScale(2, RoundingMode.HALF_UP)) : "0,00");

            params.put("CAE_NRO", comprobante.getCae() != null ? comprobante.getCae() : "");
            params.put("CAE_VENCIMIENTO", "");
            params.put("QR_IMAGE_PATH", "");
            params.put("COPIA_LABEL", "ORIGINAL");

            new SwingWorker<JasperPrint, Void>() {
                @Override
                protected JasperPrint doInBackground() throws Exception {
                    InputStream jasperStream = getClass().getClassLoader()
                        .getResourceAsStream("reportes/factura.jasper");
                    if (jasperStream == null) {
                        throw new Exception("No se encontro reportes/factura.jasper");
                    }
                    JRDataSource dataSource = (items != null && !items.isEmpty())
                        ? new JRBeanCollectionDataSource(items)
                        : new JRBeanCollectionDataSource(java.util.Collections.singletonList(
                            new ItemFacturaDTO("", comprobante.getDescripcion() != null ? comprobante.getDescripcion() : "",
                                BigDecimal.ONE, "Unidad", comprobante.getImporteNeto() != null ? comprobante.getImporteNeto() : BigDecimal.ZERO, BigDecimal.ZERO)));
                    return JasperFillManager.fillReport(jasperStream, params, dataSource);
                }
                @Override
                protected void done() {
                    try {
                        JasperPrint jp = get();
                        JasperViewer viewer = new JasperViewer(jp, false);
                        viewer.setVisible(true);
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            viewer.toFront();
                            viewer.repaint();
                            viewer.requestFocus();
                        });
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(view, "Error al generar la vista previa: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== HELPERS =====================

    private boolean completarDatosReceptorPendientes() {
        String condIva = (String) view.getCmbCondicionIva().getSelectedItem();
        if (condIva == null || condIva.isEmpty()) {
            String[] options = {"", "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
                "Responsable Monotributo"};
            Object input = JOptionPane.showInputDialog(view,
                "Seleccione la Condici\u00f3n IVA del receptor:", "Datos del Receptor",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (input == null || input.toString().isEmpty()) return false;
            view.setCmbCondicionIva(input.toString());
            condIva = input.toString();
        }

        String nroDoc = view.getCmbNroDoc().getEditorText().trim();
        String razonSocial = view.getCmbRazonSocial().getEditorText().trim();
        String domicilio = view.getTxtDomicilio().getText().trim();

        if (!"Consumidor Final".equals(condIva)) {
            if (nroDoc.isEmpty()) {
                String input = JOptionPane.showInputDialog(view,
                    "Ingrese el CUIT/Nro.Doc del receptor:", "Datos del Receptor",
                    JOptionPane.QUESTION_MESSAGE);
                if (input == null || input.trim().isEmpty()) return false;
                view.setNroDoc(input.trim());
                if (input.trim().length() > 8) view.setTipoDoc("CUIT");
            }
            if (razonSocial.isEmpty()) {
                String input = JOptionPane.showInputDialog(view,
                    "Ingrese la Raz\u00f3n Social del receptor:", "Datos del Receptor",
                    JOptionPane.QUESTION_MESSAGE);
                if (input == null || input.trim().isEmpty()) return false;
                view.setRazonSocial(input.trim());
            }
            if (domicilio.isEmpty()) {
                String input = JOptionPane.showInputDialog(view,
                    "Ingrese el Domicilio del receptor:", "Datos del Receptor",
                    JOptionPane.QUESTION_MESSAGE);
                if (input == null) return false;
                view.getTxtDomicilio().setText(input.trim());
            }
        }
        return true;
    }

    private boolean validarDatosReceptor() {
        view.clearReceptorErrorBorders();

        if (view.getCmbPuntoVenta().getSelectedItem() == null || view.getCmbPuntoVenta().getSelectedItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Seleccione un Punto de Venta", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (view.getCmbTipoComprobante().getSelectedItem() == null || view.getCmbTipoComprobante().getSelectedItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Seleccione un Tipo de Comprobante", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (view.getCmbConcepto().getSelectedItem() == null || view.getCmbConcepto().getSelectedItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Seleccione un Concepto", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String condVenta = obtenerCondicionesVenta();
        if (condVenta.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                "Debe seleccionar al menos una Condicion de Venta",
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return view.validarReceptorConBordes();
    }

    private CuitConfigDTO obtenerCuitSeleccionado() {
        List<CuitConfigDTO> cuits = getCuitsActivos();
        if (cuits.isEmpty()) return null;
        return cuits.get(0);
    }

    private String validarTipoComprobante(CuitConfigDTO emisor, ComprobanteDTO comprobante) {
        String condEmisor = emisor.getCondicionIva();
        String condReceptor = comprobante.getCondicionIvaReceptor();
        int tipo = comprobante.getTipoComprobante();
        String tipoTexto = (String) view.getCmbTipoComprobante().getSelectedItem();
        if (tipoTexto == null) tipoTexto = "desconocido";

        if ("IVA Responsable Inscripto".equals(condEmisor)) {
            boolean esTipoA = tipo >= 1 && tipo <= 4;
            boolean esTipoB = tipo >= 6 && tipo <= 9;
            boolean receptorEsRI = "IVA Responsable Inscripto".equals(condReceptor);

            if (esTipoA && !receptorEsRI) {
                return tipoTexto + " requiere un receptor Responsable Inscripto.\n"
                    + "El receptor es \"" + condReceptor + "\".\n\n"
                    + "Cambie a Factura B o seleccione un cliente Responsable Inscripto.";
            }
            if (esTipoB && receptorEsRI) {
                return tipoTexto + " no puede emitirse a un Responsable Inscripto.\n"
                    + "El receptor es \"" + condReceptor + "\".\n\n"
                    + "Cambie a Factura A o seleccione un cliente Consumidor Final / Exento / Monotributo.";
            }
        }

        return null;
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

            long ultimoNumero;
            if (modoPrueba) {
                ultimoNumero = comprobanteDAO.getUltimoNumero(
                    comprobante.getCuitEmisor(),
                    comprobante.getPuntoVenta(),
                    comprobante.getTipoComprobante()
                );
            } else {
                long desdeARCA = servicioWSFEv1.consultarUltimoAutorizado(
                    comprobante.getCuitEmisor(),
                    comprobante.getPuntoVenta(),
                    comprobante.getTipoComprobante(),
                    cuit.getRutaCertificado(),
                    cuit.getPasswordCert()
                );
                if (desdeARCA > 0) {
                    ultimoNumero = desdeARCA;
                    System.out.println("Último autorizado desde ARCA: " + ultimoNumero);
                } else {
                    ultimoNumero = comprobanteDAO.getUltimoNumero(
                        comprobante.getCuitEmisor(),
                        comprobante.getPuntoVenta(),
                        comprobante.getTipoComprobante()
                    );
                    System.out.println("FECompUltimoAutorizado no disponible, usando contador local: " + ultimoNumero);
                }
            }
            comprobante.setNumero(ultimoNumero + 1);
            System.out.println("DEBUG emitirFactura: cuitEmisor=" + comprobante.getCuitEmisor()
                + " ptoVta=" + comprobante.getPuntoVenta()
                + " tipo=" + comprobante.getTipoComprobante()
                + " ultimoAutorizado=" + ultimoNumero
                + " numero=" + comprobante.getNumero()
                + " fecha=" + comprobante.getFechaEmision()
                + " modoPrueba=" + modoPrueba);

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

            RespuestaCAE respuesta = null;
            if (modoPrueba) {
                respuesta = new RespuestaCAE(
                    String.format("%011d", comprobante.hashCode() % 100000000000L),
                    LocalDate.now().plusDays(15),
                    comprobante.getNumero()
                );
                respuesta.setMensaje("MODO PRUEBA - CAE ficticio generado");
            } else {
                int intentos = 0;
                while (intentos < 100) {
                    respuesta = servicioWSFEv1.emitirComprobante(
                        comprobante,
                        cuit.getRutaCertificado(),
                        cuit.getPasswordCert()
                    );
                    if (respuesta.isExitosa() || !"10016".equals(respuesta.getCodigoError())) break;
                    comprobante.setNumero(comprobante.getNumero() + 1);
                    intentos++;
                    System.out.println("10016 → reintento " + intentos + " con número: " + comprobante.getNumero());
                }
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

                    String rutaPDF = new GestorFacturaPDF().generarFactura(comprobante, cuit, items);
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
        String rutaPDF = new GestorFacturaPDF().generarFactura(comprobante, cuit, items);
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
