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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class VentanaSeleccionCliente extends JDialog {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);

    private ControladorClientes controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private ClienteDTO clienteSeleccionado;

    public VentanaSeleccionCliente(java.awt.Window owner) {
        super(owner, "Seleccionar Cliente", ModalityType.APPLICATION_MODAL);
        controlador = new ControladorClientes();
        initComponents();
        cargarClientes();
    }

    private void initComponents() {
        setSize(700, 500);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblTitulo = new JLabel("SELECCIONAR CLIENTE");
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panelSuperior.add(lblTitulo, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1;
        txtBuscar = new JTextField(20);
        JButton btnBuscar = crearBoton("BUSCAR");
        btnBuscar.addActionListener(e -> buscarCliente());
        JButton btnMostrarTodos = crearBoton("MOSTRAR TODOS");
        btnMostrarTodos.addActionListener(e -> cargarClientes());
        JButton btnSeleccionar = crearBoton("SELECCIONAR");
        btnSeleccionar.addActionListener(e -> seleccionarCliente());
        JButton btnCancelar = crearBoton("CANCELAR");
        btnCancelar.addActionListener(e -> dispose());

        gbc.gridx = 0; panelSuperior.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1; panelSuperior.add(txtBuscar, gbc);
        gbc.gridx = 2; panelSuperior.add(btnBuscar, gbc);
        gbc.gridx = 3; panelSuperior.add(btnMostrarTodos, gbc);

        String[] columnas = {"ID", "Documento", "Razon Social", "Condicion IVA", "Domicilio", "Telefono"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Cambria", Font.PLAIN, 11));
        tabla.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) seleccionarCliente();
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.add(btnSeleccionar);
        panelBotones.add(btnCancelar);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
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
