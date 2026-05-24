package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorRemitos;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.modelo.RemitoItemDTO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentanaRemitos extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ControladorRemitos controlador;
    private JTable tablaRemitos;
    private DefaultTableModel modeloTablaRemitos;
    private JTable tablaItems;
    private DefaultTableModel modeloTablaItems;
    private JTextField txtNumero, txtFechaEmision, txtFechaEntrega;
    private JTextField txtCuitReceptor, txtRazonSocial, txtDomicilio;
    private JTextArea txtObservaciones;
    private JComboBox<String> cmbEstado;
    private JTextField txtCodigo, txtDescripcion, txtCantidad;

    public VentanaRemitos() {
        controlador = new ControladorRemitos();
        initComponents();
        cargarRemitos();
    }

    private void initComponents() {
        setTitle("Gestion de Remitos");
        setSize(1000, 700);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("REMITOS");
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        String[] colRemitos = {"ID", "Numero", "Fecha Emision", "Receptor", "Estado"};
        modeloTablaRemitos = new DefaultTableModel(colRemitos, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaRemitos = new JTable(modeloTablaRemitos);
        tablaRemitos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarRemitoSeleccionado();
        });

        JButton btnNuevo = crearBoton("NUEVO REMITO");
        btnNuevo.addActionListener(e -> nuevoRemito());

        JPanel panelListaBotones = new JPanel();
        panelListaBotones.setBackground(COLOR_FONDO);
        panelListaBotones.add(btnNuevo);

        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        panelIzquierdo.add(new JScrollPane(tablaRemitos), BorderLayout.CENTER);
        panelIzquierdo.add(panelListaBotones, BorderLayout.SOUTH);

        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNumero = new JTextField(20);
        txtNumero.setEditable(false);
        txtFechaEmision = new JTextField(10);
        txtFechaEmision.setText(LocalDate.now().format(FMT));
        txtFechaEntrega = new JTextField(10);
        cmbEstado = new JComboBox<>(new String[]{"pendiente", "entregado", "anulado"});

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panelDerecho.add(new JLabel("Numero Remito:"), gbc);
        gbc.gridx = 1; panelDerecho.add(txtNumero, gbc);
        gbc.gridx = 2; panelDerecho.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 3; panelDerecho.add(cmbEstado, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panelDerecho.add(new JLabel("Fecha Emision:"), gbc);
        gbc.gridx = 1; panelDerecho.add(txtFechaEmision, gbc);
        gbc.gridx = 2; panelDerecho.add(new JLabel("Fecha Entrega:"), gbc);
        gbc.gridx = 3; panelDerecho.add(txtFechaEntrega, gbc);

        row++; gbc.gridx = 0; gbc.gridy = row;
        panelDerecho.add(new JLabel("CUIT Receptor:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; panelDerecho.add(txtCuitReceptor = new JTextField(30), gbc);

        row++; gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = row;
        panelDerecho.add(new JLabel("Razon Social:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; panelDerecho.add(txtRazonSocial = new JTextField(30), gbc);

        row++; gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = row;
        panelDerecho.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; panelDerecho.add(txtDomicilio = new JTextField(30), gbc);

        String[] colItems = {"Codigo", "Descripcion", "Cantidad", "U. Medida"};
        modeloTablaItems = new DefaultTableModel(colItems, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaItems = new JTable(modeloTablaItems);
        JScrollPane scrollItems = new JScrollPane(tablaItems);

        JPanel panelItemForm = new JPanel();
        panelItemForm.setBackground(COLOR_FONDO);
        txtCodigo = new JTextField(8);
        txtDescripcion = new JTextField(20);
        txtCantidad = new JTextField(5);
        txtCantidad.setText("1");
        JButton btnAgregarItem = crearBoton("+ AGREGAR");
        btnAgregarItem.addActionListener(e -> agregarItem());
        JButton btnEliminarItem = crearBoton("- ELIMINAR");
        btnEliminarItem.addActionListener(e -> eliminarItem());
        panelItemForm.add(new JLabel("Codigo:"));
        panelItemForm.add(txtCodigo);
        panelItemForm.add(new JLabel("Desc:"));
        panelItemForm.add(txtDescripcion);
        panelItemForm.add(new JLabel("Cant:"));
        panelItemForm.add(txtCantidad);
        panelItemForm.add(btnAgregarItem);
        panelItemForm.add(btnEliminarItem);

        row++; gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panelDerecho.add(scrollItems, gbc);

        row++; gbc.gridy = row;
        panelDerecho.add(panelItemForm, gbc);

        row++; gbc.gridy = row;
        JLabel lblObs = new JLabel("Observaciones:");
        txtObservaciones = new JTextArea(3, 30);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        panelDerecho.add(lblObs, gbc);
        row++; gbc.gridy = row;
        panelDerecho.add(scrollObs, gbc);

        row++; gbc.gridy = row;
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(COLOR_FONDO);
        JButton btnGuardar = crearBoton("GUARDAR REMITO");
        btnGuardar.addActionListener(e -> guardarRemito());
        JButton btnCambiarEstado = crearBoton("CAMBIAR ESTADO");
        btnCambiarEstado.addActionListener(e -> cambiarEstado());
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCambiarEstado);

        row++; gbc.gridy = row;
        panelDerecho.add(panelBotones, gbc);

        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);
    }

    private void cargarRemitos() {
        modeloTablaRemitos.setRowCount(0);
        List<RemitoDTO> lista = controlador.listarTodos();
        for (RemitoDTO r : lista) {
            modeloTablaRemitos.addRow(new Object[]{
                r.getId(), r.getNumeroRemito(),
                r.getFechaEmision() != null ? r.getFechaEmision().format(FMT) : "",
                r.getRazonSocialReceptor(), r.getEstado()
            });
        }
    }

    private void cargarRemitoSeleccionado() {
        int row = tablaRemitos.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) modeloTablaRemitos.getValueAt(row, 0);
        RemitoDTO r = controlador.buscarPorId(id);
        if (r == null) return;

        txtNumero.setText(r.getNumeroRemito());
        txtFechaEmision.setText(r.getFechaEmision() != null ? r.getFechaEmision().format(FMT) : "");
        txtFechaEntrega.setText(r.getFechaEntrega() != null ? r.getFechaEntrega().format(FMT) : "");
        txtCuitReceptor.setText(r.getCuitReceptor());
        txtRazonSocial.setText(r.getRazonSocialReceptor());
        txtDomicilio.setText(r.getDomicilioReceptor());
        cmbEstado.setSelectedItem(r.getEstado());
        txtObservaciones.setText(r.getObservaciones());
        modeloTablaItems.setRowCount(0);
    }

    private void nuevoRemito() {
        txtNumero.setText(controlador.generarNumeroRemito());
        txtFechaEmision.setText(LocalDate.now().format(FMT));
        txtFechaEntrega.setText("");
        txtCuitReceptor.setText("");
        txtRazonSocial.setText("");
        txtDomicilio.setText("");
        cmbEstado.setSelectedItem("pendiente");
        txtObservaciones.setText("");
        modeloTablaItems.setRowCount(0);

        CuitConfigDTO cuit = controlador.getCuitActivo();
        if (cuit != null) {
            txtRazonSocial.setText(cuit.getRazonSocial());
        }
    }

    private void agregarItem() {
        String desc = txtDescripcion.getText().trim();
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese descripcion del item", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modeloTablaItems.addRow(new Object[]{
            txtCodigo.getText().trim(), desc, txtCantidad.getText().trim(), "Unidad"
        });
        txtCodigo.setText("");
        txtDescripcion.setText("");
        txtCantidad.setText("1");
    }

    private void eliminarItem() {
        int row = tablaItems.getSelectedRow();
        if (row >= 0) modeloTablaItems.removeRow(row);
    }

    private void guardarRemito() {
        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la razon social del receptor", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (modeloTablaItems.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un item", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RemitoDTO remito = new RemitoDTO();
        int row = tablaRemitos.getSelectedRow();
        if (row >= 0) remito.setId((Integer) modeloTablaRemitos.getValueAt(row, 0));
        if (!txtNumero.getText().isEmpty()) remito.setNumeroRemito(txtNumero.getText());
        remito.setFechaEmision(LocalDate.parse(txtFechaEmision.getText(), FMT));
        if (!txtFechaEntrega.getText().isEmpty()) {
            remito.setFechaEntrega(LocalDate.parse(txtFechaEntrega.getText(), FMT));
        }
        remito.setCuitReceptor(txtCuitReceptor.getText().trim());
        remito.setRazonSocialReceptor(txtRazonSocial.getText().trim());
        remito.setDomicilioReceptor(txtDomicilio.getText().trim());
        remito.setEstado((String) cmbEstado.getSelectedItem());
        remito.setObservaciones(txtObservaciones.getText().trim());

        CuitConfigDTO cuit = controlador.getCuitActivo();
        if (cuit != null) {
            remito.setCuitEmisor(cuit.getCuit());
            remito.setRazonSocialEmisor(cuit.getRazonSocial());
        }

        List<RemitoItemDTO> items = new ArrayList<>();
        for (int i = 0; i < modeloTablaItems.getRowCount(); i++) {
            RemitoItemDTO item = new RemitoItemDTO();
            item.setCodigo(modeloTablaItems.getValueAt(i, 0) != null ? modeloTablaItems.getValueAt(i, 0).toString() : "");
            item.setDescripcion(modeloTablaItems.getValueAt(i, 1).toString());
            item.setCantidad(new BigDecimal(modeloTablaItems.getValueAt(i, 2).toString().replace(",", ".")));
            item.setUnidadMedida(modeloTablaItems.getValueAt(i, 3) != null ? modeloTablaItems.getValueAt(i, 3).toString() : "Unidad");
            items.add(item);
        }
        remito.setItems(items);

        int id = controlador.guardarRemito(remito);
        if (id > 0) {
            JOptionPane.showMessageDialog(this, "Remito guardado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarRemitos();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar remito", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstado() {
        int row = tablaRemitos.getSelectedRow();
        if (row < 0) return;
        int id = (Integer) modeloTablaRemitos.getValueAt(row, 0);
        String estado = (String) cmbEstado.getSelectedItem();
        if (controlador.actualizarEstado(id, estado)) {
            JOptionPane.showMessageDialog(this, "Estado actualizado", "Exito", JOptionPane.INFORMATION_MESSAGE);
            cargarRemitos();
        }
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
