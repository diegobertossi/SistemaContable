package com.els.facturacion.vista;

import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.dao.RemitoPreimpresoConfigDAO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.RemitoPreimpresoConfigDTO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class VentanaConfiguracion extends JFrame {

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

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;

    // ─── CUITs tab ────────────────────────────────────────────────────
    private JTable tablaCuits;
    private DefaultTableModel modeloCuits;
    private CuitDAO cuitDao;
    private JTextField txtCuit;
    private JTextField txtRazonSocial;
    private JComboBox<String> cmbCondicionIva;
    private JTextField txtPuntoVenta;
    private JTextField txtDomicilio;
    private JTextField txtIngresosBrutos;
    private JComponent dateInicioActividades;
    private JTextField txtRutaCertificado;
    private JPasswordField txtPassword;
    private JButton btnSeleccionarArchivo;
    private JPanel panelFormularioCuits;
    private JPanel panelBotonesCuits;
    private JLabel lblCertInfo;
    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBackup;

    // ─── Remitos tab ──────────────────────────────────────────────────
    private JTable tablaRemitos;
    private DefaultTableModel modeloRemitos;
    private RemitoPreimpresoConfigDAO remitoDao;
    private JTextField txtHabilitarPuntoVenta;
    private JTextField txtCAI;
    private JComponent dateVencimiento;
    private JTextField txtDel;
    private JTextField txtHasta;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminarRemito;

    // ─── Status bar ───────────────────────────────────────────────────
    private JPanel statusBar;
    private JLabel lblStatus;

    public VentanaConfiguracion() {
        setTitle("MÓDULO CONFIGURACIÓN");
        setSize(1024, 650);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        cuitDao = new CuitDAO();
        remitoDao = new RemitoPreimpresoConfigDAO();
        initComponents();
        applyTheme(currentTheme);
        cargarTablaCuits();
        cargarTablaRemitos();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabbedPane.addTab("CUITs", crearPanelCuits());
        tabbedPane.addTab("Remitos", crearPanelRemitos());

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(currentTheme.statusBarBg);
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturación Electrónica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(currentTheme.statusBarFg);
        statusBar.add(lblStatus);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PANEL CUITS
    // ═══════════════════════════════════════════════════════════════════

    private JPanel crearPanelCuits() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(currentTheme.bgBase);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("GESTION DE CERTIFICADOS Y CUITs", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        lblTitulo.setBackground(currentTheme.bgSurface);
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        String[] columnasCuits = {"ID", "CUIT", "Razon Social", "Condicion IVA", "Punto Venta", "Activo"};
        modeloCuits = new DefaultTableModel(columnasCuits, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 5 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 5) {
                    boolean marcado = (Boolean) aValue;
                    int id = (int) getValueAt(row, 0);
                    if (marcado) {
                        cuitDao.activarExclusivo(id);
                        for (int r = 0; r < getRowCount(); r++) {
                            super.setValueAt(r == row, r, 5);
                        }
                    } else {
                        int activos = cuitDao.contarActivos();
                        if (activos <= 1) {
                            super.setValueAt(Boolean.TRUE, row, 5);
                            return;
                        }
                        cuitDao.activarExclusivo(-1);
                        super.setValueAt(false, row, 5);
                    }
                    fireTableDataChanged();
                } else {
                    super.setValueAt(aValue, row, column);
                }
            }
        };

        tablaCuits = new JTable(modeloCuits);
        tablaCuits.setFont(FUENTE_TABLA);
        tablaCuits.setRowHeight(22);
        tablaCuits.setIntercellSpacing(new Dimension(3, 2));
        tablaCuits.setShowGrid(true);
        JScrollPane scrollCuits = new JScrollPane(tablaCuits);

        panelFormularioCuits = new JPanel(new GridBagLayout());
        panelFormularioCuits.setBackground(currentTheme.bgSurface);
        txtCuit = new JTextField(15);
        txtCuit.setFont(FUENTE_INPUT_BOLD);
        txtCuit.setDisabledTextColor(getDisabledFg());
        txtCuit.setCaretColor(currentTheme.textPrimary);
        txtRazonSocial = new JTextField(20);
        txtRazonSocial.setFont(FUENTE_INPUT_BOLD);
        txtRazonSocial.setDisabledTextColor(getDisabledFg());
        txtRazonSocial.setCaretColor(currentTheme.textPrimary);

        cmbCondicionIva = new JComboBox<>(new String[]{"IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final", "Responsable Monotributo"});
        cmbCondicionIva.setFont(FUENTE_INPUT_BOLD);
        cmbCondicionIva.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(getFieldBg(cmbCondicionIva.isEnabled()));
                setForeground(cmbCondicionIva.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                setFont(cmbCondicionIva.getFont());
                return this;
            }
            @Override
            public void paintComponent(Graphics g) {
                setBackground(getFieldBg(cmbCondicionIva.isEnabled()));
                setForeground(cmbCondicionIva.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        });
        installComboUI(cmbCondicionIva);

        txtPuntoVenta = new JTextField(10);
        txtPuntoVenta.setFont(FUENTE_INPUT_BOLD);
        txtPuntoVenta.setDisabledTextColor(getDisabledFg());
        txtPuntoVenta.setCaretColor(currentTheme.textPrimary);

        txtDomicilio = new JTextField(25);
        txtDomicilio.setFont(FUENTE_INPUT_BOLD);
        txtDomicilio.setDisabledTextColor(getDisabledFg());
        txtDomicilio.setCaretColor(currentTheme.textPrimary);

        txtIngresosBrutos = new JTextField(15);
        txtIngresosBrutos.setFont(FUENTE_INPUT_BOLD);
        txtIngresosBrutos.setDisabledTextColor(getDisabledFg());
        txtIngresosBrutos.setCaretColor(currentTheme.textPrimary);

        dateInicioActividades = crearDateChooser();
        themeDateField(dateInicioActividades, currentTheme);

        txtRutaCertificado = new JTextField(25);
        txtRutaCertificado.setFont(FUENTE_INPUT_BOLD);
        txtRutaCertificado.setDisabledTextColor(getDisabledFg());
        txtRutaCertificado.setCaretColor(currentTheme.textPrimary);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(FUENTE_INPUT_BOLD);
        txtPassword.setDisabledTextColor(getDisabledFg());
        txtPassword.setCaretColor(currentTheme.textPrimary);

        lblCertInfo = new JLabel(" ");
        lblCertInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        btnSeleccionarArchivo = new JButton("SELECCIONAR");
        btnSeleccionarArchivo.setFont(FUENTE_BOTON);
        btnSeleccionarArchivo.setForeground(currentTheme.textPrimary);
        btnSeleccionarArchivo.setBackground(currentTheme.btnBg);
        btnSeleccionarArchivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeleccionarArchivo.setFocusPainted(false);

        Insets insL = new Insets(4, 5, 4, 2);
        Insets insF = new Insets(4, 2, 4, 5);
        int row = 0;

        JLabel lblCuit = new JLabel("CUIT:");
        lblCuit.setFont(FUENTE_LABEL);
        lblCuit.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblCuit, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(txtCuit, new GridBagConstraints(1, row, 1, 1, 0.3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));
        JLabel lblCond = new JLabel("Condicion IVA:");
        lblCond.setFont(FUENTE_LABEL);
        lblCond.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblCond, new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(cmbCondicionIva, new GridBagConstraints(3, row, 1, 1, 0.7, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        row++;
        JLabel lblRs = new JLabel("Razon Social:");
        lblRs.setFont(FUENTE_LABEL);
        lblRs.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblRs, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(txtRazonSocial, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        row++;
        JLabel lblPv = new JLabel("Punto de Venta:");
        lblPv.setFont(FUENTE_LABEL);
        lblPv.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblPv, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(txtPuntoVenta, new GridBagConstraints(1, row, 1, 1, 0.3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));
        JLabel lblIb = new JLabel("Ingresos Brutos:");
        lblIb.setFont(FUENTE_LABEL);
        lblIb.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblIb, new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(txtIngresosBrutos, new GridBagConstraints(3, row, 1, 1, 0.7, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        row++;
        JLabel lblDom = new JLabel("Domicilio Comercial:");
        lblDom.setFont(FUENTE_LABEL);
        lblDom.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblDom, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(txtDomicilio, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        row++;
        JLabel lblFia = new JLabel("Fecha Inicio Actividades:");
        lblFia.setFont(FUENTE_LABEL);
        lblFia.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblFia, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(dateInicioActividades, new GridBagConstraints(1, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        row++;
        JLabel lblCert = new JLabel("Certificado .p12:");
        lblCert.setFont(FUENTE_LABEL);
        lblCert.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblCert, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(txtRutaCertificado, new GridBagConstraints(1, row, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));
        panelFormularioCuits.add(btnSeleccionarArchivo, new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        row++;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(FUENTE_LABEL);
        lblPass.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblPass, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(txtPassword, new GridBagConstraints(1, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        row++;
        JLabel lblInfoTit = new JLabel("Certificado:");
        lblInfoTit.setFont(FUENTE_LABEL);
        lblInfoTit.setForeground(currentTheme.textPrimary);
        panelFormularioCuits.add(lblInfoTit, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormularioCuits.add(lblCertInfo, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        panelBotonesCuits = new JPanel();
        panelBotonesCuits.setBackground(currentTheme.bgSurface);

        btnAgregar = new JButton("AGREGAR");
        btnAgregar.setFont(FUENTE_BOTON);
        btnAgregar.setForeground(currentTheme.textPrimary);
        btnAgregar.setBackground(currentTheme.btnBg);
        btnAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(this::btnAgregarAction);

        btnModificar = new JButton("MODIFICAR");
        btnModificar.setFont(FUENTE_BOTON);
        btnModificar.setForeground(currentTheme.textPrimary);
        btnModificar.setBackground(currentTheme.btnBg);
        btnModificar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnModificar.setFocusPainted(false);
        btnModificar.addActionListener(this::btnModificarAction);

        btnEliminar = new JButton("ELIMINAR");
        btnEliminar.setFont(FUENTE_BOTON);
        btnEliminar.setForeground(currentTheme.textPrimary);
        btnEliminar.setBackground(currentTheme.btnBg);
        btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(this::btnEliminarAction);

        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setFont(FUENTE_BOTON);
        btnLimpiar.setForeground(currentTheme.textPrimary);
        btnLimpiar.setBackground(currentTheme.btnBg);
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(this::btnLimpiarAction);

        btnBackup = new JButton("BACKUP");
        btnBackup.setFont(FUENTE_BOTON);
        btnBackup.setForeground(currentTheme.textPrimary);
        btnBackup.setBackground(currentTheme.btnBg);
        btnBackup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBackup.setFocusPainted(false);
        btnBackup.addActionListener(e -> new VentanaBackup().setVisible(true));

        btnSeleccionarArchivo.addActionListener(this::btnSeleccionarAction);
        tablaCuits.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionadaCuits();
            }
        });

        panelBotonesCuits.add(btnAgregar);
        panelBotonesCuits.add(btnModificar);
        panelBotonesCuits.add(btnEliminar);
        panelBotonesCuits.add(btnLimpiar);
        panelBotonesCuits.add(btnBackup);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(currentTheme.bgSurface);
        panelSur.add(panelFormularioCuits, BorderLayout.NORTH);
        panelSur.add(panelBotonesCuits, BorderLayout.SOUTH);

        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(currentTheme.bgBase);
        southWrapper.add(panelSur, BorderLayout.CENTER);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollCuits, BorderLayout.CENTER);
        panel.add(southWrapper, BorderLayout.SOUTH);

        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════
    //  PANEL REMITOS
    // ═══════════════════════════════════════════════════════════════════

    private JPanel crearPanelRemitos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(currentTheme.bgBase);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(currentTheme.bgBase);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        // ─── Left panel: Form fields ────────────────────────────────
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(currentTheme.bgSurface);
        TitledBorder leftBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.borderLight),
                "CONFIGURACIÓN REMITO PREIMPRESO",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                currentTheme.brand);
        leftPanel.setBorder(leftBorder);

        txtHabilitarPuntoVenta = new JTextField(10);
        txtHabilitarPuntoVenta.setFont(FUENTE_INPUT_BOLD);
        txtHabilitarPuntoVenta.setDisabledTextColor(getDisabledFg());
        txtHabilitarPuntoVenta.setCaretColor(currentTheme.textPrimary);

        txtCAI = new JTextField(15);
        txtCAI.setFont(FUENTE_INPUT_BOLD);
        txtCAI.setDisabledTextColor(getDisabledFg());
        txtCAI.setCaretColor(currentTheme.textPrimary);

        dateVencimiento = crearDateChooser();
        themeDateField(dateVencimiento, currentTheme);

        txtDel = new JTextField(8);
        txtDel.setFont(FUENTE_INPUT_BOLD);
        txtDel.setDisabledTextColor(getDisabledFg());
        txtDel.setCaretColor(currentTheme.textPrimary);

        txtHasta = new JTextField(8);
        txtHasta.setFont(FUENTE_INPUT_BOLD);
        txtHasta.setDisabledTextColor(getDisabledFg());
        txtHasta.setCaretColor(currentTheme.textPrimary);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 5, 4, 5);

        int colLabel = 0, colField = 1;
        g.gridx = colLabel;
        g.gridy = 0;
        g.gridwidth = 1;
        g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lblPtoVta = new JLabel("Habilitar Punto de Venta:");
        lblPtoVta.setFont(FUENTE_LABEL);
        lblPtoVta.setForeground(currentTheme.textPrimary);
        leftPanel.add(lblPtoVta, cloneGbc(g));

        g.gridx = colField;
        g.weightx = 1;
        g.anchor = GridBagConstraints.EAST;
        txtHabilitarPuntoVenta.setHorizontalAlignment(SwingConstants.RIGHT);
        leftPanel.add(txtHabilitarPuntoVenta, cloneGbc(g));

        g.gridy = 1;
        g.gridx = colLabel;
        g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lblCAI = new JLabel("CAI:");
        lblCAI.setFont(FUENTE_LABEL);
        lblCAI.setForeground(currentTheme.textPrimary);
        leftPanel.add(lblCAI, cloneGbc(g));

        g.gridx = colField;
        g.weightx = 1;
        g.anchor = GridBagConstraints.EAST;
        txtCAI.setHorizontalAlignment(SwingConstants.RIGHT);
        leftPanel.add(txtCAI, cloneGbc(g));

        g.gridy = 2;
        g.gridx = colLabel;
        g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lblVto = new JLabel("Fecha Vencimiento:");
        lblVto.setFont(FUENTE_LABEL);
        lblVto.setForeground(currentTheme.textPrimary);
        leftPanel.add(lblVto, cloneGbc(g));

        g.gridx = colField;
        g.weightx = 1;
        g.anchor = GridBagConstraints.EAST;
        leftPanel.add(dateVencimiento, cloneGbc(g));

        g.gridy = 3;
        g.gridx = colLabel;
        g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lblDel = new JLabel("Del:");
        lblDel.setFont(FUENTE_LABEL);
        lblDel.setForeground(currentTheme.textPrimary);
        leftPanel.add(lblDel, cloneGbc(g));

        g.gridx = colField;
        g.weightx = 1;
        g.anchor = GridBagConstraints.EAST;
        txtDel.setHorizontalAlignment(SwingConstants.RIGHT);
        leftPanel.add(txtDel, cloneGbc(g));

        g.gridy = 4;
        g.gridx = colLabel;
        g.weightx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lblHasta = new JLabel("Hasta:");
        lblHasta.setFont(FUENTE_LABEL);
        lblHasta.setForeground(currentTheme.textPrimary);
        leftPanel.add(lblHasta, cloneGbc(g));

        g.gridx = colField;
        g.weightx = 1;
        g.anchor = GridBagConstraints.EAST;
        txtHasta.setHorizontalAlignment(SwingConstants.RIGHT);
        leftPanel.add(txtHasta, cloneGbc(g));

        // filler row
        g.gridy = 5;
        g.gridx = 0;
        g.gridwidth = 2;
        g.weightx = 1;
        g.weighty = 1;
        g.fill = GridBagConstraints.BOTH;
        leftPanel.add(new JPanel(), cloneGbc(g));

        // ─── Vertical separator ─────────────────────────────────────
        JSeparator vSep = new JSeparator(JSeparator.VERTICAL);
        vSep.setPreferredSize(new Dimension(2, 1));
        vSep.setForeground(currentTheme.borderLight);

        // ─── Right panel: Table ─────────────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(currentTheme.bgSurface);
        TitledBorder rightBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.borderLight),
                "REGISTROS GUARDADOS",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                currentTheme.brand);
        rightPanel.setBorder(rightBorder);

        String[] columnasRemitos = {"ID", "PUNTO DE VENTA", "CAI", "VENCIMIENTO", "DESDE", "HASTA"};
        modeloRemitos = new DefaultTableModel(columnasRemitos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRemitos = new JTable(modeloRemitos);
        tablaRemitos.setFont(FUENTE_TABLA);
        tablaRemitos.setRowHeight(22);
        tablaRemitos.setIntercellSpacing(new Dimension(3, 2));
        tablaRemitos.setShowGrid(true);
        JScrollPane scrollRemitos = new JScrollPane(tablaRemitos);
        rightPanel.add(scrollRemitos, BorderLayout.CENTER);

        tablaRemitos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionadaRemitos();
            }
        });

        // ─── Layout left/right ──────────────────────────────────────
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        contentPanel.add(leftPanel, cloneGbc(gbc));

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        contentPanel.add(vSep, cloneGbc(gbc));

        gbc.gridx = 2;
        gbc.weightx = 0.65;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(rightPanel, cloneGbc(gbc));

        panel.add(contentPanel, BorderLayout.CENTER);

        // ─── Buttons panel ──────────────────────────────────────────
        JPanel panelBotonesRemitos = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panelBotonesRemitos.setBackground(currentTheme.bgSurface);

        btnGuardar = new JButton("GUARDAR");
        btnGuardar.setFont(FUENTE_BOTON);
        btnGuardar.setForeground(currentTheme.textPrimary);
        btnGuardar.setBackground(currentTheme.btnBg);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(this::btnGuardarAction);

        btnEditar = new JButton("EDITAR");
        btnEditar.setFont(FUENTE_BOTON);
        btnEditar.setForeground(currentTheme.textPrimary);
        btnEditar.setBackground(currentTheme.btnBg);
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditar.setFocusPainted(false);
        btnEditar.addActionListener(this::btnEditarAction);

        btnEliminarRemito = new JButton("ELIMINAR");
        btnEliminarRemito.setFont(FUENTE_BOTON);
        btnEliminarRemito.setForeground(currentTheme.textPrimary);
        btnEliminarRemito.setBackground(currentTheme.btnBg);
        btnEliminarRemito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminarRemito.setFocusPainted(false);
        btnEliminarRemito.addActionListener(this::btnEliminarRemitoAction);

        panelBotonesRemitos.add(btnGuardar);
        panelBotonesRemitos.add(btnEditar);
        panelBotonesRemitos.add(btnEliminarRemito);
        panel.add(panelBotonesRemitos, BorderLayout.SOUTH);

        return panel;
    }

    private GridBagConstraints cloneGbc(GridBagConstraints g) {
        return (GridBagConstraints) g.clone();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  CUIT CRUD
    // ═══════════════════════════════════════════════════════════════════

    private void cargarTablaCuits() {
        modeloCuits.setRowCount(0);
        List<CuitConfigDTO> lista = cuitDao.listarTodos();
        for (CuitConfigDTO dto : lista) {
            modeloCuits.addRow(new Object[]{
                dto.getId(), dto.getCuit(), dto.getRazonSocial(),
                dto.getCondicionIva(), dto.getPuntoVenta(), dto.getActivo()
            });
        }
    }

    private void cargarFilaSeleccionadaCuits() {
        int selectedRow = tablaCuits.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) modeloCuits.getValueAt(selectedRow, 0);
            CuitConfigDTO dto = cuitDao.buscarPorId(id);
            if (dto != null) {
                txtCuit.setText(dto.getCuit());
                txtRazonSocial.setText(dto.getRazonSocial());
                cmbCondicionIva.setSelectedItem(dto.getCondicionIva());
                txtPuntoVenta.setText(String.valueOf(dto.getPuntoVenta()));
                txtDomicilio.setText(dto.getDomicilio());
                txtIngresosBrutos.setText(dto.getIngresosBrutos());
                setDateChooserDate(dateInicioActividades, dto.getFechaInicioActividades());
                txtRutaCertificado.setText(dto.getRutaCertificado());
                txtPassword.setText(dto.getPasswordCert());
                actualizarInfoCertificado(dto.getRutaCertificado(), dto.getPasswordCert());
            }
        }
    }

    private void actualizarInfoCertificado(String rutaP12, String password) {
        if (rutaP12 == null || rutaP12.isEmpty() || password == null || password.isEmpty()) {
            lblCertInfo.setText(" ");
            lblCertInfo.setForeground(currentTheme.textPrimary);
            return;
        }
        try (FileInputStream fis = new FileInputStream(rutaP12)) {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(fis, password.toCharArray());
            String alias = ks.aliases().nextElement();
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
            LocalDate notBefore = cert.getNotBefore().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate notAfter = cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = LocalDate.now();
            long daysUntilExp = ChronoUnit.DAYS.between(now, notAfter);

            String estado;
            Color color;
            if (now.isAfter(notAfter)) {
                estado = "VENCIDO";
                color = new Color(200, 40, 40);
            } else if (daysUntilExp <= 30) {
                estado = "PRÓXIMO A VENCER (" + daysUntilExp + " días)";
                color = new Color(200, 150, 30);
            } else {
                estado = "VIGENTE";
                color = new Color(30, 150, 30);
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String info = String.format("%s — Válido: %s → %s — %s",
                cert.getSubjectX500Principal().getName(),
                notBefore.format(fmt), notAfter.format(fmt), estado);
            lblCertInfo.setText(info);
            lblCertInfo.setForeground(color);
        } catch (Exception e) {
            lblCertInfo.setText("No se pudo leer el certificado: " + e.getMessage());
            lblCertInfo.setForeground(new Color(200, 40, 40));
        }
    }

    private void btnAgregarAction(java.awt.event.ActionEvent e) {
        if (!validarCamposCuits()) return;

        boolean hayActivos = cuitDao.contarActivos() > 0;
        CuitConfigDTO dto = new CuitConfigDTO(
            txtCuit.getText().trim(),
            txtRazonSocial.getText().trim(),
            (String) cmbCondicionIva.getSelectedItem(),
            Integer.parseInt(txtPuntoVenta.getText().trim()),
            txtRutaCertificado.getText().trim(),
            new String(txtPassword.getPassword()),
            txtDomicilio.getText().trim(),
            txtIngresosBrutos.getText().trim(),
            getDateChooserText(dateInicioActividades)
        );
        dto.setActivo(!hayActivos);

        int id = cuitDao.insertar(dto);
        if (id > 0) {
            if (dto.getActivo()) {
                cuitDao.activarExclusivo(id);
            }
            JOptionPane.showMessageDialog(this, "CUIT guardado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaCuits();
            limpiarCamposCuits();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar CUIT", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnModificarAction(java.awt.event.ActionEvent e) {
        int selectedRow = tablaCuits.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCamposCuits()) return;

        int id = (int) modeloCuits.getValueAt(selectedRow, 0);
        CuitConfigDTO dto = cuitDao.buscarPorId(id);
        dto.setCuit(txtCuit.getText().trim());
        dto.setRazonSocial(txtRazonSocial.getText().trim());
        dto.setCondicionIva((String) cmbCondicionIva.getSelectedItem());
        dto.setPuntoVenta(Integer.parseInt(txtPuntoVenta.getText().trim()));
        dto.setDomicilio(txtDomicilio.getText().trim());
        dto.setIngresosBrutos(txtIngresosBrutos.getText().trim());
        dto.setFechaInicioActividades(getDateChooserText(dateInicioActividades));
        dto.setRutaCertificado(txtRutaCertificado.getText().trim());
        dto.setPasswordCert(new String(txtPassword.getPassword()));

        if (cuitDao.actualizar(dto)) {
            JOptionPane.showMessageDialog(this, "CUIT modificado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaCuits();
            limpiarCamposCuits();
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar CUIT", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEliminarAction(java.awt.event.ActionEvent e) {
        int selectedRow = tablaCuits.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Esta seguro de eliminar este registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) modeloCuits.getValueAt(selectedRow, 0);
            if (cuitDao.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "CUIT eliminado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaCuits();
                limpiarCamposCuits();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar CUIT", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnLimpiarAction(java.awt.event.ActionEvent e) {
        limpiarCamposCuits();
    }

    private void btnSeleccionarAction(java.awt.event.ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtRutaCertificado.setText(fileChooser.getSelectedFile().getAbsolutePath());
            actualizarInfoCertificado(txtRutaCertificado.getText(), new String(txtPassword.getPassword()));
        }
    }

    private boolean validarCamposCuits() {
        if (txtCuit.getText().trim().isEmpty() || txtCuit.getText().trim().length() != 11) {
            JOptionPane.showMessageDialog(this, "CUIT debe tener 11 digitos", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese razon social", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtPuntoVenta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese punto de venta", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtRutaCertificado.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione certificado .p12", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiarCamposCuits() {
        txtCuit.setText("");
        txtRazonSocial.setText("");
        cmbCondicionIva.setSelectedIndex(0);
        txtPuntoVenta.setText("");
        txtDomicilio.setText("");
        txtIngresosBrutos.setText("");
        setDateChooserDate(dateInicioActividades, null);
        txtRutaCertificado.setText("");
        txtPassword.setText("");
        lblCertInfo.setText(" ");
        lblCertInfo.setForeground(currentTheme.textPrimary);
        tablaCuits.clearSelection();
    }

    // ═══════════════════════════════════════════════════════════════════
    //  REMITOS CRUD
    // ═══════════════════════════════════════════════════════════════════

    private void cargarTablaRemitos() {
        modeloRemitos.setRowCount(0);
        List<RemitoPreimpresoConfigDTO> lista = remitoDao.listarTodos();
        for (RemitoPreimpresoConfigDTO dto : lista) {
            String vto = dto.getFechaVencimiento();
            if (vto != null && vto.length() == 10) {
                try {
                    vto = LocalDate.parse(vto, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception ex) { }
            }
            modeloRemitos.addRow(new Object[]{
                dto.getId(),
                dto.getPuntoVenta(),
                dto.getCai(),
                vto,
                dto.getDesde(),
                dto.getHasta()
            });
        }
    }

    private void cargarFilaSeleccionadaRemitos() {
        int selectedRow = tablaRemitos.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) modeloRemitos.getValueAt(selectedRow, 0);
            RemitoPreimpresoConfigDTO dto = remitoDao.buscarPorId(id);
            if (dto != null) {
                txtHabilitarPuntoVenta.setText(String.valueOf(dto.getPuntoVenta()));
                txtCAI.setText(String.valueOf(dto.getCai()));
                setDateChooserDate(dateVencimiento, dto.getFechaVencimiento());
                txtDel.setText(String.valueOf(dto.getDesde()));
                txtHasta.setText(String.valueOf(dto.getHasta()));
            }
        }
    }

    private void limpiarCamposRemitos() {
        txtHabilitarPuntoVenta.setText("");
        txtCAI.setText("");
        setDateChooserDate(dateVencimiento, null);
        txtDel.setText("");
        txtHasta.setText("");
        tablaRemitos.clearSelection();
    }

    private boolean validarCamposRemitos() {
        if (txtHabilitarPuntoVenta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese Punto de Venta", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtCAI.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese CAI", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtDel.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese Desde", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtHasta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese Hasta", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtHabilitarPuntoVenta.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Punto de Venta debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Long.parseLong(txtCAI.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "CAI debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtDel.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Del debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtHasta.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Hasta debe ser numérico", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void btnGuardarAction(java.awt.event.ActionEvent e) {
        if (!validarCamposRemitos()) return;

        int ptoVta = Integer.parseInt(txtHabilitarPuntoVenta.getText().trim());
        long cai = Long.parseLong(txtCAI.getText().trim());
        String vto = getDateChooserText(dateVencimiento);
        int desde = Integer.parseInt(txtDel.getText().trim());
        int hasta = Integer.parseInt(txtHasta.getText().trim());

        // Check if punto_venta already exists
        RemitoPreimpresoConfigDTO existente = remitoDao.buscarPorPuntoVenta(ptoVta);
        if (existente != null) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Ya existe un registro para el Punto de Venta " + ptoVta
                    + ".\nDesea actualizarlo?",
                    "Registro existente", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                existente.setCai(cai);
                existente.setFechaVencimiento(vto);
                existente.setDesde(desde);
                existente.setHasta(hasta);
                if (remitoDao.actualizar(existente)) {
                    JOptionPane.showMessageDialog(this, "Registro actualizado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                    cargarTablaRemitos();
                    limpiarCamposRemitos();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar registro", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            return;
        }

        RemitoPreimpresoConfigDTO dto = new RemitoPreimpresoConfigDTO(ptoVta, cai, vto, desde, hasta);
        int id = remitoDao.insertar(dto);
        if (id > 0) {
            JOptionPane.showMessageDialog(this, "Registro guardado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaRemitos();
            limpiarCamposRemitos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar registro", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEditarAction(java.awt.event.ActionEvent e) {
        int selectedRow = tablaRemitos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCamposRemitos()) return;

        int id = (int) modeloRemitos.getValueAt(selectedRow, 0);
        int ptoVta = Integer.parseInt(txtHabilitarPuntoVenta.getText().trim());
        long cai = Long.parseLong(txtCAI.getText().trim());
        String vto = getDateChooserText(dateVencimiento);
        int desde = Integer.parseInt(txtDel.getText().trim());
        int hasta = Integer.parseInt(txtHasta.getText().trim());

        RemitoPreimpresoConfigDTO dto = remitoDao.buscarPorId(id);
        if (dto == null) {
            JOptionPane.showMessageDialog(this, "Registro no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dto.setPuntoVenta(ptoVta);
        dto.setCai(cai);
        dto.setFechaVencimiento(vto);
        dto.setDesde(desde);
        dto.setHasta(hasta);

        if (remitoDao.actualizar(dto)) {
            JOptionPane.showMessageDialog(this, "Registro editado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarTablaRemitos();
            limpiarCamposRemitos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al editar registro", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEliminarRemitoAction(java.awt.event.ActionEvent e) {
        int selectedRow = tablaRemitos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Esta seguro de eliminar este registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) modeloRemitos.getValueAt(selectedRow, 0);
            if (remitoDao.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Registro eliminado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarTablaRemitos();
                limpiarCamposRemitos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar registro", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    //  DATE CHOOSER HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private JComponent crearDateChooser() {
        try {
            Class<?> clazz = Class.forName("com.toedter.calendar.JDateChooser");
            JComponent chooser = (JComponent) clazz.getDeclaredConstructor().newInstance();
            clazz.getMethod("setDateFormatString", String.class).invoke(chooser, "dd/MM/yyyy");
            clazz.getMethod("setDate", java.util.Date.class).invoke(chooser,
                java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            chooser.setPreferredSize(new Dimension(140, 24));
            chooser.addPropertyChangeListener("date", e -> themeDateField(chooser, currentTheme));
            installDateFocusListener(chooser);
            return chooser;
        } catch (Exception e) {
            JTextField tf = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            tf.setPreferredSize(new Dimension(140, 24));
            tf.setEditable(false);
            return tf;
        }
    }

    private void themeDateField(JComponent comp, Theme t) {
        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setBackground(getFieldBg(true));
            tf.setForeground(t.textPrimary);
            tf.setDisabledTextColor(getDisabledFg());
            tf.setCaretColor(t.textPrimary);
            tf.setFont(FUENTE_INPUT_BOLD);
        } else {
            for (java.awt.Component c : comp.getComponents()) {
                if (c instanceof JTextField) {
                    JTextField tf = (JTextField) c;
                    tf.setBackground(getFieldBg(true));
                    tf.setForeground(t.textPrimary);
                    tf.setDisabledTextColor(getDisabledFg());
                    tf.setCaretColor(t.textPrimary);
                    tf.setFont(FUENTE_INPUT_BOLD);
                }
                if (c instanceof java.awt.Container) {
                    themeDateField((JComponent) c, t);
                }
            }
        }
    }

    private void installDateFocusListener(JComponent chooser) {
        for (java.awt.Component c : chooser.getComponents()) {
            if (c instanceof JTextField) {
                JTextField tf = (JTextField) c;
                tf.addFocusListener(new java.awt.event.FocusAdapter() {
                    @Override
                    public void focusLost(java.awt.event.FocusEvent e) {
                        tf.setForeground(currentTheme.textPrimary);
                    }
                });
                return;
            }
            if (c instanceof java.awt.Container) {
                installDateFocusListener((JComponent) c);
            }
        }
    }

    private void setDateChooserDate(JComponent comp, String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                if (!(comp instanceof JTextField)) {
                    comp.getClass().getMethod("setDate", java.util.Date.class).invoke(comp, (java.util.Date) null);
                } else {
                    ((JTextField) comp).setText("");
                }
                return;
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate ld = LocalDate.parse(dateStr, fmt);
            java.util.Date date = java.util.Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
            if (!(comp instanceof JTextField)) {
                comp.getClass().getMethod("setDate", java.util.Date.class).invoke(comp, date);
            } else {
                ((JTextField) comp).setText(ld.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        } catch (Exception e) {
        }
    }

    private String getDateChooserText(JComponent comp) {
        try {
            if (!(comp instanceof JTextField)) {
                java.util.Date date = (java.util.Date) comp.getClass().getMethod("getDate").invoke(comp);
                if (date != null) {
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            } else {
                String text = ((JTextField) comp).getText();
                if (text != null && !text.isEmpty()) {
                    return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    // ═══════════════════════════════════════════════════════════════════
    //  THEME / HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        getContentPane().setBackground(t.bgBase);

        if (tabbedPane != null) {
            tabbedPane.setBackground(t.bgBase);
            tabbedPane.setForeground(t.textPrimary);
        }

        if (statusBar != null) statusBar.setBackground(t.statusBarBg);
        if (lblStatus != null) lblStatus.setForeground(t.statusBarFg);

        // ─── CUITs fields ────────────────────────────────────────────
        applyThemeCuits(t);

        // ─── Remitos fields ──────────────────────────────────────────
        applyThemeRemitos(t);
    }

    private void applyThemeCuits(Theme t) {
        if (panelFormularioCuits != null) {
            panelFormularioCuits.setBackground(t.bgSurface);
            themeLabels(panelFormularioCuits, t);
        }
        if (panelBotonesCuits != null) panelBotonesCuits.setBackground(t.bgSurface);

        themeField(txtCuit, t);
        themeField(txtRazonSocial, t);
        themeField(txtPuntoVenta, t);
        themeField(txtDomicilio, t);
        themeField(txtIngresosBrutos, t);
        if (dateInicioActividades != null) themeDateField(dateInicioActividades, t);
        themeField(txtRutaCertificado, t);
        if (txtPassword != null) {
            txtPassword.setBackground(getFieldBg(txtPassword.isEnabled()));
            txtPassword.setForeground(t.textPrimary);
            txtPassword.setDisabledTextColor(getDisabledFg());
            txtPassword.setCaretColor(t.textPrimary);
        }
        if (cmbCondicionIva != null) {
            cmbCondicionIva.setBackground(getFieldBg(cmbCondicionIva.isEnabled()));
            cmbCondicionIva.setForeground(cmbCondicionIva.isEnabled() ? t.textPrimary : getDisabledFg());
            installComboUI(cmbCondicionIva);
        }

        themeBtn(btnAgregar, t);
        themeBtn(btnModificar, t);
        themeBtn(btnEliminar, t);
        themeBtn(btnLimpiar, t);
        themeBtn(btnBackup, t);
        themeBtn(btnSeleccionarArchivo, t);

        if (tablaCuits != null) {
            TablaRenderer.applyTo(tablaCuits, t);
            if (tablaCuits.getTableHeader() != null) {
                Theme.styleTableHeader(tablaCuits.getTableHeader(), t);
            }
        }
        if (lblCertInfo != null) {
            String txt = lblCertInfo.getText();
            if (txt == null || txt.trim().isEmpty() || txt.equals(" ")) {
                lblCertInfo.setForeground(t.textPrimary);
            } else if (txt.contains("VIGENTE")) {
                lblCertInfo.setForeground(new Color(30, 150, 30));
            } else if (txt.contains("VENCIDO") && !txt.contains("PRÓXIMO")) {
                lblCertInfo.setForeground(new Color(200, 40, 40));
            } else if (txt.contains("PRÓXIMO")) {
                lblCertInfo.setForeground(new Color(200, 150, 30));
            }
        }
    }

    private void applyThemeRemitos(Theme t) {
        themeField(txtHabilitarPuntoVenta, t);
        themeField(txtCAI, t);
        if (dateVencimiento != null) themeDateField(dateVencimiento, t);
        themeField(txtDel, t);
        themeField(txtHasta, t);

        themeBtn(btnGuardar, t);
        themeBtn(btnEditar, t);
        themeBtn(btnEliminarRemito, t);

        if (tablaRemitos != null) {
            TablaRenderer.applyTo(tablaRemitos, t);
            if (tablaRemitos.getTableHeader() != null) {
                Theme.styleTableHeader(tablaRemitos.getTableHeader(), t);
            }
        }
    }

    private void themeField(JTextField field, Theme t) {
        if (field != null) {
            field.setBackground(getFieldBg(field.isEnabled()));
            field.setForeground(t.textPrimary);
            field.setDisabledTextColor(getDisabledFg());
            field.setCaretColor(t.textPrimary);
        }
    }

    private void themeBtn(JButton btn, Theme t) {
        if (btn != null) {
            btn.setForeground(t.textPrimary);
            btn.setBackground(t.btnBg);
        }
    }

    private void themeLabels(java.awt.Container c, Theme t) {
        for (java.awt.Component comp : c.getComponents()) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(t.textPrimary);
            }
            if (comp instanceof java.awt.Container) {
                themeLabels((java.awt.Container) comp, t);
            }
        }
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
}
