package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorPagos;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.FacturaPagoDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
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
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VentanaPagos extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Cambria", Font.BOLD, 12);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ControladorPagos controlador;

    private JTable tablaFacturas;
    private DefaultTableModel modeloTablaFacturas;

    private JTable tablaItems;
    private DefaultTableModel modeloTablaItems;

    private JTable tablaPagos;
    private DefaultTableModel modeloTablaPagos;

    private JLabel lblSaldoPendiente;
    private JLabel lblInfoFactura;
    private JTextField txtMontoPago;
    private JComboBox<String> cmbFormaPago;
    private JButton btnPagarItem;
    private JButton btnPagar;
    private JButton btnGenerarRecibo;

    private int comprobanteSeleccionadoId = -1;
    private List<Integer> pagosIds;

    public VentanaPagos() {
        controlador = new ControladorPagos();
        initComponents();
        cargarFacturas();
        cargarHistorialCompleto();
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Pagos / Cobranzas");
        setSize(1200, 750);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JSplitPane splitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitHorizontal.setResizeWeight(0.35);
        splitHorizontal.setBorder(null);
        splitHorizontal.setBackground(COLOR_FONDO);

        splitHorizontal.setLeftComponent(crearPanelFacturas());
        splitHorizontal.setRightComponent(crearPanelDetalle());

        add(splitHorizontal);
    }

    private JPanel crearPanelFacturas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TITULO),
                "FACTURAS PENDIENTES",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Cambria", Font.BOLD, 13), COLOR_TEXTO
            )
        ));

        String[] colFacturas = {"ID", "Tipo", "N\u00famero", "Fecha", "Cliente", "Total", "Estado"};
        modeloTablaFacturas = new DefaultTableModel(colFacturas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaFacturas = new JTable(modeloTablaFacturas);
        tablaFacturas.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaFacturas.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaFacturas.getTableHeader().setBackground(COLOR_BOTON);
        tablaFacturas.setRowHeight(22);
        tablaFacturas.setAutoCreateRowSorter(true);
        tablaFacturas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFacturaSeleccionada();
        });

        tablaFacturas.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaFacturas.getColumnModel().getColumn(1).setPreferredWidth(70);
        tablaFacturas.getColumnModel().getColumn(2).setPreferredWidth(90);
        tablaFacturas.getColumnModel().getColumn(3).setPreferredWidth(70);
        tablaFacturas.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablaFacturas.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaFacturas.getColumnModel().getColumn(6).setPreferredWidth(60);

        panel.add(new JScrollPane(tablaFacturas), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelInferior.setBackground(COLOR_FONDO);
        JButton btnRefrescar = crearBoton("REFRESCAR");
        btnRefrescar.addActionListener(e -> cargarFacturas());
        panelInferior.add(btnRefrescar);
        panel.add(panelInferior, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 5, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TITULO),
                "DETALLE DE FACTURA",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Cambria", Font.BOLD, 13), COLOR_TEXTO
            )
        ));

        panel.add(crearPanelHeader(), BorderLayout.NORTH);
        panel.add(crearPanelCentral(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelHeader() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        lblInfoFactura = new JLabel("Seleccione una factura de la lista");
        lblInfoFactura.setFont(new Font("Cambria", Font.BOLD, 11));
        lblInfoFactura.setForeground(COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblInfoFactura, gbc);

        lblSaldoPendiente = new JLabel("Saldo Pendiente: $ 0,00", SwingConstants.RIGHT);
        lblSaldoPendiente.setFont(new Font("Cambria", Font.BOLD, 15));
        lblSaldoPendiente.setForeground(new Color(180, 0, 0));
        gbc.gridx = 1;
        panel.add(lblSaldoPendiente, gbc);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setResizeWeight(0.6);
        splitVertical.setBorder(null);
        splitVertical.setBackground(COLOR_FONDO);

        splitVertical.setTopComponent(crearPanelItems());
        splitVertical.setBottomComponent(crearPanelHistorial());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_FONDO);
        wrapper.add(splitVertical);
        return wrapper;
    }

    private JPanel crearPanelItems() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "ITEMS DE LA FACTURA",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 12), COLOR_TEXTO
        ));

        String[] colItems = {"ID", "C\u00f3digo", "Descripci\u00f3n", "Cant.", "P. Unitario", "Subtotal", "Estado"};
        modeloTablaItems = new DefaultTableModel(colItems, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaItems = new JTable(modeloTablaItems);
        tablaItems.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaItems.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaItems.getTableHeader().setBackground(COLOR_BOTON);
        tablaItems.setRowHeight(22);

        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(250);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(40);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(6).setPreferredWidth(60);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaItems.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tablaItems.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        panel.add(new JScrollPane(tablaItems), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelAcciones.setBackground(COLOR_FONDO);

        btnPagarItem = crearBoton("PAGAR ITEM SELECCIONADO");
        btnPagarItem.addActionListener(e -> pagarItemSeleccionado());
        panelAcciones.add(btnPagarItem);

        panelAcciones.add(new JLabel("   Pago Total / Parcial:"));

        txtMontoPago = new JTextField(10);
        txtMontoPago.setFont(new Font("Cambria", Font.PLAIN, 12));
        txtMontoPago.setHorizontalAlignment(JTextField.RIGHT);
        panelAcciones.add(txtMontoPago);

        cmbFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Cheque", "Tarjeta", "Mercado Pago", "Otra"});
        cmbFormaPago.setFont(FUENTE_BOTON);
        panelAcciones.add(cmbFormaPago);

        btnPagar = crearBoton("REGISTRAR PAGO");
        btnPagar.addActionListener(e -> registrarPago());
        panelAcciones.add(btnPagar);

        panel.add(panelAcciones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "HISTORIAL DE PAGOS",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 12), COLOR_TEXTO
        ));

        String[] colPagos = {"Monto", "Fecha", "Forma de Pago", "Factura", "Recibo N\u00b0", "Seleccionar"};
        modeloTablaPagos = new DefaultTableModel(colPagos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                if (col != 5) return false;
                Object reciboVal = getValueAt(row, 4);
                return reciboVal == null || reciboVal.toString().isEmpty();
            }
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 5 ? Boolean.class : Object.class;
            }
        };
        tablaPagos = new JTable(modeloTablaPagos);
        tablaPagos.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaPagos.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaPagos.getTableHeader().setBackground(COLOR_BOTON);
        tablaPagos.setRowHeight(22);

        tablaPagos.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaPagos.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaPagos.getColumnModel().getColumn(2).setPreferredWidth(120);
        tablaPagos.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaPagos.getColumnModel().getColumn(4).setPreferredWidth(70);
        tablaPagos.getColumnModel().getColumn(5).setPreferredWidth(70);

        DefaultTableCellRenderer montoRenderer = new DefaultTableCellRenderer();
        montoRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaPagos.getColumnModel().getColumn(0).setCellRenderer(montoRenderer);

        panel.add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelAcciones.setBackground(COLOR_FONDO);
        btnGenerarRecibo = crearBoton("GENERAR RECIBO");
        btnGenerarRecibo.addActionListener(e -> generarReciboDesdePago());
        panelAcciones.add(btnGenerarRecibo);
        JButton btnVerReciboPagos = crearBoton("VER RECIBO");
        btnVerReciboPagos.addActionListener(e -> verReciboPagoSeleccionado());
        panelAcciones.add(btnVerReciboPagos);
        panel.add(panelAcciones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarFacturas() {
        modeloTablaFacturas.setRowCount(0);
        List<ComprobanteDTO> lista = controlador.listarFacturasPendientes();
        for (ComprobanteDTO c : lista) {
            String estado = c.getEstadoPago();
            String estadoDisplay = "pendiente".equals(estado) ? "Pendiente"
                : "pagada_parcial".equals(estado) ? "Parcial"
                : "pagada_total".equals(estado) ? "Pagada" : estado;
            String totalStr = c.getImporteTotal() != null ? "$ " + DF.format(c.getImporteTotal()) : "";
            modeloTablaFacturas.addRow(new Object[]{
                c.getId(),
                c.getTipoComprobanteStr(),
                String.format("%04d-%08d", c.getPuntoVenta(), c.getNumero()),
                c.getFechaEmision() != null ? c.getFechaEmision().format(FMT) : "",
                c.getRazonSocialRec(),
                totalStr,
                estadoDisplay
            });
        }
        comprobanteSeleccionadoId = -1;
        lblInfoFactura.setText("Seleccione una factura de la lista");
        lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
        modeloTablaItems.setRowCount(0);
    }

    private void cargarFacturaSeleccionada() {
        int row = tablaFacturas.getSelectedRow();
        if (row < 0) return;
        if (modeloTablaFacturas.getValueAt(row, 0) == null) return;
        comprobanteSeleccionadoId = (Integer) modeloTablaFacturas.getValueAt(row, 0);
        cargarDetalleFactura(comprobanteSeleccionadoId);
    }

    private void cargarDetalleFactura(int facturaId) {
        ComprobanteDTO comp = controlador.buscarFactura(facturaId);
        if (comp != null) {
            String info = "Factura: " + comp.getTipoComprobanteStr()
                + " " + String.format("%04d-%08d", comp.getPuntoVenta(), comp.getNumero())
                + "  |  Cliente: " + comp.getRazonSocialRec()
                + "  |  Total: $ " + DF.format(comp.getImporteTotal());
            lblInfoFactura.setText(info);
            BigDecimal saldo = controlador.getSaldoPendiente(facturaId);
            lblSaldoPendiente.setText("Saldo Pendiente: $ " + DF.format(saldo));
        } else {
            lblInfoFactura.setText("Factura #" + facturaId + " (eliminada)");
            lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
        }

        List<ItemFacturaDTO> items = controlador.getItemsFactura(facturaId);
        modeloTablaItems.setRowCount(0);
        for (ItemFacturaDTO item : items) {
            modeloTablaItems.addRow(new Object[]{
                item.getId(), item.getCodigo(), item.getDescripcion(),
                item.getCantidad(),
                item.getPrecioUnitario() != null ? DF.format(item.getPrecioUnitario()) : "",
                item.getSubtotal() != null ? DF.format(item.getSubtotal()) : "",
                item.getEstadoPago() != null ? item.getEstadoPago() : ""
            });
        }

    }

    private void cargarHistorialCompleto() {
        List<FacturaPagoDTO> pagos = controlador.getTodosLosPagos();
        modeloTablaPagos.setRowCount(0);
        pagosIds = new ArrayList<>();
        for (FacturaPagoDTO p : pagos) {
            pagosIds.add(p.getId());
            modeloTablaPagos.addRow(new Object[]{
                p.getMonto() != null ? "$ " + DF.format(p.getMonto()) : "",
                p.getFechaPago() != null ? p.getFechaPago().format(FMT) : "",
                p.getFormaPago() != null ? p.getFormaPago() : "",
                p.getComprobanteStr() != null ? p.getComprobanteStr() : "",
                p.getReciboId() != null ? p.getReciboNumero() : "",
                p.getReciboId() == null ? Boolean.FALSE : Boolean.TRUE
            });
        }
    }

    private void registrarPago() {
        if (comprobanteSeleccionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String montoStr = txtMontoPago.getText().trim();
        if (montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BigDecimal monto;
        try {
            monto = new BigDecimal(montoStr.replace(",", "."));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto inv\u00e1lido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String formaPago = (String) cmbFormaPago.getSelectedItem();

        BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
        if (monto.compareTo(saldo) > 0) {
            JOptionPane.showMessageDialog(this, "El monto ($ " + DF.format(monto)
                + ") supera el saldo pendiente ($ " + DF.format(saldo) + ")", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        controlador.registrarPago(comprobanteSeleccionadoId, monto, formaPago, null);
        JOptionPane.showMessageDialog(this, "Pago registrado correctamente", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
        txtMontoPago.setText("");
        recargarYSeleccionarFactura(comprobanteSeleccionadoId);
    }

    private void pagarItemSeleccionado() {
        if (comprobanteSeleccionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int itemRow = tablaItems.getSelectedRow();
        if (itemRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un item de la factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object idObj = modeloTablaItems.getValueAt(itemRow, 0);
        if (idObj == null) return;
        int itemId = (Integer) idObj;

        String estadoPago = (String) modeloTablaItems.getValueAt(itemRow, 6);
        if ("pagado".equals(estadoPago)) {
            JOptionPane.showMessageDialog(this, "Este item ya fue pagado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object subtotalObj = modeloTablaItems.getValueAt(itemRow, 5);
        if (subtotalObj == null) return;
        BigDecimal monto;
        try {
            monto = new BigDecimal(subtotalObj.toString().replace(",", "").replace("$", "").trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener el monto del item", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String formaPagoItem = (String) cmbFormaPago.getSelectedItem();
        boolean todosPagados = controlador.registrarPagoItem(itemId, comprobanteSeleccionadoId, monto, formaPagoItem, null);
        JOptionPane.showMessageDialog(this, "Item marcado como pagado", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);

        if (todosPagados) {
            controlador.setEstadoFactura(comprobanteSeleccionadoId, "pagada_total");
        } else {
            controlador.setEstadoFactura(comprobanteSeleccionadoId, "pagada_parcial");
        }

        recargarYSeleccionarFactura(comprobanteSeleccionadoId);
    }

    private void generarReciboDesdePago() {
        if (pagosIds == null || pagosIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pagos en el historial", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> idsSeleccionados = new ArrayList<>();
        for (int i = 0; i < modeloTablaPagos.getRowCount(); i++) {
            Boolean checked = (Boolean) modeloTablaPagos.getValueAt(i, 5);
            if (Boolean.TRUE.equals(checked)) {
                idsSeleccionados.add(pagosIds.get(i));
            }
        }

        if (idsSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un pago con el checkbox", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String reciboNro = controlador.generarReciboDesdePagos(idsSeleccionados);

        if (reciboNro == null) {
            JOptionPane.showMessageDialog(this, "Error al generar el recibo", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Recibo " + reciboNro + " generado correctamente", "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
            recargarYSeleccionarFactura(comprobanteSeleccionadoId);
        }
    }

    private void verReciboPagoSeleccionado() {
        int row = tablaPagos.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pago del historial", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Object reciboObj = modeloTablaPagos.getValueAt(row, 4);
        if (reciboObj == null || reciboObj.toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El pago seleccionado no tiene un recibo asociado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String reciboNro = reciboObj.toString();
        String ruta = com.els.facturacion.pdf.GestorReciboPDF.getRutaPDF(reciboNro);
        if (ruta == null) {
            JOptionPane.showMessageDialog(this, "No se pudo determinar la ubicaci\u00f3n del PDF", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File pdf = new File(ruta);
        if (!pdf.exists()) {
            JOptionPane.showMessageDialog(this, "El archivo PDF no existe:\n" + ruta, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Desktop.getDesktop().open(pdf);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al abrir el PDF:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recargarYSeleccionarFactura(int facturaId) {
        cargarFacturas();
        cargarHistorialCompleto();
        boolean reencontrada = false;
        for (int i = 0; i < modeloTablaFacturas.getRowCount(); i++) {
            Object idObj = modeloTablaFacturas.getValueAt(i, 0);
            if (idObj != null && (Integer) idObj == facturaId) {
                tablaFacturas.setRowSelectionInterval(i, i);
                reencontrada = true;
                break;
            }
        }
        if (!reencontrada) {
            comprobanteSeleccionadoId = facturaId;
            cargarDetalleFactura(facturaId);
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
