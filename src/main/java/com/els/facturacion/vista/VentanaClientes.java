package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorClientes;
import com.els.facturacion.modelo.ClienteDTO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class VentanaClientes extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);

    private ControladorClientes controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JTextField txtNroDoc;
    private JTextField txtRazonSocial;
    private JTextField txtDomicilio;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JComboBox<String> cmbTipoDoc;
    private JComboBox<String> cmbCondicionIva;

    public VentanaClientes() {
        controlador = new ControladorClientes();
        initComponents();
        cargarClientes();
    }

    private void initComponents() {
        setTitle("Gestion de Clientes");
        setSize(900, 600);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(COLOR_FONDO);
        GridBagConstraints gbc_titulo = new GridBagConstraints();
        gbc_titulo.insets = new Insets(5, 5, 5, 5);
        gbc_titulo.fill = GridBagConstraints.HORIZONTAL;
        gbc_titulo.gridx = 0; gbc_titulo.gridy = 0; gbc_titulo.gridwidth = 5;

        JLabel lblTitulo = new JLabel("MODULO DE CLIENTES");
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXTO);
        panelSuperior.add(lblTitulo, gbc_titulo);

        txtBuscar = new JTextField(20);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarCliente(); }
            public void removeUpdate(DocumentEvent e) { buscarCliente(); }
            public void changedUpdate(DocumentEvent e) { buscarCliente(); }
        });
        JButton btnImportar = new JButton("IMPORTAR DE REPARSOFT");
        btnImportar.setFont(FUENTE_BOTON);
        btnImportar.setForeground(COLOR_TEXTO);
        btnImportar.setBackground(COLOR_BOTON);
        btnImportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImportar.setFocusPainted(false);
        btnImportar.addActionListener(e -> importarClientes());

        GridBagConstraints gbc_buscar_label = new GridBagConstraints();
        gbc_buscar_label.insets = new Insets(5, 5, 5, 5);
        gbc_buscar_label.fill = GridBagConstraints.HORIZONTAL;
        gbc_buscar_label.gridwidth = 1; gbc_buscar_label.gridx = 0; gbc_buscar_label.gridy = 1;

        GridBagConstraints gbc_txtBuscar = new GridBagConstraints();
        gbc_txtBuscar.insets = new Insets(5, 5, 5, 5);
        gbc_txtBuscar.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtBuscar.gridwidth = 1; gbc_txtBuscar.gridx = 1; gbc_txtBuscar.gridy = 1;

        GridBagConstraints gbc_btnImportar = new GridBagConstraints();
        gbc_btnImportar.insets = new Insets(5, 5, 5, 5);
        gbc_btnImportar.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnImportar.gridwidth = 1; gbc_btnImportar.gridx = 2; gbc_btnImportar.gridy = 1;

        panelSuperior.add(new JLabel("Buscar:"), gbc_buscar_label);
        panelSuperior.add(txtBuscar, gbc_txtBuscar);
        panelSuperior.add(btnImportar, gbc_btnImportar);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(COLOR_FONDO);

        cmbTipoDoc = new JComboBox<>(new String[]{"CUIT", "DNI"});
        cmbCondicionIva = new JComboBox<>(new String[]{
            "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
            "Responsable Monotributo", "Proveedor del Exterior", "Cliente del Exterior",
            "IVA Liberado - Ley 19.640", "Monotributista Social", "IVA No Alcanzado"
        });

        txtNroDoc = new JTextField(15);
        txtRazonSocial = new JTextField(25);
        txtDomicilio = new JTextField(25);
        txtTelefono = new JTextField(15);
        txtEmail = new JTextField(20);

        int row = 0;

        GridBagConstraints fgc_tipoDoc_label = new GridBagConstraints();
        fgc_tipoDoc_label.insets = new Insets(3, 5, 3, 5);
        fgc_tipoDoc_label.fill = GridBagConstraints.HORIZONTAL;
        fgc_tipoDoc_label.gridx = 0; fgc_tipoDoc_label.gridy = row;
        panelForm.add(new JLabel("Tipo Doc:"), fgc_tipoDoc_label);

        GridBagConstraints fgc_cmbTipoDoc = new GridBagConstraints();
        fgc_cmbTipoDoc.insets = new Insets(3, 5, 3, 5);
        fgc_cmbTipoDoc.fill = GridBagConstraints.HORIZONTAL;
        fgc_cmbTipoDoc.gridx = 1; fgc_cmbTipoDoc.gridy = row;
        panelForm.add(cmbTipoDoc, fgc_cmbTipoDoc);

        GridBagConstraints fgc_nroDoc_label = new GridBagConstraints();
        fgc_nroDoc_label.insets = new Insets(3, 5, 3, 5);
        fgc_nroDoc_label.fill = GridBagConstraints.HORIZONTAL;
        fgc_nroDoc_label.gridx = 2; fgc_nroDoc_label.gridy = row;
        panelForm.add(new JLabel("Nro Documento:"), fgc_nroDoc_label);

        GridBagConstraints fgc_txtNroDoc = new GridBagConstraints();
        fgc_txtNroDoc.insets = new Insets(3, 5, 3, 5);
        fgc_txtNroDoc.fill = GridBagConstraints.HORIZONTAL;
        fgc_txtNroDoc.gridx = 3; fgc_txtNroDoc.gridy = row;
        panelForm.add(txtNroDoc, fgc_txtNroDoc);

        row++;

        GridBagConstraints fgc_razonSocial_label = new GridBagConstraints();
        fgc_razonSocial_label.insets = new Insets(3, 5, 3, 5);
        fgc_razonSocial_label.fill = GridBagConstraints.HORIZONTAL;
        fgc_razonSocial_label.gridx = 0; fgc_razonSocial_label.gridy = row;
        panelForm.add(new JLabel("Razon Social:"), fgc_razonSocial_label);

        GridBagConstraints fgc_txtRazonSocial = new GridBagConstraints();
        fgc_txtRazonSocial.insets = new Insets(3, 5, 3, 5);
        fgc_txtRazonSocial.fill = GridBagConstraints.HORIZONTAL;
        fgc_txtRazonSocial.gridx = 1; fgc_txtRazonSocial.gridwidth = 3; fgc_txtRazonSocial.gridy = row;
        panelForm.add(txtRazonSocial, fgc_txtRazonSocial);

        row++;

        GridBagConstraints fgc_condIva_label = new GridBagConstraints();
        fgc_condIva_label.insets = new Insets(3, 5, 3, 5);
        fgc_condIva_label.fill = GridBagConstraints.HORIZONTAL;
        fgc_condIva_label.gridx = 0; fgc_condIva_label.gridwidth = 1; fgc_condIva_label.gridy = row;
        panelForm.add(new JLabel("Condicion IVA:"), fgc_condIva_label);

        GridBagConstraints fgc_cmbCondIva = new GridBagConstraints();
        fgc_cmbCondIva.insets = new Insets(3, 5, 3, 5);
        fgc_cmbCondIva.fill = GridBagConstraints.HORIZONTAL;
        fgc_cmbCondIva.gridx = 1; fgc_cmbCondIva.gridwidth = 3; fgc_cmbCondIva.gridy = row;
        panelForm.add(cmbCondicionIva, fgc_cmbCondIva);

        row++;

        GridBagConstraints fgc_domicilio_label = new GridBagConstraints();
        fgc_domicilio_label.insets = new Insets(3, 5, 3, 5);
        fgc_domicilio_label.fill = GridBagConstraints.HORIZONTAL;
        fgc_domicilio_label.gridx = 0; fgc_domicilio_label.gridwidth = 1; fgc_domicilio_label.gridy = row;
        panelForm.add(new JLabel("Domicilio:"), fgc_domicilio_label);

        GridBagConstraints fgc_txtDomicilio = new GridBagConstraints();
        fgc_txtDomicilio.insets = new Insets(3, 5, 3, 5);
        fgc_txtDomicilio.fill = GridBagConstraints.HORIZONTAL;
        fgc_txtDomicilio.gridx = 1; fgc_txtDomicilio.gridy = row;
        panelForm.add(txtDomicilio, fgc_txtDomicilio);

        GridBagConstraints fgc_telefono_label = new GridBagConstraints();
        fgc_telefono_label.insets = new Insets(3, 5, 3, 5);
        fgc_telefono_label.fill = GridBagConstraints.HORIZONTAL;
        fgc_telefono_label.gridx = 2; fgc_telefono_label.gridy = row;
        panelForm.add(new JLabel("Telefono:"), fgc_telefono_label);

        GridBagConstraints fgc_txtTelefono = new GridBagConstraints();
        fgc_txtTelefono.insets = new Insets(3, 5, 3, 5);
        fgc_txtTelefono.fill = GridBagConstraints.HORIZONTAL;
        fgc_txtTelefono.gridx = 3; fgc_txtTelefono.gridy = row;
        panelForm.add(txtTelefono, fgc_txtTelefono);

        row++;

        GridBagConstraints fgc_email_label = new GridBagConstraints();
        fgc_email_label.insets = new Insets(3, 5, 3, 5);
        fgc_email_label.fill = GridBagConstraints.HORIZONTAL;
        fgc_email_label.gridx = 0; fgc_email_label.gridy = row;
        panelForm.add(new JLabel("Email:"), fgc_email_label);

        GridBagConstraints fgc_txtEmail = new GridBagConstraints();
        fgc_txtEmail.insets = new Insets(3, 5, 3, 5);
        fgc_txtEmail.fill = GridBagConstraints.HORIZONTAL;
        fgc_txtEmail.gridx = 1; fgc_txtEmail.gridy = row;
        panelForm.add(txtEmail, fgc_txtEmail);

        JPanel panelBotonesForm = new JPanel();
        panelBotonesForm.setBackground(COLOR_FONDO);
        JButton btnGuardar = new JButton("GUARDAR CLIENTE");
        btnGuardar.setFont(FUENTE_BOTON);
        btnGuardar.setForeground(COLOR_TEXTO);
        btnGuardar.setBackground(COLOR_BOTON);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarCliente());
        JButton btnNuevo = new JButton("NUEVO");
        btnNuevo.setFont(FUENTE_BOTON);
        btnNuevo.setForeground(COLOR_TEXTO);
        btnNuevo.setBackground(COLOR_BOTON);
        btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> limpiarFormulario());
        panelBotonesForm.add(btnNuevo);
        panelBotonesForm.add(btnGuardar);

        row++;

        GridBagConstraints fgc_panelBotones = new GridBagConstraints();
        fgc_panelBotones.insets = new Insets(3, 5, 3, 5);
        fgc_panelBotones.fill = GridBagConstraints.HORIZONTAL;
        fgc_panelBotones.gridx = 0; fgc_panelBotones.gridy = row; fgc_panelBotones.gridwidth = 4;
        panelForm.add(panelBotonesForm, fgc_panelBotones);

        String[] columnas = {"ID", "Razon Social", "Tipo Doc", "Nro Doc", "Condicion IVA", "Telefono", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
        tabla.setFont(new Font("Cambria", Font.PLAIN, 10));
        tabla.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 10));
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarClienteSeleccionado();
        });

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelForm, BorderLayout.SOUTH);
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = controlador.listarTodos();
        for (ClienteDTO c : lista) {
            modeloTabla.addRow(new Object[]{
                c.getId(), c.getRazonSocial(), c.getTipoDocumento(),
                c.getNroDocumento(), c.getCondicionIva(),
                c.getTelefono(), c.getEmail()
            });
        }
    }

    private void buscarCliente() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) { cargarClientes(); return; }
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = controlador.buscarPorRazonSocial(termino);
        for (ClienteDTO c : lista) {
            modeloTabla.addRow(new Object[]{
                c.getId(), c.getRazonSocial(), c.getTipoDocumento(),
                c.getNroDocumento(), c.getCondicionIva(),
                c.getTelefono(), c.getEmail()
            });
        }
    }

    private void cargarClienteSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        txtRazonSocial.setText(modeloTabla.getValueAt(row, 1) != null ? modeloTabla.getValueAt(row, 1).toString() : "");
        cmbTipoDoc.setSelectedItem(modeloTabla.getValueAt(row, 2));
        txtNroDoc.setText(modeloTabla.getValueAt(row, 3) != null ? modeloTabla.getValueAt(row, 3).toString() : "");
        cmbCondicionIva.setSelectedItem(modeloTabla.getValueAt(row, 4) != null ? modeloTabla.getValueAt(row, 4).toString() : "Consumidor Final");
        txtTelefono.setText(modeloTabla.getValueAt(row, 5) != null ? modeloTabla.getValueAt(row, 5).toString() : "");
        txtEmail.setText(modeloTabla.getValueAt(row, 6) != null ? modeloTabla.getValueAt(row, 6).toString() : "");
    }

    private void guardarCliente() {
        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Razon Social es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ClienteDTO cli = new ClienteDTO();
        int row = tabla.getSelectedRow();
        if (row >= 0 && modeloTabla.getValueAt(row, 0) != null) {
            cli.setId((Integer) modeloTabla.getValueAt(row, 0));
        }
        cli.setTipoDocumento((String) cmbTipoDoc.getSelectedItem());
        String nroDoc = txtNroDoc.getText().trim();
        cli.setNroDocumento(nroDoc.isEmpty() ? "0" : nroDoc);
        cli.setRazonSocial(txtRazonSocial.getText().trim());
        cli.setCondicionIva((String) cmbCondicionIva.getSelectedItem());
        cli.setDomicilio(txtDomicilio.getText().trim());
        cli.setTelefono(txtTelefono.getText().trim());
        cli.setEmail(txtEmail.getText().trim());

        int id = controlador.guardarCliente(cli);
        if (id > 0 || cli.getId() != null) {
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarClientes();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar cliente", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importarClientes() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "Desea importar clientes desde ReparSoft? (Se importaran solo clientes nuevos)",
            "Importar Clientes", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) return;

        List<ClienteDTO> importados = controlador.importarDesdeReparsoft();
        JOptionPane.showMessageDialog(this,
            "Importacion completada.\nClientes importados: " + importados.size(),
            "Resultado", JOptionPane.INFORMATION_MESSAGE);
        cargarClientes();
    }

    private void limpiarFormulario() {
        tabla.clearSelection();
        cmbTipoDoc.setSelectedIndex(0);
        txtNroDoc.setText("");
        txtRazonSocial.setText("");
        cmbCondicionIva.setSelectedIndex(0);
        txtDomicilio.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
    }

}
