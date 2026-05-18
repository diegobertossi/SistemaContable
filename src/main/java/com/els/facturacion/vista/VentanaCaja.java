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
    private JComboBox<Integer> cmbAnio;
    private JLabel lblSaldo;
    private DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private JTextField txtELS;

    public VentanaCaja() {
        controlador = new ControladorCaja();
        initComponents();
        cargarMovimientosPorAnio();
        actualizarSaldo();
    }

    private void initComponents() {
        setTitle("Caja - Movimiento de Fondos");
        setSize(1000, 600);
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

        String[] columnas = {"Fecha", "Tipo", "Cliente", "Forma de Pago", "Cobro Efectivo", "Cobro Patagonia", "Pago Efectivo", "Pago Patagonia", "Compra Dólares", "ELS"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Cambria", Font.PLAIN, 10));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < columnas.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(100);
        }
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new java.awt.Dimension(1000, 250));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(FUENTE_BOTON);
        lblFecha.setForeground(COLOR_TEXTO);

        JCheckBox chkCompraDolares = new JCheckBox("Compra Dólares");
        chkCompraDolares.setFont(FUENTE_BOTON);
        chkCompraDolares.setForeground(COLOR_TEXTO);
        chkCompraDolares.setBackground(COLOR_FONDO);

        JLabel lblCliente = new JLabel("Cliente/Nota:");
        lblCliente.setFont(FUENTE_BOTON);
        lblCliente.setForeground(COLOR_TEXTO);

        JLabel lblMovimiento = new JLabel("Movimiento:");
        lblMovimiento.setFont(FUENTE_BOTON);
        lblMovimiento.setForeground(COLOR_TEXTO);

        JLabel lblFormaPago = new JLabel("Forma Pago:");
        lblFormaPago.setFont(FUENTE_BOTON);
        lblFormaPago.setForeground(COLOR_TEXTO);

        JLabel lblELS = new JLabel("ELS:");
        lblELS.setFont(FUENTE_BOTON);
        lblELS.setForeground(COLOR_TEXTO);

        JTextField txtFecha = new JTextField(10);
        txtFecha.setFont(new Font("Cambria", Font.PLAIN, 11));
        txtFecha.setText(LocalDate.now().format(fechaFormatter));

        JTextField txtDolares = new JTextField(8);
        txtDolares.setFont(new Font("Cambria", Font.PLAIN, 11));
        txtDolares.setEnabled(false);

        JTextField txtCotizacion = new JTextField(8);
        txtCotizacion.setFont(new Font("Cambria", Font.PLAIN, 11));
        txtCotizacion.setEnabled(false);

        JTextField txtPesosGastados = new JTextField(10);
        txtPesosGastados.setFont(new Font("Cambria", Font.PLAIN, 11));
        txtPesosGastados.setEnabled(false);
        txtPesosGastados.setEditable(false);

        JTextField txtCliente = new JTextField(18);
        txtCliente.setFont(new Font("Cambria", Font.PLAIN, 11));

        JComboBox<String> cmbMovimiento = new JComboBox<>(new String[]{"Cobro", "Pago"});
        cmbMovimiento.setFont(FUENTE_BOTON);

        JComboBox<String> cmbFormaPago = new JComboBox<>(new String[]{
            "Efectivo", "Transferencia", "Transferencia+Efectivo", "Débito", "Otra"
        });
        cmbFormaPago.setFont(FUENTE_BOTON);

        JTextField txtEfectivo = new JTextField(8);
        txtEfectivo.setFont(new Font("Cambria", Font.PLAIN, 11));

        JLabel lblELS1 = new JLabel("N° ELS:");
        lblELS1.setFont(FUENTE_BOTON);
        lblELS1.setForeground(COLOR_TEXTO);

        txtELS = new JTextField(8);
        txtELS.setFont(new Font("Cambria", Font.PLAIN, 11));

        chkCompraDolares.addActionListener(e -> {
            boolean enabled = chkCompraDolares.isSelected();
            txtDolares.setEnabled(enabled);
            txtCotizacion.setEnabled(enabled);
            cmbFormaPago.setEnabled(true);
            txtCliente.setEnabled(!enabled);
            cmbMovimiento.setEnabled(!enabled);
            txtEfectivo.setEnabled(!enabled);
            txtELS.setEnabled(!enabled);
            
            if (enabled) {
                txtEfectivo.setText("");
                txtCliente.setText("");
                cmbMovimiento.setSelectedIndex(0);
                cmbFormaPago.setSelectedIndex(0);
                txtELS.setText("");
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
        panelBotones.setBackground(COLOR_FONDO);

        JButton btnAgregar = crearBoton("AGREGAR");
        btnAgregar.addActionListener(e -> {
            try {
                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim(), fechaFormatter);
                String cliente = txtCliente.getText().trim();
                String formaPago = (String) cmbFormaPago.getSelectedItem();
                String movimiento = (String) cmbMovimiento.getSelectedItem();
                String numeroELS = txtELS.getText().trim();
                String elsTexto = numeroELS.isEmpty() ? "" : " - ELS " + numeroELS;

                if (chkCompraDolares.isSelected()) {
                    BigDecimal dolares = new BigDecimal(txtDolares.getText().trim());
                    BigDecimal cotizacion = new BigDecimal(txtCotizacion.getText().trim());
                    BigDecimal pesos = dolares.multiply(cotizacion);
                    
                    String formaPagoDolar = (String) cmbFormaPago.getSelectedItem();
                    String esEfectivoDolar = formaPagoDolar.equals("Efectivo") ? "EFECTIVO" : "BANCO";
                    
                    String descripcion = "COMPRA DÓLARES|" + dolares.toString() + "|" + cotizacion.toString() + "|" + esEfectivoDolar;
                    int id = controlador.registrarMovimiento(fecha, "pago", descripcion, pesos);
                    
                    if (id > 0) {
                        String tipoPago = formaPagoDolar.equals("Efectivo") ? "Efectivo" : formaPagoDolar;
                        JOptionPane.showMessageDialog(this, "Compra de dólares registrada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        txtDolares.setText("");
                        txtCotizacion.setText("");
                        txtPesosGastados.setText("");
                        cargarMovimientosPorAnio();
                        actualizarSaldo();
                    }
                } else {
                    BigDecimal monto = BigDecimal.ZERO;
                    boolean tieneDatos = false;

                    if (!txtEfectivo.getText().trim().isEmpty()) {
                        monto = new BigDecimal(txtEfectivo.getText().trim());
                        tieneDatos = true;
                    }

                    if (!tieneDatos || cliente.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Complete los datos requeridos", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String tipoMov = movimiento.equals("Cobro") ? "cobro" : "pago";
                    boolean esEfectivo = formaPago.equals("Efectivo");
                    String descripcion = esEfectivo 
                        ? cliente + " - Efectivo" + elsTexto 
                        : cliente + " - " + formaPago + elsTexto;

                    int id = controlador.registrarMovimiento(fecha, tipoMov, descripcion, monto);
                    if (id > 0) {
                        JOptionPane.showMessageDialog(this, "Movimiento registrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        txtCliente.setText("");
                        txtEfectivo.setText("");
                        txtFecha.setText(LocalDate.now().format(fechaFormatter));
                        cargarMovimientosPorAnio();
                        actualizarSaldo();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnEliminar = crearBoton("ELIMINAR");
        btnEliminar.addActionListener(e -> btnEliminarAction());

        JButton btnActualizar = crearBoton("ACTUALIZAR");
        btnActualizar.addActionListener(e -> {
            cargarMovimientosPorAnio();
            actualizarSaldo();
        });

        JButton btnEditar = crearBoton("EDITAR");
        btnEditar.addActionListener(e -> btnEditarAction());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(lblFecha, gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtFecha, gbc);
        gbc.gridx = 2;
        panelFormulario.add(chkCompraDolares, gbc);
        gbc.gridx = 3;
        panelFormulario.add(new JLabel("Dólares:"), gbc);
        gbc.gridx = 4;
        panelFormulario.add(txtDolares, gbc);
        gbc.gridx = 5;
        panelFormulario.add(new JLabel("Cotización:"), gbc);
        gbc.gridx = 6;
        panelFormulario.add(txtCotizacion, gbc);
        gbc.gridx = 7;
        panelFormulario.add(new JLabel("Pesos:"), gbc);
        gbc.gridx = 8;
        panelFormulario.add(txtPesosGastados, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(lblCliente, gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelFormulario.add(txtCliente, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 3;
        panelFormulario.add(lblMovimiento, gbc);
        gbc.gridx = 4;
        panelFormulario.add(cmbMovimiento, gbc);
        gbc.gridx = 5;
        panelFormulario.add(lblFormaPago, gbc);
        gbc.gridx = 6;
        panelFormulario.add(cmbFormaPago, gbc);

gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtEfectivo, gbc);
        gbc.gridx = 2;
        panelFormulario.add(lblELS1, gbc);
        gbc.gridx = 3;
        panelFormulario.add(txtELS, gbc);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_FONDO);
        panelSur.add(panelFormulario, BorderLayout.CENTER);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
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

    private void btnEditarAction() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un movimiento para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fechaActual = (String) modeloTabla.getValueAt(selectedRow, 0);
        String tipoActual = (String) modeloTabla.getValueAt(selectedRow, 1);
        String clienteActual = (String) modeloTabla.getValueAt(selectedRow, 2);
        String formaPagoActual = (String) modeloTabla.getValueAt(selectedRow, 3);
        String montoActual = (String) modeloTabla.getValueAt(selectedRow, 4);
        if (montoActual.isEmpty()) montoActual = (String) modeloTabla.getValueAt(selectedRow, 5);
        if (montoActual.isEmpty()) montoActual = (String) modeloTabla.getValueAt(selectedRow, 6);
        if (montoActual.isEmpty()) montoActual = (String) modeloTabla.getValueAt(selectedRow, 7);
        String elsActual = (String) modeloTabla.getValueAt(selectedRow, 9);

        JTextField txtFechaEdit = new JTextField(fechaActual, 10);
        JTextField txtClienteEdit = new JTextField(clienteActual, 20);
        JTextField txtFormaPagoEdit = new JTextField(formaPagoActual, 15);
        JTextField txtMontoEdit = new JTextField(montoActual.replace("$", ""), 10);
        JTextField txtELSEdit = new JTextField(elsActual != null ? elsActual : "", 8);

        JPanel panelEdit = new JPanel(new GridBagLayout());
        panelEdit.setBackground(COLOR_FONDO);
        GridBagConstraints gbcEdit = new GridBagConstraints();
        gbcEdit.insets = new Insets(5, 5, 5, 5);
        gbcEdit.fill = GridBagConstraints.HORIZONTAL;

        gbcEdit.gridx = 0; gbcEdit.gridy = 0;
        panelEdit.add(new JLabel("Fecha:"), gbcEdit);
        gbcEdit.gridx = 1;
        panelEdit.add(txtFechaEdit, gbcEdit);

        gbcEdit.gridx = 0; gbcEdit.gridy = 1;
        panelEdit.add(new JLabel("Cliente:"), gbcEdit);
        gbcEdit.gridx = 1;
        panelEdit.add(txtClienteEdit, gbcEdit);

        gbcEdit.gridx = 0; gbcEdit.gridy = 2;
        panelEdit.add(new JLabel("Forma Pago:"), gbcEdit);
        gbcEdit.gridx = 1;
        panelEdit.add(txtFormaPagoEdit, gbcEdit);

        gbcEdit.gridx = 0; gbcEdit.gridy = 3;
        panelEdit.add(new JLabel("Monto:"), gbcEdit);
        gbcEdit.gridx = 1;
        panelEdit.add(txtMontoEdit, gbcEdit);

        gbcEdit.gridx = 0; gbcEdit.gridy = 4;
        panelEdit.add(new JLabel("ELS:"), gbcEdit);
        gbcEdit.gridx = 1;
        panelEdit.add(txtELSEdit, gbcEdit);

        int result = JOptionPane.showConfirmDialog(this, panelEdit, "Editar Movimiento", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            LocalDate fecha = null;
            String nuevoCliente = "";
            String nuevaFormaPago = "";
            BigDecimal nuevoMonto = BigDecimal.ZERO;
            String nuevoELS = "";
            
            try {
                if (!txtFechaEdit.getText().trim().isEmpty()) {
                    fecha = LocalDate.parse(txtFechaEdit.getText().trim(), fechaFormatter);
                }
                nuevoCliente = txtClienteEdit.getText().trim();
                nuevaFormaPago = txtFormaPagoEdit.getText().trim();
                nuevoMonto = new BigDecimal(txtMontoEdit.getText().trim());
                nuevoELS = txtELSEdit.getText().trim();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al parsear datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String elsTexto = nuevoELS.isEmpty() ? "" : " ELS " + nuevoELS;

            List<CajaMovimientoDTO> lista = controlador.listarMovimientos();
            for (CajaMovimientoDTO dto : lista) {
                String desc = dto.getDescripcion() != null ? dto.getDescripcion() : "";
                boolean fechaMatch = (fecha == null && dto.getFecha() == null) || 
                    (fecha != null && dto.getFecha() != null && dto.getFecha().format(fechaFormatter).equals(fechaActual));

                if (fechaMatch && dto.getTipo().equalsIgnoreCase(tipoActual) && desc.contains(clienteActual)) {
                    String nuevaDesc = nuevoCliente;
                    if (!nuevaFormaPago.isEmpty()) {
                        nuevaDesc += " - " + nuevaFormaPago;
                    }
                    nuevaDesc += elsTexto;

                    dto.setFecha(fecha);
                    dto.setDescripcion(nuevaDesc);
                    dto.setMonto(nuevoMonto);

                    if (controlador.actualizarMovimiento(dto)) {
                        JOptionPane.showMessageDialog(this, "Movimiento actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarMovimientosPorAnio();
                        actualizarSaldo();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al actualizar", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                }
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
            String desc = dto.getDescripcion() != null ? dto.getDescripcion() : "";
            String montoStr = dto.getMonto() != null ? "$" + dto.getMonto().toString() : "$0.00";
            String fechaStr = dto.getFecha() != null ? dto.getFecha().format(fechaFormatter) : "";
            
            boolean esCompraDolares = desc.contains("COMPRA DÓLARES|") || desc.contains("COMPRA DOLARES|");
            String els = "";
            
            if (esCompraDolares) {
                String[] parts = desc.split("\\|");
                String dolaresStr = parts.length > 1 ? "$" + parts[1] : "";
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
                String formaPago = parts.length > 1 ? parts[1] : "";
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
                String formaPago = parts.length > 1 ? parts[1] : "";
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

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaCaja().setVisible(true));
    }
}