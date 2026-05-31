package com.els.facturacion.vista;

import com.els.facturacion.util.AutoCompleteComboBox;
import javax.swing.BorderFactory;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

public class VentanaFacturacion extends javax.swing.JFrame {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
    private JCheckBox chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra;
    private JTextField txtComprobanteAsoc;
    private JButton btnImportarRemito;

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

    public VentanaFacturacion() {
        initComponents();
    }

    private void initComponents() {
        setTitle("FacturaSoft v1.0 \u2014 Sistema de Facturaci\u00f3n Electr\u00f3nica");
        setSize(1024, 720);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        // ===================== CARD LAYOUT =====================
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(TemaFacturaSoft.BG_APP);

        // ===================== DATOS CARD =====================
        JPanel datosWrapper = new JPanel(new GridBagLayout());
        datosWrapper.setBackground(TemaFacturaSoft.BG_APP);

        JPanel centerCol = new JPanel(new GridBagLayout());
        centerCol.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 160)), new EmptyBorder(10, 10, 10, 10)));
        centerCol.setBackground(TemaFacturaSoft.BG_APP);

        centerCol.add(crearSeccionPuntoVenta(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
        centerCol.add(crearSeccionEmision(), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
        centerCol.add(crearSeccionReceptor(), new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));

        chkModoPrueba = new JCheckBox("MODO PRUEBA");
        chkModoPrueba.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        chkModoPrueba.setForeground(new Color(200, 0, 0));
        chkModoPrueba.setBackground(TemaFacturaSoft.BG_APP);

        btnSiguiente = new JButton("SIGUIENTE >>");
        estilizarBoton(btnSiguiente);
        JPanel panelNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 3));
        panelNav.setBackground(TemaFacturaSoft.BG_APP);
        panelNav.add(chkModoPrueba);
        panelNav.add(btnSiguiente);
        centerCol.add(panelNav, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));

        datosWrapper.add(centerCol, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        JScrollPane scroll = new JScrollPane(datosWrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel datosCard = new JPanel(new BorderLayout());
        datosCard.setBackground(TemaFacturaSoft.BG_APP);
        datosCard.add(scroll);

        // ===================== OPERACION CARD =====================
        JPanel panelOperacion = new JPanel(new BorderLayout(5, 5));
        panelOperacion.setBackground(TemaFacturaSoft.BG_APP);
        panelOperacion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(TemaFacturaSoft.BG_APP);
        btnAnterior = new JButton("<< ANTERIOR");
        estilizarBoton(btnAnterior);
        JLabel lblTitulo = new JLabel("DATOS DE LA OPERACION", SwingConstants.CENTER);
        lblTitulo.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(15f));
        lblTitulo.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        panelSuperior.add(btnAnterior, BorderLayout.WEST);
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        String[] columnas = {"ELS", "PRODUCTO/SERVICIO", "CANTIDAD", "U. MEDIDA", "P. UNITARIO", "SUBTOTAL", "SEL"};
        modeloTablaItems = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 6 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 5;
            }
        };
        tablaItems = new JTable(modeloTablaItems);
        TemaFacturaSoft.aplicarEstiloTabla(tablaItems);
        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(50);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(6).setPreferredWidth(40);

        // Currency renderer for P. UNITARIO and SUBTOTAL
        DecimalFormat currencyFormat = new DecimalFormat("$ #,##0.00");
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value != null) {
                    String text = value.toString().trim();
                    if (!text.isEmpty()) {
                        try {
                            String clean = text.replace("$", "").replace(".", "").replace(",", ".");
                            BigDecimal num = new BigDecimal(clean);
                            setText(currencyFormat.format(num));
                            return;
                        } catch (Exception ignored) {}
                    }
                }
                setText(value != null ? value.toString() : "");
            }
        };
        currencyRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tablaItems.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);
        tablaItems.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);

        TemaFacturaSoft.centrarColumnas(tablaItems, 0, 2, 3);

        TemaFacturaSoft.aplicarEstiloTablaHeaderCentrado(tablaItems);

        JScrollPane scrollTabla = new JScrollPane(tablaItems);

        btnAgregarItem = new JButton("+ AGREGAR ITEM");
        estilizarBoton(btnAgregarItem);
        btnEliminarItem = new JButton("- ELIMINAR ITEM");
        estilizarBoton(btnEliminarItem);
        cmbAlicuotaIva = new JComboBox<>(new String[]{"21%", "10.5%", "0%", "27%"});
        cmbAlicuotaIva.setPreferredSize(new Dimension(80, 24));
        cmbAlicuotaIva.setPrototypeDisplayValue("21%");

        JPanel panelSur = new JPanel(new GridBagLayout());
        panelSur.setBackground(TemaFacturaSoft.BG_APP);
        Insets panelIns = new Insets(2, 5, 2, 5);

        // Row 0 - Items management + Totals
        JPanel panelItems = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panelItems.setBackground(TemaFacturaSoft.BG_APP);
        panelItems.add(new JLabel("IVA:"));
        panelItems.add(cmbAlicuotaIva);
        panelItems.add(btnAgregarItem);
        panelItems.add(btnEliminarItem);

        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        panelTotales.setBackground(TemaFacturaSoft.BG_APP);

        lblTotal = new JLabel("$ 0,00");
        lblTotal.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(16f));
        lblTotal.setForeground(TemaFacturaSoft.ACCENT_SUCCESS);

        txtImporteNeto = new JTextField(10);
        txtImporteNeto.setEditable(false);
        txtImporteNeto.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtImporteNeto.setPreferredSize(new Dimension(90, 24));
        txtImporteNeto.setHorizontalAlignment(JTextField.RIGHT);
        txtImporteIva = new JTextField(10);
        txtImporteIva.setEditable(false);
        txtImporteIva.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtImporteIva.setPreferredSize(new Dimension(90, 24));
        txtImporteIva.setHorizontalAlignment(JTextField.RIGHT);
        txtOtrosImpuestos = new JTextField(8);
        txtOtrosImpuestos.setText("0,00");
        txtOtrosImpuestos.setPreferredSize(new Dimension(80, 24));
        txtOtrosImpuestos.setHorizontalAlignment(JTextField.RIGHT);
        txtOtrosImpuestos.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtImporteTotal = new JTextField(10);
        txtImporteTotal.setEditable(false);
        txtImporteTotal.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtImporteTotal.setPreferredSize(new Dimension(100, 24));
        txtImporteTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtImporteTotal.setForeground(TemaFacturaSoft.ACCENT_SUCCESS);

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

        // Row 1 - Emitir + Limpiar
        JPanel panelEmitir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        panelEmitir.setBackground(TemaFacturaSoft.BG_APP);

        btnEmitir = new JButton("GUARDAR / EMITIR FACTURA");
        btnEmitir.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(13f));
        btnEmitir.setBackground(TemaFacturaSoft.ACCENT_SUCCESS);
        btnEmitir.setForeground(Color.WHITE);
        btnEmitir.setFocusPainted(false);
        btnEmitir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar = new JButton("LIMPIAR");
        estilizarBoton(btnLimpiar);

        panelEmitir.add(btnEmitir);
        panelEmitir.add(btnLimpiar);

        panelSur.add(panelEmitir, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, panelIns, 0, 0));

        panelOperacion.add(panelSuperior, BorderLayout.NORTH);
        panelOperacion.add(scrollTabla, BorderLayout.CENTER);
        panelOperacion.add(panelSur, BorderLayout.SOUTH);

        panelPrincipal.add(datosCard, "datos");
        panelPrincipal.add(panelOperacion, "operacion");

        // ===================== WRAPPER + STATUS BAR =====================
        JPanel panelPrincipalWrapper = new JPanel(new BorderLayout());
        panelPrincipalWrapper.setBackground(TemaFacturaSoft.BG_APP);
        panelPrincipalWrapper.add(panelPrincipal, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(TemaFacturaSoft.BG_STATUS_BAR);
        JLabel lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));
        lblStatus.setForeground(TemaFacturaSoft.TEXT_MUTED);
        statusBar.add(lblStatus);
        panelPrincipalWrapper.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(panelPrincipalWrapper);
    }

    // ===================== SECTION BUILDERS =====================

    private JPanel crearSeccionPuntoVenta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(TemaFacturaSoft.crearSeccion("PUNTO DE VENTA Y TIPO DE COMPROBANTE"));
        panel.setBackground(TemaFacturaSoft.BG_APP);
        Insets ins = new Insets(3, 6, 3, 6);

        panel.add(new JLabel("Punto de Venta:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        cmbPuntoVenta = new JComboBox<>(new String[]{"00001"});
        cmbPuntoVenta.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        cmbPuntoVenta.setPreferredSize(new Dimension(80, 24));
        cmbPuntoVenta.setPrototypeDisplayValue("00001");
        panel.add(cmbPuntoVenta, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));

        panel.add(new JLabel("Tipo Comprobante:"), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        cmbTipoComprobante = new JComboBox<>(new String[]{
            "Factura C", "Nota de D\u00e9bito C", "Nota de Cr\u00e9dito C",
            "Recibo C", "Factura de Cr\u00e9dito Electr\u00f3nica MiPymes (FCE) C",
            "Nota de D\u00e9bito Electr\u00f3nica MiPymes (FCE) C",
            "Nota de Cr\u00e9dito Electr\u00f3nica MiPymes (FCE) C"
        });
        cmbTipoComprobante.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        cmbTipoComprobante.setPreferredSize(new Dimension(240, 24));
        cmbTipoComprobante.setPrototypeDisplayValue("Factura de Cr\u00e9dito Electr\u00f3nica");
        panel.add(cmbTipoComprobante, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        return panel;
    }

    private JPanel crearSeccionEmision() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(TemaFacturaSoft.crearSeccion("DATOS DE EMISION"));
        panel.setBackground(TemaFacturaSoft.BG_APP);
        Insets ins = new Insets(4, 8, 4, 8);

        txtEmisorRazonSocial = new JTextField(25);
        txtEmisorRazonSocial.setEditable(false);
        txtEmisorRazonSocial.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtEmisorRazonSocial.setPreferredSize(new Dimension(300, 24));
        txtEmisorRazonSocial.setBackground(TemaFacturaSoft.BG_INPUT_DISABLED);

        txtEmisorCuit = new JTextField(15);
        txtEmisorCuit.setEditable(false);
        txtEmisorCuit.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtEmisorCuit.setPreferredSize(new Dimension(120, 24));
        txtEmisorCuit.setBackground(TemaFacturaSoft.BG_INPUT_DISABLED);

        txtEmisorCondicionIva = new JTextField(15);
        txtEmisorCondicionIva.setEditable(false);
        txtEmisorCondicionIva.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtEmisorCondicionIva.setPreferredSize(new Dimension(120, 24));
        txtEmisorCondicionIva.setBackground(TemaFacturaSoft.BG_INPUT_DISABLED);

        dateFecha = crearDateChooser();
        cmbConcepto = new JComboBox<>(new String[]{"Productos", "Servicios", "Productos y Servicios"});
        cmbConcepto.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        cmbConcepto.setPreferredSize(new Dimension(180, 24));
        cmbConcepto.setPrototypeDisplayValue("Productos");

        txtActividades = new JTextField(25);
        txtActividades.setText("331290 - Reparacion y mantenimiento de maquinaria de uso especial n.c.p.");
        txtActividades.setEditable(false);
        txtActividades.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtActividades.setPreferredSize(new Dimension(300, 24));
        txtActividades.setBackground(TemaFacturaSoft.BG_INPUT_DISABLED);

        datePeriodoDesde = crearDateChooser();
        datePeriodoHasta = crearDateChooser();
        datePeriodoVto = crearDateChooser();

        int row = 0;
        panel.add(new JLabel("Emisor Razon Social:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(txtEmisorRazonSocial, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        panel.add(new JLabel("Emisor CUIT:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(txtEmisorCuit, new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(new JLabel("     Condicion IVA:"), new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(txtEmisorCondicionIva, new GridBagConstraints(3, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));

        row++;
        panel.add(new JLabel("Fecha del comprobante:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(dateFecha, new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(new JLabel("     Concepto:"), new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(cmbConcepto, new GridBagConstraints(3, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));

        row++;
        panel.add(new JLabel("Actividades Asociadas:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(txtActividades, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        panel.add(new JLabel("Periodo facturado:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(datePeriodoDesde, new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(new JLabel("Hasta:"), new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(datePeriodoHasta, new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(new JLabel("Vto.:"), new GridBagConstraints(4, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(datePeriodoVto, new GridBagConstraints(5, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));

        return panel;
    }

    private JPanel crearSeccionReceptor() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(TemaFacturaSoft.crearSeccion("DATOS DEL RECEPTOR"));
        panel.setBackground(TemaFacturaSoft.BG_APP);
        Insets ins = new Insets(4, 8, 4, 8);

        cmbCondicionIva = new JComboBox<>(new String[]{
            "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
            "Responsable Monotributo", "Proveedor del Exterior", "Cliente del Exterior",
            "IVA Liberado - Ley 19.640", "Monotributista Social", "IVA No Alcanzado"
        });
        cmbCondicionIva.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        cmbCondicionIva.setPreferredSize(new Dimension(200, 24));
        cmbCondicionIva.setPrototypeDisplayValue("IVA Responsable Inscripto");

        cmbTipoDoc = new JComboBox<>(new String[]{"CUIT", "DNI"});
        cmbTipoDoc.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        cmbTipoDoc.setPreferredSize(new Dimension(80, 24));
        cmbTipoDoc.setPrototypeDisplayValue("CUIT");

        cmbNroDoc = new AutoCompleteComboBox();
        cmbNroDoc.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        cmbNroDoc.setPreferredSize(new Dimension(140, 24));

        cmbRazonSocial = new AutoCompleteComboBox();
        cmbRazonSocial.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        cmbRazonSocial.setPreferredSize(new Dimension(220, 24));

        txtDomicilio = new JTextField(20);
        txtDomicilio.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtDomicilio.setPreferredSize(new Dimension(180, 24));

        txtEmail = new JTextField(18);
        txtEmail.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtEmail.setPreferredSize(new Dimension(160, 24));

        txtComprobanteAsoc = new JTextField(12);
        txtComprobanteAsoc.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        txtComprobanteAsoc.setPreferredSize(new Dimension(110, 24));

        int row = 0;
        panel.add(new JLabel("Condicion IVA:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(cmbCondicionIva, new GridBagConstraints(1, row, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(new JLabel("Doc:"), new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(cmbTipoDoc, new GridBagConstraints(4, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(cmbNroDoc, new GridBagConstraints(5, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        panel.add(new JLabel("Razon Social:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(cmbRazonSocial, new GridBagConstraints(1, row, 5, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        panel.add(new JLabel("Domicilio:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(txtDomicilio, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        panel.add(new JLabel("Email:"), new GridBagConstraints(4, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(txtEmail, new GridBagConstraints(5, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        panel.add(new JLabel("Cond. Venta:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));

        JPanel panelChecks = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelChecks.setBackground(TemaFacturaSoft.BG_APP);
        chkContado = new JCheckBox("Contado");
        chkTarjetaDeb = new JCheckBox("Tarj. Debito");
        chkTarjetaCred = new JCheckBox("Tarj. Credito");
        chkCC = new JCheckBox("Cta. Cte.");
        chkCheque = new JCheckBox("Cheque");
        chkTransf = new JCheckBox("Transf.");
        chkOtra = new JCheckBox("Otra");
        for (JCheckBox c : new JCheckBox[]{chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra}) {
            c.setBackground(TemaFacturaSoft.BG_APP);
            c.setFont(TemaFacturaSoft.FONT_UI_BOLD);
            c.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        }
        panelChecks.add(chkContado);
        panelChecks.add(chkTarjetaDeb);
        panelChecks.add(chkTarjetaCred);
        panelChecks.add(chkCC);
        panelChecks.add(chkCheque);
        panelChecks.add(chkTransf);
        panelChecks.add(chkOtra);
        panel.add(panelChecks, new GridBagConstraints(1, row, 5, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));

        row++;
        JLabel lblCompAsoc = new JLabel("Comp. Asoc.:");
        panel.add(lblCompAsoc, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        panel.add(txtComprobanteAsoc, new GridBagConstraints(1, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        btnImportarRemito = new JButton("Importar ReparSoft");
        btnImportarRemito.setFont(TemaFacturaSoft.FONT_UI_BOLD);
        panel.add(btnImportarRemito, new GridBagConstraints(2, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));

        return panel;
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
        btn.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btn.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btn.setBackground(TemaFacturaSoft.BG_SURFACE);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
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
    public JButton getBtnImportarRemito() { return btnImportarRemito; }
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
