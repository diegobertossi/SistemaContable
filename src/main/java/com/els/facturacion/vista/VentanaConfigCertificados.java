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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class VentanaConfigCertificados extends JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Cambria", Font.BOLD, 11);

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
        getContentPane().setBackground(COLOR_FONDO);

        dao = new CuitDAO();
        initComponents();
        cargarTabla();
    }

    private void initComponents() {
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("GESTION DE CERTIFICADOS Y CUITs", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBackground(COLOR_FONDO);

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
        tabla.setFont(new Font("Cambria", Font.PLAIN, 11));
        JScrollPane scrollTabla = new JScrollPane(tabla);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCuit = new JTextField(15);
        txtCuit.setFont(new Font("Cambria", Font.PLAIN, 11));

        txtRazonSocial = new JTextField(20);
        txtRazonSocial.setFont(new Font("Cambria", Font.PLAIN, 11));

        cmbCondicionIva = new JComboBox<>(new String[]{"RI", "Monotributista", "Exento", "Consumidor Final"});
        cmbCondicionIva.setFont(FUENTE_BOTON);

        txtPuntoVenta = new JTextField(10);
        txtPuntoVenta.setFont(new Font("Cambria", Font.PLAIN, 11));

        txtRutaCertificado = new JTextField(25);
        txtRutaCertificado.setFont(new Font("Cambria", Font.PLAIN, 11));

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Cambria", Font.PLAIN, 11));

        btnSeleccionarArchivo = crearBoton("SELECCIONAR");

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl1 = crearLabel("CUIT:");
        panelFormulario.add(lbl1, gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtCuit, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl2 = crearLabel("Razon Social:");
        panelFormulario.add(lbl2, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelFormulario.add(txtRazonSocial, gbc);

        row++;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl3 = crearLabel("Condicion IVA:");
        panelFormulario.add(lbl3, gbc);
        gbc.gridx = 1;
        panelFormulario.add(cmbCondicionIva, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl4 = crearLabel("Punto de Venta:");
        panelFormulario.add(lbl4, gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtPuntoVenta, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl5 = crearLabel("Certificado .p12:");
        panelFormulario.add(lbl5, gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtRutaCertificado, gbc);
        gbc.gridx = 2;
        panelFormulario.add(btnSeleccionarArchivo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl6 = crearLabel("Password:");
        panelFormulario.add(lbl6, gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtPassword, gbc);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(COLOR_FONDO);

        JButton btnAgregar = crearBoton("AGREGAR");
        btnAgregar.addActionListener(this::btnAgregarAction);

        JButton btnModificar = crearBoton("MODIFICAR");
        btnModificar.addActionListener(this::btnModificarAction);

        JButton btnEliminar = crearBoton("ELIMINAR");
        btnEliminar.addActionListener(this::btnEliminarAction);

        JButton btnLimpiar = crearBoton("LIMPIAR");
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
        panelSur.setBackground(COLOR_FONDO);
        panelSur.add(panelFormulario, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_LABEL);
        lbl.setForeground(COLOR_TEXTO);
        return lbl;
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
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