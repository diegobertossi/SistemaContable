package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorRecibos;
import com.els.facturacion.modelo.ReciboDTO;
import com.els.facturacion.modelo.ReciboFacturaDTO;
import com.els.facturacion.modelo.ReciboPagoDTO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
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
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class VentanaRecibos extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Cambria", Font.BOLD, 12);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ControladorRecibos controlador;

    private JTable tablaRecibos;
    private DefaultTableModel modeloTablaRecibos;

    private JTable tablaPagos;
    private DefaultTableModel modeloTablaPagos;

    private JTable tablaFacturas;
    private DefaultTableModel modeloTablaFacturas;

    private JLabel lblInfoRecibo;

    public VentanaRecibos() {
        controlador = new ControladorRecibos();
        initComponents();
        cargarRecibos();
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Recibos");
        setSize(1100, 700);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JSplitPane splitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitHorizontal.setResizeWeight(0.35);
        splitHorizontal.setBorder(null);
        splitHorizontal.setBackground(COLOR_FONDO);

        splitHorizontal.setLeftComponent(crearPanelLista());
        splitHorizontal.setRightComponent(crearPanelDetalle());

        add(splitHorizontal);
    }

    private JPanel crearPanelLista() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_TITULO),
                "RECIBOS",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Cambria", Font.BOLD, 13), COLOR_TEXTO
            )
        ));

        String[] colRecibos = {"ID", "N\u00famero", "Fecha", "Cliente", "Total"};
        modeloTablaRecibos = new DefaultTableModel(colRecibos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaRecibos = new JTable(modeloTablaRecibos);
        tablaRecibos.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaRecibos.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaRecibos.getTableHeader().setBackground(COLOR_BOTON);
        tablaRecibos.setRowHeight(22);
        tablaRecibos.setAutoCreateRowSorter(true);
        tablaRecibos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarReciboSeleccionado();
        });

        tablaRecibos.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaRecibos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaRecibos.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaRecibos.getColumnModel().getColumn(3).setPreferredWidth(180);
        tablaRecibos.getColumnModel().getColumn(4).setPreferredWidth(80);

        panel.add(new JScrollPane(tablaRecibos), BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelInferior.setBackground(COLOR_FONDO);
        JButton btnRefrescar = new JButton("REFRESCAR");
        btnRefrescar.setFont(FUENTE_BOTON);
        btnRefrescar.setForeground(COLOR_TEXTO);
        btnRefrescar.setBackground(COLOR_BOTON);
        btnRefrescar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarRecibos());
        panelInferior.add(btnRefrescar);
        JButton btnVerRecibo = new JButton("VER RECIBO");
        btnVerRecibo.setFont(FUENTE_BOTON);
        btnVerRecibo.setForeground(COLOR_TEXTO);
        btnVerRecibo.setBackground(COLOR_BOTON);
        btnVerRecibo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerRecibo.setFocusPainted(false);
        btnVerRecibo.addActionListener(e -> verReciboSeleccionado());
        panelInferior.add(btnVerRecibo);
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
                "DETALLE DEL RECIBO",
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

        lblInfoRecibo = new JLabel("Seleccione un recibo de la lista");
        lblInfoRecibo.setFont(FUENTE_LABEL);
        lblInfoRecibo.setForeground(COLOR_TEXTO);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblInfoRecibo, gbc);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JSplitPane splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setResizeWeight(0.5);
        splitVertical.setBorder(null);
        splitVertical.setBackground(COLOR_FONDO);

        splitVertical.setTopComponent(crearPanelFormasPago());
        splitVertical.setBottomComponent(crearPanelFacturas());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_FONDO);
        wrapper.add(splitVertical);
        return wrapper;
    }

    private JPanel crearPanelFormasPago() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "FORMAS DE PAGO",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 12), COLOR_TEXTO
        ));

        String[] colPagos = {"Forma de Pago", "Monto", "Referencia"};
        modeloTablaPagos = new DefaultTableModel(colPagos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPagos = new JTable(modeloTablaPagos);
        tablaPagos.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaPagos.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaPagos.getTableHeader().setBackground(COLOR_BOTON);
        tablaPagos.setRowHeight(22);

        tablaPagos.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablaPagos.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaPagos.getColumnModel().getColumn(2).setPreferredWidth(200);

        DefaultTableCellRenderer montoRenderer = new DefaultTableCellRenderer();
        montoRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaPagos.getColumnModel().getColumn(1).setCellRenderer(montoRenderer);

        panel.add(new JScrollPane(tablaPagos), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelFacturas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "FACTURAS ASOCIADAS",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 12), COLOR_TEXTO
        ));

        String[] colFacturas = {"Factura ID", "Nro Factura", "Monto Aplicado"};
        modeloTablaFacturas = new DefaultTableModel(colFacturas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaFacturas = new JTable(modeloTablaFacturas);
        tablaFacturas.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaFacturas.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaFacturas.getTableHeader().setBackground(COLOR_BOTON);
        tablaFacturas.setRowHeight(22);

        tablaFacturas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaFacturas.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaFacturas.getColumnModel().getColumn(2).setPreferredWidth(120);

        DefaultTableCellRenderer montoRenderer = new DefaultTableCellRenderer();
        montoRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaFacturas.getColumnModel().getColumn(2).setCellRenderer(montoRenderer);

        panel.add(new JScrollPane(tablaFacturas), BorderLayout.CENTER);

        return panel;
    }

    private void cargarRecibos() {
        modeloTablaRecibos.setRowCount(0);
        List<ReciboDTO> lista = controlador.listarTodos();
        for (ReciboDTO r : lista) {
            modeloTablaRecibos.addRow(new Object[]{
                r.getId(),
                r.getNumeroRecibo(),
                r.getFechaCobro() != null ? r.getFechaCobro().format(FMT) : "",
                r.getRazonSocialCliente(),
                r.getMontoTotal() != null ? "$ " + DF.format(r.getMontoTotal()) : ""
            });
        }
        lblInfoRecibo.setText("Seleccione un recibo de la lista");
        modeloTablaPagos.setRowCount(0);
        modeloTablaFacturas.setRowCount(0);
    }

    private void verReciboSeleccionado() {
        int viewRow = tablaRecibos.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un recibo de la lista", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int row = tablaRecibos.convertRowIndexToModel(viewRow);
        if (modeloTablaRecibos.getValueAt(row, 0) == null) return;
        String reciboNro = (String) modeloTablaRecibos.getValueAt(row, 1);
        if (reciboNro == null || reciboNro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El recibo no tiene un n\u00famero asociado", "Informaci\u00f3n", JOptionPane.INFORMATION_MESSAGE);
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

    private void cargarReciboSeleccionado() {
        int viewRow = tablaRecibos.getSelectedRow();
        if (viewRow < 0) return;
        int row = tablaRecibos.convertRowIndexToModel(viewRow);
        if (modeloTablaRecibos.getValueAt(row, 0) == null) return;
        int id = (Integer) modeloTablaRecibos.getValueAt(row, 0);

        ReciboDTO r = controlador.buscarPorId(id);
        if (r == null) return;

        String info = "Recibo: " + r.getNumeroRecibo()
            + "  |  Fecha: " + (r.getFechaCobro() != null ? r.getFechaCobro().format(FMT) : "")
            + "  |  Cliente: " + r.getRazonSocialCliente()
            + "  |  Total: $ " + DF.format(r.getMontoTotal());
        lblInfoRecibo.setText(info);

        modeloTablaPagos.setRowCount(0);
        if (r.getFormasPago() != null) {
            for (ReciboPagoDTO p : r.getFormasPago()) {
                modeloTablaPagos.addRow(new Object[]{
                    p.getFormaPago(),
                    p.getMonto() != null ? "$ " + DF.format(p.getMonto()) : "",
                    p.getReferencia() != null ? p.getReferencia() : ""
                });
            }
        }

        modeloTablaFacturas.setRowCount(0);
        if (r.getFacturas() != null) {
            for (ReciboFacturaDTO f : r.getFacturas()) {
                modeloTablaFacturas.addRow(new Object[]{
                    f.getComprobanteId(),
                    f.getNumeroFactura() != null ? f.getNumeroFactura() : "",
                    f.getMontoAplicado() != null ? "$ " + DF.format(f.getMontoAplicado()) : ""
                });
            }
        }
    }
}
