package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorPagos;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.FacturaPagoDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
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
    private JLabel lblTotalFactura;
    private JTextField txtMontoPago;
    private JComboBox<String> cmbFormaPago;
    private JButton btnPagarItem;
    private JButton btnPagarCompleta;
    private JButton btnPagar;
    private JButton btnGenerarRecibo;
    private JButton btnRefresh;
    private JButton btnVerRecibo;

    private int comprobanteSeleccionadoId = -1;
    private List<Integer> pagosIds;
    private List<Integer> facturaIds;
    private List<Integer> itemIds;
    private List<String> reciboOriginalNums;
    private boolean txtMontoPagoUpdating = false;

    public VentanaPagos() {
        controlador = new ControladorPagos();
        initComponents();
        cargarFacturas();
        cargarHistorialCompleto();
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Pagos / Cobranzas");
        setSize(1024, 750);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JSplitPane splitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitHorizontal.setBorder(null);
        splitHorizontal.setBackground(COLOR_FONDO);

        btnPagarItem = new JButton("PAGAR ITEM");
        estilizarBoton(btnPagarItem);
        btnPagarItem.setPreferredSize(new Dimension(120, 28));
        btnPagarItem.addActionListener(e -> {
            if (comprobanteSeleccionadoId < 0) { JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            int itemRow = tablaItems.getSelectedRow();
            if (itemRow < 0) { JOptionPane.showMessageDialog(this, "Seleccione un item de la factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if ("pagado".equals(modeloTablaItems.getValueAt(itemRow, 5))) { JOptionPane.showMessageDialog(this, "Este item ya fue pagado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de pagar este item?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String fp = mostrarSelectorMedioPago();
                if (fp != null) { cmbFormaPago.setSelectedItem(fp); pagarItemSeleccionado(); }
            }
        });

        btnPagarCompleta = new JButton("PAGAR FACTURA");
        estilizarBoton(btnPagarCompleta);
        btnPagarCompleta.setPreferredSize(new Dimension(135, 28));
        btnPagarCompleta.addActionListener(e -> {
            if (comprobanteSeleccionadoId < 0) { JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
            if (saldo.compareTo(BigDecimal.ZERO) <= 0) { JOptionPane.showMessageDialog(this, "La factura ya est\u00e1 totalmente pagada", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de pagar toda la factura?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String fp = mostrarSelectorMedioPago();
                if (fp != null) { cmbFormaPago.setSelectedItem(fp); pagarFacturaCompleta(); }
            }
        });

        btnPagar = new JButton("PAGAR");
        estilizarBoton(btnPagar);
        btnPagar.setPreferredSize(new Dimension(70, 28));
        btnPagar.addActionListener(e -> {
            if (comprobanteSeleccionadoId < 0) { JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE); return; }
            String montoStr = txtMontoPago.getText().trim().replace("$", "").trim();
            if (montoStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese un monto", "Error", JOptionPane.ERROR_MESSAGE); return; }
            BigDecimal monto;
            try { monto = new BigDecimal(montoStr.replace(".", "").replace(",", ".")); }
            catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Monto inv\u00e1lido", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (monto.compareTo(BigDecimal.ZERO) <= 0) { JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE); return; }
            BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
            if (monto.compareTo(saldo) > 0) { JOptionPane.showMessageDialog(this, "El monto ($ " + DF.format(monto) + ") supera el saldo pendiente ($ " + DF.format(saldo) + ")", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de registrar este pago por $ " + DF.format(monto) + "?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String fp = mostrarSelectorMedioPago();
                if (fp != null) { cmbFormaPago.setSelectedItem(fp); registrarPago(); }
            }
        });

        btnRefresh = new JButton("REFRESCAR");
        estilizarBoton(btnRefresh);
        btnRefresh.setPreferredSize(new Dimension(110, 28));
        btnRefresh.addActionListener(e -> cargarFacturas());

        btnVerRecibo = new JButton("VER RECIBO");
        estilizarBoton(btnVerRecibo);
        btnVerRecibo.setPreferredSize(new Dimension(110, 28));
        btnVerRecibo.addActionListener(e -> verReciboPagoSeleccionado());

        btnGenerarRecibo = new JButton("GENERAR RECIBO");
        estilizarBoton(btnGenerarRecibo);
        btnGenerarRecibo.setPreferredSize(new Dimension(140, 28));
        btnGenerarRecibo.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de generar el recibo?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                generarReciboDesdePago();
        });

        JPanel panelIzq = crearPanelFacturas();
        JPanel panelDer = crearPanelDetalle();
        panelIzq.setMinimumSize(new Dimension(0, 0));
        panelDer.setMinimumSize(new Dimension(0, 0));
        splitHorizontal.setLeftComponent(panelIzq);
        splitHorizontal.setRightComponent(panelDer);

        getContentPane().add(splitHorizontal);
        javax.swing.SwingUtilities.invokeLater(() -> splitHorizontal.setDividerLocation(0.45));
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

        String[] colFacturas = {"Tipo", "N\u00famero", "Fecha", "Cliente", "Total", "Estado"};
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

        tablaFacturas.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaFacturas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaFacturas.getColumnModel().getColumn(2).setPreferredWidth(70);
        tablaFacturas.getColumnModel().getColumn(3).setPreferredWidth(170);
        tablaFacturas.getColumnModel().getColumn(4).setPreferredWidth(90);
        tablaFacturas.getColumnModel().getColumn(5).setPreferredWidth(70);

        panel.add(new JScrollPane(tablaFacturas), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelInferior.setBackground(COLOR_FONDO);
        panelInferior.add(btnRefresh);
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(6, 8, 10, 8));

        lblSaldoPendiente = new JLabel("Saldo Pendiente: $ 0,00");
        lblSaldoPendiente.setFont(new Font("Cambria", Font.BOLD, 15));
        lblSaldoPendiente.setForeground(new Color(180, 0, 0));
        lblSaldoPendiente.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblSaldoPendiente);

        panel.add(Box.createVerticalStrut(4));

        lblInfoFactura = new JLabel("Seleccione una factura de la lista");
        lblInfoFactura.setFont(new Font("Cambria", Font.BOLD, 15));
        lblInfoFactura.setForeground(COLOR_TEXTO);
        lblInfoFactura.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblInfoFactura);

        lblTotalFactura = new JLabel("");
        lblTotalFactura.setFont(new Font("Cambria", Font.BOLD, 15));
        lblTotalFactura.setForeground(COLOR_TEXTO);
        lblTotalFactura.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTotalFactura);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setResizeWeight(0.4);
        splitVertical.setBorder(null);
        splitVertical.setBackground(COLOR_FONDO);

        splitVertical.setTopComponent(crearPanelItems());
        splitVertical.setBottomComponent(crearPanelHistorial());
        javax.swing.SwingUtilities.invokeLater(() -> splitVertical.setDividerLocation(0.4));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_FONDO);
        wrapper.add(splitVertical);
        return wrapper;
    }

    private JPanel crearPanelItems() {
        JPanel panel = new JPanel(new BorderLayout(3, 3));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "ITEMS DE LA FACTURA",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 11), COLOR_TEXTO
        ));

        String[] colItems = {"ELS", "Descripci\u00f3n", "Cant.", "P. Unitario", "Subtotal", "Estado"};
        modeloTablaItems = new DefaultTableModel(colItems, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaItems = new JTable(modeloTablaItems);
        tablaItems.setFont(new Font("Cambria", Font.PLAIN, 10));
        tablaItems.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 10));
        tablaItems.getTableHeader().setBackground(COLOR_BOTON);
        tablaItems.setRowHeight(18);

        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(55);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(220);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(35);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(75);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(75);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(55);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c = 0; c < tablaItems.getColumnCount(); c++) {
            if (c != 1) tablaItems.getColumnModel().getColumn(c).setCellRenderer(centerRenderer);
        }

        panel.add(new JScrollPane(tablaItems), BorderLayout.CENTER);

        txtMontoPago = new JTextField();
        txtMontoPago.setFont(new Font("Cambria", Font.BOLD, 14));
        txtMontoPago.setHorizontalAlignment(JTextField.RIGHT);
        txtMontoPago.setPreferredSize(new Dimension(130, 28));
        txtMontoPago.setMinimumSize(new Dimension(130, 28));

        ((AbstractDocument) txtMontoPago.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                if (txtMontoPagoUpdating || esNumeroValido(fb, offset, text)) {
                    super.insertString(fb, offset, text, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (txtMontoPagoUpdating || esNumeroValido(fb, offset, text)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
            }
            private boolean esNumeroValido(FilterBypass fb, int offset, String text) throws BadLocationException {
                if (text == null || text.isEmpty()) return true;
                String curText = fb.getDocument().getText(0, fb.getDocument().getLength());
                StringBuilder sb = new StringBuilder(curText);
                sb.insert(offset, text);
                String nuevo = sb.toString();
                if (nuevo.isEmpty()) return true;
                return nuevo.matches("\\d*(,\\d{0,2})?");
            }
        });

        txtMontoPago.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                txtMontoPago.selectAll();
            }
        });

        txtMontoPago.addActionListener(e -> {
            String raw = txtMontoPago.getText().trim().replace("$", "").trim();
            if (!raw.isEmpty()) {
                try {
                    String clean = raw.replace(".", "").replace(",", ".");
                    BigDecimal val = new BigDecimal(clean);
                    txtMontoPagoUpdating = true;
                    txtMontoPago.setText("$ " + DF.format(val));
                    txtMontoPagoUpdating = false;
                } catch (Exception ignored) {}
            }
        });

        cmbFormaPago = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Cheque", "Tarjeta", "Mercado Pago", "Otra"});
        cmbFormaPago.setFont(FUENTE_BOTON);
        cmbFormaPago.setPreferredSize(new Dimension(90, 28));

        JPanel box1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        box1.setBackground(COLOR_FONDO);
        box1.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        box1.add(btnPagarItem);
        box1.add(btnPagarCompleta);

        JPanel box2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        box2.setBackground(COLOR_FONDO);
        box2.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        box2.add(new JLabel("Importe:"));
        box2.add(txtMontoPago);
        box2.add(btnPagar);

        JPanel panelAcciones = new JPanel();
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));
        panelAcciones.setBackground(COLOR_FONDO);
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(8, 4, 6, 4));
        panelAcciones.add(box1);
        panelAcciones.add(Box.createVerticalStrut(6));
        panelAcciones.add(box2);

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

        int[] pagosAnchos = {75, 65, 80, 85, 80, 40};
        for (int i = 0; i < pagosAnchos.length; i++) {
            tablaPagos.getColumnModel().getColumn(i).setPreferredWidth(pagosAnchos[i]);
        }

        DefaultTableCellRenderer centerPagosRenderer = new DefaultTableCellRenderer();
        centerPagosRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaPagos.getColumnCount(); i++) {
            if (i == 5) continue;
            tablaPagos.getColumnModel().getColumn(i).setCellRenderer(centerPagosRenderer);
        }

        DefaultTableCellRenderer headerPagosRenderer = (DefaultTableCellRenderer) tablaPagos.getTableHeader().getDefaultRenderer();
        headerPagosRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelAcciones.setBackground(COLOR_FONDO);
        panelAcciones.add(btnGenerarRecibo);
        panelAcciones.add(btnVerRecibo);
        panel.add(panelAcciones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarFacturas() {
        modeloTablaFacturas.setRowCount(0);
        facturaIds = new ArrayList<>();
        List<ComprobanteDTO> lista = controlador.listarFacturasPendientes();
        for (ComprobanteDTO c : lista) {
            facturaIds.add(c.getId());
            String estado = c.getEstadoPago();
            String estadoDisplay = "pendiente".equals(estado) ? "Pendiente"
                : "pagada_parcial".equals(estado) ? "Parcial"
                : "pagada_total".equals(estado) ? "Pagada" : estado;
            String totalStr = c.getImporteTotal() != null ? "$ " + DF.format(c.getImporteTotal()) : "";
            modeloTablaFacturas.addRow(new Object[]{
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
        lblTotalFactura.setText("");
        lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
        modeloTablaItems.setRowCount(0);
    }

    private void cargarFacturaSeleccionada() {
        int row = tablaFacturas.getSelectedRow();
        if (row < 0) return;
        if (facturaIds == null || row >= facturaIds.size()) return;
        comprobanteSeleccionadoId = facturaIds.get(row);
        cargarDetalleFactura(comprobanteSeleccionadoId);
    }

    private void cargarDetalleFactura(int facturaId) {
        ComprobanteDTO comp = controlador.buscarFactura(facturaId);
        if (comp != null) {
            String tipoNum = comp.getTipoComprobanteStr()
                + " " + String.format("%04d-%08d", comp.getPuntoVenta(), comp.getNumero());
            String cliente = "Cliente: " + comp.getRazonSocialRec();
            String total = "Total: $ " + DF.format(comp.getImporteTotal());
            lblInfoFactura.setText(tipoNum + "  |  " + cliente);
            lblTotalFactura.setText(total);
            BigDecimal saldo = controlador.getSaldoPendiente(facturaId);
            lblSaldoPendiente.setText("Saldo Pendiente: $ " + DF.format(saldo));
            txtMontoPagoUpdating = true;
            txtMontoPago.setText("$ " + DF.format(saldo));
            txtMontoPagoUpdating = false;
        } else {
            lblInfoFactura.setText("#" + facturaId + " (eliminada)");
            lblTotalFactura.setText("");
            lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
            txtMontoPago.setText("");
        }

        List<ItemFacturaDTO> items = controlador.getItemsFactura(facturaId);
        modeloTablaItems.setRowCount(0);
        itemIds = new ArrayList<>();
        for (int i = items.size() - 1; i >= 0; i--) {
            ItemFacturaDTO item = items.get(i);
            itemIds.add(item.getId());
            String cantStr = item.getCantidad() != null ? String.valueOf(item.getCantidad().intValue()) : "";
            modeloTablaItems.addRow(new Object[]{
                item.getCodigo(), item.getDescripcion(),
                cantStr,
                item.getPrecioUnitario() != null ? "$ " + DF.format(item.getPrecioUnitario()) : "",
                item.getSubtotal() != null ? "$ " + DF.format(item.getSubtotal()) : "",
                item.getEstadoPago() != null ? item.getEstadoPago() : ""
            });
        }

    }

    private void cargarHistorialCompleto() {
        List<FacturaPagoDTO> pagos = controlador.getTodosLosPagos();
        modeloTablaPagos.setRowCount(0);
        pagosIds = new ArrayList<>();
        reciboOriginalNums = new ArrayList<>();
        for (FacturaPagoDTO p : pagos) {
            pagosIds.add(p.getId());
            reciboOriginalNums.add(p.getReciboNumero());
            String compStr = p.getComprobanteStr();
            if (compStr != null && compStr.contains(" ")) {
                compStr = compStr.substring(compStr.indexOf(' ') + 1).trim();
            }
            String reciboStr = p.getReciboNumero();
            if (reciboStr != null && reciboStr.startsWith("RE ")) {
                reciboStr = reciboStr.substring(3);
            }
            modeloTablaPagos.addRow(new Object[]{
                p.getMonto() != null ? "$ " + DF.format(p.getMonto()) : "",
                p.getFechaPago() != null ? p.getFechaPago().format(FMT) : "",
                p.getFormaPago() != null ? p.getFormaPago() : "",
                compStr != null ? compStr : "",
                p.getReciboId() != null ? reciboStr : "",
                p.getReciboId() == null ? Boolean.FALSE : Boolean.TRUE
            });
        }
    }

    private void registrarPago() {
        if (comprobanteSeleccionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String montoStr = txtMontoPago.getText().trim().replace("$", "").trim();
        if (montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BigDecimal monto;
        try {
            monto = new BigDecimal(montoStr.replace(".", "").replace(",", "."));
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

        int itemId = itemIds.get(itemRow);

        String estadoPago = (String) modeloTablaItems.getValueAt(itemRow, 5);
        if ("pagado".equals(estadoPago)) {
            JOptionPane.showMessageDialog(this, "Este item ya fue pagado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object subtotalObj = modeloTablaItems.getValueAt(itemRow, 4);
        if (subtotalObj == null) return;
        BigDecimal monto;
        try {
            monto = new BigDecimal(subtotalObj.toString().replace("$", "").trim().replace(".", "").replace(",", "."));
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

    private void pagarFacturaCompleta() {
        if (comprobanteSeleccionadoId < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        BigDecimal saldo = controlador.getSaldoPendiente(comprobanteSeleccionadoId);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "La factura ya est\u00e1 totalmente pagada", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String formaPago = (String) cmbFormaPago.getSelectedItem();
        controlador.pagarFacturaCompleta(comprobanteSeleccionadoId, formaPago);
        JOptionPane.showMessageDialog(this, "Factura pagada completamente por $ " + DF.format(saldo), "\u00c9xito", JOptionPane.INFORMATION_MESSAGE);
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
        String reciboNro = reciboOriginalNums != null && row < reciboOriginalNums.size() ? reciboOriginalNums.get(row) : null;
        if (reciboNro == null || reciboNro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El pago seleccionado no tiene un recibo asociado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
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
        BigDecimal saldo = controlador.getSaldoPendiente(facturaId);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            cargarFacturas();
            cargarHistorialCompleto();
            comprobanteSeleccionadoId = -1;
            modeloTablaItems.setRowCount(0);
            txtMontoPagoUpdating = true;
            txtMontoPago.setText("");
            txtMontoPagoUpdating = false;
            lblInfoFactura.setText("Seleccione una factura de la lista");
            lblTotalFactura.setText("");
            lblSaldoPendiente.setText("Saldo Pendiente: $ 0,00");
            return;
        }
        cargarFacturas();
        cargarHistorialCompleto();
        boolean reencontrada = false;
        for (int i = 0; i < facturaIds.size(); i++) {
            if (facturaIds.get(i) == facturaId) {
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

    private void estilizarBoton(JButton btn) {
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }

    private String mostrarSelectorMedioPago() {
        JDialog dialog = new JDialog(this, "Medio de pago", true);
        dialog.getContentPane().setBackground(COLOR_FONDO);

        JComboBox<String> combo = new JComboBox<>(new String[]{"Efectivo", "Transferencia", "Cheque", "Tarjeta", "Mercado Pago", "Otra"});
        combo.setFont(FUENTE_BOTON);
        combo.setBackground(Color.WHITE);

        JLabel lbl = new JLabel("Seleccione el medio de pago:");
        lbl.setFont(new Font("Cambria", Font.BOLD, 13));
        lbl.setForeground(COLOR_TEXTO);

        JButton btnOk = new JButton("ACEPTAR");
        estilizarBoton(btnOk);
        JButton btnCancel = new JButton("CANCELAR");
        estilizarBoton(btnCancel);

        JPanel content = new JPanel(new BorderLayout(8, 12));
        content.setBackground(COLOR_FONDO);
        content.setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));
        content.add(lbl, BorderLayout.NORTH);
        content.add(combo, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(COLOR_FONDO);
        btnPanel.add(btnOk);
        btnPanel.add(btnCancel);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        final String[] result = new String[1];

        btnOk.addActionListener(e -> {
            result[0] = (String) combo.getSelectedItem();
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
        return result[0];
    }
}
