package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorClientes;
import com.els.facturacion.modelo.ClienteDTO;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class VentanaClientes extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_INPUT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.PLAIN, 11);
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
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
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
    private JPanel panelBotonesForm;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEditar;
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
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
        cargarClientes();
        sincronizarConReparsoft();
    }

    private void initComponents() {
        setTitle("Gestion de Clientes");
        setSize(1024, 600);
        setResizable(false);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        // ── Panel superior (solo título) ──
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        lblTitulo = new JLabel("MODULO DE CLIENTES");
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
        txtNroDoc.setFont(FUENTE_INPUT);
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
        txtRazonSocial.setFont(FUENTE_INPUT);
        txtRazonSocial.setDisabledTextColor(getDisabledFg());
        txtDomicilio = new TextPrompt("");
        txtDomicilio.setFont(FUENTE_INPUT);
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
        panelForm.add(new JLabel("Tipo Doc:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(cmbTipoDoc, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        panelForm.add(new JLabel("N\u00b0 Doc:"), new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(txtNroDoc, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

        row++;
        lblRazonSocial = new JLabel("Razon Social:");
        panelForm.add(lblRazonSocial, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 2), 0, 0));
        panelForm.add(txtRazonSocial, new GridBagConstraints(1, row, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 3, 5), 0, 0));

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

        // ── Espaciador vertical para que el form ocupe el espacio ──
        row++;
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panelForm.add(panel, new GridBagConstraints(0, row, 2, 1, 1, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        // ── Tabla (lado izquierdo) ──
        String[] columnas = {"ID", "Tipo", "Razon Social", "Tipo Doc", "Nro Doc", "Condicion IVA", "Domicilio", "Telefono", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);
        tabla.setIntercellSpacing(new Dimension(4, 2));
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
        int[] hiddenCols = {1, 3, 5, 6, 8};
        for (int c : hiddenCols) {
            tabla.getColumnModel().getColumn(c).setMinWidth(0);
            tabla.getColumnModel().getColumn(c).setMaxWidth(0);
            tabla.getColumnModel().getColumn(c).setPreferredWidth(0);
        }
        tabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(4).setMaxWidth(120);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(7).setMaxWidth(120);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarClienteSeleccionado();
        });

        scrollTabla = new JScrollPane(tabla);

        // ── Buscador (lado izquierdo, arriba de la tabla) ──
        txtBuscar = new JTextField(20);
        txtBuscar.setHorizontalAlignment(JTextField.LEFT);
        txtBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarCliente(); }
            public void removeUpdate(DocumentEvent e) { buscarCliente(); }
            public void changedUpdate(DocumentEvent e) { buscarCliente(); }
        });

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        panelBusqueda.setBackground(currentTheme.bgSurface);
        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(FUENTE_LABEL);
        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBackground(currentTheme.bgBase);
        panelIzquierdo.add(panelBusqueda, BorderLayout.NORTH);
        panelIzquierdo.add(scrollTabla, BorderLayout.CENTER);

        // ── Panel contenedor derecho: form arriba, botones abajo fijos ──
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        panelDerecho = new JPanel(new BorderLayout(0, 6));
        panelDerecho.setBackground(currentTheme.bgBase);
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

        // ── Layout principal: 50/50 fijo con GridLayout (ignora preferred sizes) ──
        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 2, 0)) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int borderColor = currentTheme.bgBase.getRed() > 128 ? 120 : 60;
                Color c1 = new Color(borderColor, borderColor + 8, borderColor + 20, 100);
                Color c2 = new Color(borderColor, borderColor + 8, borderColor + 20, 0);
                int gapX = getWidth() / 2 - 1;
                g2.setPaint(new java.awt.GradientPaint(gapX, 0, c1, gapX + 2, 0, c2));
                g2.fillRect(gapX, 0, 2, getHeight());
                g2.dispose();
            }
        };
        panelCentro.setBackground(currentTheme.bgBase);
        panelCentro.add(panelIzquierdo);
        panelCentro.add(panelDerecho);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(panelCentro, BorderLayout.CENTER);
        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
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
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = controlador.listarTodos();
        for (ClienteDTO c : lista) {
            String tipo = "empresa".equals(c.getTipoPersona()) ? "Empresa" : "Particular";
            modeloTabla.addRow(new Object[]{
                c.getId(), tipo, c.getRazonSocial(), c.getTipoDocumento(),
                c.getNroDocumento(), c.getCondicionIva(),
                c.getDomicilio(), c.getTelefono(), c.getEmail()
            });
        }
    }

    private void buscarCliente() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) { cargarClientes(); return; }
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = controlador.buscarPorRazonSocial(termino);
        for (ClienteDTO c : lista) {
            String tipo = "empresa".equals(c.getTipoPersona()) ? "Empresa" : "Particular";
            modeloTabla.addRow(new Object[]{
                c.getId(), tipo, c.getRazonSocial(), c.getTipoDocumento(),
                c.getNroDocumento(), c.getCondicionIva(),
                c.getDomicilio(), c.getTelefono(), c.getEmail()
            });
        }
    }

    private void cargarClienteSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            modoEdicion = false;
            setFormEditable(false);
            return;
        }
        String tipo = modeloTabla.getValueAt(row, 1) != null ? modeloTabla.getValueAt(row, 1).toString() : "Empresa";
        if ("Particular".equals(tipo)) {
            rdParticular.setSelected(true);
        } else {
            rdEmpresa.setSelected(true);
        }
        updatePlaceholders();
        txtRazonSocial.setText(modeloTabla.getValueAt(row, 2) != null ? modeloTabla.getValueAt(row, 2).toString() : "");
        txtRazonSocial.setCaretPosition(0);
        cmbTipoDoc.setSelectedItem(modeloTabla.getValueAt(row, 3));
        txtNroDoc.setText(modeloTabla.getValueAt(row, 4) != null ? modeloTabla.getValueAt(row, 4).toString() : "");
        cmbCondicionIva.setSelectedItem(modeloTabla.getValueAt(row, 5) != null ? modeloTabla.getValueAt(row, 5).toString() : "Consumidor Final");
        txtDomicilio.setText(modeloTabla.getValueAt(row, 6) != null ? modeloTabla.getValueAt(row, 6).toString() : "");
        setPhoneTexts(modeloTabla.getValueAt(row, 7) != null ? modeloTabla.getValueAt(row, 7).toString() : "");
        setEmailTexts(modeloTabla.getValueAt(row, 8) != null ? modeloTabla.getValueAt(row, 8).toString() : "");
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
        if (txtBuscar != null) {
            txtBuscar.setForeground(t.textPrimary);
            txtBuscar.setBackground(t.bgInput);
        }
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
        if (panelDerecho != null) {
            panelDerecho.setBackground(t.bgBase);
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
        if (btnGuardar != null) { btnGuardar.setBackground(t.btnBg); btnGuardar.setForeground(t.textPrimary); }
        if (btnNuevo != null) { btnNuevo.setBackground(t.btnBg); btnNuevo.setForeground(t.textPrimary); }
        if (btnEditar != null) { btnEditar.setBackground(t.btnBg); btnEditar.setForeground(t.textPrimary); }
        if (scrollTabla != null) scrollTabla.getViewport().setBackground(t.bgBase);
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t, Collections.emptySet(), Collections.singleton(2));
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
        }
        if (statusBar != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
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
