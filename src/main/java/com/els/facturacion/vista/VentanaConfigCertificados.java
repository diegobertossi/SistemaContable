package com.els.facturacion.vista;

import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.modelo.CuitConfigDTO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
    private JPanel southWrapper;

    public VentanaConfigCertificados() {
        setTitle("Configuracion de Certificados ARCA");
        setSize(950, 620);
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

        cmbCondicionIva = new JComboBox<>(new String[]{"RI", "Monotributista", "Exento", "Consumidor Final"});
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

        txtRutaCertificado = new JTextField(25);
        txtRutaCertificado.setFont(FUENTE_INPUT_BOLD);
        txtRutaCertificado.setDisabledTextColor(getDisabledFg());
        txtRutaCertificado.setCaretColor(currentTheme.textPrimary);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(FUENTE_INPUT_BOLD);
        txtPassword.setDisabledTextColor(getDisabledFg());
        txtPassword.setCaretColor(currentTheme.textPrimary);

        btnSeleccionarArchivo = new JButton("SELECCIONAR");
        btnSeleccionarArchivo.setFont(FUENTE_BOTON);
        btnSeleccionarArchivo.setForeground(currentTheme.textPrimary);
        btnSeleccionarArchivo.setBackground(currentTheme.btnBg);
        btnSeleccionarArchivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeleccionarArchivo.setFocusPainted(false);

        int row = 0;

        GridBagConstraints gbc_lbl1 = new GridBagConstraints();
        gbc_lbl1.insets = new Insets(5, 5, 5, 5);
        gbc_lbl1.fill = GridBagConstraints.HORIZONTAL;
        gbc_lbl1.gridx = 0; gbc_lbl1.gridy = row;
        JLabel lbl1 = new JLabel("CUIT:");
        lbl1.setFont(FUENTE_LABEL);
        lbl1.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lbl1, gbc_lbl1);
        GridBagConstraints gbc_txtCuit = new GridBagConstraints();
        gbc_txtCuit.insets = new Insets(5, 5, 5, 5);
        gbc_txtCuit.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCuit.gridx = 1; gbc_txtCuit.gridy = row;
        panelFormulario.add(txtCuit, gbc_txtCuit);

        row++;
        GridBagConstraints gbc_lbl2 = new GridBagConstraints();
        gbc_lbl2.insets = new Insets(5, 5, 5, 5);
        gbc_lbl2.fill = GridBagConstraints.HORIZONTAL;
        gbc_lbl2.gridx = 0; gbc_lbl2.gridy = row;
        JLabel lbl2 = new JLabel("Razon Social:");
        lbl2.setFont(FUENTE_LABEL);
        lbl2.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lbl2, gbc_lbl2);
        GridBagConstraints gbc_txtRazonSocial = new GridBagConstraints();
        gbc_txtRazonSocial.insets = new Insets(5, 5, 5, 5);
        gbc_txtRazonSocial.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtRazonSocial.gridx = 1; gbc_txtRazonSocial.gridy = row;
        gbc_txtRazonSocial.gridwidth = 2;
        panelFormulario.add(txtRazonSocial, gbc_txtRazonSocial);

        row++;
        GridBagConstraints gbc_lbl3 = new GridBagConstraints();
        gbc_lbl3.insets = new Insets(5, 5, 5, 5);
        gbc_lbl3.fill = GridBagConstraints.HORIZONTAL;
        gbc_lbl3.gridx = 0; gbc_lbl3.gridy = row;
        JLabel lbl3 = new JLabel("Condicion IVA:");
        lbl3.setFont(FUENTE_LABEL);
        lbl3.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lbl3, gbc_lbl3);
        GridBagConstraints gbc_cmbCondicionIva = new GridBagConstraints();
        gbc_cmbCondicionIva.insets = new Insets(5, 5, 5, 5);
        gbc_cmbCondicionIva.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbCondicionIva.gridx = 1; gbc_cmbCondicionIva.gridy = row;
        panelFormulario.add(cmbCondicionIva, gbc_cmbCondicionIva);

        row++;
        GridBagConstraints gbc_lbl4 = new GridBagConstraints();
        gbc_lbl4.insets = new Insets(5, 5, 5, 5);
        gbc_lbl4.fill = GridBagConstraints.HORIZONTAL;
        gbc_lbl4.gridx = 0; gbc_lbl4.gridy = row;
        JLabel lbl4 = new JLabel("Punto de Venta:");
        lbl4.setFont(FUENTE_LABEL);
        lbl4.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lbl4, gbc_lbl4);
        GridBagConstraints gbc_txtPuntoVenta = new GridBagConstraints();
        gbc_txtPuntoVenta.insets = new Insets(5, 5, 5, 5);
        gbc_txtPuntoVenta.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtPuntoVenta.gridx = 1; gbc_txtPuntoVenta.gridy = row;
        panelFormulario.add(txtPuntoVenta, gbc_txtPuntoVenta);

        row++;
        GridBagConstraints gbc_lbl5 = new GridBagConstraints();
        gbc_lbl5.insets = new Insets(5, 5, 5, 5);
        gbc_lbl5.fill = GridBagConstraints.HORIZONTAL;
        gbc_lbl5.gridx = 0; gbc_lbl5.gridy = row;
        JLabel lbl5 = new JLabel("Certificado .p12:");
        lbl5.setFont(FUENTE_LABEL);
        lbl5.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lbl5, gbc_lbl5);
        GridBagConstraints gbc_txtRutaCertificado = new GridBagConstraints();
        gbc_txtRutaCertificado.insets = new Insets(5, 5, 5, 5);
        gbc_txtRutaCertificado.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtRutaCertificado.gridx = 1; gbc_txtRutaCertificado.gridy = row;
        panelFormulario.add(txtRutaCertificado, gbc_txtRutaCertificado);
        GridBagConstraints gbc_btnSeleccionarArchivo = new GridBagConstraints();
        gbc_btnSeleccionarArchivo.insets = new Insets(5, 5, 5, 5);
        gbc_btnSeleccionarArchivo.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSeleccionarArchivo.gridx = 2; gbc_btnSeleccionarArchivo.gridy = row;
        panelFormulario.add(btnSeleccionarArchivo, gbc_btnSeleccionarArchivo);

        row++;
        GridBagConstraints gbc_lbl6 = new GridBagConstraints();
        gbc_lbl6.insets = new Insets(5, 5, 5, 5);
        gbc_lbl6.fill = GridBagConstraints.HORIZONTAL;
        gbc_lbl6.gridx = 0; gbc_lbl6.gridy = row;
        JLabel lbl6 = new JLabel("Password:");
        lbl6.setFont(FUENTE_LABEL);
        lbl6.setForeground(currentTheme.textPrimary);
        panelFormulario.add(lbl6, gbc_lbl6);
        GridBagConstraints gbc_txtPassword = new GridBagConstraints();
        gbc_txtPassword.insets = new Insets(5, 5, 5, 5);
        gbc_txtPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtPassword.gridx = 1; gbc_txtPassword.gridy = row;
        panelFormulario.add(txtPassword, gbc_txtPassword);

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

        panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(currentTheme.bgSurface);
        panelSur.add(panelFormulario, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(currentTheme.bgBase);
        southWrapper.add(panelSur, BorderLayout.CENTER);
        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        southWrapper.add(statusBar, BorderLayout.SOUTH);
        add(southWrapper, BorderLayout.SOUTH);
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
                txtRutaCertificado.setText(dto.getRutaCertificado());
                txtPassword.setText(dto.getPasswordCert());
            }
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
            new String(txtPassword.getPassword())
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
        txtRutaCertificado.setText("");
        txtPassword.setText("");
        tabla.clearSelection();
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
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
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
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t);
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
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
