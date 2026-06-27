package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorCaja;
import com.els.facturacion.modelo.CajaMovimientoDTO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaCaja extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 14);

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private ControladorCaja controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<Integer> cmbAnio;
    private JLabel lblSaldo;
    private JLabel lblSubCobroEfectivo;
    private JLabel lblSubCobroPatagonia;
    private JLabel lblSubPagoEfectivo;
    private JLabel lblSubPagoPatagonia;
    private JLabel lblSubCompraDolares;
    private JLabel lblSaldoTotal;
    private DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DecimalFormat formatoMoneda = new DecimalFormat("#,##0.00");
    private JTextField txtELS;
    private JTextField txtCobroEfectivo;
    private JTextField txtCobroPatagonia;
    private JTextField txtPagoEfectivo;
    private JTextField txtPagoPatagonia;
    private JTextField txtDolares;
    private JTextField txtCotizacion;
    private JTextField txtPesosGastados;
    private JComboBox<String> cmbFormaPago;
    private JCheckBox chkCompraDolares;
    private JLabel lblSubTitulo;
    private JLabel lblDatosMov;

    public VentanaCaja() {
        controlador = new ControladorCaja();
        initComponents();
        applyTheme(currentTheme);
        cargarMovimientosPorAnio();
        actualizarSaldo();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Caja - Movimiento de Fondos");
        setSize(1000, 600);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(currentTheme.bgSurface);

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(currentTheme.bgSurface);

        JLabel lblTitulo = new JLabel("CONTROL DE CAJA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        lblTitulo.setBackground(currentTheme.bgSurface);

        lblSaldo = new JLabel("Saldo: $0.00", SwingConstants.RIGHT);
        lblSaldo.setFont(FUENTE_TITULO);
        lblSaldo.setForeground(currentTheme.brand);
        lblSaldo.setBackground(currentTheme.bgBase);

        JPanel panelAnio = new JPanel();
        panelAnio.setBackground(currentTheme.bgBase);
        
        JLabel lblFiltrarAnio = new JLabel("Año:");
        lblFiltrarAnio.setFont(FUENTE_BOTON);
        lblFiltrarAnio.setForeground(currentTheme.textPrimary);
        
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

        JButton btnNuevoAnio = new JButton("NUEVO AÑO");
        estilizarBoton(btnNuevoAnio);
        btnNuevoAnio.addActionListener(e -> btnNuevoAnioAction());

        panelAnio.add(lblFiltrarAnio);
        panelAnio.add(cmbAnio);
        panelAnio.add(btnNuevoAnio);

        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        panelTitulo.add(lblSaldo, BorderLayout.EAST);

        panelSuperior.add(panelTitulo, BorderLayout.NORTH);
        panelSuperior.add(panelAnio, BorderLayout.SOUTH);

        String[] columnas = {"Fecha", "Tipo", "Cliente", "Forma de Pago", "Cobro Efectivo", "Cobro Patagonia", "Pago Efectivo", "Pago Patagonia", "Compra Dólares", "ELS"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        tabla.setRowHeight(22);
        tabla.setShowGrid(true);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 10));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < columnas.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(100);
        }
        
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabla.getSelectedRow();
                    int col = tabla.getSelectedColumn();
                    if (row >= 0 && col >= 0 && col != 1 && col != 8 && col != 9) {
                        editarCelda(row, col);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new java.awt.Dimension(1000, 250));

        JPanel panelSubtotales = new JPanel(new GridBagLayout());
        panelSubtotales.setBackground(currentTheme.bgBase);

        lblSubTitulo = new JLabel("SUBTOTALES");
        lblSubTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSubTitulo.setForeground(currentTheme.brand);
        GridBagConstraints gbcSubTitulo = new GridBagConstraints();
        gbcSubTitulo.gridx = 0; gbcSubTitulo.gridy = 0; gbcSubTitulo.gridwidth = 10;
        gbcSubTitulo.insets = new Insets(5, 10, 5, 10);
        gbcSubTitulo.fill = GridBagConstraints.HORIZONTAL;
        panelSubtotales.add(lblSubTitulo, gbcSubTitulo);

        lblSubCobroEfectivo = new JLabel("Cobro Efectivo: $ 0,00");
        lblSubCobroEfectivo.setFont(FUENTE_BOTON);
        lblSubCobroEfectivo.setForeground(currentTheme.textPrimary);
        GridBagConstraints gbcSubCobroEf = new GridBagConstraints();
        gbcSubCobroEf.gridwidth = 1; gbcSubCobroEf.gridx = 0; gbcSubCobroEf.gridy = 1;
        gbcSubCobroEf.insets = new Insets(5, 10, 5, 10);
        gbcSubCobroEf.fill = GridBagConstraints.HORIZONTAL;
        panelSubtotales.add(lblSubCobroEfectivo, gbcSubCobroEf);

        lblSubCobroPatagonia = new JLabel("Cobro Patagonia: $ 0,00");
        lblSubCobroPatagonia.setFont(FUENTE_BOTON);
        lblSubCobroPatagonia.setForeground(currentTheme.textPrimary);
        GridBagConstraints gbcSubCobroPat = new GridBagConstraints();
        gbcSubCobroPat.gridx = 1; gbcSubCobroPat.gridy = 1;
        gbcSubCobroPat.insets = new Insets(5, 10, 5, 10);
        gbcSubCobroPat.fill = GridBagConstraints.HORIZONTAL;
        panelSubtotales.add(lblSubCobroPatagonia, gbcSubCobroPat);

        lblSubPagoEfectivo = new JLabel("Pago Efectivo: $ 0,00");
        lblSubPagoEfectivo.setFont(FUENTE_BOTON);
        lblSubPagoEfectivo.setForeground(currentTheme.textPrimary);
        GridBagConstraints gbcSubPagoEf = new GridBagConstraints();
        gbcSubPagoEf.gridx = 2; gbcSubPagoEf.gridy = 1;
        gbcSubPagoEf.insets = new Insets(5, 10, 5, 10);
        gbcSubPagoEf.fill = GridBagConstraints.HORIZONTAL;
        panelSubtotales.add(lblSubPagoEfectivo, gbcSubPagoEf);

        lblSubPagoPatagonia = new JLabel("Pago Patagonia: $ 0,00");
        lblSubPagoPatagonia.setFont(FUENTE_BOTON);
        lblSubPagoPatagonia.setForeground(currentTheme.textPrimary);
        GridBagConstraints gbcSubPagoPat = new GridBagConstraints();
        gbcSubPagoPat.gridx = 3; gbcSubPagoPat.gridy = 1;
        gbcSubPagoPat.insets = new Insets(5, 10, 5, 10);
        gbcSubPagoPat.fill = GridBagConstraints.HORIZONTAL;
        panelSubtotales.add(lblSubPagoPatagonia, gbcSubPagoPat);

        lblSubCompraDolares = new JLabel("Compra Dólares: $ 0,00");
        lblSubCompraDolares.setFont(FUENTE_BOTON);
        lblSubCompraDolares.setForeground(currentTheme.textPrimary);
        GridBagConstraints gbcSubCompraDol = new GridBagConstraints();
        gbcSubCompraDol.gridx = 4; gbcSubCompraDol.gridy = 1;
        gbcSubCompraDol.insets = new Insets(5, 10, 5, 10);
        gbcSubCompraDol.fill = GridBagConstraints.HORIZONTAL;
        panelSubtotales.add(lblSubCompraDolares, gbcSubCompraDol);

        lblSaldoTotal = new JLabel("SALDO: $ 0,00");
        lblSaldoTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSaldoTotal.setForeground(currentTheme.brand);
        GridBagConstraints gbcSubSaldoTotal = new GridBagConstraints();
        gbcSubSaldoTotal.gridx = 8; gbcSubSaldoTotal.gridy = 1;
        gbcSubSaldoTotal.insets = new Insets(5, 10, 5, 10);
        gbcSubSaldoTotal.fill = GridBagConstraints.HORIZONTAL;
        panelSubtotales.add(lblSaldoTotal, gbcSubSaldoTotal);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(currentTheme.bgBase);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(FUENTE_BOTON);
        lblFecha.setForeground(currentTheme.textPrimary);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(FUENTE_BOTON);
        lblCliente.setForeground(currentTheme.textPrimary);

        JLabel lblFormaPago = new JLabel("Forma Pago:");
        lblFormaPago.setFont(FUENTE_BOTON);
        lblFormaPago.setForeground(currentTheme.textPrimary);

        JLabel lblELS = new JLabel("ELS:");
        lblELS.setFont(FUENTE_BOTON);
        lblELS.setForeground(currentTheme.textPrimary);

        JLabel lblCobroEfec = new JLabel("Cobro Efectivo:");
        lblCobroEfec.setFont(FUENTE_BOTON);
        lblCobroEfec.setForeground(currentTheme.textPrimary);

        JLabel lblCobroPat = new JLabel("Cobro Patagonia:");
        lblCobroPat.setFont(FUENTE_BOTON);
        lblCobroPat.setForeground(currentTheme.textPrimary);

        JLabel lblPagoEfec = new JLabel("Pago Efectivo:");
        lblPagoEfec.setFont(FUENTE_BOTON);
        lblPagoEfec.setForeground(currentTheme.textPrimary);

        JLabel lblPagoPat = new JLabel("Pago Patagonia:");
        lblPagoPat.setFont(FUENTE_BOTON);
        lblPagoPat.setForeground(currentTheme.textPrimary);

        JLabel lblCompraDol = new JLabel("Compra Dólares:");
        lblCompraDol.setFont(FUENTE_BOTON);
        lblCompraDol.setForeground(currentTheme.textPrimary);

        chkCompraDolares = new JCheckBox("Activo");
        chkCompraDolares.setFont(FUENTE_BOTON);
        chkCompraDolares.setForeground(currentTheme.textPrimary);
        chkCompraDolares.setBackground(currentTheme.bgBase);

        JTextField txtFecha = new JTextField(10);
        txtFecha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtFecha.setText(LocalDate.now().format(fechaFormatter));

        JTextField txtCliente = new JTextField(15);
        txtCliente.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        cmbFormaPago = new JComboBox<>(new String[]{
            "Efectivo", "Transferencia", "Transferencia+Efectivo", "Débito", "Otra"
        });
        cmbFormaPago.setFont(FUENTE_BOTON);

        txtELS = new JTextField(8);
        txtELS.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        txtCobroEfectivo = new JTextField(10);
        txtCobroEfectivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        txtCobroPatagonia = new JTextField(10);
        txtCobroPatagonia.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        txtPagoEfectivo = new JTextField(10);
        txtPagoEfectivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        txtPagoPatagonia = new JTextField(10);
        txtPagoPatagonia.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        txtDolares = new JTextField(8);
        txtDolares.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtDolares.setEnabled(false);

        txtCotizacion = new JTextField(8);
        txtCotizacion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtCotizacion.setEnabled(false);

        txtPesosGastados = new JTextField(10);
        txtPesosGastados.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtPesosGastados.setEnabled(false);
        txtPesosGastados.setEditable(false);

        chkCompraDolares.addActionListener(e -> {
            boolean enabled = chkCompraDolares.isSelected();
            txtDolares.setEnabled(enabled);
            txtCotizacion.setEnabled(enabled);
            cmbFormaPago.setEnabled(!enabled);
            txtCliente.setEnabled(!enabled);
            txtCobroEfectivo.setEnabled(!enabled);
            txtCobroPatagonia.setEnabled(!enabled);
            txtPagoEfectivo.setEnabled(!enabled);
            txtPagoPatagonia.setEnabled(!enabled);
            txtELS.setEnabled(!enabled);
            
            if (enabled) {
                txtCobroEfectivo.setText("");
                txtCobroPatagonia.setText("");
                txtPagoEfectivo.setText("");
                txtPagoPatagonia.setText("");
                txtCliente.setText("");
                cmbFormaPago.setSelectedIndex(0);
                txtELS.setText("");
                txtFecha.setText(LocalDate.now().format(fechaFormatter));
            }
        });

        txtCotizacion.addActionListener(e -> {
            try {
                BigDecimal dolares = new BigDecimal(txtDolares.getText().trim());
                BigDecimal cotizacion = new BigDecimal(txtCotizacion.getText().trim());
                txtPesosGastados.setText(dolares.multiply(cotizacion).setScale(2).toString());
            } catch (Exception ex) {
                txtPesosGastados.setText("");
            }
        });

        txtDolares.addActionListener(e -> {
            try {
                BigDecimal dolares = new BigDecimal(txtDolares.getText().trim());
                BigDecimal cotizacion = new BigDecimal(txtCotizacion.getText().trim());
                txtPesosGastados.setText(dolares.multiply(cotizacion).setScale(2).toString());
            } catch (Exception ex) {
                txtPesosGastados.setText("");
            }
        });

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(currentTheme.bgBase);

        JButton btnAgregar = new JButton("AGREGAR");
        estilizarBoton(btnAgregar);
        btnAgregar.addActionListener(e -> {
            try {
                LocalDate fecha = null;
                if (!txtFecha.getText().trim().isEmpty()) {
                    fecha = LocalDate.parse(txtFecha.getText().trim(), fechaFormatter);
                }
                String cliente = txtCliente.getText().trim();
                String formaPago = (String) cmbFormaPago.getSelectedItem();
                String numeroELS = txtELS.getText().trim();
                String elsTexto = numeroELS.isEmpty() ? "" : " ELS " + numeroELS;

                BigDecimal cobrosEfectivo = BigDecimal.ZERO;
                BigDecimal cobrosPatagonia = BigDecimal.ZERO;
                BigDecimal pagosEfectivo = BigDecimal.ZERO;
                BigDecimal pagosPatagonia = BigDecimal.ZERO;

                if (!txtCobroEfectivo.getText().trim().isEmpty()) {
                    cobrosEfectivo = new BigDecimal(txtCobroEfectivo.getText().trim().replace(",", "."));
                }
                if (!txtCobroPatagonia.getText().trim().isEmpty()) {
                    cobrosPatagonia = new BigDecimal(txtCobroPatagonia.getText().trim().replace(",", "."));
                }
                if (!txtPagoEfectivo.getText().trim().isEmpty()) {
                    pagosEfectivo = new BigDecimal(txtPagoEfectivo.getText().trim().replace(",", "."));
                }
                if (!txtPagoPatagonia.getText().trim().isEmpty()) {
                    pagosPatagonia = new BigDecimal(txtPagoPatagonia.getText().trim().replace(",", "."));
                }

                boolean tieneDatos = cobrosEfectivo.compareTo(BigDecimal.ZERO) > 0 || cobrosPatagonia.compareTo(BigDecimal.ZERO) > 0 
                    || pagosEfectivo.compareTo(BigDecimal.ZERO) > 0 || pagosPatagonia.compareTo(BigDecimal.ZERO) > 0;

                if (!tieneDatos) {
                    JOptionPane.showMessageDialog(this, "Ingrese al menos un monto", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int registros = 0;

                if (cobrosEfectivo.compareTo(BigDecimal.ZERO) > 0) {
                    String desc = cliente + " - Efectivo" + elsTexto;
                    int id = controlador.registrarMovimiento(fecha, "cobro", desc, cobrosEfectivo);
                    if (id > 0) registros++;
                }
                if (cobrosPatagonia.compareTo(BigDecimal.ZERO) > 0) {
                    String desc = cliente + " - " + formaPago + elsTexto;
                    int id = controlador.registrarMovimiento(fecha, "cobro", desc, cobrosPatagonia);
                    if (id > 0) registros++;
                }
                if (pagosEfectivo.compareTo(BigDecimal.ZERO) > 0) {
                    String desc = cliente + " - Efectivo" + elsTexto;
                    int id = controlador.registrarMovimiento(fecha, "pago", desc, pagosEfectivo);
                    if (id > 0) registros++;
                }
                if (pagosPatagonia.compareTo(BigDecimal.ZERO) > 0) {
                    String desc = cliente + " - " + formaPago + elsTexto;
                    int id = controlador.registrarMovimiento(fecha, "pago", desc, pagosPatagonia);
                    if (id > 0) registros++;
                }

                if (registros > 0) {
                    txtCliente.setText("");
                    txtCobroEfectivo.setText("");
                    txtCobroPatagonia.setText("");
                    txtPagoEfectivo.setText("");
                    txtPagoPatagonia.setText("");
                    txtELS.setText("");
                    txtDolares.setText("");
                    txtCotizacion.setText("");
                    txtPesosGastados.setText("");
                    txtFecha.setText(LocalDate.now().format(fechaFormatter));
                    cargarMovimientosPorAnio();
                    actualizarSaldo();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = new JButton("ELIMINAR");
        estilizarBoton(btnEliminar);
        btnEliminar.addActionListener(e -> btnEliminarAction());

        JButton btnActualizar = new JButton("ACTUALIZAR");
        estilizarBoton(btnActualizar);
        btnActualizar.addActionListener(e -> {
            cargarMovimientosPorAnio();
            actualizarSaldo();
        });

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        GridBagConstraints gbcDatosMov = new GridBagConstraints();
        gbcDatosMov.insets = new Insets(4, 6, 4, 6);
        gbcDatosMov.fill = GridBagConstraints.HORIZONTAL;
        gbcDatosMov.gridx = 0; gbcDatosMov.gridy = 0; gbcDatosMov.gridwidth = 8;
        lblDatosMov = new JLabel("DATOS DEL MOVIMIENTO");
        lblDatosMov.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDatosMov.setForeground(currentTheme.brand);
        panelFormulario.add(lblDatosMov, gbcDatosMov);

        GridBagConstraints gbcFecha = new GridBagConstraints();
        gbcFecha.insets = new Insets(4, 6, 4, 6);
        gbcFecha.fill = GridBagConstraints.HORIZONTAL;
        gbcFecha.gridwidth = 1; gbcFecha.gridx = 0; gbcFecha.gridy = 1;
        panelFormulario.add(lblFecha, gbcFecha);

        GridBagConstraints gbcTxtFecha = new GridBagConstraints();
        gbcTxtFecha.insets = new Insets(4, 6, 4, 6);
        gbcTxtFecha.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtFecha.gridx = 1; gbcTxtFecha.gridy = 1;
        panelFormulario.add(txtFecha, gbcTxtFecha);

        GridBagConstraints gbcCliente = new GridBagConstraints();
        gbcCliente.insets = new Insets(4, 6, 4, 6);
        gbcCliente.fill = GridBagConstraints.HORIZONTAL;
        gbcCliente.gridx = 2; gbcCliente.gridy = 1;
        panelFormulario.add(lblCliente, gbcCliente);

        GridBagConstraints gbcTxtCliente = new GridBagConstraints();
        gbcTxtCliente.insets = new Insets(4, 6, 4, 6);
        gbcTxtCliente.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtCliente.gridx = 3; gbcTxtCliente.gridy = 1; gbcTxtCliente.gridwidth = 2;
        panelFormulario.add(txtCliente, gbcTxtCliente);

        GridBagConstraints gbcFormaPago = new GridBagConstraints();
        gbcFormaPago.insets = new Insets(4, 6, 4, 6);
        gbcFormaPago.fill = GridBagConstraints.HORIZONTAL;
        gbcFormaPago.gridwidth = 1; gbcFormaPago.gridx = 5; gbcFormaPago.gridy = 1;
        panelFormulario.add(lblFormaPago, gbcFormaPago);

        GridBagConstraints gbcCmbFormaPago = new GridBagConstraints();
        gbcCmbFormaPago.insets = new Insets(4, 6, 4, 6);
        gbcCmbFormaPago.fill = GridBagConstraints.HORIZONTAL;
        gbcCmbFormaPago.gridx = 6; gbcCmbFormaPago.gridy = 1;
        panelFormulario.add(cmbFormaPago, gbcCmbFormaPago);

        GridBagConstraints gbcELS = new GridBagConstraints();
        gbcELS.insets = new Insets(4, 6, 4, 6);
        gbcELS.fill = GridBagConstraints.HORIZONTAL;
        gbcELS.gridx = 7; gbcELS.gridy = 1;
        panelFormulario.add(lblELS, gbcELS);

        GridBagConstraints gbcCobroEfec = new GridBagConstraints();
        gbcCobroEfec.insets = new Insets(4, 6, 4, 6);
        gbcCobroEfec.fill = GridBagConstraints.HORIZONTAL;
        gbcCobroEfec.gridwidth = 1; gbcCobroEfec.gridx = 0; gbcCobroEfec.gridy = 2;
        panelFormulario.add(lblCobroEfec, gbcCobroEfec);

        GridBagConstraints gbcTxtCobroEfectivo = new GridBagConstraints();
        gbcTxtCobroEfectivo.insets = new Insets(4, 6, 4, 6);
        gbcTxtCobroEfectivo.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtCobroEfectivo.gridx = 1; gbcTxtCobroEfectivo.gridy = 2;
        panelFormulario.add(txtCobroEfectivo, gbcTxtCobroEfectivo);

        GridBagConstraints gbcCobroPat = new GridBagConstraints();
        gbcCobroPat.insets = new Insets(4, 6, 4, 6);
        gbcCobroPat.fill = GridBagConstraints.HORIZONTAL;
        gbcCobroPat.gridx = 2; gbcCobroPat.gridy = 2;
        panelFormulario.add(lblCobroPat, gbcCobroPat);

        GridBagConstraints gbcTxtCobroPatagonia = new GridBagConstraints();
        gbcTxtCobroPatagonia.insets = new Insets(4, 6, 4, 6);
        gbcTxtCobroPatagonia.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtCobroPatagonia.gridx = 3; gbcTxtCobroPatagonia.gridy = 2;
        panelFormulario.add(txtCobroPatagonia, gbcTxtCobroPatagonia);

        GridBagConstraints gbcPagoEfec = new GridBagConstraints();
        gbcPagoEfec.insets = new Insets(4, 6, 4, 6);
        gbcPagoEfec.fill = GridBagConstraints.HORIZONTAL;
        gbcPagoEfec.gridx = 4; gbcPagoEfec.gridy = 2;
        panelFormulario.add(lblPagoEfec, gbcPagoEfec);

        GridBagConstraints gbcTxtPagoEfectivo = new GridBagConstraints();
        gbcTxtPagoEfectivo.insets = new Insets(4, 6, 4, 6);
        gbcTxtPagoEfectivo.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtPagoEfectivo.gridx = 5; gbcTxtPagoEfectivo.gridy = 2;
        panelFormulario.add(txtPagoEfectivo, gbcTxtPagoEfectivo);

        GridBagConstraints gbcPagoPat = new GridBagConstraints();
        gbcPagoPat.insets = new Insets(4, 6, 4, 6);
        gbcPagoPat.fill = GridBagConstraints.HORIZONTAL;
        gbcPagoPat.gridx = 6; gbcPagoPat.gridy = 2;
        panelFormulario.add(lblPagoPat, gbcPagoPat);

        GridBagConstraints gbcTxtPagoPatagonia = new GridBagConstraints();
        gbcTxtPagoPatagonia.insets = new Insets(4, 6, 4, 6);
        gbcTxtPagoPatagonia.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtPagoPatagonia.gridx = 7; gbcTxtPagoPatagonia.gridy = 2;
        panelFormulario.add(txtPagoPatagonia, gbcTxtPagoPatagonia);

        GridBagConstraints gbcSep1 = new GridBagConstraints();
        gbcSep1.insets = new Insets(4, 6, 4, 6);
        gbcSep1.fill = GridBagConstraints.HORIZONTAL;
        gbcSep1.gridx = 0; gbcSep1.gridy = 3; gbcSep1.gridwidth = 8;
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(currentTheme.borderLight);
        panelFormulario.add(sep1, gbcSep1);

        GridBagConstraints gbcChkCompraDolares = new GridBagConstraints();
        gbcChkCompraDolares.insets = new Insets(4, 6, 4, 6);
        gbcChkCompraDolares.fill = GridBagConstraints.HORIZONTAL;
        gbcChkCompraDolares.gridwidth = 1; gbcChkCompraDolares.gridx = 0; gbcChkCompraDolares.gridy = 4;
        panelFormulario.add(chkCompraDolares, gbcChkCompraDolares);

        GridBagConstraints gbcLblDolares = new GridBagConstraints();
        gbcLblDolares.insets = new Insets(4, 6, 4, 6);
        gbcLblDolares.fill = GridBagConstraints.HORIZONTAL;
        gbcLblDolares.gridx = 1; gbcLblDolares.gridy = 4;
        panelFormulario.add(new JLabel("Dólares:"), gbcLblDolares);

        GridBagConstraints gbcTxtDolares = new GridBagConstraints();
        gbcTxtDolares.insets = new Insets(4, 6, 4, 6);
        gbcTxtDolares.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtDolares.gridx = 2; gbcTxtDolares.gridy = 4;
        panelFormulario.add(txtDolares, gbcTxtDolares);

        GridBagConstraints gbcLblX = new GridBagConstraints();
        gbcLblX.insets = new Insets(4, 6, 4, 6);
        gbcLblX.fill = GridBagConstraints.HORIZONTAL;
        gbcLblX.gridx = 3; gbcLblX.gridy = 4;
        panelFormulario.add(new JLabel("x"), gbcLblX);

        GridBagConstraints gbcLblCotizacion = new GridBagConstraints();
        gbcLblCotizacion.insets = new Insets(4, 6, 4, 6);
        gbcLblCotizacion.fill = GridBagConstraints.HORIZONTAL;
        gbcLblCotizacion.gridx = 4; gbcLblCotizacion.gridy = 4;
        panelFormulario.add(new JLabel("Cotización:"), gbcLblCotizacion);

        GridBagConstraints gbcTxtCotizacion = new GridBagConstraints();
        gbcTxtCotizacion.insets = new Insets(4, 6, 4, 6);
        gbcTxtCotizacion.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtCotizacion.gridx = 5; gbcTxtCotizacion.gridy = 4;
        panelFormulario.add(txtCotizacion, gbcTxtCotizacion);

        GridBagConstraints gbcLblIgual = new GridBagConstraints();
        gbcLblIgual.insets = new Insets(4, 6, 4, 6);
        gbcLblIgual.fill = GridBagConstraints.HORIZONTAL;
        gbcLblIgual.gridx = 6; gbcLblIgual.gridy = 4;
        panelFormulario.add(new JLabel("="), gbcLblIgual);

        GridBagConstraints gbcTxtPesosGastados = new GridBagConstraints();
        gbcTxtPesosGastados.insets = new Insets(4, 6, 4, 6);
        gbcTxtPesosGastados.fill = GridBagConstraints.HORIZONTAL;
        gbcTxtPesosGastados.gridx = 7; gbcTxtPesosGastados.gridy = 4;
        panelFormulario.add(txtPesosGastados, gbcTxtPesosGastados);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(currentTheme.bgBase);
        panelSur.add(panelFormulario, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        JPanel panelCompletoSur = new JPanel(new BorderLayout());
        panelCompletoSur.setBackground(currentTheme.bgBase);
        panelCompletoSur.add(panelSubtotales, BorderLayout.NORTH);
        panelCompletoSur.add(panelSur, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelCompletoSur, BorderLayout.SOUTH);
    }

    private void estilizarBoton(JButton btn) {
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(currentTheme.textPrimary);
        btn.setBackground(currentTheme.btnBg);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }

    private String formatearMonto(BigDecimal monto) {
        if (monto == null) return "";
        return "$ " + formatoMoneda.format(monto);
    }

    private void editarCelda(int row, int col) {
        Object valorActual = modeloTabla.getValueAt(row, col);
        String valorStr = valorActual != null ? valorActual.toString().replace("$", "").replace(" ", "").trim() : "";

        JTextField txtEdit = new JTextField(valorStr);
        txtEdit.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        int result = JOptionPane.showConfirmDialog(this, txtEdit, "Editar valor", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String nuevoValor = txtEdit.getText().trim();
            
            String fecha = (String) modeloTabla.getValueAt(row, 0);
            String tipo = (String) modeloTabla.getValueAt(row, 1);
            String cliente = (String) modeloTabla.getValueAt(row, 2);
            String formaPago = (String) modeloTabla.getValueAt(row, 3);
            String els = (String) modeloTabla.getValueAt(row, 9);

            List<CajaMovimientoDTO> lista = controlador.listarMovimientos();
            for (CajaMovimientoDTO dto : lista) {
                String desc = dto.getDescripcion() != null ? dto.getDescripcion() : "";
                boolean fechaMatch = (dto.getFecha() != null && dto.getFecha().format(fechaFormatter).equals(fecha)) ||
                                     (fecha.isEmpty() && dto.getFecha() == null);

                if (fechaMatch && dto.getTipo().equalsIgnoreCase(tipo) && desc.contains(cliente)) {
                    try {
                        if (col >= 4 && col <= 7 || col == 8) {
                            BigDecimal monto = new BigDecimal(nuevoValor.replace(",", "."));
                            dto.setMonto(monto);
                        } else if (col == 0) {
                            if (!nuevoValor.isEmpty()) {
                                dto.setFecha(LocalDate.parse(nuevoValor, fechaFormatter));
                            }
                        } else if (col == 2) {
                            String nuevaDesc = nuevoValor + (formaPago != null && !formaPago.isEmpty() ? " - " + formaPago : "");
                            if (els != null && !els.isEmpty()) {
                                nuevaDesc += " ELS " + els;
                            }
                            dto.setDescripcion(nuevaDesc);
                        } else if (col == 3) {
                            String nuevaDesc = cliente + " - " + nuevoValor;
                            if (els != null && !els.isEmpty()) {
                                nuevaDesc += " ELS " + els;
                            }
                            dto.setDescripcion(nuevaDesc);
                        } else if (col == 9) {
                            String nuevaDesc = cliente + (formaPago != null && !formaPago.isEmpty() ? " - " + formaPago : "");
                            if (!nuevoValor.isEmpty()) {
                                nuevaDesc += " ELS " + nuevoValor;
                            }
                            dto.setDescripcion(nuevaDesc);
                        }

                        if (controlador.actualizarMovimiento(dto)) {
                            cargarMovimientosPorAnio();
                            actualizarSaldo();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Valor inválido: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                }
            }
        }
    }

    private void btnEliminarAction() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fecha = (String) modeloTabla.getValueAt(selectedRow, 0);
        String tipo = (String) modeloTabla.getValueAt(selectedRow, 1);
        String cliente = (String) modeloTabla.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Eliminar movimiento?\nFecha: " + fecha + "\nTipo: " + tipo + "\nCliente: " + cliente, 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<CajaMovimientoDTO> lista = controlador.listarMovimientos();
            for (CajaMovimientoDTO dto : lista) {
                String desc = dto.getDescripcion() != null ? dto.getDescripcion() : "";
                if (dto.getFecha().format(fechaFormatter).equals(fecha) 
                    && dto.getTipo().equalsIgnoreCase(tipo)
                    && desc.contains(cliente)) {
                    if (controlador.eliminarMovimiento(dto.getId())) {
                        JOptionPane.showMessageDialog(this, "Movimiento eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarMovimientosPorAnio();
                        actualizarSaldo();
                    }
                    break;
                }
            }
        }
    }

    private void actualizarSaldo() {
        BigDecimal saldo = controlador.getSaldoCaja();
        lblSaldo.setText("Saldo: $" + formatearMonto(saldo));
        
        Integer anio = (Integer) cmbAnio.getSelectedItem();
        if (anio == null) return;
        
        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        List<CajaMovimientoDTO> lista = controlador.listarMovimientos(desde, hasta);
        
        BigDecimal subCobroEfec = BigDecimal.ZERO;
        BigDecimal subCobroPat = BigDecimal.ZERO;
        BigDecimal subPagoEfec = BigDecimal.ZERO;
        BigDecimal subPagoPat = BigDecimal.ZERO;
        BigDecimal subCompraDol = BigDecimal.ZERO;
        
        for (CajaMovimientoDTO dto : lista) {
            String desc = dto.getDescripcion() != null ? dto.getDescripcion() : "";
            BigDecimal monto = dto.getMonto() != null ? dto.getMonto() : BigDecimal.ZERO;
            
            boolean esCompraDolares = desc.contains("COMPRA DÓLARES|") || desc.contains("COMPRA DOLARES|");
            
            if (esCompraDolares) {
                subCompraDol = subCompraDol.add(monto);
            } else if ("cobro".equalsIgnoreCase(dto.getTipo())) {
                if (desc.toLowerCase().contains("efectivo")) {
                    subCobroEfec = subCobroEfec.add(monto);
                } else {
                    subCobroPat = subCobroPat.add(monto);
                }
            } else if ("pago".equalsIgnoreCase(dto.getTipo())) {
                if (desc.toLowerCase().contains("efectivo")) {
                    subPagoEfec = subPagoEfec.add(monto);
                } else {
                    subPagoPat = subPagoPat.add(monto);
                }
            }
        }
        
        lblSubCobroEfectivo.setText("Cobro Efectivo: " + formatearMonto(subCobroEfec));
        lblSubCobroPatagonia.setText("Cobro Patagonia: " + formatearMonto(subCobroPat));
        lblSubPagoEfectivo.setText("Pago Efectivo: " + formatearMonto(subPagoEfec));
        lblSubPagoPatagonia.setText("Pago Patagonia: " + formatearMonto(subPagoPat));
        lblSubCompraDolares.setText("Compra Dólares: " + formatearMonto(subCompraDol));
        
        BigDecimal totalCobros = subCobroEfec.add(subCobroPat);
        BigDecimal totalPagos = subPagoEfec.add(subPagoPat).add(subCompraDol);
        BigDecimal saldoTotal = totalCobros.subtract(totalPagos);
        
        lblSaldoTotal.setText("SALDO: " + formatearMonto(saldoTotal));
    }

    private void cargarMovimientosPorAnio() {
        modeloTabla.setRowCount(0);
        Integer anio = (Integer) cmbAnio.getSelectedItem();
        if (anio == null) return;

        LocalDate desde = LocalDate.of(anio, 1, 1);
        LocalDate hasta = LocalDate.of(anio, 12, 31);
        
        List<CajaMovimientoDTO> lista = controlador.listarMovimientos(desde, hasta);
        for (CajaMovimientoDTO dto : lista) {
            String desc = dto.getDescripcion() != null ? dto.getDescripcion() : "";
            String montoStr = dto.getMonto() != null ? formatearMonto(dto.getMonto()) : "$ 0,00";
            String fechaStr = dto.getFecha() != null ? dto.getFecha().format(fechaFormatter) : "";
            
            boolean esCompraDolares = desc.contains("COMPRA DÓLARES|") || desc.contains("COMPRA DOLARES|");
            String els = "";
            
            if (esCompraDolares) {
                String[] parts = desc.split("\\|");
                String dolaresStr = parts.length > 1 ? "$ " + parts[1] : "";
                String esEfectivoDolar = parts.length > 3 ? parts[3] : "";
                
                String pesosEfectivo = "";
                String pesosBanco = "";
                
                if (esEfectivoDolar.contains("EFECTIVO")) {
                    pesosEfectivo = montoStr;
                } else {
                    pesosBanco = montoStr;
                }
                
                modeloTabla.addRow(new Object[]{
                    fechaStr,
                    "PAGO",
                    "COMPRA DÓLARES",
                    "",
                    "",
                    "",
                    pesosEfectivo,
                    pesosBanco,
                    dolaresStr,
                    els
                });
            } else if ("cobro".equalsIgnoreCase(dto.getTipo())) {
                String[] parts = desc.split(" - ");
                String cliente = parts.length > 0 ? parts[0] : "";
                String formaPagoCompleta = parts.length > 1 ? parts[1] : "";
                String formaPago = formaPagoCompleta.replaceAll("\\s+ELS\\s+\\d+$", "").trim();
                boolean esEfectivo = formaPago.toLowerCase().contains("efectivo");
                
                if (desc.toLowerCase().contains("els")) {
                    int idx = desc.toLowerCase().indexOf("els");
                    if (idx >= 0) {
                        String afterELS = desc.substring(idx + 3).trim();
                        if (!afterELS.isEmpty()) {
                            els = afterELS;
                        }
                    }
                }
                
                modeloTabla.addRow(new Object[]{
                    fechaStr,
                    "COBRO",
                    cliente,
                    formaPago,
                    esEfectivo ? montoStr : "",
                    esEfectivo ? "" : montoStr,
                    "",
                    "",
                    "",
                    els
                });
            } else {
                String[] parts = desc.split(" - ");
                String cliente = parts.length > 0 ? parts[0] : "";
                String formaPagoCompleta = parts.length > 1 ? parts[1] : "";
                String formaPago = formaPagoCompleta.replaceAll("\\s+ELS\\s+\\d+$", "").trim();
                boolean esEfectivo = formaPago.toLowerCase().contains("efectivo");
                
                if (desc.toLowerCase().contains("els")) {
                    int idx = desc.toLowerCase().indexOf("els");
                    if (idx >= 0) {
                        String afterELS = desc.substring(idx + 3).trim();
                        if (!afterELS.isEmpty()) {
                            els = afterELS;
                        }
                    }
                }
                
                modeloTabla.addRow(new Object[]{
                    fechaStr,
                    "PAGO",
                    cliente,
                    formaPago,
                    "",
                    "",
                    esEfectivo ? montoStr : "",
                    esEfectivo ? "" : montoStr,
                    "",
                    els
                });
            }
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

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (getContentPane() != null) {
            getContentPane().setBackground(t.bgBase);
        }
        themeComponent(getContentPane());

        if (lblSubTitulo != null) lblSubTitulo.setForeground(t.brand);
        if (lblDatosMov != null) lblDatosMov.setForeground(t.brand);
        if (lblSaldo != null) lblSaldo.setForeground(t.brand);
        if (lblSaldoTotal != null) lblSaldoTotal.setForeground(t.brand);
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t);
        }
    }

    private void themeComponent(Container container) {
        if (container == null) return;
        for (Component c : container.getComponents()) {
            if (c == null) continue;
            if (c instanceof JPanel) {
                c.setBackground(currentTheme.bgBase);
            } else if (c instanceof JLabel) {
                c.setForeground(currentTheme.textPrimary);
            } else if (c instanceof JButton) {
                c.setForeground(currentTheme.textPrimary);
                c.setBackground(currentTheme.btnBg);
            } else if (c instanceof JTextField) {
                c.setForeground(currentTheme.textPrimary);
                c.setBackground(currentTheme.bgInput);
            } else if (c instanceof JComboBox) {
                c.setForeground(currentTheme.textPrimary);
                c.setBackground(currentTheme.bgElevated);
            } else if (c instanceof JCheckBox) {
                c.setForeground(currentTheme.textPrimary);
                c.setBackground(currentTheme.bgBase);
            } else if (c instanceof JSeparator) {
                c.setForeground(currentTheme.borderLight);
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                if (sp.getViewport() != null) {
                    sp.getViewport().setBackground(currentTheme.bgBase);
                }
            } else if (c instanceof JTable) {
                c.setForeground(currentTheme.textPrimary);
                c.setBackground(currentTheme.bgInput);
                ((JTable) c).setGridColor(currentTheme.borderLight);
                if (((JTable) c).getTableHeader() != null) {
                    Theme.styleTableHeader(((JTable) c).getTableHeader(), currentTheme);
                }
            }
            if (c instanceof Container) {
                themeComponent((Container) c);
            }
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaCaja().setVisible(true));
    }
}