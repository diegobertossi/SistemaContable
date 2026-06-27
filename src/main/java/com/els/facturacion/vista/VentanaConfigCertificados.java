package com.els.facturacion.vista;

import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.modelo.CuitConfigDTO;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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

public class VentanaConfigCertificados extends JFrame {

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

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private CuitDAO dao;

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
    private JPanel panelSuperior;
    private JPanel panelFormulario;
    private JPanel panelBotones;
    private JPanel panelSur;
    private JScrollPane scrollTabla;
    private JLabel lblTitulo;
    private JButton btnAgregar;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBackup;
    private JLabel lblCertInfo;
    private JPanel southWrapper;

    public VentanaConfigCertificados() {
        setTitle("MÓDULO HERRAMIENTAS");
        setSize(1024, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        dao = new CuitDAO();
        initComponents();
        applyTheme(currentTheme);
        cargarTabla();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTitulo = new JLabel("GESTION DE CERTIFICADOS Y CUITs", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        lblTitulo.setBackground(currentTheme.bgSurface);

        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        String[] columnas = {"ID", "CUIT", "Razon Social", "Condicion IVA", "Punto Venta", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
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
                        dao.activarExclusivo(id);
                        for (int r = 0; r < getRowCount(); r++) {
                            super.setValueAt(r == row, r, 5);
                        }
                    } else {
                        int activos = dao.contarActivos();
                        if (activos <= 1) {
                            super.setValueAt(Boolean.TRUE, row, 5);
                            return;
                        }
                        dao.activarExclusivo(-1);
                        super.setValueAt(false, row, 5);
                    }
                    fireTableDataChanged();
                } else {
                    super.setValueAt(aValue, row, column);
                }
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(22);
        tabla.setIntercellSpacing(new Dimension(3, 2));
        tabla.setShowGrid(true);
        scrollTabla = new JScrollPane(tabla);

        panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(currentTheme.bgSurface);
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

        int row = 0;
        Insets insL = new Insets(4, 5, 4, 2);
        Insets insF = new Insets(4, 2, 4, 5);

        // Row 0: CUIT (col 0-1) + Condicion IVA (col 2-3)
        JLabel lblCuit = new JLabel("CUIT:");
        lblCuit.setFont(FUENTE_LABEL);
        lblCuit.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblCuit, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(txtCuit, new GridBagConstraints(1, row, 1, 1, 0.3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));
        JLabel lblCond = new JLabel("Condicion IVA:");
        lblCond.setFont(FUENTE_LABEL);
        lblCond.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblCond, new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(cmbCondicionIva, new GridBagConstraints(3, row, 1, 1, 0.7, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        // Row 1: Razon Social (full width)
        row++;
        JLabel lblRs = new JLabel("Razon Social:");
        lblRs.setFont(FUENTE_LABEL);
        lblRs.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblRs, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(txtRazonSocial, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        // Row 2: Punto Venta (col 0-1) + Ingresos Brutos (col 2-3)
        row++;
        JLabel lblPv = new JLabel("Punto de Venta:");
        lblPv.setFont(FUENTE_LABEL);
        lblPv.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblPv, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(txtPuntoVenta, new GridBagConstraints(1, row, 1, 1, 0.3, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));
        JLabel lblIb = new JLabel("Ingresos Brutos:");
        lblIb.setFont(FUENTE_LABEL);
        lblIb.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblIb, new GridBagConstraints(2, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(txtIngresosBrutos, new GridBagConstraints(3, row, 1, 1, 0.7, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        // Row 3: Domicilio Comercial (full width)
        row++;
        JLabel lblDom = new JLabel("Domicilio Comercial:");
        lblDom.setFont(FUENTE_LABEL);
        lblDom.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblDom, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(txtDomicilio, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        // Row 4: Fecha Inicio Actividades (col 0-1) + empty
        row++;
        JLabel lblFia = new JLabel("Fecha Inicio Actividades:");
        lblFia.setFont(FUENTE_LABEL);
        lblFia.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblFia, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(dateInicioActividades, new GridBagConstraints(1, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        // Row 5: Certificado .p12 (col 0 + field + button)
        row++;
        JLabel lblCert = new JLabel("Certificado .p12:");
        lblCert.setFont(FUENTE_LABEL);
        lblCert.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblCert, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(txtRutaCertificado, new GridBagConstraints(1, row, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));
        panelFormulario.add(btnSeleccionarArchivo, new GridBagConstraints(3, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        // Row 6: Password
        row++;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(FUENTE_LABEL);
        lblPass.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblPass, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(txtPassword, new GridBagConstraints(1, row, 1, 1, 0.5, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        // Row 7: Cert info (full width)
        row++;
        JLabel lblInfoTit = new JLabel("Certificado:");
        lblInfoTit.setFont(FUENTE_LABEL);
        lblInfoTit.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lblInfoTit, new GridBagConstraints(0, row, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insL, 0, 0));
        panelFormulario.add(lblCertInfo, new GridBagConstraints(1, row, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insF, 0, 0));

        panelBotones = new JPanel();
        panelBotones.setBackground(currentTheme.bgSurface);

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
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarFilaSeleccionada();
            }
        });

        panelBotones.add(btnAgregar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnBackup);

        panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(currentTheme.bgSurface);
        panelSur.add(panelFormulario, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(scrollTabla, BorderLayout.CENTER);
        southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(currentTheme.bgBase);
        southWrapper.add(panelSur, BorderLayout.CENTER);
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(currentTheme.statusBarBg);
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(currentTheme.statusBarFg);
        statusBar.add(lblStatus);
        southWrapper.add(statusBar, BorderLayout.SOUTH);
        getContentPane().add(southWrapper, BorderLayout.SOUTH);
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

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<CuitConfigDTO> lista = dao.listarTodos();
        for (CuitConfigDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getId(),
                dto.getCuit(),
                dto.getRazonSocial(),
                dto.getCondicionIva(),
                dto.getPuntoVenta(),
                dto.getActivo()
            });
        }
    }

    private void cargarFilaSeleccionada() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) modeloTabla.getValueAt(selectedRow, 0);
            CuitConfigDTO dto = dao.buscarPorId(id);
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
        if (!validarCampos()) return;

        boolean hayActivos = dao.contarActivos() > 0;
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

        int id = dao.insertar(dto);
        if (id > 0) {
            if (dto.getActivo()) {
                dao.activarExclusivo(id);
            }
            JOptionPane.showMessageDialog(this, "CUIT guardado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar CUIT", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnModificarAction(java.awt.event.ActionEvent e) {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para modificar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validarCampos()) return;

        int id = (int) modeloTabla.getValueAt(selectedRow, 0);
        CuitConfigDTO dto = dao.buscarPorId(id);
        dto.setCuit(txtCuit.getText().trim());
        dto.setRazonSocial(txtRazonSocial.getText().trim());
        dto.setCondicionIva((String) cmbCondicionIva.getSelectedItem());
        dto.setPuntoVenta(Integer.parseInt(txtPuntoVenta.getText().trim()));
        dto.setDomicilio(txtDomicilio.getText().trim());
        dto.setIngresosBrutos(txtIngresosBrutos.getText().trim());
        dto.setFechaInicioActividades(getDateChooserText(dateInicioActividades));
        dto.setRutaCertificado(txtRutaCertificado.getText().trim());
        dto.setPasswordCert(new String(txtPassword.getPassword()));

        if (dao.actualizar(dto)) {
            JOptionPane.showMessageDialog(this, "CUIT modificado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            limpiarCampos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar CUIT", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEliminarAction(java.awt.event.ActionEvent e) {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Esta seguro de eliminar este registro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(selectedRow, 0);
            if (dao.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "CUIT eliminado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar CUIT", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnLimpiarAction(java.awt.event.ActionEvent e) {
        limpiarCampos();
    }

    private void btnSeleccionarAction(java.awt.event.ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            txtRutaCertificado.setText(fileChooser.getSelectedFile().getAbsolutePath());
            actualizarInfoCertificado(txtRutaCertificado.getText(), new String(txtPassword.getPassword()));
        }
    }

    private boolean validarCampos() {
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

    private void limpiarCampos() {
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
        tabla.clearSelection();
    }

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
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            } else {
                String text = ((JTextField) comp).getText();
                if (text != null && !text.isEmpty()) {
                    return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        getContentPane().setBackground(t.bgBase);
        if (panelSuperior != null) panelSuperior.setBackground(t.bgSurface);
        if (lblTitulo != null) {
            lblTitulo.setForeground(t.brand);
            lblTitulo.setBackground(t.bgSurface);
        }
        if (panelFormulario != null) {
            panelFormulario.setBackground(t.bgSurface);
            themeLabels(panelFormulario, t);
        }
        if (panelBotones != null) panelBotones.setBackground(t.bgSurface);
        if (panelSur != null) panelSur.setBackground(t.bgSurface);
        if (southWrapper != null) southWrapper.setBackground(t.bgBase);
        if (statusBar != null) {
            statusBar.setBackground(t.statusBarBg);
        }
        if (lblStatus != null) {
            lblStatus.setForeground(t.statusBarFg);
        }
        if (txtCuit != null) {
            txtCuit.setBackground(getFieldBg(txtCuit.isEnabled()));
            txtCuit.setForeground(t.textPrimary);
            txtCuit.setDisabledTextColor(getDisabledFg());
            txtCuit.setCaretColor(t.textPrimary);
        }
        if (txtRazonSocial != null) {
            txtRazonSocial.setBackground(getFieldBg(txtRazonSocial.isEnabled()));
            txtRazonSocial.setForeground(t.textPrimary);
            txtRazonSocial.setDisabledTextColor(getDisabledFg());
            txtRazonSocial.setCaretColor(t.textPrimary);
        }
        if (txtPuntoVenta != null) {
            txtPuntoVenta.setBackground(getFieldBg(txtPuntoVenta.isEnabled()));
            txtPuntoVenta.setForeground(t.textPrimary);
            txtPuntoVenta.setDisabledTextColor(getDisabledFg());
            txtPuntoVenta.setCaretColor(t.textPrimary);
        }
        if (txtDomicilio != null) {
            txtDomicilio.setBackground(getFieldBg(txtDomicilio.isEnabled()));
            txtDomicilio.setForeground(t.textPrimary);
            txtDomicilio.setDisabledTextColor(getDisabledFg());
            txtDomicilio.setCaretColor(t.textPrimary);
        }
        if (txtIngresosBrutos != null) {
            txtIngresosBrutos.setBackground(getFieldBg(txtIngresosBrutos.isEnabled()));
            txtIngresosBrutos.setForeground(t.textPrimary);
            txtIngresosBrutos.setDisabledTextColor(getDisabledFg());
            txtIngresosBrutos.setCaretColor(t.textPrimary);
        }
        if (dateInicioActividades != null) {
            themeDateField(dateInicioActividades, t);
        }
        if (txtRutaCertificado != null) {
            txtRutaCertificado.setBackground(getFieldBg(txtRutaCertificado.isEnabled()));
            txtRutaCertificado.setForeground(t.textPrimary);
            txtRutaCertificado.setDisabledTextColor(getDisabledFg());
            txtRutaCertificado.setCaretColor(t.textPrimary);
        }
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
        if (btnSeleccionarArchivo != null) {
            btnSeleccionarArchivo.setForeground(t.textPrimary);
            btnSeleccionarArchivo.setBackground(t.btnBg);
        }
        if (btnAgregar != null) {
            btnAgregar.setForeground(t.textPrimary);
            btnAgregar.setBackground(t.btnBg);
        }
        if (btnModificar != null) {
            btnModificar.setForeground(t.textPrimary);
            btnModificar.setBackground(t.btnBg);
        }
        if (btnEliminar != null) {
            btnEliminar.setForeground(t.textPrimary);
            btnEliminar.setBackground(t.btnBg);
        }
        if (btnLimpiar != null) {
            btnLimpiar.setForeground(t.textPrimary);
            btnLimpiar.setBackground(t.btnBg);
        }
        if (btnBackup != null) {
            btnBackup.setForeground(t.textPrimary);
            btnBackup.setBackground(t.btnBg);
        }
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t);
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
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
        if (scrollTabla != null) {
            scrollTabla.getViewport().setBackground(t.bgBase);
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
}
