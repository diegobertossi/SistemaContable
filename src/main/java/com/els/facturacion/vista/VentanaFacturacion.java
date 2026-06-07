package com.els.facturacion.vista;

import com.els.facturacion.util.AutoCompleteComboBox;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Container;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

public class VentanaFacturacion extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private CardLayout cardLayout;
    private JPanel panelPrincipal;

    // PuntoVenta + Emision
    private JComboBox<String> cmbPuntoVenta;
    private JComboBox<String> cmbTipoComprobante;
    private JComponent dateFecha;
    private JComponent datePeriodoDesde;
    private JComponent datePeriodoHasta;
    private JComponent datePeriodoVto;
    private JComboBox<String> cmbConcepto;
    private JTextField txtActividades;

    // Emisor activo
    private JTextField txtEmisorRazonSocial;
    private JTextField txtEmisorCuit;
    private JTextField txtEmisorCondicionIva;

    // Receptor
    private JComboBox<String> cmbCondicionIva;
    private JComboBox<String> cmbTipoDoc;
    private AutoCompleteComboBox cmbRazonSocial;
    private AutoCompleteComboBox cmbNroDoc;
    private JTextField txtDomicilio;
    private JTextField txtEmail;
    private JTextField txtComprobanteAsoc;
    private JCheckBox chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra;
    private JButton btnImportarRemito;
    private JButton btnVerEquipos;

    // Operacion
    private JButton btnAnterior;
    private JTable tablaItems;
    private DefaultTableModel modeloTablaItems;
    private JLabel lblTotal;
    private JTextField txtImporteNeto;
    private JTextField txtImporteIva;
    private JTextField txtImporteTotal;
    private JTextField txtOtrosImpuestos;
    private JComboBox<String> cmbAlicuotaIva;
    private JButton btnEliminarItem;
    private JButton btnAgregarItem;
    private JButton btnEmitir;
    private JButton btnLimpiar;

    // Navigation
    private JCheckBox chkModoPrueba;
    private JButton btnSiguiente;

    // FIX: live-theme — contenedores visibles (antes locales)
    private JPanel panelPrincipalWrapper;
    private JPanel panelOperacion;
    private JPanel panelItems;
    private JPanel panelTotales;
    private JPanel panelEmitir;
    private JPanel panelAnterior;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JScrollPane scrollTabla;
    private JPanel panelSuperiorOp;
    private JLabel lblTituloOp;
    private JLabel lblTituloDatos;
    private JLabel lblSubtituloOp;
    private String subtituloText = "";
    private JPanel panelSur;
    private JPanel datosCard;
    private JPanel centerCol;
    private JPanel datosWrapper;
    private JPanel panelNav;
    // Secciones con titled border
    private JPanel secPuntoVenta;
    private JPanel secEmision;
    private JPanel secReceptor;
    private JPanel panelChecks;

    public VentanaFacturacion() {
        initComponents();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        Font segoeUI = new Font("Segoe UI", Font.PLAIN, 11);
        UIManager.put("defaultFont", segoeUI);
        UIManager.put("Button.font", FUENTE_BOTON);
        UIManager.put("CheckBox.font", segoeUI);
        UIManager.put("ComboBox.font", FUENTE_BOTON);
        UIManager.put("Label.font", segoeUI);
        UIManager.put("TextField.font", FUENTE_BOTON);
        UIManager.put("Table.font", segoeUI);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 11));
        UIManager.put("TitledBorder.font", new Font("Segoe UI", Font.BOLD, 11));
        setTitle("FacturaSoft v1.0 \u2014 Sistema de Facturaci\u00f3n Electr\u00f3nica");
        setSize(1024, 600);
        setMinimumSize(new Dimension(1024, 600));
        setMaximumSize(new Dimension(1024, 600));
        setResizable(false);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgSurface);

        // ===================== CARD LAYOUT =====================
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(currentTheme.bgSurface);

        // ===================== DATOS CARD =====================
        datosWrapper = new JPanel();
        datosWrapper.setLayout(new BoxLayout(datosWrapper, BoxLayout.Y_AXIS));
        datosWrapper.setBackground(currentTheme.bgSurface);

        lblTituloDatos = new JLabel("GENERACION DE COMPROBANTES", SwingConstants.CENTER);
        lblTituloDatos.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloDatos.setForeground(currentTheme.brand);
        lblTituloDatos.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelTituloDatos = new JPanel();
        panelTituloDatos.setLayout(new BoxLayout(panelTituloDatos, BoxLayout.Y_AXIS));
        panelTituloDatos.setBackground(currentTheme.bgSurface);
        panelTituloDatos.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        panelTituloDatos.add(lblTituloDatos);

        centerCol = new JPanel(new GridBagLayout());
        centerCol.setBorder(new CompoundBorder(new LineBorder(currentTheme.brand), new EmptyBorder(10, 10, 10, 10)));
        centerCol.setBackground(currentTheme.bgSurface);
        centerCol.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerCol.setMaximumSize(new Dimension(900, 2000));

        centerCol.add(crearSeccionPuntoVenta(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
        centerCol.add(crearSeccionEmision(), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
        centerCol.add(crearSeccionReceptor(), new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));

        chkModoPrueba = new JCheckBox("MODO PRUEBA");
        chkModoPrueba.setOpaque(false);
        chkModoPrueba.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkModoPrueba.setForeground(currentTheme.danger);
        chkModoPrueba.setBackground(currentTheme.bgBase);

        btnSiguiente = new JButton("SIGUIENTE >>");
        btnSiguiente.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSiguiente.setForeground(currentTheme.textPrimary);
        btnSiguiente.setBackground(currentTheme.btnBg);
        btnSiguiente.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSiguiente.setFocusPainted(false);
        btnSiguiente.setMargin(new Insets(6, 14, 6, 14));
        panelNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 3));
        panelNav.setBackground(currentTheme.bgSurface);
        panelNav.add(chkModoPrueba);
        panelNav.add(btnSiguiente);
        centerCol.add(panelNav, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));

        datosWrapper.add(centerCol);

        datosCard = new JPanel(new BorderLayout());
        datosCard.setBackground(currentTheme.bgSurface);
        datosCard.add(panelTituloDatos, BorderLayout.NORTH);
        datosCard.add(datosWrapper, BorderLayout.CENTER);

        // ===================== OPERACION CARD =====================
        panelOperacion = new JPanel(new BorderLayout(5, 5));
        panelOperacion.setBackground(currentTheme.bgSurface);
        panelOperacion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panelSuperiorOp = new JPanel();
        panelSuperiorOp.setLayout(new BoxLayout(panelSuperiorOp, BoxLayout.Y_AXIS));
        panelSuperiorOp.setBackground(currentTheme.bgSurface);
        lblTituloOp = new JLabel("DATOS DE LA OPERACION", SwingConstants.CENTER);
        lblTituloOp.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloOp.setForeground(currentTheme.brand);
        lblTituloOp.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSuperiorOp.add(lblTituloOp);
        panelSuperiorOp.add(Box.createRigidArea(new Dimension(0, 4)));
        lblSubtituloOp = new JLabel(" ", SwingConstants.CENTER);
        lblSubtituloOp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtituloOp.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSuperiorOp.add(lblSubtituloOp);
        panelSuperiorOp.setBorder(BorderFactory.createEmptyBorder(3, 0, 6, 0));

        String[] columnas = {"ELS", "PRODUCTO/SERVICIO", "CANTIDAD", "U. MEDIDA", "P. UNITARIO", "SUBTOTAL", "SEL"};
        modeloTablaItems = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 6 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 && column != 5;
            }
        };
        tablaItems = new JTable(modeloTablaItems);
        tablaItems.setRowHeight(36);
        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(400);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(6).setPreferredWidth(40);

        scrollTabla = new JScrollPane(tablaItems);

        btnAgregarItem = new JButton("+ AGREGAR ITEM");
        btnAgregarItem.setFont(FUENTE_BOTON);
        btnAgregarItem.setForeground(currentTheme.textPrimary);
        btnAgregarItem.setBackground(currentTheme.btnBg);
        btnAgregarItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregarItem.setFocusPainted(false);
        btnEliminarItem = new JButton("- ELIMINAR ITEM");
        btnEliminarItem.setFont(FUENTE_BOTON);
        btnEliminarItem.setForeground(currentTheme.textPrimary);
        btnEliminarItem.setBackground(currentTheme.btnBg);
        btnEliminarItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminarItem.setFocusPainted(false);
        cmbAlicuotaIva = new JComboBox<>(new String[]{"", "21%", "10.5%", "0%", "27%"});
        cmbAlicuotaIva.setPreferredSize(new Dimension(80, 24));
        cmbAlicuotaIva.setPrototypeDisplayValue("21%");

        panelSur = new JPanel(new GridBagLayout());
        panelSur.setBackground(currentTheme.bgBase);
        Insets panelIns = new Insets(2, 5, 2, 5);

        // Row 0 - Items management + Totals
        panelItems = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panelItems.setBackground(currentTheme.bgBase);
        panelItems.add(new JLabel("IVA:"));
        panelItems.add(cmbAlicuotaIva);
        panelItems.add(btnAgregarItem);
        panelItems.add(btnEliminarItem);

        panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        panelTotales.setBackground(currentTheme.bgBase);

        lblTotal = new JLabel("$ 0,00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setForeground(currentTheme.brandDark);

        txtImporteNeto = new JTextField(8);
        txtImporteNeto.setEditable(false);
        txtImporteNeto.setFont(FUENTE_BOTON);
        txtImporteNeto.setPreferredSize(new Dimension(75, 24));
        txtImporteNeto.setHorizontalAlignment(JTextField.RIGHT);
        txtImporteIva = new JTextField(8);
        txtImporteIva.setEditable(false);
        txtImporteIva.setFont(FUENTE_BOTON);
        txtImporteIva.setPreferredSize(new Dimension(75, 24));
        txtImporteIva.setHorizontalAlignment(JTextField.RIGHT);
        txtOtrosImpuestos = new JTextField(6);
        txtOtrosImpuestos.setText("0,00");
        txtOtrosImpuestos.setPreferredSize(new Dimension(65, 24));
        txtOtrosImpuestos.setHorizontalAlignment(JTextField.RIGHT);
        txtOtrosImpuestos.setFont(FUENTE_BOTON);
        txtImporteTotal = new JTextField(8);
        txtImporteTotal.setEditable(false);
        txtImporteTotal.setFont(FUENTE_BOTON);
        txtImporteTotal.setPreferredSize(new Dimension(85, 24));
        txtImporteTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtImporteTotal.setForeground(currentTheme.brandDark);

        panelTotales.add(new JLabel("Neto:"));
        panelTotales.add(txtImporteNeto);
        panelTotales.add(new JLabel("IVA:"));
        panelTotales.add(txtImporteIva);
        panelTotales.add(new JLabel("O.Imp:"));
        panelTotales.add(txtOtrosImpuestos);
        panelTotales.add(new JLabel("Total:"));
        panelTotales.add(txtImporteTotal);
        panelSur.add(panelItems, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, panelIns, 0, 0));
        panelSur.add(panelTotales, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, panelIns, 0, 0));

        // Row 1 - Anterior (left) + Emitir/Limpiar (right)
        btnAnterior = new JButton("<< ANTERIOR");
        btnAnterior.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAnterior.setForeground(currentTheme.textPrimary);
        btnAnterior.setBackground(currentTheme.btnBg);
        btnAnterior.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAnterior.setFocusPainted(false);
        btnAnterior.setMargin(new Insets(6, 14, 6, 14));

        btnEmitir = new JButton("GUARDAR / EMITIR FACTURA");
        btnEmitir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEmitir.setBackground(currentTheme.brandDark);
        btnEmitir.setForeground(Color.WHITE);
        btnEmitir.setFocusPainted(false);
        btnEmitir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEmitir.setMargin(new Insets(6, 16, 6, 16));
        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLimpiar.setForeground(currentTheme.textPrimary);
        btnLimpiar.setBackground(currentTheme.btnBg);
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setMargin(new Insets(6, 14, 6, 14));

        panelAnterior = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panelAnterior.setBackground(currentTheme.bgBase);
        panelAnterior.add(btnAnterior);

        panelEmitir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        panelEmitir.setBackground(currentTheme.bgBase);
        panelEmitir.add(btnEmitir);
        panelEmitir.add(btnLimpiar);

        panelSur.add(panelAnterior, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, panelIns, 0, 0));
        panelSur.add(panelEmitir, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, panelIns, 0, 0));

        panelOperacion.add(panelSuperiorOp, BorderLayout.NORTH);
        panelOperacion.add(scrollTabla, BorderLayout.CENTER);
        panelOperacion.add(panelSur, BorderLayout.SOUTH);

        panelPrincipal.add(datosCard, "datos");
        panelPrincipal.add(panelOperacion, "operacion");

        // ===================== WRAPPER + STATUS BAR =====================
        panelPrincipalWrapper = new JPanel(new BorderLayout());
        panelPrincipalWrapper.setBackground(currentTheme.bgSurface);
        panelPrincipalWrapper.add(panelPrincipal, BorderLayout.CENTER);

        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        panelPrincipalWrapper.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(panelPrincipalWrapper);
        applyTheme(currentTheme);
    }

    // ===================== SECTION BUILDERS =====================

    private JPanel crearSeccionPuntoVenta() {
        secPuntoVenta = new JPanel(new GridBagLayout());
        secPuntoVenta.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "PUNTO DE VENTA Y TIPO DE COMPROBANTE",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), currentTheme.textPrimary
        ));
        secPuntoVenta.setBackground(currentTheme.bgSurface);
        Insets ins = new Insets(3, 6, 3, 6);

        JLabel label = new JLabel("Punto de Venta:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        secPuntoVenta.add(label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        cmbPuntoVenta = new JComboBox<>(new String[]{"", "00001"});
        cmbPuntoVenta.setFont(FUENTE_BOTON);
        cmbPuntoVenta.setPreferredSize(new Dimension(80, 24));
        cmbPuntoVenta.setPrototypeDisplayValue("00001");
        secPuntoVenta.add(cmbPuntoVenta, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));

        secPuntoVenta.add(new JLabel("Tipo Comprobante:"), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        cmbTipoComprobante = new JComboBox<>(new String[]{
            "", "Factura C", "Nota de D\u00e9bito C", "Nota de Cr\u00e9dito C",
            "Recibo C", "Factura de Cr\u00e9dito Electr\u00f3nica MiPymes (FCE) C",
            "Nota de D\u00e9bito Electr\u00f3nica MiPymes (FCE) C",
            "Nota de Cr\u00e9dito Electr\u00f3nica MiPymes (FCE) C"
        });
        cmbTipoComprobante.setFont(FUENTE_BOTON);
        cmbTipoComprobante.setPreferredSize(new Dimension(240, 24));
        cmbTipoComprobante.setPrototypeDisplayValue("Factura de Cr\u00e9dito Electr\u00f3nica");
        secPuntoVenta.add(cmbTipoComprobante, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        return secPuntoVenta;
    }

    private JPanel crearSeccionEmision() {
        secEmision = new JPanel(new GridBagLayout());
        secEmision.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "DATOS DE EMISION",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), currentTheme.textPrimary
        ));
        secEmision.setBackground(currentTheme.bgSurface);
        Insets ins = new Insets(4, 8, 4, 8);

        txtEmisorRazonSocial = new JTextField(25);
        txtEmisorRazonSocial.setEditable(false);
        txtEmisorRazonSocial.setFont(FUENTE_BOTON);
        txtEmisorRazonSocial.setPreferredSize(new Dimension(300, 24));
        txtEmisorRazonSocial.setBackground(currentTheme.bgInput);

        txtEmisorCuit = new JTextField(15);
        txtEmisorCuit.setEditable(false);
        txtEmisorCuit.setFont(FUENTE_BOTON);
        txtEmisorCuit.setPreferredSize(new Dimension(120, 24));
        txtEmisorCuit.setBackground(currentTheme.bgInput);

        txtEmisorCondicionIva = new JTextField(15);
        txtEmisorCondicionIva.setEditable(false);
        txtEmisorCondicionIva.setFont(FUENTE_BOTON);
        txtEmisorCondicionIva.setPreferredSize(new Dimension(120, 24));
        txtEmisorCondicionIva.setBackground(currentTheme.bgInput);

        dateFecha = crearDateChooser();
        cmbConcepto = new JComboBox<>(new String[]{"", "Productos", "Servicios", "Productos y Servicios"});
        cmbConcepto.setFont(FUENTE_BOTON);
        cmbConcepto.setPreferredSize(new Dimension(180, 24));
        cmbConcepto.setPrototypeDisplayValue("Productos");

        txtActividades = new JTextField(25);
        txtActividades.setText("331290 - Reparacion y mantenimiento de maquinaria de uso especial n.c.p.");
        txtActividades.setEditable(false);
        txtActividades.setFont(FUENTE_BOTON);
        txtActividades.setPreferredSize(new Dimension(300, 24));
        txtActividades.setBackground(currentTheme.bgInput);

        datePeriodoDesde = crearDateChooser();
        datePeriodoHasta = crearDateChooser();
        datePeriodoVto = crearDateChooser();

        int row = 0;
        secEmision.add(new JLabel("Emisor Razon Social:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(txtEmisorRazonSocial, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        secEmision.add(new JLabel("Emisor CUIT:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(txtEmisorCuit, new GridBagConstraints(1, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        secEmision.add(new JLabel("Condicion IVA:"), new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(txtEmisorCondicionIva, new GridBagConstraints(3, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        secEmision.add(new JLabel("Fecha del comprobante:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(dateFecha, new GridBagConstraints(1, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        secEmision.add(new JLabel("Concepto:"), new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(cmbConcepto, new GridBagConstraints(3, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        secEmision.add(new JLabel("Actividades Asociadas:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(txtActividades, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        secEmision.add(new JLabel("Periodo facturado:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(datePeriodoDesde, new GridBagConstraints(1, row, 1, 1, 0.33, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        secEmision.add(new JLabel("Hasta:"), new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(datePeriodoHasta, new GridBagConstraints(3, row, 1, 1, 0.33, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        secEmision.add(new JLabel("Vto.:"), new GridBagConstraints(4, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        secEmision.add(datePeriodoVto, new GridBagConstraints(5, row, 1, 1, 0.34, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        return secEmision;
    }

    private JPanel crearSeccionReceptor() {
        secReceptor = new JPanel(new GridBagLayout());
        secReceptor.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "DATOS DEL RECEPTOR",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 11), currentTheme.textPrimary
        ));
        secReceptor.setBackground(currentTheme.bgSurface);
        Insets ins = new Insets(4, 8, 4, 8);

        cmbCondicionIva = new JComboBox<>(new String[]{
            "", "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
            "Responsable Monotributo", "Proveedor del Exterior", "Cliente del Exterior",
            "IVA Liberado - Ley 19.640", "Monotributista Social", "IVA No Alcanzado"
        });
        cmbCondicionIva.setFont(FUENTE_BOTON);
        cmbCondicionIva.setPreferredSize(new Dimension(200, 24));
        cmbCondicionIva.setPrototypeDisplayValue("IVA Responsable Inscripto");

        cmbTipoDoc = new JComboBox<>(new String[]{"", "CUIT", "DNI"});
        cmbTipoDoc.setFont(FUENTE_BOTON);
        cmbTipoDoc.setPreferredSize(new Dimension(80, 24));
        cmbTipoDoc.setPrototypeDisplayValue("CUIT");

        cmbNroDoc = new AutoCompleteComboBox();
        cmbNroDoc.setData(Arrays.asList(""));
        cmbNroDoc.setFont(FUENTE_BOTON);
        cmbNroDoc.setPreferredSize(new Dimension(140, 24));

        cmbRazonSocial = new AutoCompleteComboBox();
        cmbRazonSocial.setData(Arrays.asList(""));
        cmbRazonSocial.setFont(FUENTE_BOTON);
        cmbRazonSocial.setPreferredSize(new Dimension(150, 24));

        txtDomicilio = new JTextField(20);
        txtDomicilio.setFont(FUENTE_BOTON);
        txtDomicilio.setPreferredSize(new Dimension(180, 24));

        txtEmail = new JTextField(18);
        txtEmail.setFont(FUENTE_BOTON);
        txtEmail.setPreferredSize(new Dimension(160, 24));

        int row = 0;
        secReceptor.add(new JLabel("Condicion IVA:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(cmbCondicionIva, new GridBagConstraints(1, row, 2, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(new JLabel("Doc:"), new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(cmbTipoDoc, new GridBagConstraints(4, row, 1, 1, 0.2, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(cmbNroDoc, new GridBagConstraints(5, row, 1, 1, 0.3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));

        row++;
        secReceptor.add(new JLabel("Razon Social:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(cmbRazonSocial, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));

        row++;
        secReceptor.add(new JLabel("Domicilio:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(txtDomicilio, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(new JLabel("Email:"), new GridBagConstraints(4, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(txtEmail, new GridBagConstraints(5, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));

        row++;
        txtComprobanteAsoc = new JTextField(15);
        txtComprobanteAsoc.setFont(FUENTE_BOTON);
        txtComprobanteAsoc.setForeground(currentTheme.textPrimary);
        txtComprobanteAsoc.setBackground(currentTheme.bgInput);
        secReceptor.add(new JLabel("N° OC:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 8, 5, 8), 0, 0));
        secReceptor.add(txtComprobanteAsoc, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));

        row++;
        secReceptor.add(new JLabel("Cond. Venta:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 8, 5, 8), 0, 0));

        panelChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelChecks.setBackground(currentTheme.bgSurface);
        chkContado = new JCheckBox("Contado");
        chkContado.setOpaque(false);
        chkTarjetaDeb = new JCheckBox("Tarj. Debito");
        chkTarjetaDeb.setOpaque(false);
        chkTarjetaCred = new JCheckBox("Tarj. Credito");
        chkTarjetaCred.setOpaque(false);
        chkCC = new JCheckBox("Cta. Cte.");
        chkCC.setOpaque(false);
        chkCheque = new JCheckBox("Cheque");
        chkCheque.setOpaque(false);
        chkTransf = new JCheckBox("Transf.");
        chkTransf.setOpaque(false);
        chkOtra = new JCheckBox("Otra");
        chkOtra.setOpaque(false);
        for (JCheckBox c : new JCheckBox[]{chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra}) {
            c.setBackground(currentTheme.bgBase);
            c.setFont(FUENTE_BOTON);
            c.setForeground(currentTheme.textPrimary);
        }
        panelChecks.add(chkContado);
        panelChecks.add(chkTarjetaDeb);
        panelChecks.add(chkTarjetaCred);
        panelChecks.add(chkCC);
        panelChecks.add(chkCheque);
        panelChecks.add(chkTransf);
        panelChecks.add(chkOtra);
        secReceptor.add(panelChecks, new GridBagConstraints(1, row, 5, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 8, 5, 8), 0, 0));

        row++;
        btnImportarRemito = new JButton("VER REMITOS");
        btnImportarRemito.setFont(FUENTE_BOTON);
        btnImportarRemito.setForeground(currentTheme.textPrimary);
        btnImportarRemito.setBackground(currentTheme.btnBg);
        btnImportarRemito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImportarRemito.setFocusPainted(false);
        btnVerEquipos = new JButton("VER EQUIPOS");
        btnVerEquipos.setFont(FUENTE_BOTON);
        btnVerEquipos.setForeground(currentTheme.textPrimary);
        btnVerEquipos.setBackground(currentTheme.btnBg);
        btnVerEquipos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerEquipos.setFocusPainted(false);
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(currentTheme.bgSurface);
        panelBotones.add(btnImportarRemito);
        panelBotones.add(btnVerEquipos);
        secReceptor.add(panelBotones, new GridBagConstraints(0, row, 6, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 8, 4, 8), 0, 0));

        return secReceptor;
    }

    // ===================== UI FACTORIES =====================

    private JComponent crearDateChooser() {
        try {
            Class<?> clazz = Class.forName("com.toedter.calendar.JDateChooser");
            JComponent chooser = (JComponent) clazz.getDeclaredConstructor().newInstance();
            clazz.getMethod("setDateFormatString", String.class).invoke(chooser, "dd/MM/yyyy");
            clazz.getMethod("setDate", java.util.Date.class).invoke(chooser,
                java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            chooser.setPreferredSize(new Dimension(110, 24));
            return chooser;
        } catch (Exception e) {
            JTextField tf = new JTextField(LocalDate.now().format(FMT));
            tf.setPreferredSize(new Dimension(110, 24));
            tf.setEditable(false);
            return tf;
        }
    }

    private void estilizarBoton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setForeground(currentTheme.textPrimary);
        btn.setBackground(currentTheme.btnBg);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(currentTheme.hoverBg);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(currentTheme.btnBg);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                btn.setBackground(currentTheme.pressedBg);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                btn.setBackground(currentTheme.hoverBg);
            }
        });
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        Color titledFg = t.textPrimary;
        Color titledLine = t.brand;
        Font titledFont = new Font("Segoe UI", Font.BOLD, 11);

        // FIX: live-theme — contenedores raíz (no usar getContentPane(), está fuera del árbol)
        if (panelPrincipalWrapper != null) panelPrincipalWrapper.setBackground(t.bgSurface);
        if (panelPrincipal != null) panelPrincipal.setBackground(t.bgSurface);
        if (datosCard != null) datosCard.setBackground(t.bgSurface);
        if (datosWrapper != null) datosWrapper.setBackground(t.bgSurface);
        if (panelOperacion != null) {
            panelOperacion.setBackground(t.bgSurface);
            panelOperacion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }
        if (panelSuperiorOp != null) panelSuperiorOp.setBackground(t.bgSurface);
        if (panelSur != null) panelSur.setBackground(t.bgBase);
        if (panelItems != null) panelItems.setBackground(t.bgBase);
        if (panelTotales != null) panelTotales.setBackground(t.bgBase);
        if (panelEmitir != null) panelEmitir.setBackground(t.bgBase);
        if (panelAnterior != null) panelAnterior.setBackground(t.bgBase);
        if (statusBar != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        }
        if (lblTituloOp != null) lblTituloOp.setForeground(t.brand);
        if (lblTituloDatos != null) lblTituloDatos.setForeground(t.brand);
        if (lblSubtituloOp != null) actualizarSubtitulo();
        if (centerCol != null) {
            centerCol.setBackground(t.bgSurface);
            centerCol.setBorder(new CompoundBorder(new LineBorder(t.brand), new EmptyBorder(10, 10, 10, 10)));
        }
        if (panelNav != null) panelNav.setBackground(t.bgSurface);
        // Section panels con titled border
        if (secPuntoVenta != null) {
            secPuntoVenta.setBackground(t.bgSurface);
            secPuntoVenta.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(t.brand),
                "PUNTO DE VENTA Y TIPO DE COMPROBANTE",
                TitledBorder.LEFT, TitledBorder.TOP, titledFont, titledFg));
        }
        if (secEmision != null) {
            secEmision.setBackground(t.bgSurface);
            secEmision.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(t.brand),
                "DATOS DE EMISION",
                TitledBorder.LEFT, TitledBorder.TOP, titledFont, titledFg));
        }
        if (secReceptor != null) {
            secReceptor.setBackground(t.bgSurface);
            secReceptor.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(t.brand),
                "DATOS DEL RECEPTOR",
                TitledBorder.LEFT, TitledBorder.TOP, titledFont, titledFg));
        }
        if (panelChecks != null) panelChecks.setBackground(t.bgSurface);
        // Scroll panes
        if (scrollTabla != null) {
            scrollTabla.getViewport().setBackground(t.bgSurface);
            scrollTabla.setBorder(BorderFactory.createLineBorder(t.borderLight));
        }

        // FIX: live-theme — inputs y controles (field references, siempre funcionan)
        if (cmbPuntoVenta != null) { cmbPuntoVenta.setBackground(t.bgInput); cmbPuntoVenta.setForeground(t.textPrimary); }
        if (cmbTipoComprobante != null) { cmbTipoComprobante.setBackground(t.bgInput); cmbTipoComprobante.setForeground(t.textPrimary); }
        if (dateFecha != null) { dateFecha.setBackground(t.bgInput); dateFecha.setForeground(t.textPrimary); }
        if (datePeriodoDesde != null) { datePeriodoDesde.setBackground(t.bgInput); datePeriodoDesde.setForeground(t.textPrimary); }
        if (datePeriodoHasta != null) { datePeriodoHasta.setBackground(t.bgInput); datePeriodoHasta.setForeground(t.textPrimary); }
        if (datePeriodoVto != null) { datePeriodoVto.setBackground(t.bgInput); datePeriodoVto.setForeground(t.textPrimary); }
        if (cmbConcepto != null) { cmbConcepto.setBackground(t.bgInput); cmbConcepto.setForeground(t.textPrimary); }
        if (txtActividades != null) { txtActividades.setBackground(t.bgInput); txtActividades.setForeground(t.textPrimary); }
        if (txtEmisorRazonSocial != null) { txtEmisorRazonSocial.setBackground(t.bgInput); txtEmisorRazonSocial.setForeground(t.textPrimary); }
        if (txtEmisorCuit != null) { txtEmisorCuit.setBackground(t.bgInput); txtEmisorCuit.setForeground(t.textPrimary); }
        if (txtEmisorCondicionIva != null) { txtEmisorCondicionIva.setBackground(t.bgInput); txtEmisorCondicionIva.setForeground(t.textPrimary); }
        if (cmbCondicionIva != null) { cmbCondicionIva.setBackground(t.bgInput); cmbCondicionIva.setForeground(t.textPrimary); }
        if (cmbTipoDoc != null) { cmbTipoDoc.setBackground(t.bgInput); cmbTipoDoc.setForeground(t.textPrimary); }
        if (cmbRazonSocial != null) { cmbRazonSocial.setBackground(t.bgInput); cmbRazonSocial.setForeground(t.textPrimary); }
        if (cmbNroDoc != null) { cmbNroDoc.setBackground(t.bgInput); cmbNroDoc.setForeground(t.textPrimary); }
        if (txtDomicilio != null) { txtDomicilio.setBackground(t.bgInput); txtDomicilio.setForeground(t.textPrimary); }
        if (txtEmail != null) { txtEmail.setBackground(t.bgInput); txtEmail.setForeground(t.textPrimary); }
        if (txtComprobanteAsoc != null) { txtComprobanteAsoc.setBackground(t.bgInput); txtComprobanteAsoc.setForeground(t.textPrimary); }
        if (chkContado != null) { chkContado.setBackground(t.bgBase); chkContado.setForeground(t.textPrimary); }
        if (chkTarjetaDeb != null) { chkTarjetaDeb.setBackground(t.bgBase); chkTarjetaDeb.setForeground(t.textPrimary); }
        if (chkTarjetaCred != null) { chkTarjetaCred.setBackground(t.bgBase); chkTarjetaCred.setForeground(t.textPrimary); }
        if (chkCC != null) { chkCC.setBackground(t.bgBase); chkCC.setForeground(t.textPrimary); }
        if (chkCheque != null) { chkCheque.setBackground(t.bgBase); chkCheque.setForeground(t.textPrimary); }
        if (chkTransf != null) { chkTransf.setBackground(t.bgBase); chkTransf.setForeground(t.textPrimary); }
        if (chkOtra != null) { chkOtra.setBackground(t.bgBase); chkOtra.setForeground(t.textPrimary); }
        if (btnImportarRemito != null) { btnImportarRemito.setBackground(t.btnBg); btnImportarRemito.setForeground(t.textPrimary); }
        if (btnVerEquipos != null) { btnVerEquipos.setBackground(t.btnBg); btnVerEquipos.setForeground(t.textPrimary); }
        if (btnAnterior != null) { btnAnterior.setBackground(t.btnBg); btnAnterior.setForeground(t.textPrimary); }
        if (tablaItems != null) {
            boolean isDarkTheme = t.bgBase.getRed() < 50;
            Color evenBg = isDarkTheme ? new Color(30, 40, 62) : new Color(210, 222, 242);
            Color oddBg  = isDarkTheme ? new Color(45, 58, 80) : new Color(235, 242, 252);
            Set<Integer> currency = new HashSet<>(Arrays.asList(4, 5));
            Set<Integer> bold     = new HashSet<>(Arrays.asList(0));
            Set<Integer> center   = new HashSet<>(Arrays.asList(0, 2, 3, 4, 5));
            TablaRenderer.applyTo(tablaItems, t, currency, bold, center, evenBg, oddBg);
            if (tablaItems.getTableHeader() != null) {
                styleHeaderBold(tablaItems.getTableHeader(), t);
            }
        }
        if (lblTotal != null) { lblTotal.setForeground(t.brandDark); }
        if (txtImporteNeto != null) { txtImporteNeto.setBackground(t.bgInput); txtImporteNeto.setForeground(t.textPrimary); }
        if (txtImporteIva != null) { txtImporteIva.setBackground(t.bgInput); txtImporteIva.setForeground(t.textPrimary); }
        if (txtImporteTotal != null) { txtImporteTotal.setBackground(t.bgInput); txtImporteTotal.setForeground(t.brandDark); }
        if (txtOtrosImpuestos != null) { txtOtrosImpuestos.setBackground(t.bgInput); txtOtrosImpuestos.setForeground(t.textPrimary); }
        if (cmbAlicuotaIva != null) { cmbAlicuotaIva.setBackground(t.bgInput); cmbAlicuotaIva.setForeground(t.textPrimary); }
        if (btnEliminarItem != null) { btnEliminarItem.setBackground(t.btnBg); btnEliminarItem.setForeground(t.textPrimary); }
        if (btnAgregarItem != null) { btnAgregarItem.setBackground(t.btnBg); btnAgregarItem.setForeground(t.textPrimary); }
        if (btnEmitir != null) {
            btnEmitir.setBackground(t.brandDark);
            btnEmitir.setForeground(Color.WHITE);
        }
        if (btnLimpiar != null) { btnLimpiar.setBackground(t.btnBg); btnLimpiar.setForeground(t.textPrimary); }
        if (chkModoPrueba != null) { chkModoPrueba.setBackground(t.bgBase); chkModoPrueba.setForeground(t.danger); }
        if (btnSiguiente != null) { btnSiguiente.setBackground(t.btnBg); btnSiguiente.setForeground(t.textPrimary); }
        // FIX: live-theme — walk desde panelPrincipalWrapper (en lugar de this, que está fuera del árbol)
        if (panelPrincipalWrapper != null) {
            themeLabels(panelPrincipalWrapper, t);
        }
        // FIX: re-aplicar color brand a los títulos DESPUÉS de themeLabels (que los sobreescribe)
        if (lblTituloDatos != null) lblTituloDatos.setForeground(t.brand);
        if (lblTituloOp != null) lblTituloOp.setForeground(t.brand);
    }

    private void themeLabels(Container c, Theme t) {
        for (java.awt.Component comp : c.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;
                if (lbl == lblTituloDatos || lbl == lblTituloOp) {
                    // Skip titles — they keep the brand color
                } else {
                    lbl.setForeground(t.textPrimary);
                }
            }
            if (comp instanceof Container) {
                themeLabels((Container) comp, t);
            }
        }
    }

    private static void styleHeaderBold(JTableHeader header, Theme theme) {
        boolean isDark = theme.bgBase.getRed() < 50;
        Color fg = isDark ? Color.WHITE : theme.textPrimary;
        Color headerBg = isDark
            ? new Color(Math.min(255, theme.bgElevated.getRed() + 20), Math.min(255, theme.bgElevated.getGreen() + 20), Math.min(255, theme.bgElevated.getBlue() + 20))
            : new Color(Math.min(255, theme.brand.getRed() + 115), Math.min(255, theme.brand.getGreen() + 110), Math.min(255, theme.brand.getBlue() + 10));
        int avg = (headerBg.getRed() + headerBg.getGreen() + headerBg.getBlue()) / 3;
        Color divider = avg < 80
            ? new Color(Math.min(255, headerBg.getRed() + 60), Math.min(255, headerBg.getGreen() + 60), Math.min(255, headerBg.getBlue() + 60))
            : new Color(Math.max(0, headerBg.getRed() - 60), Math.max(0, headerBg.getGreen() - 60), Math.max(0, headerBg.getBlue() - 60));
        Border colBorder = BorderFactory.createMatteBorder(0, 0, 1, 2, divider);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                  boolean isSelected, boolean hasFocus, int row, int column) {
                DefaultTableCellRenderer c = (DefaultTableCellRenderer)
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(fg);
                c.setBackground(headerBg);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 11));
                c.setText(value != null ? value.toString().toUpperCase() : "");
                c.setBorder(colBorder);
                return c;
            }
        });
    }

    // ===================== GETTERS =====================

    public CardLayout getCardLayout() { return cardLayout; }
    public JPanel getPanelPrincipal() { return panelPrincipal; }

    // PuntoVenta + Emision
    public JComboBox<String> getCmbPuntoVenta() { return cmbPuntoVenta; }
    public JComboBox<String> getCmbTipoComprobante() { return cmbTipoComprobante; }
    public JComponent getDateFecha() { return dateFecha; }
    public JComponent getDatePeriodoDesde() { return datePeriodoDesde; }
    public JComponent getDatePeriodoHasta() { return datePeriodoHasta; }
    public JComponent getDatePeriodoVto() { return datePeriodoVto; }
    public JComboBox<String> getCmbConcepto() { return cmbConcepto; }
    public JTextField getTxtActividades() { return txtActividades; }

    // Receptor
    public JComboBox<String> getCmbCondicionIva() { return cmbCondicionIva; }
    public JComboBox<String> getCmbTipoDoc() { return cmbTipoDoc; }
    public AutoCompleteComboBox getCmbRazonSocial() { return cmbRazonSocial; }
    public AutoCompleteComboBox getCmbNroDoc() { return cmbNroDoc; }
    public JTextField getTxtDomicilio() { return txtDomicilio; }
    public JTextField getTxtEmail() { return txtEmail; }
    public JCheckBox getChkContado() { return chkContado; }
    public JCheckBox getChkTarjetaDeb() { return chkTarjetaDeb; }
    public JCheckBox getChkTarjetaCred() { return chkTarjetaCred; }
    public JCheckBox getChkCC() { return chkCC; }
    public JCheckBox getChkCheque() { return chkCheque; }
    public JCheckBox getChkTransf() { return chkTransf; }
    public JCheckBox getChkOtra() { return chkOtra; }
    public JTextField getTxtComprobanteAsoc() { return txtComprobanteAsoc; }

    // Operacion
    public JButton getBtnAnterior() { return btnAnterior; }
    public JTable getTablaItems() { return tablaItems; }
    public DefaultTableModel getModeloItems() { return modeloTablaItems; }
    public JLabel getLblTotal() { return lblTotal; }
    public JTextField getTxtImporteNeto() { return txtImporteNeto; }
    public JTextField getTxtImporteIva() { return txtImporteIva; }
    public JTextField getTxtImporteTotal() { return txtImporteTotal; }
    public JTextField getTxtOtrosImpuestos() { return txtOtrosImpuestos; }
    public JCheckBox getChkModoPrueba() { return chkModoPrueba; }
    public JButton getBtnSiguiente() { return btnSiguiente; }
    public void setSubtituloOp(String texto) {
        subtituloText = texto != null ? texto : "";
        actualizarSubtitulo();
    }

    private void actualizarSubtitulo() {
        if (lblSubtituloOp != null && !subtituloText.isEmpty()) {
            boolean isDark = currentTheme.bgBase.getRed() < 50;
            Color c = isDark ? new Color(210, 180, 70) : new Color(170, 135, 50);
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            lblSubtituloOp.setText("<html><font color='" + hex + "' size='5'><b>" + subtituloText + "</b></font></html>");
        } else if (lblSubtituloOp != null) {
            lblSubtituloOp.setText(" ");
        }
    }
    public JButton getBtnImportarRemito() { return btnImportarRemito; }
    public JButton getBtnVerEquipos() { return btnVerEquipos; }
    public JButton getBtnAgregarItem() { return btnAgregarItem; }
    public JButton getBtnEliminarItem() { return btnEliminarItem; }
    public JComboBox<String> getCmbAlicuotaIva() { return cmbAlicuotaIva; }
    public JButton getBtnEmitir() { return btnEmitir; }
    public JButton getBtnLimpiar() { return btnLimpiar; }

    // ===================== SETTERS / MUTATORS =====================

    public void setRazonSocial(String texto) { cmbRazonSocial.setEditorText(texto); }
    public void setNroDoc(String texto) { cmbNroDoc.setEditorText(texto); }
    public void setTipoDoc(String tipo) { cmbTipoDoc.setSelectedItem(tipo); }
    public void setCmbCondicionIva(String value) { cmbCondicionIva.setSelectedItem(value); }

    public void actualizarEmisor(String razonSocial, String cuit, String condicionIva) {
        txtEmisorRazonSocial.setText(razonSocial);
        txtEmisorCuit.setText(cuit);
        txtEmisorCondicionIva.setText(condicionIva);
    }

    public void actualizarTiposComprobante(String condicionIva) {
        cmbTipoComprobante.removeAllItems();
        cmbTipoComprobante.addItem("");
        String letra = "RI".equals(condicionIva) ? " A" : " C";
        cmbTipoComprobante.addItem("Factura" + letra);
        cmbTipoComprobante.addItem("Nota de Debito" + letra);
        cmbTipoComprobante.addItem("Nota de Credito" + letra);
        cmbTipoComprobante.addItem("Recibo" + letra);
        cmbTipoComprobante.addItem("Factura de Credito Electronica MiPymes (FCE)" + letra.trim());
        cmbTipoComprobante.addItem("Nota de Debito Electronica MiPymes (FCE)" + letra.trim());
        cmbTipoComprobante.addItem("Nota de Credito Electronica MiPymes (FCE)" + letra.trim());
        if ("RI".equals(condicionIva)) {
            String letraB = " B";
            cmbTipoComprobante.addItem("Factura" + letraB);
            cmbTipoComprobante.addItem("Nota de Debito" + letraB);
            cmbTipoComprobante.addItem("Nota de Credito" + letraB);
            cmbTipoComprobante.addItem("Recibo" + letraB);
            cmbTipoComprobante.addItem("Factura de Credito Electronica MiPymes (FCE)" + letraB.trim());
            cmbTipoComprobante.addItem("Nota de Debito Electronica MiPymes (FCE)" + letraB.trim());
            cmbTipoComprobante.addItem("Nota de Credito Electronica MiPymes (FCE)" + letraB.trim());
        }
    }

    public JTextField getTxtEmisorRazonSocial() { return txtEmisorRazonSocial; }
    public JTextField getTxtEmisorCuit() { return txtEmisorCuit; }
    public JTextField getTxtEmisorCondicionIva() { return txtEmisorCondicionIva; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        SwingUtilities.invokeLater(() -> {
            VentanaFacturacion v = new VentanaFacturacion();
            new com.els.facturacion.controlador.ControladorFacturacion(v).inicializar();
            v.setVisible(true);
        });
    }
}
