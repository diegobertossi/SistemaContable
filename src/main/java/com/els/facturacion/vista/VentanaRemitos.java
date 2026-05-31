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
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VentanaRemitos extends javax.swing.JFrame {

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
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        JPanel panelIzquierdo = new JPanel(new BorderLayout(5, 5));
        panelIzquierdo.setBackground(TemaFacturaSoft.BG_APP);

        JLabel lblTitulo = new JLabel("REMITOS");
        lblTitulo.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(16f));
        lblTitulo.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        lblTitulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        String[] colRemitos = {"ID", "Numero", "Fecha Emision", "Receptor", "Estado"};
        modeloTablaRemitos = new DefaultTableModel(colRemitos, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaRemitos = new JTable(modeloTablaRemitos);
        TemaFacturaSoft.aplicarEstiloTabla(tablaRemitos);
        tablaRemitos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarRemitoSeleccionado();
        });

        JButton btnNuevo = new JButton("NUEVO REMITO");
        btnNuevo.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnNuevo.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnNuevo.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevo.setFocusPainted(false);
        btnNuevo.addActionListener(e -> nuevoRemito());

        JPanel panelListaBotones = new JPanel();
        panelListaBotones.setBackground(TemaFacturaSoft.BG_APP);
        panelListaBotones.add(btnNuevo);

        panelIzquierdo.add(lblTitulo, BorderLayout.NORTH);
        panelIzquierdo.add(new JScrollPane(tablaRemitos), BorderLayout.CENTER);
        panelIzquierdo.add(panelListaBotones, BorderLayout.SOUTH);

        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(TemaFacturaSoft.BG_APP);
        txtNumero = new JTextField(20);
        txtNumero.setEditable(false);
        txtFechaEmision = new JTextField(10);
        txtFechaEmision.setText(LocalDate.now().format(FMT));
        txtFechaEntrega = new JTextField(10);
        cmbEstado = new JComboBox<>(new String[]{"pendiente", "entregado", "anulado"});

        int row = 0;

        GridBagConstraints gbc_lblNumero = new GridBagConstraints();
        gbc_lblNumero.insets = new Insets(3, 5, 3, 5);
        gbc_lblNumero.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblNumero.gridx = 0; gbc_lblNumero.gridy = row;
        panelDerecho.add(new JLabel("Numero Remito:"), gbc_lblNumero);

        GridBagConstraints gbc_txtNumero = new GridBagConstraints();
        gbc_txtNumero.insets = new Insets(3, 5, 3, 5);
        gbc_txtNumero.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtNumero.gridx = 1; gbc_txtNumero.gridy = row;
        panelDerecho.add(txtNumero, gbc_txtNumero);

        GridBagConstraints gbc_lblEstado = new GridBagConstraints();
        gbc_lblEstado.insets = new Insets(3, 5, 3, 5);
        gbc_lblEstado.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblEstado.gridx = 2; gbc_lblEstado.gridy = row;
        panelDerecho.add(new JLabel("Estado:"), gbc_lblEstado);

        GridBagConstraints gbc_cmbEstado = new GridBagConstraints();
        gbc_cmbEstado.insets = new Insets(3, 5, 3, 5);
        gbc_cmbEstado.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbEstado.gridx = 3; gbc_cmbEstado.gridy = row;
        panelDerecho.add(cmbEstado, gbc_cmbEstado);

        row++;

        GridBagConstraints gbc_lblFechaEmision = new GridBagConstraints();
        gbc_lblFechaEmision.insets = new Insets(3, 5, 3, 5);
        gbc_lblFechaEmision.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblFechaEmision.gridx = 0; gbc_lblFechaEmision.gridy = row;
        panelDerecho.add(new JLabel("Fecha Emision:"), gbc_lblFechaEmision);

        GridBagConstraints gbc_txtFechaEmision = new GridBagConstraints();
        gbc_txtFechaEmision.insets = new Insets(3, 5, 3, 5);
        gbc_txtFechaEmision.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtFechaEmision.gridx = 1; gbc_txtFechaEmision.gridy = row;
        panelDerecho.add(txtFechaEmision, gbc_txtFechaEmision);

        GridBagConstraints gbc_lblFechaEntrega = new GridBagConstraints();
        gbc_lblFechaEntrega.insets = new Insets(3, 5, 3, 5);
        gbc_lblFechaEntrega.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblFechaEntrega.gridx = 2; gbc_lblFechaEntrega.gridy = row;
        panelDerecho.add(new JLabel("Fecha Entrega:"), gbc_lblFechaEntrega);

        GridBagConstraints gbc_txtFechaEntrega = new GridBagConstraints();
        gbc_txtFechaEntrega.insets = new Insets(3, 5, 3, 5);
        gbc_txtFechaEntrega.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtFechaEntrega.gridx = 3; gbc_txtFechaEntrega.gridy = row;
        panelDerecho.add(txtFechaEntrega, gbc_txtFechaEntrega);

        row++;

        GridBagConstraints gbc_lblCuit = new GridBagConstraints();
        gbc_lblCuit.insets = new Insets(3, 5, 3, 5);
        gbc_lblCuit.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblCuit.gridx = 0; gbc_lblCuit.gridy = row;
        panelDerecho.add(new JLabel("CUIT Receptor:"), gbc_lblCuit);

        GridBagConstraints gbc_txtCuit = new GridBagConstraints();
        gbc_txtCuit.insets = new Insets(3, 5, 3, 5);
        gbc_txtCuit.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtCuit.gridx = 1; gbc_txtCuit.gridwidth = 3; gbc_txtCuit.gridy = row;
        panelDerecho.add(txtCuitReceptor = new JTextField(30), gbc_txtCuit);

        row++;

        GridBagConstraints gbc_lblRazonSocial = new GridBagConstraints();
        gbc_lblRazonSocial.insets = new Insets(3, 5, 3, 5);
        gbc_lblRazonSocial.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblRazonSocial.gridx = 0; gbc_lblRazonSocial.gridy = row;
        panelDerecho.add(new JLabel("Razon Social:"), gbc_lblRazonSocial);

        GridBagConstraints gbc_txtRazonSocial = new GridBagConstraints();
        gbc_txtRazonSocial.insets = new Insets(3, 5, 3, 5);
        gbc_txtRazonSocial.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtRazonSocial.gridx = 1; gbc_txtRazonSocial.gridwidth = 3; gbc_txtRazonSocial.gridy = row;
        panelDerecho.add(txtRazonSocial = new JTextField(30), gbc_txtRazonSocial);

        row++;

        GridBagConstraints gbc_lblDomicilio = new GridBagConstraints();
        gbc_lblDomicilio.insets = new Insets(3, 5, 3, 5);
        gbc_lblDomicilio.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblDomicilio.gridx = 0; gbc_lblDomicilio.gridy = row;
        panelDerecho.add(new JLabel("Domicilio:"), gbc_lblDomicilio);

        GridBagConstraints gbc_txtDomicilio = new GridBagConstraints();
        gbc_txtDomicilio.insets = new Insets(3, 5, 3, 5);
        gbc_txtDomicilio.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtDomicilio.gridx = 1; gbc_txtDomicilio.gridwidth = 3; gbc_txtDomicilio.gridy = row;
        panelDerecho.add(txtDomicilio = new JTextField(30), gbc_txtDomicilio);

        String[] colItems = {"Codigo", "Descripcion", "Cantidad", "U. Medida"};
        modeloTablaItems = new DefaultTableModel(colItems, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaItems = new JTable(modeloTablaItems);
        TemaFacturaSoft.aplicarEstiloTabla(tablaItems);
        JScrollPane scrollItems = new JScrollPane(tablaItems);

        JPanel panelItemForm = new JPanel();
        panelItemForm.setBackground(TemaFacturaSoft.BG_APP);
        txtCodigo = new JTextField(8);
        txtDescripcion = new JTextField(20);
        txtCantidad = new JTextField(5);
        txtCantidad.setText("1");
        JButton btnAgregarItem = new JButton("+ AGREGAR");
        btnAgregarItem.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnAgregarItem.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnAgregarItem.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnAgregarItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregarItem.setFocusPainted(false);
        btnAgregarItem.addActionListener(e -> agregarItem());
        JButton btnEliminarItem = new JButton("- ELIMINAR");
        btnEliminarItem.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnEliminarItem.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnEliminarItem.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnEliminarItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminarItem.setFocusPainted(false);
        btnEliminarItem.addActionListener(e -> eliminarItem());
        panelItemForm.add(new JLabel("Codigo:"));
        panelItemForm.add(txtCodigo);
        panelItemForm.add(new JLabel("Desc:"));
        panelItemForm.add(txtDescripcion);
        panelItemForm.add(new JLabel("Cant:"));
        panelItemForm.add(txtCantidad);
        panelItemForm.add(btnAgregarItem);
        panelItemForm.add(btnEliminarItem);

        row++;

        GridBagConstraints gbc_scrollItems = new GridBagConstraints();
        gbc_scrollItems.insets = new Insets(3, 5, 3, 5);
        gbc_scrollItems.fill = GridBagConstraints.HORIZONTAL;
        gbc_scrollItems.gridx = 0; gbc_scrollItems.gridy = row; gbc_scrollItems.gridwidth = 4;
        panelDerecho.add(scrollItems, gbc_scrollItems);

        row++;

        GridBagConstraints gbc_itemForm = new GridBagConstraints();
        gbc_itemForm.insets = new Insets(3, 5, 3, 5);
        gbc_itemForm.fill = GridBagConstraints.HORIZONTAL;
        gbc_itemForm.gridx = 0; gbc_itemForm.gridy = row; gbc_itemForm.gridwidth = 4;
        panelDerecho.add(panelItemForm, gbc_itemForm);

        row++;

        GridBagConstraints gbc_lblObs = new GridBagConstraints();
        gbc_lblObs.insets = new Insets(3, 5, 3, 5);
        gbc_lblObs.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblObs.gridx = 0; gbc_lblObs.gridy = row; gbc_lblObs.gridwidth = 4;
        JLabel lblObs = new JLabel("Observaciones:");
        txtObservaciones = new JTextArea(3, 30);
        JScrollPane scrollObs = new JScrollPane(txtObservaciones);
        panelDerecho.add(lblObs, gbc_lblObs);

        row++;

        GridBagConstraints gbc_scrollObs = new GridBagConstraints();
        gbc_scrollObs.insets = new Insets(3, 5, 3, 5);
        gbc_scrollObs.fill = GridBagConstraints.HORIZONTAL;
        gbc_scrollObs.gridx = 0; gbc_scrollObs.gridy = row; gbc_scrollObs.gridwidth = 4;
        panelDerecho.add(scrollObs, gbc_scrollObs);

        row++;

        GridBagConstraints gbc_panelBotones = new GridBagConstraints();
        gbc_panelBotones.insets = new Insets(3, 5, 3, 5);
        gbc_panelBotones.fill = GridBagConstraints.HORIZONTAL;
        gbc_panelBotones.gridx = 0; gbc_panelBotones.gridy = row; gbc_panelBotones.gridwidth = 4;
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(TemaFacturaSoft.BG_APP);
        JButton btnGuardar = new JButton("GUARDAR REMITO");
        btnGuardar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnGuardar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnGuardar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnGuardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(e -> guardarRemito());
        JButton btnCambiarEstado = new JButton("CAMBIAR ESTADO");
        btnCambiarEstado.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnCambiarEstado.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnCambiarEstado.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnCambiarEstado.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCambiarEstado.setFocusPainted(false);
        btnCambiarEstado.addActionListener(e -> cambiarEstado());
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCambiarEstado);
        panelDerecho.add(panelBotones, gbc_panelBotones);

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


}
