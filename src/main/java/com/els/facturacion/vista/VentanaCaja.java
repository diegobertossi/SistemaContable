package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorCaja;
import com.els.facturacion.modelo.CajaMovimientoDTO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaCaja extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_TITULO = new Font("Cambria", Font.BOLD, 14);

    private ControladorCaja controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtDescripcion;
    private JTextField txtMonto;
    private JComboBox<String> cmbTipo;
    private JComboBox<Integer> cmbAnio;
    private JLabel lblSaldo;
    private DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VentanaCaja() {
        controlador = new ControladorCaja();
        initComponents();
        cargarMovimientosPorAnio();
        actualizarSaldo();
    }

    private void initComponents() {
        setTitle("Caja - Movimiento de Fondos");
        setSize(850, 520);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_FONDO);

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("CONTROL DE CAJA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBackground(COLOR_FONDO);

        lblSaldo = new JLabel("Saldo: $0.00", SwingConstants.RIGHT);
        lblSaldo.setFont(FUENTE_TITULO);
        lblSaldo.setForeground(COLOR_TITULO);
        lblSaldo.setBackground(COLOR_FONDO);

        JPanel panelAnio = new JPanel();
        panelAnio.setBackground(COLOR_FONDO);
        
        JLabel lblFiltrarAnio = new JLabel("Año:");
        lblFiltrarAnio.setFont(FUENTE_BOTON);
        lblFiltrarAnio.setForeground(COLOR_TEXTO);
        
        cmbAnio = new JComboBox<>();
        cmbAnio.setFont(FUENTE_BOTON);
        cmbAnio.addItem(2024);
        cmbAnio.addItem(2025);
        cmbAnio.addItem(2026);
        cmbAnio.setSelectedItem(2026);
        cmbAnio.addActionListener(e -> {
            cargarMovimientosPorAnio();
            actualizarSaldo();
        });

        JButton btnNuevoAnio = crearBoton("NUEVO AÑO");
        btnNuevoAnio.addActionListener(e -> btnNuevoAnioAction());

        panelAnio.add(lblFiltrarAnio);
        panelAnio.add(cmbAnio);
        panelAnio.add(btnNuevoAnio);

        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        panelTitulo.add(lblSaldo, BorderLayout.EAST);

        panelSuperior.add(panelTitulo, BorderLayout.NORTH);
        panelSuperior.add(panelAnio, BorderLayout.SOUTH);

        String[] columnas = {"ID", "Fecha", "Tipo", "Descripcion", "Monto"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Cambria", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(tabla);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(FUENTE_BOTON);
        lblTipo.setForeground(COLOR_TEXTO);

        JLabel lblDesc = new JLabel("Descripcion:");
        lblDesc.setFont(FUENTE_BOTON);
        lblDesc.setForeground(COLOR_TEXTO);

        JLabel lblMonto = new JLabel("Monto:");
        lblMonto.setFont(FUENTE_BOTON);
        lblMonto.setForeground(COLOR_TEXTO);

        cmbTipo = new JComboBox<>(new String[]{"cobro", "pago"});
        cmbTipo.setFont(FUENTE_BOTON);
        txtDescripcion = new JTextField(25);
        txtDescripcion.setFont(new Font("Cambria", Font.PLAIN, 11));
        txtMonto = new JTextField(12);
        txtMonto.setFont(new Font("Cambria", Font.PLAIN, 11));

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(lblTipo, gbc);
        gbc.gridx = 1;
        panelFormulario.add(cmbTipo, gbc);

        gbc.gridx = 2;
        panelFormulario.add(lblDesc, gbc);
        gbc.gridx = 3;
        panelFormulario.add(txtDescripcion, gbc);

        gbc.gridx = 4;
        panelFormulario.add(lblMonto, gbc);
        gbc.gridx = 5;
        panelFormulario.add(txtMonto, gbc);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(COLOR_FONDO);

        JButton btnAgregar = crearBoton("AGREGAR");
        btnAgregar.addActionListener(e -> btnAgregarAction());

        JButton btnEliminar = crearBoton("ELIMINAR");
        btnEliminar.addActionListener(e -> btnEliminarAction());

        JButton btnActualizar = crearBoton("ACTUALIZAR");
        btnActualizar.addActionListener(e -> {
            cargarMovimientos();
            actualizarSaldo();
        });

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelFormulario, BorderLayout.SOUTH);
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

    private void cargarMovimientos() {
        modeloTabla.setRowCount(0);
        List<CajaMovimientoDTO> lista = controlador.listarMovimientos();
        for (CajaMovimientoDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getId(),
                dto.getFecha() != null ? dto.getFecha().format(fechaFormatter) : "",
                dto.getTipo(),
                dto.getDescripcion(),
                dto.getMonto() != null ? "$" + dto.getMonto().toString() : "$0.00"
            });
        }
    }

    private void btnAgregarAction() {
        try {
            String tipo = (String) cmbTipo.getSelectedItem();
            String descripcion = txtDescripcion.getText().trim();
            BigDecimal monto = new BigDecimal(txtMonto.getText().trim());

            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese descripcion", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id = controlador.registrarMovimiento(LocalDate.now(), tipo, descripcion, monto);

            if (id > 0) {
                JOptionPane.showMessageDialog(this, "Movimiento registrado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                txtDescripcion.setText("");
                txtMonto.setText("");
                cargarMovimientos();
                actualizarSaldo();
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto invalido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnEliminarAction() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar este movimiento?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(selectedRow, 0);
            if (controlador.eliminarMovimiento(id)) {
                JOptionPane.showMessageDialog(this, "Movimiento eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarMovimientos();
                actualizarSaldo();
            }
        }
    }

    private void actualizarSaldo() {
        BigDecimal saldo = controlador.getSaldoCaja();
        lblSaldo.setText("Saldo: $" + saldo.toString());
    }

    private void cargarMovimientosPorAnio() {
        modeloTabla.setRowCount(0);
        Integer anio = (Integer) cmbAnio.getSelectedItem();
        if (anio == null) return;

        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        
        List<CajaMovimientoDTO> lista = controlador.listarMovimientos(desde, hasta);
        for (CajaMovimientoDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getId(),
                dto.getFecha() != null ? dto.getFecha().format(fechaFormatter) : "",
                dto.getTipo(),
                dto.getDescripcion(),
                dto.getMonto() != null ? "$" + dto.getMonto().toString() : "$0.00"
            });
        }
    }

    private void btnNuevoAnioAction() {
        String[] opciones = {"2024", "2025", "2026", "2027", "2028"};
        String nuevoAnio = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione el nuevo año a crear:",
            "Nuevo Año",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[opciones.length - 1]
        );
        
        if (nuevoAnio != null) {
            int anioInt = Integer.parseInt(nuevoAnio);
            boolean existe = false;
            for (int i = 0; i < cmbAnio.getItemCount(); i++) {
                if (cmbAnio.getItemAt(i) == anioInt) {
                    existe = true;
                    break;
                }
            }
            if (!existe) {
                cmbAnio.addItem(anioInt);
            }
            cmbAnio.setSelectedItem(anioInt);
            JOptionPane.showMessageDialog(this, "Año " + nuevoAnio + " seleccionado. Ahora puede agregar movimientos.", "Información", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaCaja().setVisible(true));
    }
}