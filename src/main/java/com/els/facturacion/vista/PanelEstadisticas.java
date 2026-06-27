package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PanelEstadisticas extends JPanel {

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
    private ControladorFacturacion controlador;

    // Panels
    private JLabel lblTitulo;
    private JPanel panelSuperior;
    private JPanel panelToggle;
    private JPanel panelContenido;
    private JPanel panelDetalle;
    private JPanel panelFiltros;
    private JPanel panelSubtotales;
    private JPanel panelTotalesView;

    // Toggle buttons
    private JToggleButton btnDetalle;
    private JToggleButton btnTotales;
    private ButtonGroup grupoToggle;

    // Filter components
    private JRadioButton radioMensual;
    private JRadioButton radioRango;
    private JComboBox<String> cmbAnio;
    private JComboBox<String> cmbMes;
    private JComponent dateDesde;
    private JComponent dateHasta;
    private ButtonGroup grupoFiltro;

    // Filter labels
    private JLabel lblAnio;
    private JLabel lblMes;
    private JLabel lblDesde;
    private JLabel lblHasta;
    private JLabel lblFiltroEstado;
    private JComboBox<String> cmbFiltroEstado;
    private JLabel lblFiltroAnioTotales;
    private JComboBox<String> cmbFiltroAnioTotales;



    // Totals labels
    private JLabel lblTotalLabel;
    private JLabel lblTotalBruto;
    private JLabel lblTotalIva;
    private JLabel lblTotalNeto;

    // Table
    private JTable tablaDetalle;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;

    // Totales view
    private JTable tablaTotales;
    private DefaultTableModel modeloTotales;
    private JScrollPane scrollTotales;
    private JLabel lblTituloTotales;

    // Layout
    private CardLayout cardContenido;

    // Data
    private List<ComprobanteDTO> allComprobantes;

    // ──────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
    }

    private String fmt$(BigDecimal val) {
        return val != null ? "$ " + DF.format(val) : "$ 0.00";
    }

    // ──────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────

    public PanelEstadisticas() {
        controlador = new ControladorFacturacion();
        allComprobantes = new ArrayList<>();
        initComponents();
        applyTheme(currentTheme);
        cargarDatos();
    }

    // ──────────────────────────────────────────────
    // initComponents
    // ──────────────────────────────────────────────

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(currentTheme.bgBase);

        // ── Title ──
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        lblTitulo = new JLabel("ESTAD\u00cdSTICAS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        panelSuperior.add(lblTitulo);
        add(panelSuperior, BorderLayout.NORTH);

        // ── Toggle buttons (DETALLE / TOTALES) ──
        btnDetalle = new JToggleButton("DETALLE", true);
        btnDetalle.setFont(FUENTE_BOTON);
        btnDetalle.setFocusPainted(false);
        btnDetalle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDetalle.addActionListener(e -> {
            if (btnDetalle.isSelected()) {
                cardContenido.show(panelContenido, "detalle");
            } else {
                btnDetalle.setSelected(true);
            }
        });

        btnTotales = new JToggleButton("TOTALES");
        btnTotales.setFont(FUENTE_BOTON);
        btnTotales.setFocusPainted(false);
        btnTotales.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTotales.addActionListener(e -> {
            if (btnTotales.isSelected()) {
                cardContenido.show(panelContenido, "totales");
                cargarResumen();
            } else {
                btnTotales.setSelected(true);
            }
        });

        grupoToggle = new ButtonGroup();
        grupoToggle.add(btnDetalle);
        grupoToggle.add(btnTotales);

        panelToggle = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelToggle.setBackground(currentTheme.bgSurface);
        panelToggle.add(btnDetalle);
        panelToggle.add(btnTotales);

        // ── CardLayout container ──
        cardContenido = new CardLayout();
        panelContenido = new JPanel(cardContenido);
        panelContenido.setBackground(currentTheme.bgBase);

        // ==================== DETALLE CARD ====================
        panelDetalle = new JPanel(new BorderLayout(0, 4));
        panelDetalle.setBackground(currentTheme.bgBase);

        buildFilterPanel();
        buildTable();
        buildTotalsPanel();

        panelDetalle.add(panelFiltros, BorderLayout.NORTH);
        panelDetalle.add(scrollTabla, BorderLayout.CENTER);
        panelDetalle.add(panelSubtotales, BorderLayout.SOUTH);

        // ==================== TOTALES CARD ====================
        buildTotalesView();

        panelContenido.add(panelDetalle, "detalle");
        panelContenido.add(panelTotalesView, "totales");

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(currentTheme.bgBase);
        panelCentro.add(panelToggle, BorderLayout.NORTH);
        panelCentro.add(panelContenido, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

    }

    // ──────────────────────────────────────────────
    // Filter panel
    // ──────────────────────────────────────────────

    private void buildFilterPanel() {
        panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        panelFiltros.setBackground(currentTheme.bgSurface);

        // ── Radio: Período mensual ──
        radioMensual = new JRadioButton("Per\u00edodo mensual") {
            @Override
            protected void paintComponent(Graphics g) {
                setForeground(isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        };
        radioMensual.setFont(FUENTE_LABEL);
        radioMensual.setBackground(currentTheme.bgSurface);
        radioMensual.setSelected(true);
        radioMensual.addActionListener(e -> actualizarEstadoFiltros());

        GridBagConstraints gbcRadioMensual = new GridBagConstraints();
        gbcRadioMensual.fill = GridBagConstraints.HORIZONTAL;
        gbcRadioMensual.insets = new Insets(2, 4, 2, 4);
        gbcRadioMensual.gridx = 0; gbcRadioMensual.gridy = 0;
        panelFiltros.add(radioMensual, gbcRadioMensual);

        lblAnio = new JLabel("A\u00d1O:");
        lblAnio.setFont(FUENTE_LABEL);
        lblAnio.setForeground(currentTheme.textPrimary);

        cmbAnio = new JComboBox<>(new String[]{"2025", "2026", "2027", "2028", "2029", "2030"});
        cmbAnio.setFont(FUENTE_INPUT_BOLD);
        cmbAnio.setPreferredSize(new Dimension(90, 24));
        cmbAnio.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        installComboUI(cmbAnio);
        cmbAnio.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(cmbAnio.isEnabled()));
                setForeground(isSelected ? list.getSelectionForeground() :
                    (cmbAnio.isEnabled() ? currentTheme.textPrimary : getDisabledFg()));
                setFont(cmbAnio.getFont());
                return this;
            }
        });

        GridBagConstraints gbcLblAnio = new GridBagConstraints();
        gbcLblAnio.fill = GridBagConstraints.HORIZONTAL;
        gbcLblAnio.insets = new Insets(2, 4, 2, 4);
        gbcLblAnio.gridx = 1; gbcLblAnio.gridy = 0;
        panelFiltros.add(lblAnio, gbcLblAnio);

        GridBagConstraints gbcCmbAnio = new GridBagConstraints();
        gbcCmbAnio.fill = GridBagConstraints.HORIZONTAL;
        gbcCmbAnio.insets = new Insets(2, 4, 2, 4);
        gbcCmbAnio.gridx = 2; gbcCmbAnio.gridy = 0;
        panelFiltros.add(cmbAnio, gbcCmbAnio);

        lblMes = new JLabel("MES:");
        lblMes.setFont(FUENTE_LABEL);
        lblMes.setForeground(currentTheme.textPrimary);

        cmbMes = new JComboBox<>(new String[]{
            "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
            "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
        });
        cmbMes.setFont(FUENTE_INPUT_BOLD);
        cmbMes.setPreferredSize(new Dimension(140, 24));
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        installComboUI(cmbMes);
        cmbMes.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(cmbMes.isEnabled()));
                setForeground(isSelected ? list.getSelectionForeground() :
                    (cmbMes.isEnabled() ? currentTheme.textPrimary : getDisabledFg()));
                setFont(cmbMes.getFont());
                return this;
            }
        });

        GridBagConstraints gbcLblMes = new GridBagConstraints();
        gbcLblMes.fill = GridBagConstraints.HORIZONTAL;
        gbcLblMes.insets = new Insets(2, 4, 2, 4);
        gbcLblMes.gridx = 3; gbcLblMes.gridy = 0;
        panelFiltros.add(lblMes, gbcLblMes);

        GridBagConstraints gbcCmbMes = new GridBagConstraints();
        gbcCmbMes.fill = GridBagConstraints.HORIZONTAL;
        gbcCmbMes.insets = new Insets(2, 4, 2, 4);
        gbcCmbMes.gridx = 4; gbcCmbMes.gridy = 0;
        panelFiltros.add(cmbMes, gbcCmbMes);

        // ── Radio: Rango personalizado ──
        radioRango = new JRadioButton("Rango personalizado") {
            @Override
            protected void paintComponent(Graphics g) {
                setForeground(isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        };
        radioRango.setFont(FUENTE_LABEL);
        radioRango.setBackground(currentTheme.bgSurface);
        radioRango.addActionListener(e -> actualizarEstadoFiltros());

        GridBagConstraints gbcRadioRango = new GridBagConstraints();
        gbcRadioRango.fill = GridBagConstraints.HORIZONTAL;
        gbcRadioRango.insets = new Insets(2, 4, 2, 4);
        gbcRadioRango.gridx = 0; gbcRadioRango.gridy = 1;
        panelFiltros.add(radioRango, gbcRadioRango);

        lblDesde = new JLabel("DESDE:");
        lblDesde.setFont(FUENTE_LABEL);
        lblDesde.setForeground(currentTheme.textPrimary);

        dateDesde = crearDateChooser();

        lblHasta = new JLabel("HASTA:");
        lblHasta.setFont(FUENTE_LABEL);
        lblHasta.setForeground(currentTheme.textPrimary);

        dateHasta = crearDateChooser();

        GridBagConstraints gbcLblDesde = new GridBagConstraints();
        gbcLblDesde.fill = GridBagConstraints.HORIZONTAL;
        gbcLblDesde.insets = new Insets(2, 4, 2, 4);
        gbcLblDesde.gridx = 1; gbcLblDesde.gridy = 1;
        panelFiltros.add(lblDesde, gbcLblDesde);

        GridBagConstraints gbcDateDesde = new GridBagConstraints();
        gbcDateDesde.fill = GridBagConstraints.HORIZONTAL;
        gbcDateDesde.insets = new Insets(2, 4, 2, 4);
        gbcDateDesde.gridx = 2; gbcDateDesde.gridy = 1;
        panelFiltros.add(dateDesde, gbcDateDesde);

        GridBagConstraints gbcLblHasta = new GridBagConstraints();
        gbcLblHasta.fill = GridBagConstraints.HORIZONTAL;
        gbcLblHasta.insets = new Insets(2, 4, 2, 4);
        gbcLblHasta.gridx = 3; gbcLblHasta.gridy = 1;
        panelFiltros.add(lblHasta, gbcLblHasta);

        GridBagConstraints gbcDateHasta = new GridBagConstraints();
        gbcDateHasta.fill = GridBagConstraints.HORIZONTAL;
        gbcDateHasta.insets = new Insets(2, 4, 2, 4);
        gbcDateHasta.gridx = 4; gbcDateHasta.gridy = 1;
        panelFiltros.add(dateHasta, gbcDateHasta);

        // ── Adicional filters (row 2) ──
        lblFiltroEstado = new JLabel("ESTADO:");
        lblFiltroEstado.setFont(FUENTE_LABEL);
        lblFiltroEstado.setForeground(currentTheme.textPrimary);

        cmbFiltroEstado = new JComboBox<>(new String[]{"--Todos--", "Pendiente", "Parcial", "Pagada"});
        cmbFiltroEstado.setFont(FUENTE_INPUT_BOLD);
        cmbFiltroEstado.setPreferredSize(new Dimension(110, 24));
        installComboUI(cmbFiltroEstado);
        cmbFiltroEstado.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(true));
                setForeground(isSelected ? list.getSelectionForeground() : currentTheme.textPrimary);
                setFont(cmbFiltroEstado.getFont());
                return this;
            }
        });

        GridBagConstraints gbcLblEstado = new GridBagConstraints();
        gbcLblEstado.fill = GridBagConstraints.HORIZONTAL;
        gbcLblEstado.insets = new Insets(2, 4, 2, 4);
        gbcLblEstado.gridx = 0; gbcLblEstado.gridy = 2;
        panelFiltros.add(lblFiltroEstado, gbcLblEstado);

        GridBagConstraints gbcCmbEstado = new GridBagConstraints();
        gbcCmbEstado.fill = GridBagConstraints.HORIZONTAL;
        gbcCmbEstado.insets = new Insets(2, 4, 2, 4);
        gbcCmbEstado.gridx = 1; gbcCmbEstado.gridy = 2;
        panelFiltros.add(cmbFiltroEstado, gbcCmbEstado);

        // Apply button
        JButton btnFiltrar = new JButton("FILTRAR");
        btnFiltrar.setFont(FUENTE_BOTON);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFiltrar.addActionListener(e -> cargarDatos());

        GridBagConstraints gbcBtnFiltrar = new GridBagConstraints();
        gbcBtnFiltrar.fill = GridBagConstraints.HORIZONTAL;
        gbcBtnFiltrar.insets = new Insets(2, 12, 2, 4);
        gbcBtnFiltrar.gridx = 5; gbcBtnFiltrar.gridy = 0; gbcBtnFiltrar.gridheight = 2;
        panelFiltros.add(btnFiltrar, gbcBtnFiltrar);

        // Button group
        grupoFiltro = new ButtonGroup();
        grupoFiltro.add(radioMensual);
        grupoFiltro.add(radioRango);

        actualizarEstadoFiltros();
    }

    // ──────────────────────────────────────────────
    // Table
    // ──────────────────────────────────────────────

    private void buildTable() {
        String[] columnas = {"FECHA", "N\u00b0 FACTURA", "CLIENTE", "ELS", "BRUTO", "IVA", "NETO", "ESTADO"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDetalle = new JTable(modeloTabla);
        tablaDetalle.setFont(FUENTE_TABLA);
        tablaDetalle.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaDetalle.setRowHeight(22);
        tablaDetalle.setShowGrid(true);
        tablaDetalle.setAutoCreateRowSorter(false);
        tablaDetalle.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tablaDetalle.getTableHeader().setResizingAllowed(false);
        tablaDetalle.getTableHeader().setReorderingAllowed(false);

        int[] widths = {90, 110, 200, 90, 100, 100, 100, 80};
        for (int i = 0; i < widths.length; i++) {
            tablaDetalle.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        scrollTabla = new JScrollPane(tablaDetalle);
    }

    // ──────────────────────────────────────────────
    // TOTALES view
    // ──────────────────────────────────────────────

    private void buildTotalesView() {
        panelTotalesView = new JPanel(new BorderLayout(0, 4));
        panelTotalesView.setBackground(currentTheme.bgBase);

        lblTituloTotales = new JLabel("RESUMEN POR PER\u00cdODO", SwingConstants.CENTER);
        lblTituloTotales.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTituloTotales.setForeground(currentTheme.brand);

        // ── Año filter ──
        lblFiltroAnioTotales = new JLabel("AÑO:");
        lblFiltroAnioTotales.setFont(FUENTE_LABEL);
        lblFiltroAnioTotales.setForeground(currentTheme.textPrimary);

        cmbFiltroAnioTotales = new JComboBox<>(new String[]{"2025", "2026", "2027", "2028", "2029", "2030"});
        cmbFiltroAnioTotales.setFont(FUENTE_INPUT_BOLD);
        cmbFiltroAnioTotales.setPreferredSize(new Dimension(90, 24));
        cmbFiltroAnioTotales.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        installComboUI(cmbFiltroAnioTotales);
        cmbFiltroAnioTotales.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(true));
                setForeground(isSelected ? list.getSelectionForeground() : currentTheme.textPrimary);
                setFont(cmbFiltroAnioTotales.getFont());
                return this;
            }
        });
        cmbFiltroAnioTotales.addActionListener(e -> cargarResumen());

        JPanel panelTotalesFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelTotalesFilter.setOpaque(false);
        panelTotalesFilter.add(lblFiltroAnioTotales);
        panelTotalesFilter.add(cmbFiltroAnioTotales);

        JPanel panelTotalesNorth = new JPanel(new BorderLayout());
        panelTotalesNorth.setOpaque(false);
        panelTotalesNorth.add(lblTituloTotales, BorderLayout.NORTH);
        panelTotalesNorth.add(panelTotalesFilter, BorderLayout.CENTER);

        String[] cols = {"MES", "CANT.", "NETO", "IVA", "BRUTO"};
        modeloTotales = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTotales = new JTable(modeloTotales);
        tablaTotales.setFont(FUENTE_TABLA);
        tablaTotales.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaTotales.getTableHeader().setResizingAllowed(false);
        tablaTotales.setRowHeight(22);
        tablaTotales.setShowGrid(true);
        tablaTotales.setAutoCreateRowSorter(false);
        tablaTotales.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollTotales = new JScrollPane(tablaTotales);

        panelTotalesView.add(panelTotalesNorth, BorderLayout.NORTH);
        panelTotalesView.add(scrollTotales, BorderLayout.CENTER);
    }

    // ──────────────────────────────────────────────
    // Totals panel (below table in DETALLE)
    // ──────────────────────────────────────────────

    private void buildTotalsPanel() {
        panelSubtotales = new JPanel();
        panelSubtotales.setLayout(new BoxLayout(panelSubtotales, BoxLayout.Y_AXIS));

        panelSubtotales.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        int[] widths = {90, 110, 200, 90, 100, 100, 100};
        double tw = 0;
        for (int w : widths) tw += w;
        double[] weights = new double[widths.length];
        for (int i = 0; i < widths.length; i++) weights[i] = (double) widths[i] / tw;

        // ── Header row: BRUTO / IVA / NETO ──
        JPanel headerRow = new JPanel(new GridBagLayout());

        GridBagConstraints gbcHdrEmpty = new GridBagConstraints();
        gbcHdrEmpty.fill = GridBagConstraints.HORIZONTAL;
        gbcHdrEmpty.insets = new Insets(0, 1, 0, 1);
        gbcHdrEmpty.gridx = 0; gbcHdrEmpty.gridwidth = 4;
        gbcHdrEmpty.weightx = weights[0] + weights[1] + weights[2] + weights[3];
        headerRow.add(new JLabel(""), gbcHdrEmpty);

        JLabel lblHdrBruto = new JLabel("BRUTO", SwingConstants.CENTER);
        lblHdrBruto.setFont(FUENTE_LABEL);
        GridBagConstraints gbcHdrBruto = new GridBagConstraints();
        gbcHdrBruto.fill = GridBagConstraints.HORIZONTAL;
        gbcHdrBruto.insets = new Insets(0, 1, 0, 1);
        gbcHdrBruto.gridx = 4; gbcHdrBruto.gridwidth = 1;
        gbcHdrBruto.weightx = weights[4];
        headerRow.add(lblHdrBruto, gbcHdrBruto);

        JLabel lblHdrIva = new JLabel("IVA", SwingConstants.CENTER);
        lblHdrIva.setFont(FUENTE_LABEL);
        GridBagConstraints gbcHdrIva = new GridBagConstraints();
        gbcHdrIva.fill = GridBagConstraints.HORIZONTAL;
        gbcHdrIva.insets = new Insets(0, 1, 0, 1);
        gbcHdrIva.gridx = 5; gbcHdrIva.gridwidth = 1;
        gbcHdrIva.weightx = weights[5];
        headerRow.add(lblHdrIva, gbcHdrIva);

        JLabel lblHdrNeto = new JLabel("NETO", SwingConstants.CENTER);
        lblHdrNeto.setFont(FUENTE_LABEL);
        GridBagConstraints gbcHdrNeto = new GridBagConstraints();
        gbcHdrNeto.fill = GridBagConstraints.HORIZONTAL;
        gbcHdrNeto.insets = new Insets(0, 1, 0, 1);
        gbcHdrNeto.gridx = 6; gbcHdrNeto.gridwidth = 1;
        gbcHdrNeto.weightx = weights[6];
        headerRow.add(lblHdrNeto, gbcHdrNeto);

        // ── Total row ──
        lblTotalLabel = new JLabel("TOTAL:");
        lblTotalLabel.setFont(FUENTE_INPUT_BOLD.deriveFont(14f));

        lblTotalBruto = new JLabel("$ 0.00");
        lblTotalBruto.setFont(FUENTE_INPUT_BOLD.deriveFont(14f));
        lblTotalBruto.setHorizontalAlignment(SwingConstants.RIGHT);

        lblTotalIva = new JLabel("$ 0.00");
        lblTotalIva.setFont(FUENTE_INPUT_BOLD.deriveFont(14f));
        lblTotalIva.setHorizontalAlignment(SwingConstants.RIGHT);

        lblTotalNeto = new JLabel("$ 0.00");
        lblTotalNeto.setFont(FUENTE_INPUT_BOLD.deriveFont(14f));
        lblTotalNeto.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel totalRow = buildTotalsRow(lblTotalLabel, lblTotalBruto, lblTotalIva, lblTotalNeto);

        panelSubtotales.add(headerRow);
        panelSubtotales.add(totalRow);
    }

    private JPanel buildTotalsRow(JLabel label, JLabel bruto, JLabel iva, JLabel neto) {
        JPanel row = new JPanel(new GridBagLayout());

        int[] widths = {90, 110, 200, 90, 100, 100, 100};
        double total = 0;
        for (int w : widths) total += w;
        double[] weights = new double[widths.length];
        for (int i = 0; i < widths.length; i++) weights[i] = (double) widths[i] / total;

        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcLabel.insets = new Insets(2, 1, 2, 1);
        gbcLabel.gridx = 0; gbcLabel.gridwidth = 4;
        gbcLabel.weightx = weights[0] + weights[1] + weights[2] + weights[3];
        gbcLabel.anchor = GridBagConstraints.WEST;
        row.add(label, gbcLabel);

        GridBagConstraints gbcBruto = new GridBagConstraints();
        gbcBruto.fill = GridBagConstraints.HORIZONTAL;
        gbcBruto.insets = new Insets(2, 1, 2, 1);
        gbcBruto.gridx = 4; gbcBruto.gridwidth = 1;
        gbcBruto.weightx = weights[4];
        gbcBruto.anchor = GridBagConstraints.EAST;
        row.add(bruto, gbcBruto);

        GridBagConstraints gbcIva = new GridBagConstraints();
        gbcIva.fill = GridBagConstraints.HORIZONTAL;
        gbcIva.insets = new Insets(2, 1, 2, 1);
        gbcIva.gridx = 5; gbcIva.gridwidth = 1;
        gbcIva.weightx = weights[5];
        gbcIva.anchor = GridBagConstraints.EAST;
        row.add(iva, gbcIva);

        GridBagConstraints gbcNeto = new GridBagConstraints();
        gbcNeto.fill = GridBagConstraints.HORIZONTAL;
        gbcNeto.insets = new Insets(2, 1, 2, 1);
        gbcNeto.gridx = 6; gbcNeto.gridwidth = 1;
        gbcNeto.weightx = weights[6];
        gbcNeto.anchor = GridBagConstraints.EAST;
        row.add(neto, gbcNeto);

        return row;
    }

    // ──────────────────────────────────────────────
    // Filter state management
    // ──────────────────────────────────────────────

    private void actualizarEstadoFiltros() {
        boolean mensual = radioMensual.isSelected();
        cmbAnio.setEnabled(mensual);
        cmbMes.setEnabled(mensual);
        dateDesde.setEnabled(!mensual);
        dateHasta.setEnabled(!mensual);
        setDateChooserEnabled(dateDesde, !mensual);
        setDateChooserEnabled(dateHasta, !mensual);
    }

    // ──────────────────────────────────────────────
    // Load detail data
    // ──────────────────────────────────────────────

    public void cargarDatos() {
        LocalDate desde;
        LocalDate hasta;

        if (radioMensual.isSelected()) {
            int anio = Integer.parseInt((String) cmbAnio.getSelectedItem());
            int mes = cmbMes.getSelectedIndex() + 1;
            desde = LocalDate.of(anio, mes, 1);
            hasta = desde.withDayOfMonth(desde.lengthOfMonth());
        } else {
            desde = getDateChooserDate(dateDesde);
            hasta = getDateChooserDate(dateHasta);
            if (desde == null || hasta == null) {
                desde = LocalDate.now().withDayOfMonth(1);
                hasta = LocalDate.now();
            }
        }

        allComprobantes = controlador.buscarComprobantes(desde, hasta);
        if (allComprobantes == null) allComprobantes = new ArrayList<>();
        poblarTablaDetalle();
    }

    private void poblarTablaDetalle() {
        modeloTabla.setRowCount(0);

        String filtroEstado = (String) cmbFiltroEstado.getSelectedItem();
        boolean filtrarEstado = filtroEstado != null && !"--Todos--".equals(filtroEstado);

        BigDecimal sumNeto = BigDecimal.ZERO;
        BigDecimal sumIva = BigDecimal.ZERO;
        BigDecimal sumBruto = BigDecimal.ZERO;

        for (ComprobanteDTO c : allComprobantes) {
            String estado = c.getEstadoPago();
            String estadoDisplay = "pendiente".equals(estado) ? "Pendiente"
                : "pagada_parcial".equals(estado) ? "Parcial"
                : "pagada_total".equals(estado) ? "Pagada" : (estado != null ? estado : "");

            if (filtrarEstado && !filtroEstado.equals(estadoDisplay)) {
                continue;
            }

            String fecha = c.getFechaEmision() != null ? c.getFechaEmision().format(FMT) : "";
            String nroFactura = String.format("%04d-%08d",
                c.getPuntoVenta() != null ? c.getPuntoVenta() : 0,
                c.getNumero() != null ? c.getNumero() : 0);
            String cliente = c.getRazonSocialRec() != null ? c.getRazonSocialRec() : "";

            String els = obtenerEls(c);
            if (els == null || els.isEmpty()) {
                els = c.getElsAsociado() != null ? c.getElsAsociado().toString() : "";
            }

            BigDecimal neto = c.getImporteNeto() != null ? c.getImporteNeto() : BigDecimal.ZERO;
            BigDecimal iva = c.getImporteIva() != null ? c.getImporteIva() : BigDecimal.ZERO;
            BigDecimal bruto = c.getImporteTotal() != null ? c.getImporteTotal() : BigDecimal.ZERO;

            sumNeto = sumNeto.add(neto);
            sumIva = sumIva.add(iva);
            sumBruto = sumBruto.add(bruto);

            modeloTabla.addRow(new Object[]{
                fecha, nroFactura, cliente, els,
                fmt$(bruto), fmt$(iva), fmt$(neto), estadoDisplay
            });
        }

        actualizarTotales(sumNeto, sumIva, sumBruto);
    }

    private void actualizarTotales(BigDecimal neto, BigDecimal iva, BigDecimal bruto) {
        String sn = fmt$(neto);
        String si = fmt$(iva);
        String sb = fmt$(bruto);

        lblTotalBruto.setText(sb);
        lblTotalIva.setText(si);
        lblTotalNeto.setText(sn);
    }

    private String obtenerEls(ComprobanteDTO c) {
        try {
            List<ItemFacturaDTO> items = controlador.getItemsFactura(c.getId());
            if (items != null && !items.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ItemFacturaDTO item : items) {
                    if (item.getElsReferencia() != null) {
                        if (sb.length() > 0) sb.append(", ");
                        sb.append(item.getElsReferencia());
                    }
                }
                return sb.toString();
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    // ──────────────────────────────────────────────
    // Load summary for TOTALES view
    // ──────────────────────────────────────────────

    private void cargarResumen() {
        modeloTotales.setRowCount(0);
        if (allComprobantes.isEmpty()) return;

        String anioSel = (String) cmbFiltroAnioTotales.getSelectedItem();
        int anio = anioSel != null ? Integer.parseInt(anioSel) : LocalDate.now().getYear();

        String[] meses = {"ENERO","FEBRERO","MARZO","ABRIL","MAYO","JUNIO",
            "JULIO","AGOSTO","SEPTIEMBRE","OCTUBRE","NOVIEMBRE","DICIEMBRE"};

        BigDecimal[] sumNeto = new BigDecimal[12];
        BigDecimal[] sumIva = new BigDecimal[12];
        BigDecimal[] sumBruto = new BigDecimal[12];
        int[] cant = new int[12];
        for (int i = 0; i < 12; i++) {
            sumNeto[i] = BigDecimal.ZERO;
            sumIva[i] = BigDecimal.ZERO;
            sumBruto[i] = BigDecimal.ZERO;
        }

        for (ComprobanteDTO c : allComprobantes) {
            if (c.getFechaEmision() == null) continue;
            if (c.getFechaEmision().getYear() != anio) continue;
            int mes = c.getFechaEmision().getMonthValue() - 1;
            cant[mes]++;
            sumNeto[mes] = sumNeto[mes].add(c.getImporteNeto() != null ? c.getImporteNeto() : BigDecimal.ZERO);
            sumIva[mes] = sumIva[mes].add(c.getImporteIva() != null ? c.getImporteIva() : BigDecimal.ZERO);
            sumBruto[mes] = sumBruto[mes].add(c.getImporteTotal() != null ? c.getImporteTotal() : BigDecimal.ZERO);
        }

        BigDecimal totalNeto = BigDecimal.ZERO;
        BigDecimal totalIva = BigDecimal.ZERO;
        BigDecimal totalBruto = BigDecimal.ZERO;

        for (int i = 0; i < 12; i++) {
            modeloTotales.addRow(new Object[]{
                meses[i], cant[i], fmt$(sumNeto[i]), fmt$(sumIva[i]), fmt$(sumBruto[i])
            });
            totalNeto = totalNeto.add(sumNeto[i]);
            totalIva = totalIva.add(sumIva[i]);
            totalBruto = totalBruto.add(sumBruto[i]);
        }

        modeloTotales.addRow(new Object[]{"TOTAL", "", fmt$(totalNeto), fmt$(totalIva), fmt$(totalBruto)});
    }

    // ──────────────────────────────────────────────
    // applyTheme
    // ──────────────────────────────────────────────

    public void applyTheme(Theme t) {
        currentTheme = t;

        setBackground(t.bgBase);

        if (panelSuperior != null) panelSuperior.setBackground(t.bgSurface);
        if (lblTitulo != null) {
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitulo.setForeground(t.brand);
        }

        if (panelToggle != null) panelToggle.setBackground(t.bgSurface);

        if (btnDetalle != null) {
            btnDetalle.setBackground(t.btnBg);
            btnDetalle.setForeground(t.textPrimary);
        }
        if (btnTotales != null) {
            btnTotales.setBackground(t.btnBg);
            btnTotales.setForeground(t.textPrimary);
        }

        if (panelFiltros != null) panelFiltros.setBackground(t.bgSurface);
        if (radioMensual != null) { radioMensual.setBackground(t.bgSurface); radioMensual.setForeground(t.textPrimary); }
        if (radioRango != null) { radioRango.setBackground(t.bgSurface); radioRango.setForeground(t.textPrimary); }

        if (lblAnio != null) { lblAnio.setForeground(t.textPrimary); lblAnio.setFont(FUENTE_LABEL); }
        if (lblMes != null) { lblMes.setForeground(t.textPrimary); lblMes.setFont(FUENTE_LABEL); }
        if (lblDesde != null) { lblDesde.setForeground(t.textPrimary); lblDesde.setFont(FUENTE_LABEL); }
        if (lblHasta != null) { lblHasta.setForeground(t.textPrimary); lblHasta.setFont(FUENTE_LABEL); }

        if (cmbAnio != null) {
            installComboUI(cmbAnio);
            cmbAnio.setBackground(getFieldBg(cmbAnio.isEnabled()));
            cmbAnio.setForeground(cmbAnio.isEnabled() ? t.textPrimary : getDisabledFg());
        }
        if (cmbMes != null) {
            installComboUI(cmbMes);
            cmbMes.setBackground(getFieldBg(cmbMes.isEnabled()));
            cmbMes.setForeground(cmbMes.isEnabled() ? t.textPrimary : getDisabledFg());
        }

        if (dateDesde != null) themeDateField(dateDesde, t);
        if (dateHasta != null) themeDateField(dateHasta, t);

        if (lblFiltroEstado != null) { lblFiltroEstado.setForeground(t.textPrimary); lblFiltroEstado.setFont(FUENTE_LABEL); }
        if (cmbFiltroEstado != null) {
            installComboUI(cmbFiltroEstado);
            cmbFiltroEstado.setBackground(getFieldBg(true));
            cmbFiltroEstado.setForeground(t.textPrimary);
        }

        if (panelSubtotales != null) {
            panelSubtotales.setBackground(t.bgSurface);
            panelSubtotales.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.brand),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
            themeTotalsLabels(panelSubtotales, t);
        }

        if (panelDetalle != null) panelDetalle.setBackground(t.bgBase);
        if (panelContenido != null) panelContenido.setBackground(t.bgBase);

        if (scrollTabla != null) scrollTabla.getViewport().setBackground(t.bgBase);
        if (tablaDetalle != null) {
            boolean isDark = t.bgBase.getRed() < 50;
            Color evenBg = isDark ? new Color(30, 40, 62) : new Color(210, 222, 242);
            Color oddBg  = isDark ? new Color(45, 58, 80) : new Color(235, 242, 252);
            Set<Integer> currency = new HashSet<>(Arrays.asList(4, 5, 6));
            Set<Integer> bold     = new HashSet<>(Arrays.asList(0));
            Set<Integer> center   = new HashSet<>(Arrays.asList(0, 1, 3, 4, 5, 6, 7));
            TablaRenderer.applyTo(tablaDetalle, t, currency, bold, center, evenBg, oddBg);
            // Estado column color coding
            boolean dark = t.bgBase.getRed() < 50;
            Color pagadaBg   = dark ? new Color(30, 70, 40)   : new Color(220, 248, 220);
            Color pendienteBg = dark ? new Color(70, 30, 30)   : new Color(255, 200, 200);
            Color parcialBg   = dark ? new Color(75, 58, 25)   : new Color(255, 220, 165);
            javax.swing.table.TableCellRenderer base = tablaDetalle.getDefaultRenderer(Object.class);
            tablaDetalle.getColumnModel().getColumn(7).setCellRenderer(
                (javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) -> {
                    java.awt.Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    if (!isSelected && value != null) {
                        String v = value.toString();
                        if ("Pagada".equals(v))            c.setBackground(pagadaBg);
                        else if ("Pendiente".equals(v))    c.setBackground(pendienteBg);
                        else if ("Parcial".equals(v))      c.setBackground(parcialBg);
                    }
                    return c;
                });
            if (tablaDetalle.getTableHeader() != null) {
                Theme.styleTableHeader(tablaDetalle.getTableHeader(), t);
            }
        }


        if (panelTotalesView != null) panelTotalesView.setBackground(t.bgBase);
        if (lblTituloTotales != null) lblTituloTotales.setForeground(t.brand);
        if (lblFiltroAnioTotales != null) { lblFiltroAnioTotales.setForeground(t.textPrimary); lblFiltroAnioTotales.setFont(FUENTE_LABEL); }
        if (cmbFiltroAnioTotales != null) {
            installComboUI(cmbFiltroAnioTotales);
            cmbFiltroAnioTotales.setBackground(getFieldBg(true));
            cmbFiltroAnioTotales.setForeground(t.textPrimary);
        }
        if (scrollTotales != null) scrollTotales.getViewport().setBackground(t.bgBase);
        if (tablaTotales != null) {
            boolean isDark = t.bgBase.getRed() < 50;
            Color evenBg = isDark ? new Color(30, 40, 62) : new Color(210, 222, 242);
            Color oddBg  = isDark ? new Color(45, 58, 80) : new Color(235, 242, 252);
            Color lastRowBg = isDark ? new Color(50, 70, 100) : new Color(170, 195, 230);
            Set<Integer> currency = new HashSet<>(Arrays.asList(2, 3, 4));
            Set<Integer> bold     = new HashSet<>();
            Set<Integer> center   = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4));
            TablaRenderer.applyTo(tablaTotales, t, currency, bold, center, evenBg, oddBg);
            // Last row highlighting (TOTAL)
            javax.swing.table.TableCellRenderer base = tablaTotales.getDefaultRenderer(Object.class);
            for (int i = 0; i < tablaTotales.getColumnCount(); i++) {
                final int col = i;
                tablaTotales.getColumnModel().getColumn(col).setCellRenderer(
                    (javax.swing.JTable table, Object value, boolean isSel, boolean hasFoc, int row, int c) -> {
                        java.awt.Component comp = base.getTableCellRendererComponent(table, value, isSel, hasFoc, row, c);
                        if (row == table.getRowCount() - 1) {
                            comp.setFont(comp.getFont().deriveFont(java.awt.Font.BOLD));
                            if (!isSel) {
                                comp.setBackground(lastRowBg);
                                if (comp instanceof javax.swing.JComponent) {
                                    ((javax.swing.JComponent) comp).setBorder(
                                        javax.swing.BorderFactory.createCompoundBorder(
                                            javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, t.brand),
                                            javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4)));
                                }
                            }
                        } else if (comp instanceof javax.swing.JComponent) {
                            ((javax.swing.JComponent) comp).setBorder(
                                javax.swing.BorderFactory.createEmptyBorder(1, 4, 1, 4));
                        }
                        return comp;
                    });
            }
            if (tablaTotales.getTableHeader() != null) {
                Theme.styleTableHeader(tablaTotales.getTableHeader(), t);
            }
        }
    }

    private void themeTotalsLabels(Container container, Theme t) {
        for (java.awt.Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(t.bgSurface);
                for (java.awt.Component c2 : ((JPanel) c).getComponents()) {
                    if (c2 instanceof JLabel) {
                        ((JLabel) c2).setForeground(t.textPrimary);
                    }
                }
            }
            if (c instanceof Container) {
                themeTotalsLabels((Container) c, t);
            }
        }
    }

    // ──────────────────────────────────────────────
    // DateChooser helpers (reflection)
    // ──────────────────────────────────────────────

    private JComponent crearDateChooser() {
        try {
            Class<?> clazz = Class.forName("com.toedter.calendar.JDateChooser");
            JComponent chooser = (JComponent) clazz.getDeclaredConstructor().newInstance();
            clazz.getMethod("setDateFormatString", String.class).invoke(chooser, "dd/MM/yyyy");
            clazz.getMethod("setDate", java.util.Date.class).invoke(chooser,
                java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            chooser.setPreferredSize(new Dimension(110, 24));
            chooser.addPropertyChangeListener("date", e -> themeDateField(chooser, currentTheme));
            installDateFocusListener(chooser);
            return chooser;
        } catch (Exception e) {
            JTextField tf = new JTextField(LocalDate.now().format(FMT));
            tf.setPreferredSize(new Dimension(110, 24));
            tf.setEditable(false);
            return tf;
        }
    }

    private void setDateChooserEnabled(JComponent comp, boolean enabled) {
        try {
            if (!(comp instanceof javax.swing.JTextField)) {
                comp.getClass().getMethod("setEnabled", boolean.class).invoke(comp, enabled);
            }
            for (java.awt.Component c : comp.getComponents()) {
                if (c instanceof javax.swing.JTextField) {
                    javax.swing.JTextField tf = (javax.swing.JTextField) c;
                    tf.setEnabled(enabled);
                    tf.setEditable(enabled);
                    tf.setBackground(getFieldBg(enabled));
                    tf.setDisabledTextColor(getDisabledFg());
                }
                if (c instanceof javax.swing.JButton) {
                    c.setEnabled(enabled);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static LocalDate getDateChooserDate(JComponent comp) {
        try {
            if (!(comp instanceof javax.swing.JTextField)) {
                Class<?> clazz = comp.getClass();
                java.util.Date d = (java.util.Date) clazz.getMethod("getDate").invoke(comp);
                if (d != null) {
                    return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                }
            } else {
                String txt = ((javax.swing.JTextField) comp).getText().trim();
                if (!txt.isEmpty()) {
                    return LocalDate.parse(txt, FMT);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void installDateFocusListener(JComponent chooser) {
        for (java.awt.Component c : chooser.getComponents()) {
            if (c instanceof javax.swing.JTextField) {
                javax.swing.JTextField tf = (javax.swing.JTextField) c;
                tf.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        tf.setForeground(currentTheme.textPrimary);
                    }
                });
                return;
            }
            if (c instanceof Container) {
                installDateFocusListener((JComponent) c);
            }
        }
    }

    private void themeDateField(JComponent comp, Theme t) {
        if (comp instanceof javax.swing.JTextField) {
            javax.swing.JTextField tf = (javax.swing.JTextField) comp;
            tf.setBackground(getFieldBg(true));
            tf.setForeground(t.textPrimary);
            tf.setDisabledTextColor(getDisabledFg());
            tf.setCaretColor(t.textPrimary);
            tf.setFont(FUENTE_INPUT_BOLD);
        } else {
            for (java.awt.Component c : comp.getComponents()) {
                if (c instanceof javax.swing.JTextField) {
                    javax.swing.JTextField tf = (javax.swing.JTextField) c;
                    tf.setBackground(getFieldBg(true));
                    tf.setForeground(t.textPrimary);
                    tf.setDisabledTextColor(getDisabledFg());
                    tf.setCaretColor(t.textPrimary);
                    tf.setFont(FUENTE_INPUT_BOLD);
                }
                if (c instanceof Container) {
                    themeDateField((JComponent) c, t);
                }
            }
        }
    }

    // ──────────────────────────────────────────────
    // Combo UI helpers
    // ──────────────────────────────────────────────

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
        if (editorComp instanceof javax.swing.JTextField) {
            javax.swing.JTextField ed = (javax.swing.JTextField) editorComp;
            ed.setBackground(getFieldBg(combo.isEnabled()));
            ed.setForeground(combo.isEnabled() ? t.textPrimary : getDisabledFg());
            ed.setDisabledTextColor(getDisabledFg());
            ed.setCaretColor(t.textPrimary);
        }
    }
}
