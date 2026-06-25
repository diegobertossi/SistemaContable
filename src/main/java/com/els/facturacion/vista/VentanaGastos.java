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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaGastos extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 14);

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

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
    private JPanel panelSuperior;
    private JPanel panelFiltro;
    private JPanel panelFormulario;
    private JPanel panelBotones;
    private JLabel lblTitulo;
    private JLabel lblMes;
    private JLabel lblAnio;
    private JLabel lblCat;
    private JLabel lblDesc;
    private JLabel lblMonto;
    private JButton btnFiltrar;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JButton btnAgregar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public VentanaGastos() {
        controlador = new ControladorGastos();
        initComponents();
        applyTheme(currentTheme);
        cargarCategorias();
        cargarGastos();
        actualizarTotal();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Gestion de Gastos");
        setSize(900, 520);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(currentTheme.bgSurface);

        lblTitulo = new JLabel("REGISTRO DE GASTOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);

        lblTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(FUENTE_TITULO);
        lblTotal.setForeground(currentTheme.brand);

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
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabla.setRowHeight(22);
        tabla.setShowGrid(true);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        JScrollPane scrollPane = new JScrollPane(tabla);

        panelFiltro = new JPanel();
        panelFiltro.setBackground(currentTheme.bgBase);

        cmbMes = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        cmbAnio = new JComboBox<>();
        int anioActual = LocalDate.now().getYear();
        for (int a = anioActual - 2; a <= anioActual + 1; a++) {
            cmbAnio.addItem(a);
        }
        cmbMes.setSelectedItem(LocalDate.now().getMonthValue());
        cmbAnio.setSelectedItem(anioActual);

        lblMes = new JLabel("Mes:");
        lblMes.setFont(FUENTE_BOTON);
        lblMes.setForeground(currentTheme.textPrimary);

        lblAnio = new JLabel("Anio:");
        lblAnio.setFont(FUENTE_BOTON);
        lblAnio.setForeground(currentTheme.textPrimary);

        btnFiltrar = new JButton("FILTRAR");
        btnFiltrar.setFont(FUENTE_BOTON);
        btnFiltrar.setForeground(currentTheme.textPrimary);
        btnFiltrar.setBackground(currentTheme.btnBg);
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

        panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(currentTheme.bgBase);
        lblCat = new JLabel("Categoria:");
        lblCat.setFont(FUENTE_BOTON);
        lblCat.setForeground(currentTheme.textPrimary);

        lblDesc = new JLabel("Descripcion:");
        lblDesc.setFont(FUENTE_BOTON);
        lblDesc.setForeground(currentTheme.textPrimary);

        lblMonto = new JLabel("Monto:");
        lblMonto.setFont(FUENTE_BOTON);
        lblMonto.setForeground(currentTheme.textPrimary);

        cmbCategoria = new JComboBox<>();
        txtDescripcion = new JTextField(20);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtMonto = new JTextField(10);
        txtMonto.setFont(new Font("Segoe UI", Font.PLAIN, 11));

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

        panelBotones = new JPanel();
        panelBotones.setBackground(currentTheme.bgBase);

        btnAgregar = new JButton("AGREGAR");
        btnAgregar.setFont(FUENTE_BOTON);
        btnAgregar.setForeground(currentTheme.textPrimary);
        btnAgregar.setBackground(currentTheme.btnBg);
        btnAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> btnAgregarAction());

        btnEliminar = new JButton("ELIMINAR");
        btnEliminar.setFont(FUENTE_BOTON);
        btnEliminar.setForeground(currentTheme.textPrimary);
        btnEliminar.setBackground(currentTheme.btnBg);
        btnEliminar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> btnEliminarAction());

        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setFont(FUENTE_BOTON);
        btnLimpiar.setForeground(currentTheme.textPrimary);
        btnLimpiar.setBackground(currentTheme.btnBg);
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
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(currentTheme.bgBase);
        southWrapper.add(panelFormulario, BorderLayout.CENTER);
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

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (t == null) return;
        getContentPane().setBackground(t.bgBase);
        if (panelSuperior != null) panelSuperior.setBackground(t.bgBase);
        if (panelFiltro != null) panelFiltro.setBackground(t.bgBase);
        if (panelFormulario != null) panelFormulario.setBackground(t.bgBase);
        if (panelBotones != null) panelBotones.setBackground(t.bgBase);
        if (statusBar != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        }
        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (lblTotal != null) lblTotal.setForeground(t.brand);
        if (lblMes != null) lblMes.setForeground(t.textPrimary);
        if (lblAnio != null) lblAnio.setForeground(t.textPrimary);
        if (lblCat != null) lblCat.setForeground(t.textPrimary);
        if (lblDesc != null) lblDesc.setForeground(t.textPrimary);
        if (lblMonto != null) lblMonto.setForeground(t.textPrimary);
        if (btnFiltrar != null) {
            btnFiltrar.setBackground(t.btnBg);
            btnFiltrar.setForeground(t.textPrimary);
        }
        if (btnAgregar != null) {
            btnAgregar.setBackground(t.btnBg);
            btnAgregar.setForeground(t.textPrimary);
        }
        if (btnEliminar != null) {
            btnEliminar.setBackground(t.btnBg);
            btnEliminar.setForeground(t.textPrimary);
        }
        if (btnLimpiar != null) {
            btnLimpiar.setBackground(t.btnBg);
            btnLimpiar.setForeground(t.textPrimary);
        }
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t);
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
        }
        if (txtDescripcion != null) {
            txtDescripcion.setForeground(t.textPrimary);
            txtDescripcion.setBackground(t.bgInput);
        }
        if (txtMonto != null) {
            txtMonto.setForeground(t.textPrimary);
            txtMonto.setBackground(t.bgInput);
        }
        if (cmbCategoria != null) {
            cmbCategoria.setForeground(t.textPrimary);
            cmbCategoria.setBackground(t.bgElevated);
        }
        if (cmbMes != null) {
            cmbMes.setForeground(t.textPrimary);
            cmbMes.setBackground(t.bgElevated);
        }
        if (cmbAnio != null) {
            cmbAnio.setForeground(t.textPrimary);
            cmbAnio.setBackground(t.bgElevated);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaGastos().setVisible(true));
    }
}
