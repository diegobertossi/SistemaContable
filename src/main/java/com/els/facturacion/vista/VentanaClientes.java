package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorClientes;
import com.els.facturacion.modelo.ClienteDTO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class VentanaClientes extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

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
    private JPanel panelSuperior;
    private JLabel lblTitulo;
    private JRadioButton rdParticular;
    private JRadioButton rdEmpresa;
    private ButtonGroup groupTipoPersona;
    private JPanel panelForm;
    private JPanel panelBotonesForm;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JScrollPane scrollTabla;
    private JPanel southWrapper;
    private boolean modoEdicion;
    private boolean guardando;

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
        setSize(900, 600);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(currentTheme.bgSurface);
        GridBagConstraints gbc_titulo = new GridBagConstraints();
        gbc_titulo.insets = new Insets(5, 5, 5, 5);
        gbc_titulo.fill = GridBagConstraints.HORIZONTAL;
        gbc_titulo.gridx = 0; gbc_titulo.gridy = 0; gbc_titulo.gridwidth = 5;

        lblTitulo = new JLabel("MODULO DE CLIENTES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        panelSuperior.add(lblTitulo, gbc_titulo);

        txtBuscar = new JTextField(20);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarCliente(); }
            public void removeUpdate(DocumentEvent e) { buscarCliente(); }
            public void changedUpdate(DocumentEvent e) { buscarCliente(); }
        });

        GridBagConstraints gbc_buscar_label = new GridBagConstraints();
        gbc_buscar_label.insets = new Insets(5, 5, 5, 5);
        gbc_buscar_label.fill = GridBagConstraints.HORIZONTAL;
        gbc_buscar_label.gridwidth = 1; gbc_buscar_label.gridx = 0; gbc_buscar_label.gridy = 1;

        GridBagConstraints gbc_txtBuscar = new GridBagConstraints();
        gbc_txtBuscar.insets = new Insets(5, 5, 5, 5);
        gbc_txtBuscar.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtBuscar.gridwidth = 1; gbc_txtBuscar.gridx = 1; gbc_txtBuscar.gridy = 1;

        panelSuperior.add(new JLabel("Buscar:"), gbc_buscar_label);
        panelSuperior.add(txtBuscar, gbc_txtBuscar);

        panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(currentTheme.bgSurface);

        cmbTipoDoc = new JComboBox<>(new String[]{"CUIT", "DNI"});
        cmbCondicionIva = new JComboBox<>(new String[]{
            "", "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
            "Responsable Monotributo", "Proveedor del Exterior", "Cliente del Exterior",
            "IVA Liberado - Ley 19.640", "Monotributista Social", "IVA No Alcanzado"
        });

        txtNroDoc = new JTextField(15);
        txtRazonSocial = new JTextField(25);
        txtDomicilio = new JTextField(25);
        txtTelefono = new JTextField(15);
        txtEmail = new JTextField(20);

        int row = 0;

        rdParticular = new JRadioButton("Particular");
        rdEmpresa = new JRadioButton("Empresa");
        rdEmpresa.setSelected(true);
        groupTipoPersona = new ButtonGroup();
        groupTipoPersona.add(rdParticular);
        groupTipoPersona.add(rdEmpresa);

        rdParticular.addActionListener(e -> {
            cmbTipoDoc.setSelectedItem("DNI");
            cmbCondicionIva.setSelectedItem("Consumidor Final");
        });
        rdEmpresa.addActionListener(e -> {
            cmbTipoDoc.setSelectedItem("CUIT");
            cmbCondicionIva.setSelectedIndex(0);
        });

        GridBagConstraints fgc_tipoLabel = new GridBagConstraints();
        fgc_tipoLabel.insets = new Insets(3, 5, 3, 5);
        fgc_tipoLabel.fill = GridBagConstraints.HORIZONTAL;
        fgc_tipoLabel.gridx = 0; fgc_tipoLabel.gridy = row;
        panelForm.add(new JLabel("Tipo:"), fgc_tipoLabel);

        GridBagConstraints fgc_rdParticular = new GridBagConstraints();
        fgc_rdParticular.insets = new Insets(3, 5, 3, 5);
        fgc_rdParticular.fill = GridBagConstraints.HORIZONTAL;
        fgc_rdParticular.gridx = 1; fgc_rdParticular.gridy = row;
        panelForm.add(rdParticular, fgc_rdParticular);

        GridBagConstraints fgc_rdEmpresa = new GridBagConstraints();
        fgc_rdEmpresa.insets = new Insets(3, 5, 3, 5);
        fgc_rdEmpresa.fill = GridBagConstraints.HORIZONTAL;
        fgc_rdEmpresa.gridx = 2; fgc_rdEmpresa.gridy = row;
        panelForm.add(rdEmpresa, fgc_rdEmpresa);

        row++;

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

        panelBotonesForm = new JPanel();
        panelBotonesForm.setBackground(currentTheme.bgSurface);
        btnGuardar = new JButton("GUARDAR CLIENTE");
        btnGuardar.setFont(FUENTE_BOTON);
        btnGuardar.setForeground(currentTheme.textPrimary);
        btnGuardar.setBackground(currentTheme.btnBg);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setFocusPainted(false);
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
        btnNuevo.addActionListener(e -> limpiarFormulario());
        panelBotonesForm.add(btnNuevo);
        panelBotonesForm.add(btnGuardar);
        btnEditar = new JButton("EDITAR");
        btnEditar.setFont(FUENTE_BOTON);
        btnEditar.setForeground(currentTheme.brand);
        btnEditar.setBackground(currentTheme.bgSurface);
        btnEditar.setBorder(BorderFactory.createLineBorder(currentTheme.brand));
        btnEditar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEditar.setFocusPainted(false);
        btnEditar.addActionListener(e -> habilitarEdicion(true));
        panelBotonesForm.add(btnEditar);

        row++;

        GridBagConstraints fgc_panelBotones = new GridBagConstraints();
        fgc_panelBotones.insets = new Insets(3, 5, 3, 5);
        fgc_panelBotones.fill = GridBagConstraints.HORIZONTAL;
        fgc_panelBotones.gridx = 0; fgc_panelBotones.gridy = row; fgc_panelBotones.gridwidth = 4;
        panelForm.add(panelBotonesForm, fgc_panelBotones);

        String[] columnas = {"ID", "Tipo", "Razon Social", "Tipo Doc", "Nro Doc", "Condicion IVA", "Domicilio", "Telefono", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarClienteSeleccionado();
        });

        add(panelSuperior, BorderLayout.NORTH);
        scrollTabla = new JScrollPane(tabla);
        add(scrollTabla, BorderLayout.CENTER);
        southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(currentTheme.bgBase);
        southWrapper.add(panelForm, BorderLayout.CENTER);
        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        southWrapper.add(statusBar, BorderLayout.SOUTH);
        add(southWrapper, BorderLayout.SOUTH);
        setFormEditable(false);
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
        txtRazonSocial.setText(modeloTabla.getValueAt(row, 2) != null ? modeloTabla.getValueAt(row, 2).toString() : "");
        cmbTipoDoc.setSelectedItem(modeloTabla.getValueAt(row, 3));
        txtNroDoc.setText(modeloTabla.getValueAt(row, 4) != null ? modeloTabla.getValueAt(row, 4).toString() : "");
        cmbCondicionIva.setSelectedItem(modeloTabla.getValueAt(row, 5) != null ? modeloTabla.getValueAt(row, 5).toString() : "Consumidor Final");
        txtDomicilio.setText(modeloTabla.getValueAt(row, 6) != null ? modeloTabla.getValueAt(row, 6).toString() : "");
        txtTelefono.setText(modeloTabla.getValueAt(row, 7) != null ? modeloTabla.getValueAt(row, 7).toString() : "");
        txtEmail.setText(modeloTabla.getValueAt(row, 8) != null ? modeloTabla.getValueAt(row, 8).toString() : "");
        modoEdicion = false;
        setFormEditable(false);
    }

    private void guardarCliente() {
        boolean esParticular = rdParticular.isSelected();
        String razonSocial = txtRazonSocial.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String nroDoc = txtNroDoc.getText().trim();
        String condicionIva = (String) cmbCondicionIva.getSelectedItem();
        String domicilio = txtDomicilio.getText().trim();

        if (esParticular) {
            if (razonSocial.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nombre es obligatorio para clientes Particular",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (telefono.isEmpty()) {
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
            if (domicilio.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Domicilio es obligatorio para Empresa",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (telefono.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tel\u00e9fono es obligatorio para Empresa",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
        cli.setTelefono(telefono);
        cli.setEmail(txtEmail.getText().trim());

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
        txtNroDoc.setEnabled(editable);
        txtRazonSocial.setEnabled(editable);
        txtDomicilio.setEnabled(editable);
        txtTelefono.setEnabled(editable);
        txtEmail.setEnabled(editable);
        cmbTipoDoc.setEnabled(editable);
        cmbCondicionIva.setEnabled(editable);
        rdParticular.setEnabled(editable);
        rdEmpresa.setEnabled(editable);
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
        cmbTipoDoc.setSelectedIndex(0);
        txtNroDoc.setText("");
        txtRazonSocial.setText("");
        cmbCondicionIva.setSelectedIndex(0);
        txtDomicilio.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
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
            rdParticular.setForeground(t.textPrimary);
            rdParticular.setBackground(t.bgSurface);
        }
        if (rdEmpresa != null) {
            rdEmpresa.setForeground(t.textPrimary);
            rdEmpresa.setBackground(t.bgSurface);
        }
        if (panelForm != null) {
            panelForm.setBackground(t.bgSurface);
            themeLabels(panelForm, t);
        }
        if (panelBotonesForm != null) panelBotonesForm.setBackground(t.bgSurface);
        if (cmbTipoDoc != null) {
            cmbTipoDoc.setForeground(t.textPrimary);
            cmbTipoDoc.setBackground(t.bgElevated);
        }
        if (cmbCondicionIva != null) {
            cmbCondicionIva.setForeground(t.textPrimary);
            cmbCondicionIva.setBackground(t.bgElevated);
        }
        if (txtNroDoc != null) {
            txtNroDoc.setForeground(t.textPrimary);
            txtNroDoc.setBackground(t.bgInput);
        }
        if (txtRazonSocial != null) {
            txtRazonSocial.setForeground(t.textPrimary);
            txtRazonSocial.setBackground(t.bgInput);
        }
        if (txtDomicilio != null) {
            txtDomicilio.setForeground(t.textPrimary);
            txtDomicilio.setBackground(t.bgInput);
        }
        if (txtTelefono != null) {
            txtTelefono.setForeground(t.textPrimary);
            txtTelefono.setBackground(t.bgInput);
        }
        if (txtEmail != null) {
            txtEmail.setForeground(t.textPrimary);
            txtEmail.setBackground(t.bgInput);
        }
        if (btnGuardar != null) { btnGuardar.setBackground(t.btnBg); btnGuardar.setForeground(t.textPrimary); }
        if (btnNuevo != null) { btnNuevo.setBackground(t.btnBg); btnNuevo.setForeground(t.textPrimary); }
        if (btnEditar != null) { btnEditar.setForeground(t.brand); btnEditar.setBackground(t.bgSurface); btnEditar.setBorder(BorderFactory.createLineBorder(t.brand)); }
        if (southWrapper != null) southWrapper.setBackground(t.bgBase);
        if (scrollTabla != null) scrollTabla.getViewport().setBackground(t.bgBase);
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t);
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
            }
            if (comp instanceof java.awt.Container) {
                themeLabels((java.awt.Container) comp, t);
            }
        }
    }

}
