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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("MODULO DE CLIENTES");
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 5;
        panelSuperior.add(lblTitulo, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        txtBuscar = new JTextField(20);
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarCliente(); }
            public void removeUpdate(DocumentEvent e) { buscarCliente(); }
            public void changedUpdate(DocumentEvent e) { buscarCliente(); }
        });
        JButton btnImportar = crearBoton("IMPORTAR DE REPARSOFT");
        btnImportar.addActionListener(e -> importarClientes());

        gbc.gridx = 0; panelSuperior.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1; panelSuperior.add(txtBuscar, gbc);
        gbc.gridx = 2; panelSuperior.add(btnImportar, gbc);
        gbc.gridx = 3; panelSuperior.add(btnImportar, gbc);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(COLOR_FONDO);
        GridBagConstraints fgc = new GridBagConstraints();
        fgc.insets = new Insets(3, 5, 3, 5);
        fgc.fill = GridBagConstraints.HORIZONTAL;

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
        fgc.gridx = 0; fgc.gridy = row;
        panelForm.add(new JLabel("Tipo Doc:"), fgc);
        fgc.gridx = 1; panelForm.add(cmbTipoDoc, fgc);
        fgc.gridx = 2; panelForm.add(new JLabel("Nro Documento:"), fgc);
        fgc.gridx = 3; panelForm.add(txtNroDoc, fgc);

        row++; fgc.gridx = 0; fgc.gridy = row;
        panelForm.add(new JLabel("Razon Social:"), fgc);
        fgc.gridx = 1; fgc.gridwidth = 3; panelForm.add(txtRazonSocial, fgc);

        row++; fgc.gridwidth = 1; fgc.gridx = 0; fgc.gridy = row;
        panelForm.add(new JLabel("Condicion IVA:"), fgc);
        fgc.gridx = 1; fgc.gridwidth = 3; panelForm.add(cmbCondicionIva, fgc);

        row++; fgc.gridwidth = 1; fgc.gridx = 0; fgc.gridy = row;
        panelForm.add(new JLabel("Domicilio:"), fgc);
        fgc.gridx = 1; panelForm.add(txtDomicilio, fgc);
        fgc.gridx = 2; panelForm.add(new JLabel("Telefono:"), fgc);
        fgc.gridx = 3; panelForm.add(txtTelefono, fgc);

        row++; fgc.gridx = 0; fgc.gridy = row;
        panelForm.add(new JLabel("Email:"), fgc);
        fgc.gridx = 1; panelForm.add(txtEmail, fgc);

        JPanel panelBotonesForm = new JPanel();
        panelBotonesForm.setBackground(COLOR_FONDO);
        JButton btnGuardar = crearBoton("GUARDAR CLIENTE");
        btnGuardar.addActionListener(e -> guardarCliente());
        JButton btnNuevo = crearBoton("NUEVO");
        btnNuevo.addActionListener(e -> limpiarFormulario());
        panelBotonesForm.add(btnNuevo);
        panelBotonesForm.add(btnGuardar);

        row++; fgc.gridx = 0; fgc.gridy = row; fgc.gridwidth = 4;
        panelForm.add(panelBotonesForm, fgc);

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

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }
}
