package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorPagos;
import com.els.facturacion.controlador.ControladorReparsoft;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class VentanaPagos extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_INPUT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_INPUT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FUENTE_TABLA = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Color DISABLED_FG_LIGHT = new Color(95, 97, 106);
    private static final Color DISABLED_FG_DARK = new Color(210, 207, 190);
    private static final Color LIGHT_READONLY_BG = new Color(236, 237, 241);
    private static final Color LIGHT_EDITABLE_BG = new Color(255, 253, 230);
    private static final Color DARK_READONLY_BG = new Color(28, 33, 55);
    private static final Color DARK_EDITABLE_BG = new Color(22, 27, 45);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private ControladorPagos controlador;
    private ControladorReparsoft controladorReparsoft;

    private JTable tablaFacturas;
    private DefaultTableModel modeloTablaFacturas;

    private JTable tablaItems;
    private DefaultTableModel modeloTablaItems;

    private JLabel lblSaldoPendiente;
    private JLabel lblInfoFactura;
    private JLabel lblTotalFactura;
    private JTextField txtMontoPago;
    private JComboBox<String> cmbFormaPago;
    private JLabel lblImporte;
    private JButton btnPagarItem;
    private JButton btnPagarCompleta;
    private JButton btnPagar;
    private JButton btnRefresh;

    private JPanel statusBar;
    private JLabel lblStatus;
    private JLabel lblTitulo;
    private JLabel lblFiltroCliente;
    private JComboBox<String> cmbFiltroCliente;
    private JPanel panelFiltroFacturas;
    private JTextField editorFiltroFacturas;

    // FIX: live-theme — contenedores visibles (antes locales)
    private JSplitPane splitHorizontal;
    private JPanel panelFacturas;
    private JPanel panelDetalle;
    private JPanel panelHeader;
    private JPanel rowSuperior;
    private JPanel panelItems;
    private JPanel panelInferior;
    private JPanel panelTitulo;
    private JPanel box1;
    private JPanel box2;
    private JPanel panelAcciones;
    private JScrollPane scrollFacturas;
    private JScrollPane scrollItems;

    private int comprobanteSeleccionadoId = -1;
    private List<Integer> facturaIds;
    private List<Integer> itemIds;
    private List<Integer> itemEls;
    private boolean txtMontoPagoUpdating = false;
    private List<ComprobanteDTO> allFacturas;
    private List<String> allClientes;
    private boolean actualizandoComboFacturas;

    public VentanaPagos() {
        controlador = new ControladorPagos();
        controladorReparsoft = new ControladorReparsoft();
        initComponents();
        applyTheme(currentTheme);
        cargarFacturas();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Pagos / Cobranzas");
        setSize(1024, 768);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        lblTitulo = new JLabel("GESTI\u00d3N DE PAGOS / COBRANZAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);

        panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(currentTheme.bgSurface);
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);

        splitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitHorizontal.setBorder(null);
        splitHorizontal.setBackground(currentTheme.bgSurface);

        btnPagarItem = new JButton("PAGAR ITEM");
        estilizarBoton(btnPagarItem);
        btnPagarItem.setPreferredSize(new Dimension(120, 28));
        btnPagarItem.addActionListener(e -> {
            if (comprobanteSeleccionadoId < 0) { JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            int itemRow = tablaItems.getSelectedRow();
            if (itemRow < 0) { JOptionPane.showMessageDialog(this, "Seleccione un item de la factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if ("pagado".equals(modeloTablaItems.getValueAt(itemRow, 4))) { JOptionPane.showMessageDialog(this, "Este item ya fue pagado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de pagar este item?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String fp = mostrarSelectorMedioPago();
                if (fp != null) { cmbFormaPago.setSelectedItem(fp); pagarItemSeleccionado(); }
            }
        });

        btnPagarCompleta = new JButton("PAGAR FACTURA");
        estilizarBoton(btnPagarCompleta);
        btnPagarCompleta.setPreferredSize(new Dimension(135, 28));
        btnPagarCompleta.addActionListener(e -> {
            if (comprobanteSeleccionadoId < 0) { JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
            if (saldo.compareTo(BigDecimal.ZERO) <= 0) { JOptionPane.showMessageDialog(this, "La factura ya est\u00e1 totalmente pagada", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de pagar toda la factura?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String fp = mostrarSelectorMedioPago();
                if (fp != null) { cmbFormaPago.setSelectedItem(fp); pagarFacturaCompleta(); }
            }
        });

        btnPagar = new JButton("PAGAR");
        estilizarBoton(btnPagar);
        btnPagar.setPreferredSize(new Dimension(70, 28));
        btnPagar.addActionListener(e -> {
            if (comprobanteSeleccionadoId < 0) { JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            String montoStr = txtMontoPago.getText().trim().replace("$", "").trim();
            if (montoStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese un monto", "Error", JOptionPane.ERROR_MESSAGE); return; }
            BigDecimal monto;
            try { monto = new BigDecimal(montoStr.replace(".", "").replace(",", ".")); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Monto inv\u00e1lido", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (monto.compareTo(BigDecimal.ZERO) <= 0) { JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE); return; }
            BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
            if (monto.compareTo(saldo) > 0) { JOptionPane.showMessageDialog(this, "El monto ($ " + DF.format(monto) + ") supera el saldo pendiente ($ " + DF.format(saldo) + ")", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de registrar este pago por $ " + DF.format(monto) + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String fp = mostrarSelectorMedioPago();
                if (fp != null) { cmbFormaPago.setSelectedItem(fp); registrarPago(); }
            }
        });

        btnRefresh = new JButton("REFRESCAR");
        estilizarBoton(btnRefresh);
        btnRefresh.setPreferredSize(new Dimension(110, 28));
        btnRefresh.addActionListener(e -> cargarFacturas());

        panelFacturas = crearPanelFacturas();
        panelDetalle = crearPanelDetalle();
        panelFacturas.setMinimumSize(new Dimension(0, 0));
        panelDetalle.setMinimumSize(new Dimension(0, 0));
        splitHorizontal.setLeftComponent(panelFacturas);
        splitHorizontal.setRightComponent(panelDetalle);

        getContentPane().add(panelTitulo, BorderLayout.NORTH);
        getContentPane().add(splitHorizontal);

        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        splitHorizontal.setDividerSize(0);
        splitHorizontal.setResizeWeight(0.5);
        javax.swing.SwingUtilities.invokeLater(() -> splitHorizontal.setDividerLocation(0.5));
    }

    private JPanel crearPanelFacturas() {
        panelFacturas = new JPanel(new BorderLayout(5, 5));
        panelFacturas.setBackground(currentTheme.bgSurface);
        panelFacturas.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "FACTURAS PENDIENTES",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), currentTheme.textPrimary
            )
        ));

        lblFiltroCliente = new JLabel("CLIENTE:");
        lblFiltroCliente.setFont(FUENTE_LABEL);
        lblFiltroCliente.setForeground(currentTheme.textPrimary);

        cmbFiltroCliente = new JComboBox<>();
        cmbFiltroCliente.setEditable(true);
        cmbFiltroCliente.setFont(FUENTE_INPUT_BOLD);
        cmbFiltroCliente.setMaximumRowCount(12);
        cmbFiltroCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(getFieldBg(cmbFiltroCliente.isEnabled()));
                setForeground(cmbFiltroCliente.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                setFont(cmbFiltroCliente.getFont());
                return this;
            }
            @Override
            public void paintComponent(Graphics g) {
                setBackground(getFieldBg(cmbFiltroCliente.isEnabled()));
                setForeground(cmbFiltroCliente.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        });
        installComboUI(cmbFiltroCliente);

        panelFiltroFacturas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panelFiltroFacturas.setBackground(currentTheme.bgSurface);
        panelFiltroFacturas.add(lblFiltroCliente);
        panelFiltroFacturas.add(cmbFiltroCliente);

        String[] colFacturas = {"N\u00famero", "Tipo", "Fecha", "Cliente", "Total", "Estado"};
        modeloTablaFacturas = new DefaultTableModel(colFacturas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaFacturas = new JTable(modeloTablaFacturas);
        tablaFacturas.setFont(FUENTE_TABLA);
        tablaFacturas.setRowHeight(22);
        tablaFacturas.setIntercellSpacing(new Dimension(3, 2));
        tablaFacturas.setShowGrid(true);
        tablaFacturas.setAutoCreateRowSorter(true);
        tablaFacturas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFacturaSeleccionada();
        });

        tablaFacturas.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        editorFiltroFacturas = (JTextField) cmbFiltroCliente.getEditor().getEditorComponent();
        editorFiltroFacturas.setFont(FUENTE_INPUT_BOLD);
        editorFiltroFacturas.setDisabledTextColor(getDisabledFg());
        editorFiltroFacturas.setCaretColor(currentTheme.textPrimary);
        themeComboEditor(cmbFiltroCliente, currentTheme);
        editorFiltroFacturas.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onFiltroFacturasCambiado(); }
            @Override public void removeUpdate(DocumentEvent e) { onFiltroFacturasCambiado(); }
            @Override public void changedUpdate(DocumentEvent e) { onFiltroFacturasCambiado(); }
        });
        cmbFiltroCliente.addActionListener(e -> {
            if (actualizandoComboFacturas) return;
            Object sel = cmbFiltroCliente.getSelectedItem();
            if (sel != null) {
                String txt = sel.toString();
                if (!txt.equals(editorFiltroFacturas.getText())) {
                    editorFiltroFacturas.setText(txt);
                }
            }
        });

        scrollFacturas = new JScrollPane(tablaFacturas);

        JPanel panelCentroFacturas = new JPanel(new BorderLayout(0, 4));
        panelCentroFacturas.setBackground(currentTheme.bgSurface);
        panelCentroFacturas.add(panelFiltroFacturas, BorderLayout.NORTH);
        panelCentroFacturas.add(scrollFacturas, BorderLayout.CENTER);
        panelFacturas.add(panelCentroFacturas, BorderLayout.CENTER);

        panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelInferior.setBackground(currentTheme.bgSurface);
        panelInferior.add(btnRefresh);
        panelFacturas.add(panelInferior, BorderLayout.SOUTH);

        return panelFacturas;
    }

    private JPanel crearPanelDetalle() {
        panelDetalle = new JPanel(new BorderLayout(5, 5));
        panelDetalle.setBackground(currentTheme.bgSurface);
        panelDetalle.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 5, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "DETALLE DE FACTURA",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), currentTheme.textPrimary
            )
        ));

        panelDetalle.add(crearPanelHeader(), BorderLayout.NORTH);
        panelDetalle.add(crearPanelCentral(), BorderLayout.CENTER);

        return panelDetalle;
    }

    private JPanel crearPanelHeader() {
        panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setBackground(currentTheme.bgSurface);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(6, 8, 10, 8));

        rowSuperior = new JPanel(new BorderLayout());
        rowSuperior.setBackground(currentTheme.bgSurface);
        rowSuperior.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSaldoPendiente = new JLabel("Saldo Pendiente: $ 0,00");
        lblSaldoPendiente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSaldoPendiente.setForeground(currentTheme.danger);
        rowSuperior.add(lblSaldoPendiente, BorderLayout.WEST);

        lblTotalFactura = new JLabel("");
        lblTotalFactura.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalFactura.setForeground(currentTheme.textPrimary);
        rowSuperior.add(lblTotalFactura, BorderLayout.EAST);

        panelHeader.add(rowSuperior);
        panelHeader.add(Box.createVerticalStrut(10));

        lblInfoFactura = new JLabel("Seleccione una factura de la lista");
        lblInfoFactura.setFont(FUENTE_INPUT_BOLD);
        lblInfoFactura.setForeground(currentTheme.textPrimary);
        lblInfoFactura.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelHeader.add(lblInfoFactura);

        return panelHeader;
    }

    private JPanel crearPanelCentral() {
        return crearPanelItems();
    }

    private JPanel crearPanelItems() {
        panelItems = new JPanel(new BorderLayout(3, 3));
        panelItems.setBackground(currentTheme.bgSurface);
        panelItems.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "ITEMS DE LA FACTURA",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13), currentTheme.textPrimary
        ));

        String[] colItems = {"ELS", "Cant.", "P. Unitario", "Subtotal", "Estado"};
        modeloTablaItems = new DefaultTableModel(colItems, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaItems = new JTable(modeloTablaItems);
        tablaItems.setFont(FUENTE_TABLA);
        tablaItems.setRowHeight(22);
        tablaItems.setIntercellSpacing(new Dimension(3, 2));
        tablaItems.setShowGrid(true);

        tablaItems.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollItems = new JScrollPane(tablaItems);
        panelItems.add(scrollItems, BorderLayout.CENTER);

        txtMontoPago = new JTextField();
        txtMontoPago.setFont(FUENTE_INPUT_BOLD);
        txtMontoPago.setHorizontalAlignment(JTextField.RIGHT);
        txtMontoPago.setDisabledTextColor(getDisabledFg());
        txtMontoPago.setCaretColor(currentTheme.textPrimary);
        txtMontoPago.setPreferredSize(new Dimension(130, 28));
        txtMontoPago.setMinimumSize(new Dimension(130, 28));

        ((AbstractDocument) txtMontoPago.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                if (txtMontoPagoUpdating || esNumeroValido(text)) {
                    super.insertString(fb, offset, text, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (txtMontoPagoUpdating || esNumeroValido(text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }
            private boolean esNumeroValido(String text) {
                if (text == null || text.isEmpty()) return true;
                return text.matches("[\\d,]*");
            }
        });

        txtMontoPago.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                txtMontoPago.selectAll();
            }
        });

        txtMontoPago.addActionListener(e -> {
            String raw = txtMontoPago.getText().trim().replace("$", "").trim();
            if (!raw.isEmpty()) {
                try {
                    String clean = raw.replace(".", "").replace(",", ".");
                    BigDecimal val = new BigDecimal(clean);
                    txtMontoPagoUpdating = true;
                    txtMontoPago.setText("$ " + DF.format(val));
                    txtMontoPagoUpdating = false;
                } catch (Exception ignored) {}
            }
        });

        cmbFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Cheque", "Tarjeta", "Mercado Pago", "Otra"});
        cmbFormaPago.setFont(FUENTE_INPUT_BOLD);
        cmbFormaPago.setPreferredSize(new Dimension(90, 28));
        cmbFormaPago.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(getFieldBg(cmbFormaPago.isEnabled()));
                setForeground(cmbFormaPago.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                setFont(cmbFormaPago.getFont());
                return this;
            }
            @Override
            public void paintComponent(Graphics g) {
                setBackground(getFieldBg(cmbFormaPago.isEnabled()));
                setForeground(cmbFormaPago.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        });
        installComboUI(cmbFormaPago);

        box1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        box1.setBackground(currentTheme.bgSurface);
        box1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        box1.add(btnPagarItem);
        box1.add(btnPagarCompleta);

        box2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 2));
        box2.setBackground(currentTheme.bgSurface);
        box2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        lblImporte = new JLabel("IMPORTE: ");
        lblImporte.setFont(FUENTE_LABEL);
        lblImporte.setForeground(currentTheme.textPrimary);
        box2.add(lblImporte);
        box2.add(txtMontoPago);
        box2.add(btnPagar);

        panelAcciones = new JPanel();
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));
        panelAcciones.setBackground(currentTheme.bgSurface);
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 4));
        panelAcciones.add(box1);
        panelAcciones.add(Box.createVerticalStrut(2));
        panelAcciones.add(box2);

        panelItems.add(panelAcciones, BorderLayout.SOUTH);

        return panelItems;
    }

    private void cargarFacturas() {
        allFacturas = controlador.listarFacturasPendientes();
        if (allFacturas == null) allFacturas = new ArrayList<>();

        allClientes = new ArrayList<>();
        for (ComprobanteDTO c : allFacturas) {
            String cli = c.getRazonSocialRec();
            if (cli != null && !cli.isEmpty() && !allClientes.contains(cli)) {
                allClientes.add(cli);
            }
        }

        aplicarFiltroFacturas();
        actualizarSugerenciasComboFacturas();

        comprobanteSeleccionadoId = -1;
        lblInfoFactura.setText("Seleccione una factura de la lista");
        lblTotalFactura.setText("");
        lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
        modeloTablaItems.setRowCount(0);
    }

    private void aplicarFiltroFacturas() {
        modeloTablaFacturas.setRowCount(0);
        facturaIds = new ArrayList<>();
        if (allFacturas == null) return;

        String filtro = obtenerTextoFiltroFacturas();
        String filtroLower = filtro.toLowerCase();

        for (ComprobanteDTO c : allFacturas) {
            String cli = c.getRazonSocialRec() != null ? c.getRazonSocialRec() : "";
            if (!filtro.isEmpty() && !cli.toLowerCase().contains(filtroLower)) {
                continue;
            }
            facturaIds.add(c.getId());
            String estado = c.getEstadoPago();
            String estadoDisplay = "pendiente".equals(estado) ? "Pendiente"
                : "pagada_parcial".equals(estado) ? "Parcial"
                : "pagada_total".equals(estado) ? "Pagada" : estado;
            String totalStr = c.getImporteTotal() != null ? "$ " + DF.format(c.getImporteTotal()) : "";
            modeloTablaFacturas.addRow(new Object[]{
                String.format("%04d-%08d", c.getPuntoVenta(), c.getNumero()),
                formatearTipo(c.getTipoComprobanteStr()),
                c.getFechaEmision() != null ? c.getFechaEmision().format(FMT) : "",
                cli,
                totalStr,
                estadoDisplay
            });
        }
        ajustarAnchoColumnas(tablaFacturas, 0, 4);
    }

    private void onFiltroFacturasCambiado() {
        if (actualizandoComboFacturas) return;
        aplicarFiltroFacturas();
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (!actualizandoComboFacturas) {
                actualizarSugerenciasComboFacturas();
            }
        });
    }

    private String obtenerTextoFiltroFacturas() {
        if (editorFiltroFacturas != null) {
            return editorFiltroFacturas.getText().trim();
        }
        return "";
    }

    private void actualizarSugerenciasComboFacturas() {
        if (allClientes == null || editorFiltroFacturas == null) return;
        String texto = editorFiltroFacturas.getText();
        String textoLower = texto.toLowerCase();

        actualizandoComboFacturas = true;
        try {
            cmbFiltroCliente.hidePopup();
            cmbFiltroCliente.removeAllItems();
            cmbFiltroCliente.addItem("");
            int agregados = 0;
            for (String cli : allClientes) {
                if (texto.isEmpty() || cli.toLowerCase().contains(textoLower)) {
                    cmbFiltroCliente.addItem(cli);
                    agregados++;
                    if (agregados >= 15) break;
                }
            }
            String textoActualEditor = editorFiltroFacturas.getText();
            if (!textoActualEditor.equals(texto)) {
                editorFiltroFacturas.setText(texto);
            }
            boolean tieneFoco = editorFiltroFacturas.hasFocus();
            if (tieneFoco && !texto.isEmpty()) {
                cmbFiltroCliente.setPopupVisible(true);
            }
        } finally {
            actualizandoComboFacturas = false;
        }
    }

    private void cargarFacturaSeleccionada() {
        int row = tablaFacturas.getSelectedRow();
        if (row < 0) return;
        if (facturaIds == null || row >= facturaIds.size()) return;
        comprobanteSeleccionadoId = facturaIds.get(row);
        cargarDetalleFactura(comprobanteSeleccionadoId);
    }

    private void cargarDetalleFactura(int facturaId) {
        ComprobanteDTO comp = controlador.buscarFactura(facturaId);
        if (comp != null) {
            String tipoNum = comp.getTipoComprobanteStr()
                + " " + String.format("%04d-%08d", comp.getPuntoVenta(), comp.getNumero());
            String cliente = "Cliente: " + comp.getRazonSocialRec();
            String total = "Total: $ " + DF.format(comp.getImporteTotal());
            lblInfoFactura.setText(tipoNum + "  |  " + cliente);
            lblTotalFactura.setText(total);
            BigDecimal saldo = controlador.getSaldoPendiente(facturaId);
            lblSaldoPendiente.setText("Saldo Pendiente: $ " + DF.format(saldo));
            txtMontoPagoUpdating = true;
            txtMontoPago.setText("$ " + DF.format(saldo));
            txtMontoPagoUpdating = false;
        } else {
            lblInfoFactura.setText("#" + facturaId + " (eliminada)");
            lblTotalFactura.setText("");
            lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
            txtMontoPago.setText("");
        }

        List<ItemFacturaDTO> items = controlador.getItemsFactura(facturaId);
        modeloTablaItems.setRowCount(0);
        itemIds = new ArrayList<>();
        itemEls = new ArrayList<>();
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemFacturaDTO item = items.get(i);
            itemIds.add(item.getId());
            itemEls.add(item.getElsReferencia());
            String cantStr = item.getCantidad() != null ? String.valueOf(item.getCantidad().intValue()) : "";
            modeloTablaItems.addRow(new Object[]{
                item.getCodigo(),
                cantStr,
                item.getPrecioUnitario() != null ? "$ " + DF.format(item.getPrecioUnitario()) : "",
                item.getSubtotal() != null ? "$ " + DF.format(item.getSubtotal()) : "",
                item.getEstadoPago() != null ? item.getEstadoPago() : ""
            });
        }
        ajustarAnchoColumnas(tablaItems);

    }

    private void registrarPago() {
        if (comprobanteSeleccionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String montoStr = txtMontoPago.getText().trim().replace("$", "").trim();
        if (montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BigDecimal monto;
        try {
            monto = new BigDecimal(montoStr.replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto inv\u00e1lido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String formaPago = (String) cmbFormaPago.getSelectedItem();

        BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
        if (monto.compareTo(saldo) > 0) {
            JOptionPane.showMessageDialog(this, "El monto ($ " + DF.format(monto)
                + ") supera el saldo pendiente ($ " + DF.format(saldo) + ")", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controlador.registrarPago(comprobanteSeleccionadoId, monto, formaPago, null);
        JOptionPane.showMessageDialog(this, "Pago registrado correctamente", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
        txtMontoPago.setText("");
        recargarYSeleccionarFactura(comprobanteSeleccionadoId);
    }

    private void pagarItemSeleccionado() {
        if (comprobanteSeleccionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int itemRow = tablaItems.getSelectedRow();
        if (itemRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un item de la factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int itemId = itemIds.get(itemRow);

        String estadoPago = (String) modeloTablaItems.getValueAt(itemRow, 4);
        if ("pagado".equals(estadoPago)) {
            JOptionPane.showMessageDialog(this, "Este item ya fue pagado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object subtotalObj = modeloTablaItems.getValueAt(itemRow, 3);
        if (subtotalObj == null) return;
        BigDecimal monto;
        try {
            monto = new BigDecimal(subtotalObj.toString().replace("$", "").trim().replace(".", "").replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el monto del item", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String formaPagoItem = (String) cmbFormaPago.getSelectedItem();
        boolean todosPagados = controlador.registrarPagoItem(itemId, comprobanteSeleccionadoId, monto, formaPagoItem, null);
        JOptionPane.showMessageDialog(this, "Item marcado como pagado", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);

        if (todosPagados) {
            controlador.setEstadoFactura(comprobanteSeleccionadoId, "pagada_total");
        } else {
            controlador.setEstadoFactura(comprobanteSeleccionadoId, "pagada_parcial");
        }

        Integer els = itemEls != null && itemRow < itemEls.size() ? itemEls.get(itemRow) : null;
        if (els != null && els > 0) {
            String base = mostrarSelectorBaseReparsoft();
            if (base != null) {
                controladorReparsoft.actualizarPagoReparsoft(els, monto, base);
            }
        }

        recargarYSeleccionarFactura(comprobanteSeleccionadoId);
    }

    private void pagarFacturaCompleta() {
        if (comprobanteSeleccionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "La factura ya est\u00e1 totalmente pagada", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<ItemFacturaDTO> items = controlador.getItemsFactura(comprobanteSeleccionadoId);

        String base = null;
        for (ItemFacturaDTO item : items) {
            if (item.getElsReferencia() != null && item.getElsReferencia() > 0) {
                if (base == null) {
                    base = mostrarSelectorBaseReparsoft();
                    if (base == null) break;
                }
                BigDecimal montoItem = item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO;
                controladorReparsoft.actualizarPagoReparsoft(item.getElsReferencia(), montoItem, base);
            }
        }

        String formaPago = (String) cmbFormaPago.getSelectedItem();
        controlador.pagarFacturaCompleta(comprobanteSeleccionadoId, formaPago);
        JOptionPane.showMessageDialog(this, "Factura pagada completamente por $ " + DF.format(saldo), "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);

        recargarYSeleccionarFactura(comprobanteSeleccionadoId);
    }

    private void recargarYSeleccionarFactura(int facturaId) {
        BigDecimal saldo = controlador.getSaldoPendiente(facturaId);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            cargarFacturas();
            comprobanteSeleccionadoId = -1;
            modeloTablaItems.setRowCount(0);
            txtMontoPagoUpdating = true;
            txtMontoPago.setText("");
            txtMontoPagoUpdating = false;
            lblInfoFactura.setText("Seleccione una factura de la lista");
            lblTotalFactura.setText("");
            lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
            return;
        }
        cargarFacturas();
        boolean reencontrada = false;
        for (int i = 0; i < facturaIds.size(); i++) {
            if (facturaIds.get(i) == facturaId) {
                tablaFacturas.setRowSelectionInterval(i, i);
                reencontrada = true;
                break;
            }
        }
        if (!reencontrada) {
            comprobanteSeleccionadoId = facturaId;
            cargarDetalleFactura(facturaId);
        }
    }

    private void estilizarBoton(JButton btn) {
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(currentTheme.textPrimary);
        btn.setBackground(currentTheme.btnBg);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
    }

    private static class CustomComboUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(comboBox.getBackground());
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            javax.swing.ListCellRenderer<Object> renderer = comboBox.getRenderer();
            java.awt.Component c;
            if (hasFocus && !isPopupVisible(comboBox)) {
                c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
            } else {
                c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
            }
            c.setFont(comboBox.getFont());
            if (hasFocus && !isPopupVisible(comboBox)) {
                c.setForeground(listBox.getSelectionForeground());
                c.setBackground(listBox.getSelectionBackground());
            } else {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            }
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void installComboUI(JComboBox<?> combo) {
        combo.setUI(new CustomComboUI());
    }

    private void themeComboEditor(JComboBox<?> combo, Theme t) {
        java.awt.Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            JTextField ed = (JTextField) editorComp;
            ed.setBackground(getFieldBg(combo.isEnabled()));
            ed.setForeground(combo.isEnabled() ? t.textPrimary : getDisabledFg());
            ed.setDisabledTextColor(getDisabledFg());
            ed.setCaretColor(t.textPrimary);
        }
    }

    private String mostrarSelectorBaseReparsoft() {
        return com.els.facturacion.util.UbicacionSistema.getNombreDbReparsoft();
    }

    private String mostrarSelectorMedioPago() {
        JDialog dialog = new JDialog(this, "Medio de pago", true);
        dialog.getContentPane().setBackground(currentTheme.bgBase);

        JComboBox<String> combo = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Cheque", "Tarjeta", "Mercado Pago", "Otra"});
        combo.setFont(FUENTE_INPUT_BOLD);

        JLabel lbl = new JLabel("Seleccione el medio de pago:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(currentTheme.textPrimary);

        JButton btnOk = new JButton("ACEPTAR");
        estilizarBoton(btnOk);
        JButton btnCancel = new JButton("CANCELAR");
        estilizarBoton(btnCancel);

        JPanel content = new JPanel(new BorderLayout(8, 12));
        content.setBackground(currentTheme.bgBase);
        content.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));
        content.add(lbl, BorderLayout.NORTH);
        content.add(combo, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(currentTheme.bgBase);
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        final String[] result = new String[1];

        btnOk.addActionListener(e -> {
            result[0] = (String) combo.getSelectedItem();
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
        return result[0];
    }

    private static Color bgAzulado(Theme t) {
        int r = t.bgBase.getRed();
        int g = t.bgBase.getGreen();
        int b = t.bgBase.getBlue();
        if (r < 50) {
            return new Color(
                Math.max(0, r - 3),
                Math.max(0, g - 3),
                Math.min(255, b + 5)
            );
        }
        return new Color(
            Math.max(0, r - 18),
            Math.max(0, g - 16),
            Math.max(0, b - 6)
        );
    }

    private static String formatearTipo(String tipoStr) {
        if (tipoStr == null || tipoStr.isEmpty()) return "";
        if (tipoStr.contains("FCE")) return "FCE";
        String[] parts = tipoStr.split(" ");
        if (parts.length < 2) return tipoStr;
        String abbr;
        switch (parts[0]) {
            case "Factura":       abbr = "F";  break;
            case "Nota":
                boolean isCredito = false;
                for (String p : parts) if (p.startsWith("C") || p.startsWith("Cr")) { isCredito = true; break; }
                abbr = isCredito ? "NC" : "ND";
                break;
            case "Recibo":        abbr = "R";  break;
            default:              abbr = parts[0]; break;
        }
        String letra = parts[parts.length - 1];
        return abbr + "-" + letra;
    }

    private void ajustarAnchoColumnas(JTable table, int... priorizar) {
        java.util.Set<Integer> prio = new java.util.HashSet<>();
        for (int p : priorizar) prio.add(p);
        for (int i = 0; i < table.getColumnCount(); i++) {
            javax.swing.table.TableColumn col = table.getColumnModel().getColumn(i);
            int width = 60;
            javax.swing.table.TableCellRenderer hr = table.getTableHeader().getDefaultRenderer();
            java.awt.Component hc = hr.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, i);
            width = Math.max(width, hc.getPreferredSize().width + 6);
            for (int r = 0; r < Math.min(table.getRowCount(), 30); r++) {
                javax.swing.table.TableCellRenderer rnd = table.getCellRenderer(r, i);
                java.awt.Component cmp = table.prepareRenderer(rnd, r, i);
                width = Math.max(width, cmp.getPreferredSize().width + 6);
            }
            if (prio.contains(i)) width = (int)(width * 1.35);
            col.setPreferredWidth(width);
        }
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        Font titledFont = new Font("Segoe UI", Font.BOLD, 13);

        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (panelTitulo != null) panelTitulo.setBackground(t.bgSurface);
        if (lblFiltroCliente != null) lblFiltroCliente.setForeground(t.textPrimary);
        if (panelFiltroFacturas != null) panelFiltroFacturas.setBackground(t.bgSurface);
        if (cmbFiltroCliente != null) {
            cmbFiltroCliente.setBackground(getFieldBg(cmbFiltroCliente.isEnabled()));
            cmbFiltroCliente.setForeground(t.textPrimary);
            installComboUI(cmbFiltroCliente);
            themeComboEditor(cmbFiltroCliente, t);
        }
        if (editorFiltroFacturas != null) {
            editorFiltroFacturas.setBackground(getFieldBg(true));
            editorFiltroFacturas.setForeground(t.textPrimary);
            editorFiltroFacturas.setDisabledTextColor(getDisabledFg());
            editorFiltroFacturas.setCaretColor(t.textPrimary);
        }

        // FIX: live-theme — contenedores raíz
        if (splitHorizontal != null) { splitHorizontal.setBackground(t.bgSurface); splitHorizontal.setBorder(null); }
        if (getContentPane() != null) getContentPane().setBackground(t.bgBase);
        if (panelFacturas != null) {
            panelFacturas.setBackground(t.bgSurface);
            panelFacturas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 5),
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(t.brand),
                    "FACTURAS PENDIENTES",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    titledFont, t.textPrimary)));
        }
        if (panelDetalle != null) {
            panelDetalle.setBackground(t.bgSurface);
            panelDetalle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 5, 10, 10),
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(t.brand),
                    "DETALLE DE FACTURA",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    titledFont, t.textPrimary)));
        }
        if (panelHeader != null) { panelHeader.setBackground(t.bgSurface); }
        if (rowSuperior != null) { rowSuperior.setBackground(t.bgSurface); }
        if (panelItems != null) {
            panelItems.setBackground(t.bgSurface);
            panelItems.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(t.brand),
                "ITEMS DE LA FACTURA",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                titledFont, t.textPrimary));
        }
        if (panelInferior != null) panelInferior.setBackground(t.bgSurface);
        if (box1 != null) {
            box1.setBackground(t.bgSurface);
            box1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.brand),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        }
        if (box2 != null) {
            box2.setBackground(t.bgSurface);
            box2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.brand),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        }
        if (panelAcciones != null) panelAcciones.setBackground(t.bgSurface);
        if (statusBar != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        }

        if (lblSaldoPendiente != null) lblSaldoPendiente.setForeground(t.danger);
        if (lblInfoFactura != null) lblInfoFactura.setForeground(t.textPrimary);
        if (lblTotalFactura != null) lblTotalFactura.setForeground(t.textPrimary);
        if (lblImporte != null) lblImporte.setForeground(t.textPrimary);
        if (btnPagarItem != null) { btnPagarItem.setBackground(t.btnBg); btnPagarItem.setForeground(t.textPrimary); }
        if (btnPagarCompleta != null) { btnPagarCompleta.setBackground(t.btnBg); btnPagarCompleta.setForeground(t.textPrimary); }
        if (btnPagar != null) { btnPagar.setBackground(t.btnBg); btnPagar.setForeground(t.textPrimary); }
        if (btnRefresh != null) { btnRefresh.setBackground(t.btnBg); btnRefresh.setForeground(t.textPrimary); }
        if (cmbFormaPago != null) {
            cmbFormaPago.setBackground(getFieldBg(cmbFormaPago.isEnabled()));
            cmbFormaPago.setForeground(cmbFormaPago.isEnabled() ? t.textPrimary : getDisabledFg());
            installComboUI(cmbFormaPago);
        }
        if (txtMontoPago != null) {
            txtMontoPago.setBackground(getFieldBg(true));
            txtMontoPago.setForeground(t.textPrimary);
            txtMontoPago.setDisabledTextColor(getDisabledFg());
            txtMontoPago.setCaretColor(t.textPrimary);
        }
        if (scrollFacturas != null) scrollFacturas.getViewport().setBackground(t.bgSurface);
        if (scrollItems != null) scrollItems.getViewport().setBackground(t.bgSurface);
        if (tablaFacturas != null) {
            TablaRenderer.applyTo(tablaFacturas, t,
                Collections.emptySet(),
                new HashSet<>(Arrays.asList(0)),
                new HashSet<>(Arrays.asList(1)),
                t.bgSurface, t.bgElevated);
            if (tablaFacturas.getTableHeader() != null) {
                Theme.styleTableHeader(tablaFacturas.getTableHeader(), t);
            }
        }
        if (tablaItems != null) {
            TablaRenderer.applyTo(tablaItems, t,
                new HashSet<>(Arrays.asList(2, 3)),
                new HashSet<>(Arrays.asList(0, 3)),
                new HashSet<>(Arrays.asList(0, 1, 2, 3, 4)),
                t.bgSurface, t.bgElevated);
            if (tablaItems.getTableHeader() != null) {
                Theme.styleTableHeader(tablaItems.getTableHeader(), t);
            }
        }
    }
}
