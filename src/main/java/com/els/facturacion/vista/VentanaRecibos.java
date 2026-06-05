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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class VentanaRecibos extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private JPanel panelLista;
    private JPanel panelInferiorLista;
    private JPanel panelDetalle;
    private JPanel panelHeader;
    private JPanel panelCentralWrapper;
    private JPanel panelFormasPago;
    private JPanel panelFacturas;
    private JButton btnRefrescar;
    private JButton btnVerRecibo;
    private JSplitPane splitHorizontal;
    private JSplitPane splitVertical;
    private JScrollPane scrollRecibos;
    private JScrollPane scrollPagos;
    private JScrollPane scrollFacturas;

    private ControladorRecibos controlador;

    private JTable tablaRecibos;
    private DefaultTableModel modeloTablaRecibos;

    private JTable tablaPagos;
    private DefaultTableModel modeloTablaPagos;

    private JTable tablaFacturas;
    private DefaultTableModel modeloTablaFacturas;

    private JLabel lblInfoRecibo;
    private JPanel statusBar;
    private JLabel lblStatus;

    public VentanaRecibos() {
        controlador = new ControladorRecibos();
        initComponents();
        applyTheme(currentTheme);
        cargarRecibos();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Recibos");
        setSize(1024, 768);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgSurface);

        splitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitHorizontal.setResizeWeight(0.35);
        splitHorizontal.setBorder(null);
        splitHorizontal.setBackground(currentTheme.bgSurface);

        splitHorizontal.setLeftComponent(crearPanelLista());
        splitHorizontal.setRightComponent(crearPanelDetalle());

        add(splitHorizontal);

        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel crearPanelLista() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panelLista = panel;
        panel.setBackground(currentTheme.bgSurface);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 5),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "RECIBOS",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), currentTheme.textPrimary
            )
        ));

        String[] colRecibos = {"ID", "N\u00famero", "Fecha", "Cliente", "Total"};
        modeloTablaRecibos = new DefaultTableModel(colRecibos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaRecibos = new JTable(modeloTablaRecibos);
        tablaRecibos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaRecibos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaRecibos.getTableHeader().setBackground(currentTheme.btnBg);
        tablaRecibos.setRowHeight(22);
        tablaRecibos.setShowGrid(true);
        tablaRecibos.setAutoCreateRowSorter(true);
        tablaRecibos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarReciboSeleccionado();
        });

        tablaRecibos.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaRecibos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaRecibos.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaRecibos.getColumnModel().getColumn(3).setPreferredWidth(180);
        tablaRecibos.getColumnModel().getColumn(4).setPreferredWidth(80);

        scrollRecibos = new JScrollPane(tablaRecibos);
        panel.add(scrollRecibos, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelInferiorLista = panelInferior;
        panelInferior.setBackground(currentTheme.bgBase);
        btnRefrescar = new JButton("REFRESCAR");
        btnRefrescar.setFont(FUENTE_BOTON);
        btnRefrescar.setForeground(currentTheme.textPrimary);
        btnRefrescar.setBackground(currentTheme.btnBg);
        btnRefrescar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarRecibos());
        panelInferior.add(btnRefrescar);
        btnVerRecibo = new JButton("VER RECIBO");
        btnVerRecibo.setFont(FUENTE_BOTON);
        btnVerRecibo.setForeground(currentTheme.textPrimary);
        btnVerRecibo.setBackground(currentTheme.btnBg);
        btnVerRecibo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerRecibo.setFocusPainted(false);
        btnVerRecibo.addActionListener(e -> verReciboSeleccionado());
        panelInferior.add(btnVerRecibo);
        panel.add(panelInferior, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelDetalle() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panelDetalle = panel;
        panel.setBackground(currentTheme.bgSurface);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 5, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "DETALLE DEL RECIBO",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), currentTheme.textPrimary
            )
        ));

        panel.add(crearPanelHeader(), BorderLayout.NORTH);
        panel.add(crearPanelCentral(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelHeader() {
        JPanel panel = new JPanel(new GridBagLayout());
        panelHeader = panel;
        panel.setBackground(currentTheme.bgSurface);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        lblInfoRecibo = new JLabel("Seleccione un recibo de la lista");
        lblInfoRecibo.setFont(FUENTE_LABEL);
        lblInfoRecibo.setForeground(currentTheme.textPrimary);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblInfoRecibo, gbc);

        return panel;
    }

    private JPanel crearPanelCentral() {
        splitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitVertical.setResizeWeight(0.5);
        splitVertical.setBorder(null);
        splitVertical.setBackground(currentTheme.bgSurface);

        splitVertical.setTopComponent(crearPanelFormasPago());
        splitVertical.setBottomComponent(crearPanelFacturas());

        panelCentralWrapper = new JPanel(new BorderLayout());
        panelCentralWrapper.setBackground(currentTheme.bgSurface);
        panelCentralWrapper.add(splitVertical);
        return panelCentralWrapper;
    }

    private JPanel crearPanelFormasPago() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panelFormasPago = panel;
        panel.setBackground(currentTheme.bgSurface);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "FORMAS DE PAGO",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), currentTheme.textPrimary
        ));

        String[] colPagos = {"Forma de Pago", "Monto", "Referencia"};
        modeloTablaPagos = new DefaultTableModel(colPagos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaPagos = new JTable(modeloTablaPagos);
        tablaPagos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaPagos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaPagos.getTableHeader().setBackground(currentTheme.btnBg);
        tablaPagos.setRowHeight(22);
        tablaPagos.setShowGrid(true);

        tablaPagos.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablaPagos.getColumnModel().getColumn(1).setPreferredWidth(120);
        tablaPagos.getColumnModel().getColumn(2).setPreferredWidth(200);

        scrollPagos = new JScrollPane(tablaPagos);
        panel.add(scrollPagos, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelFacturas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panelFacturas = panel;
        panel.setBackground(currentTheme.bgSurface);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "FACTURAS ASOCIADAS",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), currentTheme.textPrimary
        ));

        String[] colFacturas = {"Factura ID", "Nro Factura", "Monto Aplicado"};
        modeloTablaFacturas = new DefaultTableModel(colFacturas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaFacturas = new JTable(modeloTablaFacturas);
        tablaFacturas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaFacturas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaFacturas.getTableHeader().setBackground(currentTheme.btnBg);
        tablaFacturas.setRowHeight(22);
        tablaFacturas.setShowGrid(true);

        tablaFacturas.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaFacturas.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaFacturas.getColumnModel().getColumn(2).setPreferredWidth(120);


        scrollFacturas = new JScrollPane(tablaFacturas);
        panel.add(scrollFacturas, BorderLayout.CENTER);

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
                BigDecimal totalPagado = f.getTotalPagado() != null ? f.getTotalPagado() : f.getMontoAplicado();
                modeloTablaFacturas.addRow(new Object[]{
                    f.getComprobanteId(),
                    f.getNumeroFactura() != null ? f.getNumeroFactura() : "",
                    totalPagado != null ? "$ " + DF.format(totalPagado) : ""
                });
            }
        }
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        Color titledFg = t.textPrimary;
        Color titledLine = t.brand;
        Font titledFont = new Font("Segoe UI", Font.BOLD, 12);

        if (panelLista != null) {
            panelLista.setBackground(t.bgSurface);
            panelLista.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 5),
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(titledLine),
                    "RECIBOS",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    titledFont, titledFg
                )
            ));
        }
        if (panelInferiorLista != null) panelInferiorLista.setBackground(t.bgBase);
        if (panelDetalle != null) {
            panelDetalle.setBackground(t.bgSurface);
            panelDetalle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 5, 10, 10),
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(titledLine),
                    "DETALLE DEL RECIBO",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    titledFont, titledFg
                )
            ));
        }
        if (panelHeader != null) panelHeader.setBackground(t.bgSurface);
        if (panelCentralWrapper != null) panelCentralWrapper.setBackground(t.bgSurface);
        if (panelFormasPago != null) {
            panelFormasPago.setBackground(t.bgSurface);
            panelFormasPago.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(titledLine),
                "FORMAS DE PAGO",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), titledFg
            ));
        }
        if (panelFacturas != null) {
            panelFacturas.setBackground(t.bgSurface);
            panelFacturas.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(titledLine),
                "FACTURAS ASOCIADAS",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), titledFg
            ));
        }

        if (splitHorizontal != null) splitHorizontal.setBackground(t.bgSurface);
        if (splitVertical != null) splitVertical.setBackground(t.bgSurface);

        if (lblInfoRecibo != null) lblInfoRecibo.setForeground(t.textPrimary);

        if (btnRefrescar != null) {
            btnRefrescar.setForeground(t.textPrimary);
            btnRefrescar.setBackground(t.btnBg);
        }
        if (btnVerRecibo != null) {
            btnVerRecibo.setForeground(t.textPrimary);
            btnVerRecibo.setBackground(t.btnBg);
        }

        if (tablaRecibos != null) {
            TablaRenderer.applyTo(tablaRecibos, t);
            if (tablaRecibos.getTableHeader() != null) {
                Theme.styleTableHeader(tablaRecibos.getTableHeader(), t);
            }
        }
        if (tablaPagos != null) {
            TablaRenderer.applyTo(tablaPagos, t,
                new HashSet<>(Arrays.asList(1)),
                Collections.emptySet());
            if (tablaPagos.getTableHeader() != null) {
                Theme.styleTableHeader(tablaPagos.getTableHeader(), t);
            }
        }
        if (tablaFacturas != null) {
            TablaRenderer.applyTo(tablaFacturas, t,
                new HashSet<>(Arrays.asList(2)),
                Collections.emptySet());
            if (tablaFacturas.getTableHeader() != null) {
                Theme.styleTableHeader(tablaFacturas.getTableHeader(), t);
            }
        }

        if (scrollRecibos != null && scrollRecibos.getViewport() != null)
            scrollRecibos.getViewport().setBackground(t.bgSurface);
        if (scrollPagos != null && scrollPagos.getViewport() != null)
            scrollPagos.getViewport().setBackground(t.bgSurface);
        if (scrollFacturas != null && scrollFacturas.getViewport() != null)
            scrollFacturas.getViewport().setBackground(t.bgSurface);
        if (statusBar != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        }
    }
}
