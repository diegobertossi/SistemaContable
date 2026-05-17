package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorGastos;
import com.els.facturacion.modelo.CategoriaGastoDTO;
import com.els.facturacion.modelo.GastoDTO;
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

public class VentanaGastos extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_TITULO = new Font("Cambria", Font.BOLD, 14);

    private ControladorGastos controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<CategoriaGastoDTO> cmbCategoria;
    private JTextField txtDescripcion;
    private JTextField txtMonto;
    private JComboBox<Integer> cmbMes;
    private JComboBox<Integer> cmbAnio;
    private JLabel lblTotal;
    private DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VentanaGastos() {
        controlador = new ControladorGastos();
        initComponents();
        cargarCategorias();
        cargarGastos();
        actualizarTotal();
    }

    private void initComponents() {
        setTitle("Gestion de Gastos");
        setSize(900, 520);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("REGISTRO DE GASTOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_TEXTO);

        lblTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(FUENTE_TITULO);
        lblTotal.setForeground(COLOR_TITULO);

        panelSuperior.add(lblTitulo, BorderLayout.CENTER);
        panelSuperior.add(lblTotal, BorderLayout.EAST);

        String[] columnas = {"ID", "Fecha", "Categoria", "Descripcion", "Monto"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Cambria", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(tabla);

        JPanel panelFiltro = new JPanel();
        panelFiltro.setBackground(COLOR_FONDO);

        cmbMes = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        cmbAnio = new JComboBox<>();
        int anioActual = LocalDate.now().getYear();
        for (int a = anioActual - 2; a <= anioActual + 1; a++) {
            cmbAnio.addItem(a);
        }
        cmbMes.setSelectedItem(LocalDate.now().getMonthValue());
        cmbAnio.setSelectedItem(anioActual);

        JLabel lblMes = new JLabel("Mes:");
        lblMes.setFont(FUENTE_BOTON);
        lblMes.setForeground(COLOR_TEXTO);

        JLabel lblAnio = new JLabel("Anio:");
        lblAnio.setFont(FUENTE_BOTON);
        lblAnio.setForeground(COLOR_TEXTO);

        JButton btnFiltrar = crearBoton("FILTRAR");
        btnFiltrar.addActionListener(e -> {
            cargarGastos();
            actualizarTotal();
        });

        panelFiltro.add(lblMes);
        panelFiltro.add(cmbMes);
        panelFiltro.add(lblAnio);
        panelFiltro.add(cmbAnio);
        panelFiltro.add(btnFiltrar);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCat = new JLabel("Categoria:");
        lblCat.setFont(FUENTE_BOTON);
        lblCat.setForeground(COLOR_TEXTO);

        JLabel lblDesc = new JLabel("Descripcion:");
        lblDesc.setFont(FUENTE_BOTON);
        lblDesc.setForeground(COLOR_TEXTO);

        JLabel lblMonto = new JLabel("Monto:");
        lblMonto.setFont(FUENTE_BOTON);
        lblMonto.setForeground(COLOR_TEXTO);

        cmbCategoria = new JComboBox<>();
        txtDescripcion = new JTextField(20);
        txtDescripcion.setFont(new Font("Cambria", Font.PLAIN, 11));
        txtMonto = new JTextField(10);
        txtMonto.setFont(new Font("Cambria", Font.PLAIN, 11));

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(lblCat, gbc);
        gbc.gridx = 1;
        panelFormulario.add(cmbCategoria, gbc);

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

        JButton btnLimpiar = crearBoton("LIMPIAR");
        btnLimpiar.addActionListener(e -> {
            txtDescripcion.setText("");
            txtMonto.setText("");
        });

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelFiltro, BorderLayout.BEFORE_FIRST_LINE);
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

    private void cargarCategorias() {
        cmbCategoria.removeAllItems();
        List<CategoriaGastoDTO> lista = controlador.listarCategoriasActivas();
        for (CategoriaGastoDTO cat : lista) {
            cmbCategoria.addItem(cat);
        }
    }

    private void cargarGastos() {
        modeloTabla.setRowCount(0);
        Integer mes = (Integer) cmbMes.getSelectedItem();
        Integer anio = (Integer) cmbAnio.getSelectedItem();

        List<GastoDTO> lista = controlador.listarGastos(mes, anio);
        for (GastoDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getId(),
                dto.getFecha() != null ? dto.getFecha().format(fechaFormatter) : "",
                dto.getCategoriaNombre(),
                dto.getDescripcion(),
                dto.getMonto() != null ? "$" + dto.getMonto().toString() : "$0.00"
            });
        }
    }

    private void btnAgregarAction() {
        try {
            CategoriaGastoDTO categoria = (CategoriaGastoDTO) cmbCategoria.getSelectedItem();
            if (categoria == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoria", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String descripcion = txtDescripcion.getText().trim();
            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese descripcion", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal monto = new BigDecimal(txtMonto.getText().trim());

            int id = controlador.registrarGasto(LocalDate.now(), categoria.getId(), descripcion, monto);

            if (id > 0) {
                JOptionPane.showMessageDialog(this, "Gasto registrado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                txtDescripcion.setText("");
                txtMonto.setText("");
                cargarGastos();
                actualizarTotal();
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
            JOptionPane.showMessageDialog(this, "Seleccione un gasto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar este gasto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(selectedRow, 0);
            if (controlador.eliminarGasto(id)) {
                JOptionPane.showMessageDialog(this, "Gasto eliminado", "Exito", JOptionPane.INFORMATION_MESSAGE);
                cargarGastos();
                actualizarTotal();
            }
        }
    }

    private void actualizarTotal() {
        Integer mes = (Integer) cmbMes.getSelectedItem();
        Integer anio = (Integer) cmbAnio.getSelectedItem();
        BigDecimal total = controlador.getTotalGastos(mes, anio);
        lblTotal.setText("Total: $" + total.toString());
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaGastos().setVisible(true));
    }
}