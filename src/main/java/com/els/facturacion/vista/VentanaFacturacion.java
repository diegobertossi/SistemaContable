package com.els.facturacion.vista;

import com.els.facturacion.util.AutoCompleteComboBox;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

public class VentanaFacturacion extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
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
    private JLabel lblEstadoPago;
    private JComboBox<String> cmbAlicuotaIva;
    private JButton btnEliminarItem;
    private JButton btnAgregarItem;
    private JButton btnCalcular;
    private JButton btnEmitir;
    private JButton btnLimpiar;

    // Navigation
    private JCheckBox chkModoPrueba;
    private JButton btnSiguiente;

    // Menu items (needed for controller to wire actions)
    private JMenuItem itemSalir;
    private JMenuItem itemConfig;
    private JMenuItem itemHistorial;
    private JMenuItem itemCaja;
    private JMenuItem itemGastos;
    private JMenuItem itemMigrar;
    private JMenuItem itemClientes;
    private JMenuItem itemRemitos;
    private JMenuItem itemRecibos;
    private JMenuItem itemPagos;

    public VentanaFacturacion() {
        initComponents();
    }

    private void initComponents() {
        setTitle("FacturaSoft v1.0 \u2014 Sistema de Facturaci\u00f3n Electr\u00f3nica");
        setSize(1024, 720);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        // ===================== MENU BAR =====================
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(240, 240, 245));

        JMenu menuArchivo = new JMenu("Archivo");
        itemSalir = new JMenuItem("Salir");
        menuArchivo.add(itemSalir);

        JMenu menuClientes = new JMenu("Clientes");
        itemClientes = new JMenuItem("Gestion de Clientes");
        menuClientes.add(itemClientes);

        JMenu menuRemitos = new JMenu("Remitos");
        itemRemitos = new JMenuItem("Gestion de Remitos");
        menuRemitos.add(itemRemitos);

        JMenu menuRecibos = new JMenu("Recibos");
        itemRecibos = new JMenuItem("Gestion de Recibos");
        menuRecibos.add(itemRecibos);

        JMenu menuPagos = new JMenu("Pagos");
        itemPagos = new JMenuItem("Pagos / Cobranzas");
        menuPagos.add(itemPagos);

        JMenu menuHerramientas = new JMenu("Herramientas");
        itemConfig = new JMenuItem("Configurar Certificados");
        itemHistorial = new JMenuItem("Ver Comprobantes");
        itemCaja = new JMenuItem("Caja");
        itemGastos = new JMenuItem("Gastos");
        itemMigrar = new JMenuItem("Migrar desde Excel");
        menuHerramientas.add(itemConfig);
        menuHerramientas.add(itemHistorial);
        menuHerramientas.addSeparator();
        menuHerramientas.add(itemCaja);
        menuHerramientas.add(itemGastos);
        menuHerramientas.addSeparator();
        menuHerramientas.add(itemMigrar);

        menuBar.add(menuArchivo);
        menuBar.add(menuClientes);
        menuBar.add(menuRemitos);
        menuBar.add(menuRecibos);
        menuBar.add(menuPagos);
        menuBar.add(menuHerramientas);
        setJMenuBar(menuBar);

        // ===================== CARD LAYOUT =====================
        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(COLOR_FONDO);

        // ===================== DATOS CARD =====================
        JPanel datosWrapper = new JPanel(new GridBagLayout());
        datosWrapper.setBackground(COLOR_FONDO);

        JPanel centerCol = new JPanel(new GridBagLayout());
        centerCol.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 255), 2), new EmptyBorder(10, 10, 10, 10)));
        centerCol.setBackground(COLOR_FONDO);

        centerCol.add(crearSeccionPuntoVenta(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
        centerCol.add(crearSeccionEmision(), new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));
        centerCol.add(crearSeccionReceptor(), new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 8, 0), 0, 0));

        chkModoPrueba = new JCheckBox("MODO PRUEBA");
        chkModoPrueba.setFont(new Font("Cambria", Font.BOLD, 12));
        chkModoPrueba.setForeground(new Color(200, 0, 0));
        chkModoPrueba.setBackground(COLOR_FONDO);

        btnSiguiente = new JButton("SIGUIENTE >>");
        estilizarBoton(btnSiguiente);
        JPanel panelNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 3));
        panelNav.setBackground(COLOR_FONDO);
        panelNav.add(chkModoPrueba);
        panelNav.add(btnSiguiente);
        centerCol.add(panelNav, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));

        datosWrapper.add(centerCol, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        JScrollPane scroll = new JScrollPane(datosWrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel datosCard = new JPanel(new BorderLayout());
        datosCard.setBackground(COLOR_FONDO);
        datosCard.add(scroll);

        // ===================== OPERACION CARD =====================
        JPanel panelOperacion = new JPanel(new BorderLayout(5, 5));
        panelOperacion.setBackground(COLOR_FONDO);
        panelOperacion.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_FONDO);
        btnAnterior = new JButton("<< ANTERIOR");
        estilizarBoton(btnAnterior);
        JLabel lblTitulo = new JLabel("DATOS DE LA OPERACION", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_TEXTO);
        panelSuperior.add(btnAnterior, BorderLayout.WEST);
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        String[] columnas = {"Sel.", "Codigo", "Producto/Servicio", "Cantidad", "U. Medida", "P. Unitario", "Subtotal", "IVA %"};
        modeloTablaItems = new DefaultTableModel(columnas, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 6;
            }
        };
        tablaItems = new JTable(modeloTablaItems);
        tablaItems.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaItems.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaItems.getTableHeader().setBackground(COLOR_BOTON);
        tablaItems.setRowHeight(22);
        tablaItems.setShowGrid(true);
        tablaItems.setGridColor(new Color(200, 210, 230));
        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(200);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(50);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(6).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(7).setPreferredWidth(50);

        JScrollPane scrollTabla = new JScrollPane(tablaItems);

        btnAgregarItem = new JButton("+ AGREGAR ITEM");
        estilizarBoton(btnAgregarItem);
        btnEliminarItem = new JButton("- ELIMINAR ITEM");
        estilizarBoton(btnEliminarItem);
        cmbAlicuotaIva = new JComboBox<>(new String[]{"21%", "10.5%", "0%", "27%"});
        cmbAlicuotaIva.setPreferredSize(new Dimension(80, 24));
        cmbAlicuotaIva.setPrototypeDisplayValue("21%");

        JPanel panelSur = new JPanel(new BorderLayout(5, 3));
        panelSur.setBackground(COLOR_FONDO);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.add(new JLabel("IVA:"));
        panelBotones.add(cmbAlicuotaIva);
        panelBotones.add(btnAgregarItem);
        panelBotones.add(btnEliminarItem);

        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
        panelTotales.setBackground(COLOR_FONDO);

        lblTotal = new JLabel("$ 0.00");
        lblTotal.setFont(new Font("Cambria", Font.BOLD, 16));
        lblTotal.setForeground(new Color(0, 100, 0));

        txtImporteNeto = new JTextField(10);
        txtImporteNeto.setEditable(false);
        txtImporteNeto.setFont(FUENTE_BOTON);
        txtImporteNeto.setPreferredSize(new Dimension(90, 24));
        txtImporteNeto.setHorizontalAlignment(JTextField.RIGHT);
        txtImporteIva = new JTextField(10);
        txtImporteIva.setEditable(false);
        txtImporteIva.setFont(FUENTE_BOTON);
        txtImporteIva.setPreferredSize(new Dimension(90, 24));
        txtImporteIva.setHorizontalAlignment(JTextField.RIGHT);
        txtOtrosImpuestos = new JTextField(8);
        txtOtrosImpuestos.setText("0,00");
        txtOtrosImpuestos.setPreferredSize(new Dimension(80, 24));
        txtOtrosImpuestos.setHorizontalAlignment(JTextField.RIGHT);
        txtOtrosImpuestos.setFont(FUENTE_BOTON);
        txtImporteTotal = new JTextField(10);
        txtImporteTotal.setEditable(false);
        txtImporteTotal.setFont(FUENTE_BOTON);
        txtImporteTotal.setPreferredSize(new Dimension(100, 24));
        txtImporteTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtImporteTotal.setForeground(new Color(0, 100, 0));

        panelTotales.add(new JLabel("Neto:"));
        panelTotales.add(txtImporteNeto);
        panelTotales.add(new JLabel("IVA:"));
        panelTotales.add(txtImporteIva);
        panelTotales.add(new JLabel("O.Imp:"));
        panelTotales.add(txtOtrosImpuestos);
        panelTotales.add(new JLabel("Total:"));
        panelTotales.add(txtImporteTotal);
        btnCalcular = new JButton("CALCULAR");
        estilizarBoton(btnCalcular);
        panelTotales.add(btnCalcular);

        JPanel panelEmitir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        panelEmitir.setBackground(COLOR_FONDO);
        lblEstadoPago = new JLabel("Estado: Pendiente de pago");
        lblEstadoPago.setFont(FUENTE_BOTON);
        lblEstadoPago.setForeground(COLOR_TEXTO);

        btnEmitir = new JButton("GUARDAR / EMITIR FACTURA");
        btnEmitir.setFont(new Font("Cambria", Font.BOLD, 12));
        btnEmitir.setBackground(new Color(70, 160, 70));
        btnEmitir.setForeground(Color.WHITE);
        btnEmitir.setFocusPainted(false);
        btnEmitir.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 130, 50), 1),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        btnEmitir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar = new JButton("LIMPIAR");
        estilizarBoton(btnLimpiar);

        panelEmitir.add(lblEstadoPago);
        panelEmitir.add(btnEmitir);
        panelEmitir.add(btnLimpiar);

        panelSur.add(panelBotones, BorderLayout.WEST);
        panelSur.add(panelTotales, BorderLayout.CENTER);
        panelSur.add(panelEmitir, BorderLayout.SOUTH);

        panelOperacion.add(panelSuperior, BorderLayout.NORTH);
        panelOperacion.add(scrollTabla, BorderLayout.CENTER);
        panelOperacion.add(panelSur, BorderLayout.SOUTH);

        panelPrincipal.add(datosCard, "datos");
        panelPrincipal.add(panelOperacion, "operacion");

        // ===================== WRAPPER + STATUS BAR =====================
        JPanel panelPrincipalWrapper = new JPanel(new BorderLayout());
        panelPrincipalWrapper.setBackground(COLOR_FONDO);
        panelPrincipalWrapper.add(panelPrincipal, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(new Color(50, 50, 80));
        JLabel lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Cambria", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(200, 200, 220));
        statusBar.add(lblStatus);
        panelPrincipalWrapper.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(panelPrincipalWrapper);
    }

    // ===================== SECTION BUILDERS =====================

    private JPanel crearSeccionPuntoVenta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "PUNTO DE VENTA Y TIPO DE COMPROBANTE",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 11), COLOR_TEXTO
        ));
        panel.setBackground(COLOR_FONDO);
        Insets ins = new Insets(3, 6, 3, 6);

        panel.add(new JLabel("Punto de Venta:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, ins, 0, 0));
        cmbPuntoVenta = new JComboBox<>(new String[]{"00001"});
        cmbPuntoVenta.setFont(FUENTE_BOTON);
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
        cmbTipoComprobante.setFont(FUENTE_BOTON);
        cmbTipoComprobante.setPreferredSize(new Dimension(240, 24));
        cmbTipoComprobante.setPrototypeDisplayValue("Factura de Cr\u00e9dito Electr\u00f3nica");
        panel.add(cmbTipoComprobante, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, ins, 0, 0));
        return panel;
    }

    private JPanel crearSeccionEmision() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "DATOS DE EMISION",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 11), COLOR_TEXTO
        ));
        panel.setBackground(COLOR_FONDO);
        Insets ins = new Insets(4, 8, 4, 8);

        dateFecha = crearDateChooser();
        cmbConcepto = new JComboBox<>(new String[]{"Productos", "Servicios", "Productos y Servicios"});
        cmbConcepto.setFont(FUENTE_BOTON);
        cmbConcepto.setPreferredSize(new Dimension(180, 24));
        cmbConcepto.setPrototypeDisplayValue("Productos");

        txtActividades = new JTextField(25);
        txtActividades.setText("331290 - Reparacion y mantenimiento de maquinaria de uso especial n.c.p.");
        txtActividades.setEditable(false);
        txtActividades.setFont(FUENTE_BOTON);
        txtActividades.setPreferredSize(new Dimension(300, 24));
        txtActividades.setBackground(new Color(240, 240, 240));

        datePeriodoDesde = crearDateChooser();
        datePeriodoHasta = crearDateChooser();
        datePeriodoVto = crearDateChooser();

        int row = 0;
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
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "DATOS DEL RECEPTOR",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 11), COLOR_TEXTO
        ));
        panel.setBackground(COLOR_FONDO);
        Insets ins = new Insets(4, 8, 4, 8);

        cmbCondicionIva = new JComboBox<>(new String[]{
            "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
            "Responsable Monotributo", "Proveedor del Exterior", "Cliente del Exterior",
            "IVA Liberado - Ley 19.640", "Monotributista Social", "IVA No Alcanzado"
        });
        cmbCondicionIva.setFont(FUENTE_BOTON);
        cmbCondicionIva.setPreferredSize(new Dimension(200, 24));
        cmbCondicionIva.setPrototypeDisplayValue("IVA Responsable Inscripto");

        cmbTipoDoc = new JComboBox<>(new String[]{"CUIT", "DNI"});
        cmbTipoDoc.setFont(FUENTE_BOTON);
        cmbTipoDoc.setPreferredSize(new Dimension(80, 24));
        cmbTipoDoc.setPrototypeDisplayValue("CUIT");

        cmbNroDoc = new AutoCompleteComboBox();
        cmbNroDoc.setFont(FUENTE_BOTON);
        cmbNroDoc.setPreferredSize(new Dimension(140, 24));

        cmbRazonSocial = new AutoCompleteComboBox();
        cmbRazonSocial.setFont(FUENTE_BOTON);
        cmbRazonSocial.setPreferredSize(new Dimension(220, 24));

        txtDomicilio = new JTextField(20);
        txtDomicilio.setFont(FUENTE_BOTON);
        txtDomicilio.setPreferredSize(new Dimension(180, 24));

        txtEmail = new JTextField(18);
        txtEmail.setFont(FUENTE_BOTON);
        txtEmail.setPreferredSize(new Dimension(160, 24));

        txtComprobanteAsoc = new JTextField(12);
        txtComprobanteAsoc.setFont(FUENTE_BOTON);
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
        panelChecks.setBackground(COLOR_FONDO);
        chkContado = new JCheckBox("Contado");
        chkTarjetaDeb = new JCheckBox("Tarj. Debito");
        chkTarjetaCred = new JCheckBox("Tarj. Credito");
        chkCC = new JCheckBox("Cta. Cte.");
        chkCheque = new JCheckBox("Cheque");
        chkTransf = new JCheckBox("Transf.");
        chkOtra = new JCheckBox("Otra");
        for (JCheckBox c : new JCheckBox[]{chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra}) {
            c.setBackground(COLOR_FONDO);
            c.setFont(FUENTE_BOTON);
            c.setForeground(COLOR_TEXTO);
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
        btnImportarRemito.setFont(FUENTE_BOTON);
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
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
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
    public JLabel getLblEstadoPago() { return lblEstadoPago; }
    public JCheckBox getChkModoPrueba() { return chkModoPrueba; }
    public JButton getBtnSiguiente() { return btnSiguiente; }
    public JButton getBtnImportarRemito() { return btnImportarRemito; }
    public JButton getBtnAgregarItem() { return btnAgregarItem; }
    public JButton getBtnEliminarItem() { return btnEliminarItem; }
    public JButton getBtnCalcular() { return btnCalcular; }
    public JButton getBtnEmitir() { return btnEmitir; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JMenuItem getItemSalir() { return itemSalir; }
    public JMenuItem getItemConfig() { return itemConfig; }
    public JMenuItem getItemHistorial() { return itemHistorial; }
    public JMenuItem getItemCaja() { return itemCaja; }
    public JMenuItem getItemGastos() { return itemGastos; }
    public JMenuItem getItemMigrar() { return itemMigrar; }
    public JMenuItem getItemClientes() { return itemClientes; }
    public JMenuItem getItemRemitos() { return itemRemitos; }
    public JMenuItem getItemRecibos() { return itemRecibos; }
    public JMenuItem getItemPagos() { return itemPagos; }

    // ===================== SETTERS / MUTATORS =====================

    public void setRazonSocial(String texto) { cmbRazonSocial.setEditorText(texto); }
    public void setNroDoc(String texto) { cmbNroDoc.setEditorText(texto); }
    public void setTipoDoc(String tipo) { cmbTipoDoc.setSelectedItem(tipo); }
    public void setCmbCondicionIva(String value) { cmbCondicionIva.setSelectedItem(value); }

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
