package com.els.facturacion.vista;

import com.els.facturacion.dao.CuitDAO;
import com.els.facturacion.modelo.CuitConfigDTO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class VentanaConfigCertificados extends JFrame {

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

    public VentanaConfigCertificados() {
        setTitle("Configuracion de Certificados ARCA");
        setSize(950, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        dao = new CuitDAO();
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(TemaFacturaSoft.BG_APP);

        JLabel lblTitulo = new JLabel("GESTION DE CERTIFICADOS Y CUITs", SwingConstants.CENTER);
        lblTitulo.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(18f));
        lblTitulo.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);
        lblTitulo.setBackground(TemaFacturaSoft.BG_APP);

        panelSuperior.add(lblTitulo, BorderLayout.NORTH);

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
        tabla.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));
        JScrollPane scrollTabla = new JScrollPane(tabla);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(TemaFacturaSoft.BG_APP);
        txtCuit = new JTextField(15);
        txtCuit.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));

        txtRazonSocial = new JTextField(20);
        txtRazonSocial.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));

        cmbCondicionIva = new JComboBox<>(new String[]{"RI", "Monotributista", "Exento", "Consumidor Final"});
        cmbCondicionIva.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));

        txtPuntoVenta = new JTextField(10);
        txtPuntoVenta.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));

        txtRutaCertificado = new JTextField(25);
        txtRutaCertificado.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));

        btnSeleccionarArchivo = new JButton("SELECCIONAR");
        btnSeleccionarArchivo.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnSeleccionarArchivo.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnSeleccionarArchivo.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnSeleccionarArchivo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeleccionarArchivo.setFocusPainted(false);

        int row = 0;

        GridBagConstraints gbc_lbl1 = new GridBagConstraints();
        gbc_lbl1.insets = new Insets(5, 5, 5, 5);
        gbc_lbl1.fill = GridBagConstraints.HORIZONTAL;
        gbc_lbl1.gridx = 0; gbc_lbl1.gridy = row;
        JLabel lbl1 = new JLabel("CUIT:");
        lbl1.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lbl1.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
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
        lbl2.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lbl2.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
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
        lbl3.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lbl3.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
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
        lbl4.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lbl4.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
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
        lbl5.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lbl5.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
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
        lbl6.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lbl6.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        panelFormulario.add(lbl6, gbc_lbl6);
        GridBagConstraints gbc_txtPassword = new GridBagConstraints();
        gbc_txtPassword.insets = new Insets(5, 5, 5, 5);
        gbc_txtPassword.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtPassword.gridx = 1; gbc_txtPassword.gridy = row;
        panelFormulario.add(txtPassword, gbc_txtPassword);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(TemaFacturaSoft.BG_APP);

        JButton btnAgregar = new JButton("AGREGAR");
        btnAgregar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnAgregar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnAgregar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(this::btnAgregarAction);

        JButton btnModificar = new JButton("MODIFICAR");
        btnModificar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnModificar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnModificar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnModificar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnModificar.setFocusPainted(false);
        btnModificar.addActionListener(this::btnModificarAction);

        JButton btnEliminar = new JButton("ELIMINAR");
        btnEliminar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnEliminar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnEliminar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(this::btnEliminarAction);

        JButton btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnLimpiar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnLimpiar.setBackground(TemaFacturaSoft.BG_SURFACE);
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

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(TemaFacturaSoft.BG_APP);
        panelSur.add(panelFormulario, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
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
}
