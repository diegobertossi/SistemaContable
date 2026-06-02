package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorClientes;
import com.els.facturacion.modelo.ClienteDTO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

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

public class VentanaSeleccionCliente extends JDialog {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private ControladorClientes controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnMostrarTodos;
    private JButton btnSeleccionar;
    private JButton btnCancelar;
    private ClienteDTO clienteSeleccionado;
    private JPanel statusBar;
    private JLabel lblStatus;

    public VentanaSeleccionCliente(java.awt.Window owner) {
        super(owner, "Seleccionar Cliente", ModalityType.APPLICATION_MODAL);
        controlador = new ControladorClientes();
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
        cargarClientes();
    }

    private void initComponents() {
        setSize(700, 500);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(currentTheme.bgBase);

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(currentTheme.bgSurface);

        JLabel lblTitulo = new JLabel("SELECCIONAR CLIENTE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(currentTheme.brand);

        GridBagConstraints gbc_titulo = new GridBagConstraints();
        gbc_titulo.insets = new Insets(5, 5, 5, 5);
        gbc_titulo.gridx = 0; gbc_titulo.gridy = 0; gbc_titulo.gridwidth = 4;
        panelSuperior.add(lblTitulo, gbc_titulo);

        txtBuscar = new JTextField(20);

        btnBuscar = new JButton("BUSCAR");
        btnBuscar.setFont(FUENTE_BOTON);
        btnBuscar.setForeground(currentTheme.textPrimary);
        btnBuscar.setBackground(currentTheme.btnBg);
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> buscarCliente());

        btnMostrarTodos = new JButton("MOSTRAR TODOS");
        btnMostrarTodos.setFont(FUENTE_BOTON);
        btnMostrarTodos.setForeground(currentTheme.textPrimary);
        btnMostrarTodos.setBackground(currentTheme.btnBg);
        btnMostrarTodos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMostrarTodos.setFocusPainted(false);
        btnMostrarTodos.addActionListener(e -> cargarClientes());

        btnSeleccionar = new JButton("SELECCIONAR");
        btnSeleccionar.setFont(FUENTE_BOTON);
        btnSeleccionar.setForeground(currentTheme.textPrimary);
        btnSeleccionar.setBackground(currentTheme.btnBg);
        btnSeleccionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.addActionListener(e -> seleccionarCliente());

        btnCancelar = new JButton("CANCELAR");
        btnCancelar.setFont(FUENTE_BOTON);
        btnCancelar.setForeground(currentTheme.textPrimary);
        btnCancelar.setBackground(currentTheme.btnBg);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());

        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(5, 5, 5, 5);
        gbc_label.gridwidth = 1; gbc_label.gridy = 1;
        gbc_label.gridx = 0;
        panelSuperior.add(new JLabel("Buscar:"), gbc_label);

        GridBagConstraints gbc_txtBuscar = new GridBagConstraints();
        gbc_txtBuscar.insets = new Insets(5, 5, 5, 5);
        gbc_txtBuscar.gridwidth = 1; gbc_txtBuscar.gridy = 1;
        gbc_txtBuscar.gridx = 1;
        panelSuperior.add(txtBuscar, gbc_txtBuscar);

        GridBagConstraints gbc_btnBuscar = new GridBagConstraints();
        gbc_btnBuscar.insets = new Insets(5, 5, 5, 5);
        gbc_btnBuscar.gridwidth = 1; gbc_btnBuscar.gridy = 1;
        gbc_btnBuscar.gridx = 2;
        panelSuperior.add(btnBuscar, gbc_btnBuscar);

        GridBagConstraints gbc_btnMostrarTodos = new GridBagConstraints();
        gbc_btnMostrarTodos.insets = new Insets(5, 5, 5, 5);
        gbc_btnMostrarTodos.gridwidth = 1; gbc_btnMostrarTodos.gridy = 1;
        gbc_btnMostrarTodos.gridx = 3;
        panelSuperior.add(btnMostrarTodos, gbc_btnMostrarTodos);

        String[] columnas = {"ID", "Documento", "Razon Social", "Condicion IVA", "Domicilio", "Telefono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabla.setRowHeight(22);
        tabla.setShowGrid(true);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) seleccionarCliente();
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(currentTheme.bgSurface);
        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCancelar);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(currentTheme.bgBase);
        southWrapper.add(panelBotones, BorderLayout.CENTER);
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

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = controlador.listarActivos();
        for (ClienteDTO c : lista) {
            modeloTabla.addRow(new Object[]{
                c.getId(),
                c.getTipoDocumento() + ": " + c.getNroDocumento(),
                c.getRazonSocial(),
                c.getCondicionIva(),
                c.getDomicilio(),
                c.getTelefono()
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
                c.getId(),
                c.getTipoDocumento() + ": " + c.getNroDocumento(),
                c.getRazonSocial(), c.getCondicionIva(),
                c.getDomicilio(), c.getTelefono()
            });
        }
    }

    private void seleccionarCliente() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (Integer) modeloTabla.getValueAt(row, 0);
        clienteSeleccionado = controlador.buscarPorId(id);
        dispose();
    }

    public ClienteDTO getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        getContentPane().setBackground(t.bgBase);
        if (btnBuscar != null) {
            btnBuscar.setBackground(t.btnBg);
            btnBuscar.setForeground(t.textPrimary);
        }
        if (btnMostrarTodos != null) {
            btnMostrarTodos.setBackground(t.btnBg);
            btnMostrarTodos.setForeground(t.textPrimary);
        }
        if (btnSeleccionar != null) {
            btnSeleccionar.setBackground(t.btnBg);
            btnSeleccionar.setForeground(t.textPrimary);
        }
        if (btnCancelar != null) {
            btnCancelar.setBackground(t.btnBg);
            btnCancelar.setForeground(t.textPrimary);
        }
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t);
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
        }
        if (txtBuscar != null) {
            txtBuscar.setForeground(t.textPrimary);
            txtBuscar.setBackground(t.bgInput);
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

}
