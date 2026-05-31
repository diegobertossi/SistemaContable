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
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaGastos extends javax.swing.JFrame {

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
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);
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

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(TemaFacturaSoft.BG_APP);

        JLabel lblTitulo = new JLabel("REGISTRO DE GASTOS", SwingConstants.CENTER);
        lblTitulo.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(20f));
        lblTitulo.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);

        lblTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(14f));
        lblTotal.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);

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
        TemaFacturaSoft.aplicarEstiloTabla(tabla);
        JScrollPane scrollPane = new JScrollPane(tabla);

        JPanel panelFiltro = new JPanel();
        panelFiltro.setBackground(TemaFacturaSoft.BG_APP);

        cmbMes = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        cmbAnio = new JComboBox<>();
        int anioActual = LocalDate.now().getYear();
        for (int a = anioActual - 2; a <= anioActual + 1; a++) {
            cmbAnio.addItem(a);
        }
        cmbMes.setSelectedItem(LocalDate.now().getMonthValue());
        cmbAnio.setSelectedItem(anioActual);

        JLabel lblMes = new JLabel("Mes:");
        lblMes.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lblMes.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        JLabel lblAnio = new JLabel("Anio:");
        lblAnio.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lblAnio.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        JButton btnFiltrar = new JButton("FILTRAR");
        btnFiltrar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnFiltrar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnFiltrar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnFiltrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFiltrar.setFocusPainted(false);
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
        panelFormulario.setBackground(TemaFacturaSoft.BG_APP);
        JLabel lblCat = new JLabel("Categoria:");
        lblCat.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lblCat.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        JLabel lblDesc = new JLabel("Descripcion:");
        lblDesc.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lblDesc.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        JLabel lblMonto = new JLabel("Monto:");
        lblMonto.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        lblMonto.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        cmbCategoria = new JComboBox<>();
        txtDescripcion = new JTextField(20);
        txtDescripcion.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));
        txtMonto = new JTextField(10);
        txtMonto.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));

        GridBagConstraints gbc_lblCat = new GridBagConstraints();
        gbc_lblCat.insets = new Insets(5, 8, 5, 8);
        gbc_lblCat.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblCat.gridx = 0; gbc_lblCat.gridy = 0;
        panelFormulario.add(lblCat, gbc_lblCat);

        GridBagConstraints gbc_cmbCategoria = new GridBagConstraints();
        gbc_cmbCategoria.insets = new Insets(5, 8, 5, 8);
        gbc_cmbCategoria.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbCategoria.gridx = 1; gbc_cmbCategoria.gridy = 0;
        panelFormulario.add(cmbCategoria, gbc_cmbCategoria);

        GridBagConstraints gbc_lblDesc = new GridBagConstraints();
        gbc_lblDesc.insets = new Insets(5, 8, 5, 8);
        gbc_lblDesc.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblDesc.gridx = 2; gbc_lblDesc.gridy = 0;
        panelFormulario.add(lblDesc, gbc_lblDesc);

        GridBagConstraints gbc_txtDescripcion = new GridBagConstraints();
        gbc_txtDescripcion.insets = new Insets(5, 8, 5, 8);
        gbc_txtDescripcion.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtDescripcion.gridx = 3; gbc_txtDescripcion.gridy = 0;
        panelFormulario.add(txtDescripcion, gbc_txtDescripcion);

        GridBagConstraints gbc_lblMonto = new GridBagConstraints();
        gbc_lblMonto.insets = new Insets(5, 8, 5, 8);
        gbc_lblMonto.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblMonto.gridx = 4; gbc_lblMonto.gridy = 0;
        panelFormulario.add(lblMonto, gbc_lblMonto);

        GridBagConstraints gbc_txtMonto = new GridBagConstraints();
        gbc_txtMonto.insets = new Insets(5, 8, 5, 8);
        gbc_txtMonto.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtMonto.gridx = 5; gbc_txtMonto.gridy = 0;
        panelFormulario.add(txtMonto, gbc_txtMonto);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(TemaFacturaSoft.BG_APP);

        JButton btnAgregar = new JButton("AGREGAR");
        btnAgregar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnAgregar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnAgregar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> btnAgregarAction());

        JButton btnEliminar = new JButton("ELIMINAR");
        btnEliminar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnEliminar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnEliminar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> btnEliminarAction());

        JButton btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnLimpiar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnLimpiar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.setFocusPainted(false);
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
