package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorClientes;
import com.els.facturacion.dao.RemitoReparsoftLecturaDAO;
import com.els.facturacion.modelo.ClienteDTO;
import com.els.facturacion.modelo.SucursalDTO;
import com.els.facturacion.util.AutoCompleteComboBox;
import com.els.facturacion.util.UbicacionSistema;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class VentanaClientes extends javax.swing.JFrame {

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
    private static final int MAX_EMAILS = 4;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MAX_PHONES = 3;

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private ControladorClientes controlador;
    private RemitoReparsoftLecturaDAO dao;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JPanel panelFiltro;
    private AutoCompleteComboBox cmbCliente;
    private JLabel lblClienteFiltro;
    private boolean cargandoDatos = false;
    private Map<Integer, String> mapSucursales;
    private JTextField txtNroDoc;
    private JTextField txtRazonSocial;
    private JTextField txtDomicilio;
    private JLabel lblRazonSocial;
    private JComboBox<String> cmbTipoDoc;
    private JComboBox<String> cmbCondicionIva;
    private JPanel panelSuperior;
    private JLabel lblTitulo;
    private JRadioButton rdParticular;
    private JRadioButton rdEmpresa;
    private ButtonGroup groupTipoPersona;
    private JPanel panelForm;
    private JPanel panelDerecho;

    private JPanel panelIzquierdo;
    private JPanel panelCentro;
    private JPanel panelBotonesForm;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnMostrarSucursales;
    private JButton btnVolverSucursales;
    private JPanel panelSucursalView;
    private JPanel formSuc;
    private JTable tablaSucursales;
    private DefaultTableModel modeloSucursales;
    private JTextField txtSucNombre;
    private JTextField txtSucDireccion;
    private JTextField txtSucContacto;
    private List<JTextField> sucPhoneFields = new ArrayList<>();
    private JPanel sucPhonePanel;
    private JButton btnSucAddPhone;
    private JButton btnSucRemovePhone;
    private List<JTextField> sucEmailFields = new ArrayList<>();
    private JPanel sucEmailPanel;
    private JButton btnSucAddEmail;
    private JButton btnSucRemoveEmail;
    private JPanel panelBotonesSucursal;
    private JButton btnGuardarSucursal;
    private JButton btnNuevoSucursal;
    private JButton btnEditarSucursal;
    private boolean enVistaSucursales = false;
    private boolean modoEdicionSucursal = false;
    private List<SucursalDTO> sucursalesCargadas = new ArrayList<>();
    private Integer idClienteSucursalActual = null;
    private JScrollPane scrollTabla;
    private boolean modoEdicion;
    private boolean guardando;
    private List<JTextField> emailFields = new ArrayList<>();
    private JPanel emailPanel;
    private JButton btnAddEmail;
    private JButton btnRemoveEmail;
    private List<JTextField> phoneFields = new ArrayList<>();
    private JPanel phonePanel;
    private JButton btnAddPhone;
    private JButton btnRemovePhone;
    private Border defaultBorder;
    private Border normalBorder;

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
    }

    public VentanaClientes() {
        controlador = new ControladorClientes();
        dao = new RemitoReparsoftLecturaDAO();
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
        cargarClientes();
        sincronizarConReparsoft();
    }

    private void initComponents() {
        setTitle("MÓDULO CLIENTES");
        setSize(1024, 600);
        setResizable(false);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        // ── Panel superior (solo título) ──
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        lblTitulo = new JLabel("GESTIÓN DE CLIENTES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        panelSuperior.add(lblTitulo);

        // ── Formulario (lado derecho) ──
        panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(currentTheme.bgSurface);
        panelForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "DATOS DEL CLIENTE",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), currentTheme.textPrimary),
            BorderFactory.createEmptyBorder(4, 6, 6, 6)));

        cmbTipoDoc = new JComboBox<String>(new String[]{"CUIT", "DNI"});
        installComboUI(cmbTipoDoc);
        cmbTipoDoc.setForeground(currentTheme.textPrimary);
        cmbTipoDoc.setBackground(getFieldBg(false));
        cmbTipoDoc.setFont(FUENTE_INPUT_BOLD);
        cmbTipoDoc.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(cmbTipoDoc.isEnabled()));
                setForeground(isSelected ? list.getSelectionForeground() :
                    (cmbTipoDoc.isEnabled() ? currentTheme.textPrimary : getDisabledFg()));
                return this;
            }
        });
        cmbCondicionIva = new JComboBox<String>(new String[]{
            "", "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
            "Responsable Monotributo", "Proveedor del Exterior", "Cliente del Exterior",
            "IVA Liberado - Ley 19.640", "Monotributista Social", "IVA No Alcanzado"
        });
        installComboUI(cmbCondicionIva);
        cmbCondicionIva.setForeground(currentTheme.textPrimary);
        cmbCondicionIva.setBackground(getFieldBg(false));
        cmbCondicionIva.setFont(FUENTE_INPUT_BOLD);
        cmbCondicionIva.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(cmbCondicionIva.isEnabled()));
                setForeground(isSelected ? list.getSelectionForeground() :
                    (cmbCondicionIva.isEnabled() ? currentTheme.textPrimary : getDisabledFg()));
                return this;
            }
        });

        txtNroDoc = new TextPrompt("");
        txtNroDoc.setFont(FUENTE_INPUT_BOLD);
        txtNroDoc.setDisabledTextColor(getDisabledFg());
        txtNroDoc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE
                    && c != java.awt.event.KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        txtRazonSocial = new JTextField(20);
        txtRazonSocial.setFont(FUENTE_INPUT_BOLD);
        txtRazonSocial.setDisabledTextColor(getDisabledFg());
        txtDomicilio = new TextPrompt("");
        txtDomicilio.setFont(FUENTE_INPUT_BOLD);
        txtDomicilio.setDisabledTextColor(getDisabledFg());
        buildPhonePanel();
        buildEmailPanel();

        int row = 0;

        rdParticular = new JRadioButton("Particular") {
            protected void paintComponent(java.awt.Graphics g) {
                setForeground(isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        };
        rdEmpresa = new JRadioButton("Empresa") {
            protected void paintComponent(java.awt.Graphics g) {
                setForeground(isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        };
        rdEmpresa.setSelected(true);
        groupTipoPersona = new ButtonGroup();
        groupTipoPersona.add(rdParticular);
        groupTipoPersona.add(rdEmpresa);

        rdParticular.addActionListener(e -> {
            cmbTipoDoc.setSelectedItem("DNI");
            cmbCondicionIva.setSelectedItem("Consumidor Final");
            updatePlaceholders();
        });
        rdEmpresa.addActionListener(e -> {
            cmbTipoDoc.setSelectedItem("CUIT");
            cmbCondicionIva.setSelectedIndex(0);
            updatePlaceholders();
        });

        panelForm.add(new JLabel("Tipo:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        JPanel pnlTipoRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlTipoRadios.setOpaque(false);
        pnlTipoRadios.setBackground(currentTheme.bgSurface);
        pnlTipoRadios.add(rdParticular);
        pnlTipoRadios.add(rdEmpresa);
        panelForm.add(pnlTipoRadios, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        lblRazonSocial = new JLabel("Razon Social:");
        panelForm.add(lblRazonSocial, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(txtRazonSocial, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        panelForm.add(new JLabel("Tipo Doc:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(cmbTipoDoc, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        panelForm.add(new JLabel("N\u00b0 Doc:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(txtNroDoc, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        panelForm.add(new JLabel("Condicion IVA:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(cmbCondicionIva, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        panelForm.add(new JLabel("Domicilio:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(txtDomicilio, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        panelForm.add(new JLabel("Telefono:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 3, 2), 0, 0));
        panelForm.add(phonePanel, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        panelForm.add(new JLabel("Email:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 3, 2), 0, 0));
        panelForm.add(emailPanel, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        btnMostrarSucursales = new JButton("MOSTRAR SUCURSALES");
        btnMostrarSucursales.setFont(FUENTE_BOTON);
        btnMostrarSucursales.setForeground(currentTheme.textPrimary);
        btnMostrarSucursales.setBackground(currentTheme.btnBg);
        btnMostrarSucursales.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMostrarSucursales.setFocusPainted(false);
        btnMostrarSucursales.setVisible(false);
        addButtonFeedback(btnMostrarSucursales);
        btnMostrarSucursales.addActionListener(e -> toggleSucursalesView());
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        btnWrap.setOpaque(false);
        btnWrap.add(btnMostrarSucursales);
        panelForm.add(btnWrap, new GridBagConstraints(0, row, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));

        // ── Espaciador vertical para que el form ocupe el espacio ──
        row++;
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panelForm.add(panel, new GridBagConstraints(0, row, 2, 1, 1, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // ── Tabla (lado izquierdo) ──
        String[] columnas = {"ID", "Tipo", "Razon Social", "SUCURSAL", "Tipo Doc", "Nro Doc", "Condicion IVA", "Domicilio", "Telefono", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(22);
        tabla.setIntercellSpacing(new Dimension(3, 2));
        tabla.setFont(FUENTE_TABLA);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
        int[] hiddenCols = {1, 3, 4, 6, 7, 9};
        for (int c : hiddenCols) {
            tabla.getColumnModel().getColumn(c).setMinWidth(0);
            tabla.getColumnModel().getColumn(c).setMaxWidth(0);
            tabla.getColumnModel().getColumn(c).setPreferredWidth(0);
        }
        tabla.getColumnModel().getColumn(5).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(5).setMaxWidth(120);
        tabla.getColumnModel().getColumn(8).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(8).setMaxWidth(120);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarClienteSeleccionado();
        });

        scrollTabla = new JScrollPane(tabla);

        // ── Filter panel (lado izquierdo, arriba de la tabla) ──
        panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        panelFiltro.setBackground(currentTheme.bgSurface);

        lblClienteFiltro = new JLabel("CLIENTE:");
        lblClienteFiltro.setFont(FUENTE_LABEL);
        lblClienteFiltro.setForeground(currentTheme.textPrimary);
        panelFiltro.add(lblClienteFiltro);

        cmbCliente = new AutoCompleteComboBox();
        cmbCliente.setFont(FUENTE_INPUT_BOLD);
        cmbCliente.setPreferredSize(new Dimension(160, 22));
        panelFiltro.add(cmbCliente);
        themeComboEditor(cmbCliente, currentTheme);
        addLiveFilter(cmbCliente);

        cmbCliente.addActionListener(e -> onClienteFiltroChanged());

        panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(currentTheme.bgBase.getRed() > 128 ? new Color(182, 196, 220) : new Color(18, 55, 140));
        panelIzquierdo.add(panelFiltro, BorderLayout.NORTH);
        panelIzquierdo.add(scrollTabla, BorderLayout.CENTER);

        // ── Panel contenedor derecho: form arriba, botones abajo fijos ──
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        panelDerecho = new JPanel(new BorderLayout(0, 6));
        panelDerecho.setBackground(currentTheme.bgBase.getRed() > 128 ? new Color(182, 196, 220) : new Color(18, 55, 140));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));

        panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        panelBotonesForm.setBackground(currentTheme.bgSurface);
        panelBotonesForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "ACCIONES",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), currentTheme.textPrimary),
            BorderFactory.createEmptyBorder(4, 8, 6, 8)));

        btnGuardar = new JButton("GUARDAR CLIENTE");
        btnGuardar.setFont(FUENTE_BOTON);
        btnGuardar.setForeground(currentTheme.textPrimary);
        btnGuardar.setBackground(currentTheme.btnBg);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setFocusPainted(false);
        addButtonFeedback(btnGuardar);
        btnGuardar.addActionListener(e -> {
            if (guardando) return;
            guardando = true;
            try {
                guardarCliente();
            } finally {
                guardando = false;
            }
        });
        btnNuevo = new JButton("NUEVO");
        btnNuevo.setFont(FUENTE_BOTON);
        btnNuevo.setForeground(currentTheme.textPrimary);
        btnNuevo.setBackground(currentTheme.btnBg);
        btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevo.setFocusPainted(false);
        addButtonFeedback(btnNuevo);
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnEditar = new JButton("EDITAR");
        btnEditar.setFont(FUENTE_BOTON);
        btnEditar.setForeground(currentTheme.textPrimary);
        btnEditar.setBackground(currentTheme.btnBg);
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditar.setFocusPainted(false);
        addButtonFeedback(btnEditar);
        btnEditar.addActionListener(e -> habilitarEdicion(true));
        panelBotonesForm.add(btnNuevo);
        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnEditar);

        panelDerecho.add(panelForm, BorderLayout.CENTER);
        panelDerecho.add(panelBotonesForm, BorderLayout.SOUTH);
        buildSucursalPanel();

        // ── Layout principal: 50/50 fijo con GridBagLayout ──
        panelIzquierdo.setMinimumSize(new Dimension(0, 0));
        panelIzquierdo.setPreferredSize(new Dimension(0, 0));
        panelDerecho.setMinimumSize(new Dimension(0, 0));
        panelDerecho.setPreferredSize(new Dimension(0, 0));
        panelCentro = new JPanel();
        panelCentro.setLayout(new GridBagLayout());
        panelCentro.setBackground(currentTheme.bgBase.getRed() > 128 ? new Color(182, 196, 220) : new Color(18, 55, 140));
        GridBagConstraints gbcIzq = new GridBagConstraints();
        gbcIzq.gridx = 0; gbcIzq.gridy = 0;
        gbcIzq.weightx = 0.5; gbcIzq.weighty = 1.0;
        gbcIzq.fill = GridBagConstraints.BOTH;
        gbcIzq.insets = new Insets(0, 0, 0, 3);
        panelCentro.add(panelIzquierdo, gbcIzq);
        GridBagConstraints gbcDer = new GridBagConstraints();
        gbcDer.gridx = 1; gbcDer.gridy = 0;
        gbcDer.weightx = 0.5; gbcDer.weighty = 1.0;
        gbcDer.fill = GridBagConstraints.BOTH;
        gbcDer.insets = new Insets(0, 3, 0, 0);
        panelCentro.add(panelDerecho, gbcDer);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(panelCentro, BorderLayout.CENTER);
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(currentTheme.statusBarBg);
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(currentTheme.statusBarFg);
        statusBar.add(lblStatus);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
        updatePlaceholders();
        setFormEditable(false);
    }

    // ── Email multi-field system ──

    private void buildEmailPanel() {
        emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setOpaque(false);

        JTextField first = new TextPrompt("Obligatorio");
        initEmailField(first, "");
        emailFields.add(first);
        JPanel w0 = new JPanel(new BorderLayout());
        w0.setOpaque(false);
        w0.add(first, BorderLayout.CENTER);
        w0.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        emailPanel.add(w0);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        btnRow.setOpaque(false);
        btnAddEmail = new JButton("+");
        btnAddEmail.setFont(FUENTE_BOTON);
        btnAddEmail.setFocusPainted(false);
        btnAddEmail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddEmail.setPreferredSize(new Dimension(40, 20));
        btnAddEmail.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnAddEmail);
        btnAddEmail.addActionListener(e -> addEmailField());
        btnRemoveEmail = new JButton("\u2212");
        btnRemoveEmail.setFont(FUENTE_BOTON);
        btnRemoveEmail.setFocusPainted(false);
        btnRemoveEmail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRemoveEmail.setPreferredSize(new Dimension(40, 20));
        btnRemoveEmail.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnRemoveEmail);
        btnRemoveEmail.addActionListener(e -> removeEmailField());
        btnRow.add(btnAddEmail);
        btnRow.add(btnRemoveEmail);
        emailPanel.add(btnRow);

        updateEmailButtons();
    }

    private void initEmailField(JTextField field, String text) {
        field.setText(text);
        field.setFont(FUENTE_INPUT);
        field.setForeground(currentTheme.textPrimary);
        field.setBackground(getFieldBg(modoEdicion));
        field.setDisabledTextColor(getDisabledFg());
        if (defaultBorder == null) {
            defaultBorder = field.getBorder();
            normalBorder = defaultBorder;
        }
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                validateEmailBorder(field);
            }
        });
        field.addActionListener(e -> validateEmailBorder(field));
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { clearErrorBorder(field); }
            public void removeUpdate(DocumentEvent e) { clearErrorBorder(field); }
            public void changedUpdate(DocumentEvent e) { clearErrorBorder(field); }
        });
    }

    private void addEmailField() {
        if (emailFields.size() >= MAX_EMAILS) return;
        JTextField newField = new TextPrompt("Obligatorio");
        initEmailField(newField, "");
        emailFields.add(newField);
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(newField, BorderLayout.CENTER);
        w.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        emailPanel.add(w, emailPanel.getComponentCount() - 1);
        emailPanel.revalidate();
        emailPanel.repaint();
        newField.requestFocusInWindow();
        updateEmailButtons();
    }

    private void removeEmailField() {
        if (emailFields.size() <= 1) return;
        int resp = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este email?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        JTextField last = emailFields.remove(emailFields.size() - 1);
        emailPanel.remove(last.getParent());
        emailPanel.revalidate();
        emailPanel.repaint();
        updateEmailButtons();
    }

    private String getEmailTexts() {
        StringBuilder sb = new StringBuilder();
        for (JTextField f : emailFields) {
            String t = f.getText().trim();
            if (!t.isEmpty()) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(t);
            }
        }
        return sb.toString();
    }

    private void setEmailTexts(String concatenated) {
        resetEmailFields();
        if (concatenated == null || concatenated.trim().isEmpty()) return;
        String[] parts = concatenated.split("\\s*;\\s*");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                emailFields.get(0).setText(parts[i].trim());
            } else if (i < MAX_EMAILS) {
                JTextField f = new TextPrompt("Obligatorio");
                initEmailField(f, parts[i].trim());
                emailFields.add(f);
                JPanel w = new JPanel(new BorderLayout());
                w.setOpaque(false);
                w.add(f, BorderLayout.CENTER);
                w.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
                emailPanel.add(w, emailPanel.getComponentCount() - 1);
            }
        }
        emailPanel.revalidate();
        emailPanel.repaint();
        updateEmailButtons();
    }

    private void resetEmailFields() {
        while (emailFields.size() > 1) {
            JTextField f = emailFields.remove(emailFields.size() - 1);
            emailPanel.remove(f.getParent());
        }
        if (!emailFields.isEmpty()) {
            JTextField first = emailFields.get(0);
            first.setText("");
            first.setBorder(normalBorder != null ? normalBorder : defaultBorder);
        } else {
            JTextField first = new TextPrompt("Obligatorio");
            initEmailField(first, "");
            emailFields.add(first);
            JPanel w = new JPanel(new BorderLayout());
            w.setOpaque(false);
            w.add(first, BorderLayout.CENTER);
            w.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            emailPanel.add(w, 0);
        }
        emailPanel.revalidate();
        emailPanel.repaint();
        updateEmailButtons();
    }

    private void updateEmailButtons() {
        boolean canAdd = emailFields.size() < MAX_EMAILS;
        boolean canRemove = emailFields.size() > 1;
        if (btnAddEmail != null) btnAddEmail.setEnabled(canAdd);
        if (btnRemoveEmail != null) btnRemoveEmail.setEnabled(canRemove);
    }

    static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private void validateEmailBorder(JTextField field) {
        String t = field.getText().trim();
        if (!t.isEmpty() && !isValidEmail(t)) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
            if (field.hasFocus()) {
                JOptionPane.showMessageDialog(this,
                    "El email \"" + t + "\" no tiene un formato v\u00e1lido.",
                    "Email inv\u00e1lido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void clearErrorBorder(JTextField field) {
        Border b = field.getBorder();
        Color lineColor = null;
        if (b instanceof javax.swing.border.CompoundBorder) {
            Border outside = ((javax.swing.border.CompoundBorder) b).getOutsideBorder();
            if (outside instanceof javax.swing.border.LineBorder) {
                lineColor = ((javax.swing.border.LineBorder) outside).getLineColor();
            }
        } else if (b instanceof javax.swing.border.LineBorder) {
            lineColor = ((javax.swing.border.LineBorder) b).getLineColor();
        }
        if (Color.RED.equals(lineColor)) {
            String t = field.getText().trim();
            if (t.isEmpty() || isValidEmail(t)) {
                field.setBorder(normalBorder != null ? normalBorder : defaultBorder);
            }
        }
    }

    private void applyEmailFieldTheme(JTextField field) {
        field.setForeground(currentTheme.textPrimary);
        field.setBackground(getFieldBg(field.isEnabled()));
        field.setDisabledTextColor(getDisabledFg());
    }

    // ── Phone multi-field system ──

    private void buildPhonePanel() {
        phonePanel = new JPanel();
        phonePanel.setLayout(new BoxLayout(phonePanel, BoxLayout.Y_AXIS));
        phonePanel.setOpaque(false);

        JTextField first = new TextPrompt("Obligatorio");
        initPhoneField(first, "");
        phoneFields.add(first);
        JPanel w0 = new JPanel(new BorderLayout());
        w0.setOpaque(false);
        w0.add(first, BorderLayout.CENTER);
        w0.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        phonePanel.add(w0);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        btnRow.setOpaque(false);
        btnAddPhone = new JButton("+");
        btnAddPhone.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAddPhone.setFocusPainted(false);
        btnAddPhone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAddPhone.setPreferredSize(new Dimension(40, 20));
        btnAddPhone.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnAddPhone);
        btnAddPhone.addActionListener(e -> addPhoneField());
        btnRemovePhone = new JButton("\u2212");
        btnRemovePhone.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRemovePhone.setFocusPainted(false);
        btnRemovePhone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRemovePhone.setPreferredSize(new Dimension(40, 20));
        btnRemovePhone.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnRemovePhone);
        btnRemovePhone.addActionListener(e -> removePhoneField());
        btnRow.add(btnAddPhone);
        btnRow.add(btnRemovePhone);
        phonePanel.add(btnRow);

        updatePhoneButtons();
    }

    private void initPhoneField(JTextField field, String text) {
        field.setText(text);
        field.setFont(FUENTE_INPUT);
        field.setForeground(currentTheme.textPrimary);
        field.setBackground(getFieldBg(modoEdicion));
        field.setDisabledTextColor(getDisabledFg());
        if (defaultBorder == null) {
            defaultBorder = field.getBorder();
            normalBorder = defaultBorder;
        }
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE
                    && c != java.awt.event.KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
    }

    private void addPhoneField() {
        if (phoneFields.size() >= MAX_PHONES) return;
        JTextField newField = new TextPrompt("Obligatorio");
        initPhoneField(newField, "");
        phoneFields.add(newField);
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(newField, BorderLayout.CENTER);
        w.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        phonePanel.add(w, phonePanel.getComponentCount() - 1);
        phonePanel.revalidate();
        phonePanel.repaint();
        newField.requestFocusInWindow();
        updatePhoneButtons();
    }

    private void removePhoneField() {
        if (phoneFields.size() <= 1) return;
        int resp = JOptionPane.showConfirmDialog(this,
            "\u00bfEst\u00e1 seguro de eliminar este tel\u00e9fono?",
            "Confirmar eliminaci\u00f3n", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        JTextField last = phoneFields.remove(phoneFields.size() - 1);
        phonePanel.remove(last.getParent());
        phonePanel.revalidate();
        phonePanel.repaint();
        updatePhoneButtons();
    }

    private String getPhoneTexts() {
        StringBuilder sb = new StringBuilder();
        for (JTextField f : phoneFields) {
            String t = f.getText().trim();
            if (!t.isEmpty()) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(t);
            }
        }
        return sb.toString();
    }

    private void setPhoneTexts(String concatenated) {
        resetPhoneFields();
        if (concatenated == null || concatenated.trim().isEmpty()) return;
        String[] parts = concatenated.split("\\s*;\\s*");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                phoneFields.get(0).setText(parts[i].trim());
            } else if (i < MAX_PHONES) {
                JTextField f = new TextPrompt("Obligatorio");
                initPhoneField(f, parts[i].trim());
                phoneFields.add(f);
                JPanel w = new JPanel(new BorderLayout());
                w.setOpaque(false);
                w.add(f, BorderLayout.CENTER);
                w.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
                phonePanel.add(w, phonePanel.getComponentCount() - 1);
            }
        }
        phonePanel.revalidate();
        phonePanel.repaint();
        updatePhoneButtons();
    }

    private void resetPhoneFields() {
        while (phoneFields.size() > 1) {
            JTextField f = phoneFields.remove(phoneFields.size() - 1);
            phonePanel.remove(f.getParent());
        }
        if (!phoneFields.isEmpty()) {
            JTextField first = phoneFields.get(0);
            first.setText("");
            first.setBorder(normalBorder != null ? normalBorder : defaultBorder);
        } else {
            JTextField first = new TextPrompt("Obligatorio");
            initPhoneField(first, "");
            phoneFields.add(first);
            JPanel w = new JPanel(new BorderLayout());
            w.setOpaque(false);
            w.add(first, BorderLayout.CENTER);
            w.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            phonePanel.add(w, 0);
        }
        phonePanel.revalidate();
        phonePanel.repaint();
        updatePhoneButtons();
    }

    private void updatePhoneButtons() {
        boolean canAdd = phoneFields.size() < MAX_PHONES;
        boolean canRemove = phoneFields.size() > 1;
        if (btnAddPhone != null) btnAddPhone.setEnabled(canAdd);
        if (btnRemovePhone != null) btnRemovePhone.setEnabled(canRemove);
    }

    private void applyPhoneFieldTheme(JTextField field) {
        field.setForeground(currentTheme.textPrimary);
        field.setBackground(getFieldBg(field.isEnabled()));
        field.setDisabledTextColor(getDisabledFg());
    }

    private void updatePlaceholders() {
        boolean isParticular = rdParticular != null && rdParticular.isSelected();
        if (lblRazonSocial != null) {
            lblRazonSocial.setText(isParticular ? "Nombre:" : "Razon Social:");
        }
        String nroDocPlaceholder = isParticular ? "Opcional" : "Obligatorio";
        String domicilioPlaceholder = "Opcional";
        String emailPlaceholder = isParticular ? "Opcional" : "Obligatorio";
        String phonePlaceholder = "Obligatorio";
        if (txtNroDoc instanceof TextPrompt)
            ((TextPrompt) txtNroDoc).setPlaceholder(nroDocPlaceholder);
        if (txtDomicilio instanceof TextPrompt)
            ((TextPrompt) txtDomicilio).setPlaceholder(domicilioPlaceholder);
        for (JTextField f : emailFields) {
            if (f instanceof TextPrompt)
                ((TextPrompt) f).setPlaceholder(emailPlaceholder);
        }
        for (JTextField f : phoneFields) {
            if (f instanceof TextPrompt)
                ((TextPrompt) f).setPlaceholder(phonePlaceholder);
        }
    }

    // ── Inner classes ──

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

    private static class TextPrompt extends JTextField {
        private String placeholderText;

        TextPrompt(String placeholder) {
            this.placeholderText = placeholder;
        }

        @Override
        public void setText(String t) {
            super.setText(t);
            setCaretPosition(0);
        }

        void setPlaceholder(String placeholder) {
            this.placeholderText = placeholder;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!getText().isEmpty() || isFocusOwner()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.setFont(getFont().deriveFont(Font.ITALIC));
            FontMetrics fm = g2.getFontMetrics();
            Insets ins = getInsets();
            int x = ins.left + 3;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() - 1;
            g2.drawString(placeholderText, x, y);
            g2.dispose();
        }
    }

    private void cargarClientes() {
        cargandoDatos = true;
        cargarSucursales();
        modeloTabla.setRowCount(0);
        List<String> nombresClientes = new ArrayList<>();
        List<ClienteDTO> lista = controlador.listarTodos();
        for (ClienteDTO c : lista) {
            String tipo = "empresa".equals(c.getTipoPersona()) ? "Empresa" : "Particular";
            String suc = "";
            if (c.getElsReferencia() != null && mapSucursales != null) {
                suc = mapSucursales.getOrDefault(c.getElsReferencia(), "");
            }
            c.setSucursal(suc);
            nombresClientes.add(c.getRazonSocial());
            modeloTabla.addRow(new Object[]{
                c.getId(), tipo, c.getRazonSocial(), suc,
                c.getTipoDocumento(), c.getNroDocumento(), c.getCondicionIva(),
                c.getDomicilio(), c.getTelefono(), c.getEmail()
            });
        }
        nombresClientes.add(0, "--Todos--");
        cmbCliente.setData(nombresClientes);
        cargandoDatos = false;
    }

    private void cargarSucursales() {
        if (!UbicacionSistema.isSeleccionado()) return;
        String base = UbicacionSistema.getNombreDbReparsoft();
        mapSucursales = controlador.getClienteDAO().getNombresSucursalesPorCliente(base);
    }

    private void filtrarTabla() {
        String clienteText = getComboText(cmbCliente).trim();
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = controlador.listarTodos();
        for (ClienteDTO c : lista) {
            String tipo = "empresa".equals(c.getTipoPersona()) ? "Empresa" : "Particular";
            String suc = "";
            if (c.getElsReferencia() != null && mapSucursales != null) {
                suc = mapSucursales.getOrDefault(c.getElsReferencia(), "");
            }
            c.setSucursal(suc);
            boolean matchesCliente = clienteText.isEmpty() || "--Todos--".equals(clienteText) || c.getRazonSocial().toLowerCase().contains(clienteText.toLowerCase());
            if (matchesCliente) {
                modeloTabla.addRow(new Object[]{
                    c.getId(), tipo, c.getRazonSocial(), suc,
                    c.getTipoDocumento(), c.getNroDocumento(), c.getCondicionIva(),
                    c.getDomicilio(), c.getTelefono(), c.getEmail()
                });
            }
        }
    }

    private String getComboText(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        if (sel != null) {
            String s = sel.toString();
            if (s != null && !s.trim().isEmpty()) return s.trim();
        }
        return ((JTextField) combo.getEditor().getEditorComponent()).getText();
    }

    private void onClienteFiltroChanged() {
        if (cargandoDatos) return;
        filtrarTabla();
    }

    private void themeComboEditor(JComboBox<?> combo, Theme t) {
        Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            JTextField ed = (JTextField) editorComp;
            ed.setBackground(getFieldBg(combo.isEnabled()));
            ed.setForeground(combo.isEnabled() ? t.textPrimary : getDisabledFg());
            ed.setDisabledTextColor(getDisabledFg());
            ed.setCaretColor(t.textPrimary);
        }
    }

    private void buildSucursalPanel() {
        // ── Table ──
        modeloSucursales = new DefaultTableModel(new String[]{"NOMBRE SUCURSAL"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaSucursales = new JTable(modeloSucursales);
        tablaSucursales.setFont(FUENTE_TABLA);
        tablaSucursales.setRowHeight(22);
        tablaSucursales.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSucursalSeleccionada();
        });
        JScrollPane scrollSuc = new JScrollPane(tablaSucursales);
        scrollSuc.setPreferredSize(new Dimension(100, 5 * 22 + 20));

        // ── Form fields ──
        txtSucNombre = new JTextField(20);
        txtSucNombre.setFont(FUENTE_INPUT_BOLD);
        txtSucDireccion = new JTextField(20);
        txtSucDireccion.setFont(FUENTE_INPUT_BOLD);
        txtSucContacto = new JTextField(20);
        txtSucContacto.setFont(FUENTE_INPUT_BOLD);
        buildSucursalPhonePanel();
        buildSucursalEmailPanel();

        formSuc = new JPanel(new GridBagLayout());
        formSuc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "DATOS DE LA SUCURSAL",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), currentTheme.textPrimary),
            BorderFactory.createEmptyBorder(4, 6, 6, 6)));
        int r = 0;
        formSuc.add(new JLabel("Nombre:"), new GridBagConstraints(0, r, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 5, 1, 2), 0, 0));
        formSuc.add(txtSucNombre, new GridBagConstraints(1, r, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 5), 0, 0));
        r++;
        formSuc.add(new JLabel("Dirección:"), new GridBagConstraints(0, r, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 5, 1, 2), 0, 0));
        formSuc.add(txtSucDireccion, new GridBagConstraints(1, r, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 5), 0, 0));
        r++;
        formSuc.add(new JLabel("Contacto:"), new GridBagConstraints(0, r, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 5, 1, 2), 0, 0));
        formSuc.add(txtSucContacto, new GridBagConstraints(1, r, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 5), 0, 0));
        r++;
        formSuc.add(new JLabel("Tel. Contacto:"), new GridBagConstraints(0, r, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 1, 2), 0, 0));
        formSuc.add(sucPhonePanel, new GridBagConstraints(1, r, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 5), 0, 0));
        r++;
        formSuc.add(new JLabel("Email:"), new GridBagConstraints(0, r, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 5, 1, 2), 0, 0));
        formSuc.add(sucEmailPanel, new GridBagConstraints(1, r, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 5), 0, 0));
        r++;
        JPanel spacerSuc = new JPanel();
        spacerSuc.setOpaque(false);
        formSuc.add(spacerSuc, new GridBagConstraints(0, r, 2, 1, 1, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // ── Action buttons for sucursal ──
        btnVolverSucursales = new JButton("VOLVER");
        btnVolverSucursales.setFont(FUENTE_BOTON);
        btnVolverSucursales.setForeground(currentTheme.textPrimary);
        btnVolverSucursales.setBackground(currentTheme.btnBg);
        btnVolverSucursales.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolverSucursales.setFocusPainted(false);
        addButtonFeedback(btnVolverSucursales);
        btnVolverSucursales.addActionListener(e -> toggleSucursalesView());

        btnGuardarSucursal = new JButton("GUARDAR SUCURSAL");
        btnGuardarSucursal.setFont(FUENTE_BOTON);
        btnGuardarSucursal.setForeground(currentTheme.textPrimary);
        btnGuardarSucursal.setBackground(currentTheme.btnBg);
        btnGuardarSucursal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardarSucursal.setFocusPainted(false);
        addButtonFeedback(btnGuardarSucursal);
        btnGuardarSucursal.addActionListener(e -> guardarSucursal());

        btnNuevoSucursal = new JButton("NUEVA SUCURSAL");
        btnNuevoSucursal.setFont(FUENTE_BOTON);
        btnNuevoSucursal.setForeground(currentTheme.textPrimary);
        btnNuevoSucursal.setBackground(currentTheme.btnBg);
        btnNuevoSucursal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevoSucursal.setFocusPainted(false);
        addButtonFeedback(btnNuevoSucursal);
        btnNuevoSucursal.addActionListener(e -> limpiarFormSucursal());

        btnEditarSucursal = new JButton("EDITAR");
        btnEditarSucursal.setFont(FUENTE_BOTON);
        btnEditarSucursal.setForeground(currentTheme.textPrimary);
        btnEditarSucursal.setBackground(currentTheme.btnBg);
        btnEditarSucursal.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditarSucursal.setFocusPainted(false);
        addButtonFeedback(btnEditarSucursal);
        btnEditarSucursal.addActionListener(e -> habilitarEdicionSucursal(true));

        panelBotonesSucursal = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        panelBotonesSucursal.setBackground(currentTheme.bgSurface);
        panelBotonesSucursal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "ACCIONES",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), currentTheme.textPrimary),
            BorderFactory.createEmptyBorder(4, 8, 6, 8)));
        panelBotonesSucursal.add(btnNuevoSucursal);
        panelBotonesSucursal.add(btnGuardarSucursal);
        panelBotonesSucursal.add(btnEditarSucursal);
        panelBotonesSucursal.add(btnVolverSucursales);

        panelSucursalView = new JPanel(new BorderLayout(0, 4));
        panelSucursalView.setBackground(currentTheme.bgSurface);
        scrollSuc.setPreferredSize(new Dimension(100, 5 * 22 + 20));
        panelSucursalView.add(scrollSuc, BorderLayout.NORTH);
        panelSucursalView.add(formSuc, BorderLayout.CENTER);
    }

    // ── Sucursal phone multi-field ──

    private void buildSucursalPhonePanel() {
        sucPhonePanel = new JPanel();
        sucPhonePanel.setLayout(new BoxLayout(sucPhonePanel, BoxLayout.Y_AXIS));
        sucPhonePanel.setOpaque(false);
        JTextField first = new JTextField(20);
        initSucursalPhoneField(first, "");
        sucPhoneFields.add(first);
        JPanel w0 = new JPanel(new BorderLayout());
        w0.setOpaque(false);
        w0.add(first, BorderLayout.CENTER);
        w0.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        sucPhonePanel.add(w0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        btnRow.setOpaque(false);
        btnSucAddPhone = new JButton("+");
        btnSucAddPhone.setFont(FUENTE_BOTON);
        btnSucAddPhone.setFocusPainted(false);
        btnSucAddPhone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSucAddPhone.setPreferredSize(new Dimension(40, 20));
        btnSucAddPhone.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnSucAddPhone);
        btnSucAddPhone.addActionListener(e -> addSucursalPhoneField());
        btnSucRemovePhone = new JButton("\u2212");
        btnSucRemovePhone.setFont(FUENTE_BOTON);
        btnSucRemovePhone.setFocusPainted(false);
        btnSucRemovePhone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSucRemovePhone.setPreferredSize(new Dimension(40, 20));
        btnSucRemovePhone.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnSucRemovePhone);
        btnSucRemovePhone.addActionListener(e -> removeSucursalPhoneField());
        btnRow.add(btnSucAddPhone);
        btnRow.add(btnSucRemovePhone);
        sucPhonePanel.add(btnRow);
        updateSucursalPhoneButtons();
    }

    private void initSucursalPhoneField(JTextField field, String text) {
        field.setText(text);
        field.setFont(FUENTE_INPUT);
        field.setForeground(currentTheme.textPrimary);
        field.setBackground(getFieldBg(false));
        field.setDisabledTextColor(getDisabledFg());
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE
                    && c != java.awt.event.KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
    }

    private void addSucursalPhoneField() {
        if (sucPhoneFields.size() >= MAX_PHONES) return;
        JTextField newField = new JTextField(20);
        initSucursalPhoneField(newField, "");
        sucPhoneFields.add(newField);
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(newField, BorderLayout.CENTER);
        w.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        sucPhonePanel.add(w, sucPhonePanel.getComponentCount() - 1);
        sucPhonePanel.revalidate();
        sucPhonePanel.repaint();
        newField.requestFocusInWindow();
        updateSucursalPhoneButtons();
    }

    private void removeSucursalPhoneField() {
        if (sucPhoneFields.size() <= 1) return;
        int resp = JOptionPane.showConfirmDialog(this,
            "\u00bfEst\u00e1 seguro de eliminar este tel\u00e9fono?",
            "Confirmar eliminaci\u00f3n", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        JTextField last = sucPhoneFields.remove(sucPhoneFields.size() - 1);
        sucPhonePanel.remove(last.getParent());
        sucPhonePanel.revalidate();
        sucPhonePanel.repaint();
        updateSucursalPhoneButtons();
    }

    private String getSucursalPhoneTexts() {
        StringBuilder sb = new StringBuilder();
        for (JTextField f : sucPhoneFields) {
            String t = f.getText().trim();
            if (!t.isEmpty()) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(t);
            }
        }
        return sb.toString();
    }

    private void setSucursalPhoneTexts(String concatenated) {
        resetSucursalPhoneFields();
        if (concatenated == null || concatenated.trim().isEmpty()) return;
        String[] parts = concatenated.split("\\s*;\\s*");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                sucPhoneFields.get(0).setText(parts[i].trim());
            } else if (i < MAX_PHONES) {
                JTextField f = new JTextField(20);
                initSucursalPhoneField(f, parts[i].trim());
                sucPhoneFields.add(f);
                JPanel w = new JPanel(new BorderLayout());
                w.setOpaque(false);
                w.add(f, BorderLayout.CENTER);
                w.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
                sucPhonePanel.add(w, sucPhonePanel.getComponentCount() - 1);
            }
        }
        sucPhonePanel.revalidate();
        sucPhonePanel.repaint();
        updateSucursalPhoneButtons();
    }

    private void resetSucursalPhoneFields() {
        while (sucPhoneFields.size() > 1) {
            JTextField f = sucPhoneFields.remove(sucPhoneFields.size() - 1);
            sucPhonePanel.remove(f.getParent());
        }
        if (!sucPhoneFields.isEmpty()) {
            JTextField first = sucPhoneFields.get(0);
            first.setText("");
        } else {
            JTextField first = new JTextField(20);
            initSucursalPhoneField(first, "");
            sucPhoneFields.add(first);
            JPanel w = new JPanel(new BorderLayout());
            w.setOpaque(false);
            w.add(first, BorderLayout.CENTER);
            w.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            sucPhonePanel.add(w, 0);
        }
        sucPhonePanel.revalidate();
        sucPhonePanel.repaint();
        updateSucursalPhoneButtons();
    }

    private void updateSucursalPhoneButtons() {
        boolean canAdd = sucPhoneFields.size() < MAX_PHONES;
        boolean canRemove = sucPhoneFields.size() > 1;
        if (btnSucAddPhone != null) btnSucAddPhone.setEnabled(canAdd);
        if (btnSucRemovePhone != null) btnSucRemovePhone.setEnabled(canRemove);
    }

    // ── Sucursal email multi-field ──

    private void buildSucursalEmailPanel() {
        sucEmailPanel = new JPanel();
        sucEmailPanel.setLayout(new BoxLayout(sucEmailPanel, BoxLayout.Y_AXIS));
        sucEmailPanel.setOpaque(false);
        JTextField first = new JTextField(20);
        initSucursalEmailField(first, "");
        sucEmailFields.add(first);
        JPanel w0 = new JPanel(new BorderLayout());
        w0.setOpaque(false);
        w0.add(first, BorderLayout.CENTER);
        w0.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        sucEmailPanel.add(w0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 1));
        btnRow.setOpaque(false);
        btnSucAddEmail = new JButton("+");
        btnSucAddEmail.setFont(FUENTE_BOTON);
        btnSucAddEmail.setFocusPainted(false);
        btnSucAddEmail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSucAddEmail.setPreferredSize(new Dimension(40, 20));
        btnSucAddEmail.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnSucAddEmail);
        btnSucAddEmail.addActionListener(e -> addSucursalEmailField());
        btnSucRemoveEmail = new JButton("\u2212");
        btnSucRemoveEmail.setFont(FUENTE_BOTON);
        btnSucRemoveEmail.setFocusPainted(false);
        btnSucRemoveEmail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSucRemoveEmail.setPreferredSize(new Dimension(40, 20));
        btnSucRemoveEmail.setForeground(currentTheme.textPrimary);
        addButtonFeedback(btnSucRemoveEmail);
        btnSucRemoveEmail.addActionListener(e -> removeSucursalEmailField());
        btnRow.add(btnSucAddEmail);
        btnRow.add(btnSucRemoveEmail);
        sucEmailPanel.add(btnRow);
        updateSucursalEmailButtons();
    }

    private void initSucursalEmailField(JTextField field, String text) {
        field.setText(text);
        field.setFont(FUENTE_INPUT);
        field.setForeground(currentTheme.textPrimary);
        field.setBackground(getFieldBg(false));
        field.setDisabledTextColor(getDisabledFg());
        field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                validateSucursalEmailBorder(field);
            }
        });
        field.addActionListener(e -> validateSucursalEmailBorder(field));
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { clearSucursalEmailBorder(field); }
            public void removeUpdate(DocumentEvent e) { clearSucursalEmailBorder(field); }
            public void changedUpdate(DocumentEvent e) { clearSucursalEmailBorder(field); }
        });
    }

    private void addSucursalEmailField() {
        if (sucEmailFields.size() >= MAX_EMAILS) return;
        JTextField newField = new JTextField(20);
        initSucursalEmailField(newField, "");
        sucEmailFields.add(newField);
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(newField, BorderLayout.CENTER);
        w.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        sucEmailPanel.add(w, sucEmailPanel.getComponentCount() - 1);
        sucEmailPanel.revalidate();
        sucEmailPanel.repaint();
        newField.requestFocusInWindow();
        updateSucursalEmailButtons();
    }

    private void removeSucursalEmailField() {
        if (sucEmailFields.size() <= 1) return;
        int resp = JOptionPane.showConfirmDialog(this,
            "\u00bfEst\u00e1 seguro de eliminar este email?",
            "Confirmar eliminaci\u00f3n", JOptionPane.YES_NO_OPTION);
        if (resp != JOptionPane.YES_OPTION) return;
        JTextField last = sucEmailFields.remove(sucEmailFields.size() - 1);
        sucEmailPanel.remove(last.getParent());
        sucEmailPanel.revalidate();
        sucEmailPanel.repaint();
        updateSucursalEmailButtons();
    }

    private String getSucursalEmailTexts() {
        StringBuilder sb = new StringBuilder();
        for (JTextField f : sucEmailFields) {
            String t = f.getText().trim();
            if (!t.isEmpty()) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(t);
            }
        }
        return sb.toString();
    }

    private void setSucursalEmailTexts(String concatenated) {
        resetSucursalEmailFields();
        if (concatenated == null || concatenated.trim().isEmpty()) return;
        String[] parts = concatenated.split("\\s*;\\s*");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0) {
                sucEmailFields.get(0).setText(parts[i].trim());
            } else if (i < MAX_EMAILS) {
                JTextField f = new JTextField(20);
                initSucursalEmailField(f, parts[i].trim());
                sucEmailFields.add(f);
                JPanel w = new JPanel(new BorderLayout());
                w.setOpaque(false);
                w.add(f, BorderLayout.CENTER);
                w.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
                sucEmailPanel.add(w, sucEmailPanel.getComponentCount() - 1);
            }
        }
        sucEmailPanel.revalidate();
        sucEmailPanel.repaint();
        updateSucursalEmailButtons();
    }

    private void resetSucursalEmailFields() {
        while (sucEmailFields.size() > 1) {
            JTextField f = sucEmailFields.remove(sucEmailFields.size() - 1);
            sucEmailPanel.remove(f.getParent());
        }
        if (!sucEmailFields.isEmpty()) {
            JTextField first = sucEmailFields.get(0);
            first.setText("");
        } else {
            JTextField first = new JTextField(20);
            initSucursalEmailField(first, "");
            sucEmailFields.add(first);
            JPanel w = new JPanel(new BorderLayout());
            w.setOpaque(false);
            w.add(first, BorderLayout.CENTER);
            w.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            sucEmailPanel.add(w, 0);
        }
        sucEmailPanel.revalidate();
        sucEmailPanel.repaint();
        updateSucursalEmailButtons();
    }

    private void updateSucursalEmailButtons() {
        boolean canAdd = sucEmailFields.size() < MAX_EMAILS;
        boolean canRemove = sucEmailFields.size() > 1;
        if (btnSucAddEmail != null) btnSucAddEmail.setEnabled(canAdd);
        if (btnSucRemoveEmail != null) btnSucRemoveEmail.setEnabled(canRemove);
    }

    // ── Sucursal email validation (reuse patterns from cliente) ──

    private void validateSucursalEmailBorder(JTextField field) {
        String t = field.getText().trim();
        if (!t.isEmpty() && !isValidEmail(t)) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
            if (field.hasFocus()) {
                JOptionPane.showMessageDialog(this,
                    "El email \"" + t + "\" no tiene un formato v\u00e1lido.",
                    "Email inv\u00e1lido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void clearSucursalEmailBorder(JTextField field) {
        Border b = field.getBorder();
        Color lineColor = null;
        if (b instanceof javax.swing.border.CompoundBorder) {
            Border outside = ((javax.swing.border.CompoundBorder) b).getOutsideBorder();
            if (outside instanceof javax.swing.border.LineBorder) {
                lineColor = ((javax.swing.border.LineBorder) outside).getLineColor();
            }
        } else if (b instanceof javax.swing.border.LineBorder) {
            lineColor = ((javax.swing.border.LineBorder) b).getLineColor();
        }
        if (Color.RED.equals(lineColor)) {
            String t = field.getText().trim();
            if (t.isEmpty() || isValidEmail(t)) {
                field.setBorder(normalBorder != null ? normalBorder : defaultBorder);
            }
        }
    }

    // ── Sucursal edit mode ──

    private void setFormSucursalEditable(boolean editable) {
        Color bg = getFieldBg(editable);
        txtSucNombre.setEnabled(editable);
        txtSucNombre.setBackground(bg);
        txtSucDireccion.setEnabled(editable);
        txtSucDireccion.setBackground(bg);
        txtSucContacto.setEnabled(editable);
        txtSucContacto.setBackground(bg);
        for (JTextField f : sucPhoneFields) {
            f.setEnabled(editable);
            f.setBackground(bg);
        }
        btnSucAddPhone.setEnabled(editable && sucPhoneFields.size() < MAX_PHONES);
        btnSucRemovePhone.setEnabled(editable && sucPhoneFields.size() > 1);
        for (JTextField f : sucEmailFields) {
            f.setEnabled(editable);
            f.setBackground(bg);
        }
        btnSucAddEmail.setEnabled(editable && sucEmailFields.size() < MAX_EMAILS);
        btnSucRemoveEmail.setEnabled(editable && sucEmailFields.size() > 1);
        btnGuardarSucursal.setEnabled(editable);
        btnEditarSucursal.setEnabled(!editable && tablaSucursales.getSelectedRow() >= 0);
    }

    private void habilitarEdicionSucursal(boolean editable) {
        modoEdicionSucursal = editable;
        setFormSucursalEditable(editable);
        updateSucursalPhoneButtons();
        updateSucursalEmailButtons();
    }

    private void limpiarFormSucursal() {
        tablaSucursales.clearSelection();
        txtSucNombre.setText("");
        txtSucDireccion.setText("");
        txtSucContacto.setText("");
        resetSucursalPhoneFields();
        resetSucursalEmailFields();
        modoEdicionSucursal = false;
        setFormSucursalEditable(true);
    }

    private void guardarSucursal() {
        boolean hayEmailInvalido = false;
        for (JTextField f : sucEmailFields) {
            String t = f.getText().trim();
            if (!t.isEmpty() && !isValidEmail(t)) {
                hayEmailInvalido = true;
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.RED, 1),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)));
            }
        }
        if (hayEmailInvalido) {
            JOptionPane.showMessageDialog(this,
                "Uno o m\u00e1s emails tienen formato inv\u00e1lido.\nCorr\u00edjalos o elim\u00ednelos antes de guardar.",
                "Error de validaci\u00f3n", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (idClienteSucursalActual == null) {
            JOptionPane.showMessageDialog(this, "Error: no hay cliente seleccionado.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SucursalDTO suc = new SucursalDTO();
        int row = tablaSucursales.getSelectedRow();
        if (row >= 0 && row < sucursalesCargadas.size()) {
            suc.setIdSucursal(sucursalesCargadas.get(row).getIdSucursal());
        }
        suc.setIdCliente(idClienteSucursalActual);
        suc.setNombre(txtSucNombre.getText().trim());
        suc.setDireccion(txtSucDireccion.getText().trim());
        suc.setContacto(txtSucContacto.getText().trim());
        suc.setTelefono(getSucursalPhoneTexts());
        suc.setEmail(getSucursalEmailTexts());

        int result = controlador.guardarSucursal(suc);
        if (result > 0) {
            JOptionPane.showMessageDialog(this, "Sucursal guardada correctamente",
                "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
            modoEdicionSucursal = false;
            setFormSucursalEditable(false);
            sucursalesCargadas = controlador.getSucursalesPorCliente(idClienteSucursalActual);
            modeloSucursales.setRowCount(0);
            for (SucursalDTO s : sucursalesCargadas) {
                modeloSucursales.addRow(new Object[]{s.getNombre()});
            }
            cargarSucursales();
            filtrarTabla();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la sucursal.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleSucursalesView() {
        enVistaSucursales = !enVistaSucursales;
        if (enVistaSucursales) {
            int row = tabla.getSelectedRow();
            if (row < 0) { enVistaSucursales = false; return; }
            Integer id = (Integer) modeloTabla.getValueAt(row, 0);
            ClienteDTO cli = controlador.buscarPorId(id);
            if (cli == null || cli.getElsReferencia() == null) { enVistaSucursales = false; return; }
            if (!UbicacionSistema.isSeleccionado()) { enVistaSucursales = false; return; }
            idClienteSucursalActual = cli.getElsReferencia();
            sucursalesCargadas = controlador.getSucursalesPorCliente(idClienteSucursalActual);
            modeloSucursales.setRowCount(0);
            for (SucursalDTO s : sucursalesCargadas) {
                modeloSucursales.addRow(new Object[]{s.getNombre()});
            }
            modoEdicionSucursal = false;
            panelDerecho.remove(panelForm);
            panelDerecho.remove(panelBotonesForm);
            panelDerecho.add(panelSucursalView, BorderLayout.CENTER);
            panelDerecho.add(panelBotonesSucursal, BorderLayout.SOUTH);
            setFormSucursalEditable(false);
        } else {
            panelDerecho.remove(panelSucursalView);
            panelDerecho.remove(panelBotonesSucursal);
            panelDerecho.add(panelForm, BorderLayout.CENTER);
            panelDerecho.add(panelBotonesForm, BorderLayout.SOUTH);
            setFormEditable(false);
            idClienteSucursalActual = null;
            sucursalesCargadas = new ArrayList<>();
        }
        panelDerecho.revalidate();
        panelDerecho.repaint();
    }

    private void cargarSucursalSeleccionada() {
        int row = tablaSucursales.getSelectedRow();
        if (row < 0 || row >= sucursalesCargadas.size()) {
            txtSucNombre.setText("");
            txtSucDireccion.setText("");
            txtSucContacto.setText("");
            resetSucursalPhoneFields();
            resetSucursalEmailFields();
            btnEditarSucursal.setEnabled(false);
            return;
        }
        SucursalDTO s = sucursalesCargadas.get(row);
        txtSucNombre.setText(s.getNombre() != null ? s.getNombre() : "");
        txtSucDireccion.setText(s.getDireccion() != null ? s.getDireccion() : "");
        txtSucContacto.setText(s.getContacto() != null ? s.getContacto() : "");
        setSucursalPhoneTexts(s.getTelefono());
        setSucursalEmailTexts(s.getEmail());
        txtSucNombre.setCaretPosition(0);
        btnEditarSucursal.setEnabled(!modoEdicionSucursal);
    }

    private void addLiveFilter(JComboBox<?> combo) {
        Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            ((JTextField) editorComp).getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { onFilterTextChanged(); }
                public void removeUpdate(DocumentEvent e) { onFilterTextChanged(); }
                public void changedUpdate(DocumentEvent e) { onFilterTextChanged(); }
            });
        }
    }

    private void onFilterTextChanged() {
        if (cargandoDatos) return;
        filtrarTabla();
    }

    private void cargarClienteSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            modoEdicion = false;
            setFormEditable(false);
            btnMostrarSucursales.setVisible(false);
            if (enVistaSucursales) toggleSucursalesView();
            return;
        }
        if (enVistaSucursales) toggleSucursalesView();
        String tipo = modeloTabla.getValueAt(row, 1) != null ? modeloTabla.getValueAt(row, 1).toString() : "Empresa";
        if ("Particular".equals(tipo)) {
            rdParticular.setSelected(true);
        } else {
            rdEmpresa.setSelected(true);
        }
        updatePlaceholders();
        txtRazonSocial.setText(modeloTabla.getValueAt(row, 2) != null ? modeloTabla.getValueAt(row, 2).toString() : "");
        txtRazonSocial.setCaretPosition(0);
        cmbTipoDoc.setSelectedItem(modeloTabla.getValueAt(row, 4));
        txtNroDoc.setText(modeloTabla.getValueAt(row, 5) != null ? modeloTabla.getValueAt(row, 5).toString() : "");
        cmbCondicionIva.setSelectedItem(modeloTabla.getValueAt(row, 6) != null ? modeloTabla.getValueAt(row, 6).toString() : "Consumidor Final");
        txtDomicilio.setText(modeloTabla.getValueAt(row, 7) != null ? modeloTabla.getValueAt(row, 7).toString() : "");
        setPhoneTexts(modeloTabla.getValueAt(row, 8) != null ? modeloTabla.getValueAt(row, 8).toString() : "");
        setEmailTexts(modeloTabla.getValueAt(row, 9) != null ? modeloTabla.getValueAt(row, 9).toString() : "");
        String sucStr = modeloTabla.getValueAt(row, 3) != null ? modeloTabla.getValueAt(row, 3).toString() : "";
        btnMostrarSucursales.setVisible(!sucStr.isEmpty());
        modoEdicion = false;
        setFormEditable(false);
    }

    private void guardarCliente() {
        boolean esParticular = rdParticular.isSelected();
        String razonSocial = txtRazonSocial.getText().trim();
        String nroDoc = txtNroDoc.getText().trim();
        String condicionIva = (String) cmbCondicionIva.getSelectedItem();
        String domicilio = txtDomicilio.getText().trim();

        if (esParticular) {
            if (razonSocial.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre es obligatorio para clientes Particular",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (getPhoneTexts().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tel\u00e9fono es obligatorio para clientes Particular",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            if (razonSocial.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Razon Social es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nroDoc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El CUIT es obligatorio para Empresa",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (getPhoneTexts().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tel\u00e9fono es obligatorio para Empresa",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        boolean hayEmailInvalido = false;
        for (JTextField f : emailFields) {
            String t = f.getText().trim();
            if (!t.isEmpty() && !isValidEmail(t)) {
                hayEmailInvalido = true;
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.RED, 1),
                    BorderFactory.createEmptyBorder(2, 4, 2, 4)));
            }
        }
        if (hayEmailInvalido) {
            JOptionPane.showMessageDialog(this,
                "Uno o más emails tienen formato inválido.\nCorríjalos o elimínelos antes de guardar.",
                "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ClienteDTO cli = new ClienteDTO();
        if (modoEdicion) {
            int row = tabla.getSelectedRow();
            if (row >= 0 && modeloTabla.getValueAt(row, 0) != null) {
                Integer id = (Integer) modeloTabla.getValueAt(row, 0);
                cli.setId(id);
                ClienteDTO existente = controlador.buscarPorId(id);
                if (existente != null) {
                    cli.setElsReferencia(existente.getElsReferencia());
                }
            }
        }
        cli.setTipoPersona(esParticular ? "particular" : "empresa");
        cli.setTipoDocumento((String) cmbTipoDoc.getSelectedItem());
        cli.setNroDocumento(nroDoc.isEmpty() ? "" : nroDoc);
        cli.setRazonSocial(razonSocial);
        cli.setCondicionIva(condicionIva);
        cli.setDomicilio(domicilio);
        cli.setTelefono(getPhoneTexts());
        cli.setEmail(getEmailTexts());

        int id = controlador.guardarCliente(cli);
        if (id > 0 || cli.getId() != null) {
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            modoEdicion = false;
            cargarClientes();
            setFormEditable(false);
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar cliente", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sincronizarConReparsoft() {
        List<ClienteDTO> importados = controlador.importarDesdeReparsoft();
        if (!importados.isEmpty()) {
            cargarClientes();
        }
    }

    private void setFormEditable(boolean editable) {
        Color bg = getFieldBg(editable);
        txtNroDoc.setEnabled(editable);
        txtNroDoc.setBackground(bg);
        txtRazonSocial.setEnabled(editable);
        txtRazonSocial.setBackground(bg);
        txtDomicilio.setEnabled(editable);
        txtDomicilio.setBackground(bg);
        for (JTextField f : phoneFields) {
            f.setEnabled(editable);
            f.setBackground(bg);
        }
        btnAddPhone.setEnabled(editable && phoneFields.size() < MAX_PHONES);
        btnRemovePhone.setEnabled(editable && phoneFields.size() > 1);
        for (JTextField f : emailFields) {
            f.setEnabled(editable);
            f.setBackground(bg);
        }
        btnAddEmail.setEnabled(editable && emailFields.size() < MAX_EMAILS);
        btnRemoveEmail.setEnabled(editable && emailFields.size() > 1);
        cmbTipoDoc.setEnabled(editable);
        cmbTipoDoc.setBackground(bg);
        cmbTipoDoc.setForeground(editable ? currentTheme.textPrimary : getDisabledFg());
        cmbCondicionIva.setEnabled(editable);
        cmbCondicionIva.setBackground(bg);
        cmbCondicionIva.setForeground(editable ? currentTheme.textPrimary : getDisabledFg());
        rdParticular.setEnabled(editable);
        rdParticular.setForeground(editable ? currentTheme.textPrimary : getDisabledFg());
        rdEmpresa.setEnabled(editable);
        rdEmpresa.setForeground(editable ? currentTheme.textPrimary : getDisabledFg());
        btnGuardar.setEnabled(editable);
        btnEditar.setEnabled(!editable && tabla.getSelectedRow() >= 0);
    }

    private void habilitarEdicion(boolean editable) {
        modoEdicion = editable;
        setFormEditable(editable);
    }

    private void limpiarFormulario() {
        btnMostrarSucursales.setVisible(false);
        tabla.clearSelection();
        rdEmpresa.setSelected(true);
        updatePlaceholders();
        cmbTipoDoc.setSelectedIndex(0);
        txtNroDoc.setText("");
        txtRazonSocial.setText("");
        txtRazonSocial.setCaretPosition(0);
        cmbCondicionIva.setSelectedIndex(0);
        txtDomicilio.setText("");
        resetPhoneFields();
        resetEmailFields();
        modoEdicion = false;
        setFormEditable(true);
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (getContentPane() != null) getContentPane().setBackground(t.bgBase);
        if (panelSuperior != null) panelSuperior.setBackground(t.bgSurface);
        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (panelFiltro != null) {
            panelFiltro.setBackground(t.bgSurface);
            panelFiltro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.borderLight, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        }
        if (lblClienteFiltro != null) lblClienteFiltro.setForeground(t.textPrimary);
        if (cmbCliente != null) themeComboEditor(cmbCliente, t);
        Color bgCentral = t.bgBase.getRed() > 128 ? new Color(182, 196, 220) : new Color(18, 55, 140);
        if (panelCentro != null) panelCentro.setBackground(bgCentral);
        if (panelIzquierdo != null) panelIzquierdo.setBackground(bgCentral);
        if (panelDerecho != null) panelDerecho.setBackground(bgCentral);
        if (rdParticular != null) {
            rdParticular.setForeground(rdParticular.isEnabled() ? t.textPrimary : getDisabledFg());
            rdParticular.setBackground(t.bgSurface);
        }
        if (rdEmpresa != null) {
            rdEmpresa.setForeground(rdEmpresa.isEnabled() ? t.textPrimary : getDisabledFg());
            rdEmpresa.setBackground(t.bgSurface);
        }
        if (panelForm != null) {
            panelForm.setBackground(t.bgSurface);
            panelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(t.brand),
                    "DATOS DEL CLIENTE",
                    TitledBorder.LEFT, TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 14), t.textPrimary),
                BorderFactory.createEmptyBorder(4, 6, 6, 6)));
            themeLabels(panelForm, t);
        }

        if (panelBotonesForm != null) {
            panelBotonesForm.setBackground(t.bgSurface);
            panelBotonesForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(t.brand),
                    "ACCIONES",
                    TitledBorder.LEFT, TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 14), t.textPrimary),
                BorderFactory.createEmptyBorder(4, 8, 6, 8)));
        }

        if (cmbTipoDoc != null) {
            installComboUI(cmbTipoDoc);
            cmbTipoDoc.setForeground(cmbTipoDoc.isEnabled() ? t.textPrimary : getDisabledFg());
            cmbTipoDoc.setBackground(getFieldBg(cmbTipoDoc.isEnabled()));
        }
        if (cmbCondicionIva != null) {
            installComboUI(cmbCondicionIva);
            cmbCondicionIva.setForeground(cmbCondicionIva.isEnabled() ? t.textPrimary : getDisabledFg());
            cmbCondicionIva.setBackground(getFieldBg(cmbCondicionIva.isEnabled()));
        }
        if (txtNroDoc != null) {
            txtNroDoc.setForeground(t.textPrimary);
            txtNroDoc.setBackground(getFieldBg(txtNroDoc.isEnabled()));
            txtNroDoc.setDisabledTextColor(getDisabledFg());
        }
        if (txtRazonSocial != null) {
            txtRazonSocial.setForeground(t.textPrimary);
            txtRazonSocial.setBackground(getFieldBg(txtRazonSocial.isEnabled()));
            txtRazonSocial.setDisabledTextColor(getDisabledFg());
        }
        if (txtDomicilio != null) {
            txtDomicilio.setForeground(t.textPrimary);
            txtDomicilio.setBackground(getFieldBg(txtDomicilio.isEnabled()));
            txtDomicilio.setDisabledTextColor(getDisabledFg());
        }
        for (JTextField f : phoneFields) applyPhoneFieldTheme(f);
        if (btnAddPhone != null) {
            btnAddPhone.setBackground(t.btnBg);
            btnAddPhone.setForeground(t.textPrimary);
        }
        if (btnRemovePhone != null) {
            btnRemovePhone.setBackground(t.btnBg);
            btnRemovePhone.setForeground(t.textPrimary);
        }
        for (JTextField f : emailFields) applyEmailFieldTheme(f);
        if (btnAddEmail != null) {
            btnAddEmail.setBackground(t.btnBg);
            btnAddEmail.setForeground(t.textPrimary);
        }
        if (btnRemoveEmail != null) {
            btnRemoveEmail.setBackground(t.btnBg);
            btnRemoveEmail.setForeground(t.textPrimary);
        }
        if (txtSucNombre != null) {
            txtSucNombre.setForeground(t.textPrimary);
            txtSucNombre.setBackground(getFieldBg(true));
        }
        if (txtSucDireccion != null) {
            txtSucDireccion.setForeground(t.textPrimary);
            txtSucDireccion.setBackground(getFieldBg(true));
        }
        if (txtSucContacto != null) {
            txtSucContacto.setForeground(t.textPrimary);
            txtSucContacto.setBackground(getFieldBg(true));
        }
        if (btnGuardar != null) { btnGuardar.setBackground(t.btnBg); btnGuardar.setForeground(t.textPrimary); }
        if (btnNuevo != null) { btnNuevo.setBackground(t.btnBg); btnNuevo.setForeground(t.textPrimary); }
        if (btnEditar != null) { btnEditar.setBackground(t.btnBg); btnEditar.setForeground(t.textPrimary); }
        if (btnMostrarSucursales != null) { btnMostrarSucursales.setBackground(t.btnBg); btnMostrarSucursales.setForeground(t.textPrimary); }
        if (btnVolverSucursales != null) { btnVolverSucursales.setBackground(t.btnBg); btnVolverSucursales.setForeground(t.textPrimary); }
        if (btnGuardarSucursal != null) { btnGuardarSucursal.setBackground(t.btnBg); btnGuardarSucursal.setForeground(t.textPrimary); }
        if (btnNuevoSucursal != null) { btnNuevoSucursal.setBackground(t.btnBg); btnNuevoSucursal.setForeground(t.textPrimary); }
        if (btnEditarSucursal != null) { btnEditarSucursal.setBackground(t.btnBg); btnEditarSucursal.setForeground(t.textPrimary); }
        if (panelBotonesSucursal != null) {
            panelBotonesSucursal.setBackground(t.bgSurface);
            panelBotonesSucursal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(t.brand),
                    "ACCIONES",
                    TitledBorder.LEFT, TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 14), t.textPrimary),
                BorderFactory.createEmptyBorder(4, 8, 6, 8)));
        }
        if (panelSucursalView != null) {
            panelSucursalView.setBackground(t.bgSurface);
            themeLabels(panelSucursalView, t);
        }
        if (formSuc != null) {
            formSuc.setBackground(t.bgSurface);
            formSuc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(t.brand),
                    "DATOS DE LA SUCURSAL",
                    TitledBorder.LEFT, TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 14), t.textPrimary),
                BorderFactory.createEmptyBorder(4, 6, 6, 6)));
        }
        for (JTextField f : sucPhoneFields) {
            f.setForeground(t.textPrimary);
            f.setBackground(getFieldBg(f.isEnabled()));
            f.setDisabledTextColor(getDisabledFg());
        }
        if (btnSucAddPhone != null) {
            btnSucAddPhone.setBackground(t.btnBg);
            btnSucAddPhone.setForeground(t.textPrimary);
        }
        if (btnSucRemovePhone != null) {
            btnSucRemovePhone.setBackground(t.btnBg);
            btnSucRemovePhone.setForeground(t.textPrimary);
        }
        for (JTextField f : sucEmailFields) {
            f.setForeground(t.textPrimary);
            f.setBackground(getFieldBg(f.isEnabled()));
            f.setDisabledTextColor(getDisabledFg());
        }
        if (btnSucAddEmail != null) {
            btnSucAddEmail.setBackground(t.btnBg);
            btnSucAddEmail.setForeground(t.textPrimary);
        }
        if (btnSucRemoveEmail != null) {
            btnSucRemoveEmail.setBackground(t.btnBg);
            btnSucRemoveEmail.setForeground(t.textPrimary);
        }
        if (tablaSucursales != null) {
            TablaRenderer.applyTo(tablaSucursales, t, Collections.emptySet(), Collections.singleton(0));
            if (tablaSucursales.getTableHeader() != null) {
                Theme.styleTableHeader(tablaSucursales.getTableHeader(), t);
            }
        }
        if (scrollTabla != null) scrollTabla.getViewport().setBackground(t.bgBase);
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t, Collections.emptySet(), Collections.singleton(2));
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
        }
        if (statusBar != null) {
            statusBar.setBackground(t.statusBarBg);
        }
        if (lblStatus != null) {
            lblStatus.setForeground(t.statusBarFg);
        }
    }

    private void themeLabels(java.awt.Container c, Theme t) {
        for (java.awt.Component comp : c.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel lbl = (JLabel) comp;
                lbl.setForeground(t.textPrimary);
                lbl.setFont(FUENTE_LABEL);
            }
            if (comp instanceof java.awt.Container) {
                themeLabels((java.awt.Container) comp, t);
            }
        }
    }

    private void addButtonFeedback(JButton btn) {
        btn.addChangeListener(e -> {
            ButtonModel model = btn.getModel();
            if (model.isPressed()) {
                btn.setBackground(currentTheme.pressedBg);
            } else if (model.isRollover()) {
                btn.setBackground(currentTheme.hoverBg);
            } else {
                btn.setBackground(currentTheme.btnBg);
            }
        });
    }

    private void installComboUI(JComboBox<?> combo) {
        combo.setUI(new CustomComboUI());
    }

}
