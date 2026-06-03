package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorReparsoft;
import com.els.facturacion.modelo.RemitoReparsoftDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem;
import com.els.facturacion.util.UbicacionSistema;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class VentanaImportarRemito extends JDialog {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 11);

    private ControladorReparsoft controlador;
    private JTable tablaRemitos;
    private DefaultTableModel modeloTablaRemitos;
    private JTable tablaItems;
    private DefaultTableModel modeloTablaItems;
    private JLabel lblCliente;
    private JButton btnImportar;
    private JButton btnCancelar;
    private JPanel panel;
    private JPanel panelSuperior;
    private JPanel panelInferior;
    private JScrollPane scrollRemitos;
    private JScrollPane scrollItems;
    private JLabel lblBase;
    private JButton btnRefrescar;
    private JPanel statusBar;
    private JLabel lblStatus;

    private RemitoReparsoftDTO remitoSeleccionado;
    private List<RemitoReparsoftDTO> remitosCache;

    public VentanaImportarRemito(JFrame parent) {
        super(parent, "Importar Remito desde ReparSoft", true);
        this.controlador = new ControladorReparsoft();
        initComponents();
        applyTheme(currentTheme);
        cargarRemitos();
        setLocationRelativeTo(parent);
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                remitoSeleccionado = null;
            }
        });
        getContentPane().setBackground(currentTheme.bgBase);

        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(currentTheme.bgBase);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSuperior.setBackground(currentTheme.bgSurface);
        lblBase = new JLabel("Base: " + UbicacionSistema.getNombreDbReparsoft());
        lblBase.setFont(FUENTE_LABEL);
        lblBase.setForeground(currentTheme.textPrimary);
        panelSuperior.add(lblBase);

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setFont(FUENTE_BOTON);
        btnRefrescar.setForeground(currentTheme.textPrimary);
        btnRefrescar.setBackground(currentTheme.btnBg);
        btnRefrescar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarRemitos());
        panelSuperior.add(btnRefrescar);

        lblCliente = new JLabel("Seleccione un remito");
        lblCliente.setFont(FUENTE_LABEL);
        lblCliente.setForeground(currentTheme.textPrimary);
        panelSuperior.add(lblCliente);

        panel.add(panelSuperior, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        String[] colRemitos = {"REMITO", "Cliente", "CUIT", "ITEMS"};
        modeloTablaRemitos = new DefaultTableModel(colRemitos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRemitos = new JTable(modeloTablaRemitos);
        tablaRemitos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaRemitos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaRemitos.setRowHeight(22);
        tablaRemitos.setShowGrid(true);
        tablaRemitos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalleRemito();
        });
        tablaRemitos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    importarRemito();
                }
            }
        });
        tablaRemitos.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaRemitos.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaRemitos.getColumnModel().getColumn(3).setPreferredWidth(50);
        scrollRemitos = new JScrollPane(tablaRemitos);
        scrollRemitos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "REMITOS",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), currentTheme.textPrimary
        ));

        String[] colItems = {"ELS", "Equipo", "Nro Serie", "Modelo", "Marca", "Precio", "SEL"};
        modeloTablaItems = new DefaultTableModel(colItems, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 6 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 6) {
                    RemitoReparsoftItem item = getItemAtRow(row);
                    return item != null && !item.isFacturado();
                }
                return false;
            }
        };
        tablaItems = new JTable(modeloTablaItems);
        tablaItems.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaItems.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaItems.setRowHeight(22);
        tablaItems.setShowGrid(true);
        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(6).setPreferredWidth(30);
        scrollItems = new JScrollPane(tablaItems);
        scrollItems.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "ITEMS DEL REMITO",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), currentTheme.textPrimary
        ));

        splitPane.setTopComponent(scrollRemitos);
        splitPane.setBottomComponent(scrollItems);
        panel.add(splitPane, BorderLayout.CENTER);

        panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelInferior.setBackground(currentTheme.bgSurface);

        btnImportar = new JButton("IMPORTAR REMITO");
        btnImportar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnImportar.setForeground(currentTheme.textPrimary);
        btnImportar.setBackground(currentTheme.btnBg);
        btnImportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImportar.setFocusPainted(false);
        btnImportar.addActionListener(e -> importarRemito());
        panelInferior.add(btnImportar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(FUENTE_BOTON);
        btnCancelar.setForeground(currentTheme.textPrimary);
        btnCancelar.setBackground(currentTheme.btnBg);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> {
            remitoSeleccionado = null;
            dispose();
        });
        panelInferior.add(btnCancelar);

        panel.add(panelInferior, BorderLayout.SOUTH);

        add(panel);

        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void cargarRemitos() {
        String base = UbicacionSistema.getNombreDbReparsoft();
        if (base == null) return;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        modeloTablaRemitos.setRowCount(0);
        modeloTablaItems.setRowCount(0);
        lblCliente.setText("Cargando...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                remitosCache = controlador.listarRemitos(base);
                return null;
            }

            @Override
            protected void done() {
                if (remitosCache != null) {
                    for (RemitoReparsoftDTO r : remitosCache) {
                        modeloTablaRemitos.addRow(new Object[]{
                            r.getNumeroRemitoDisplay(),
                            r.getRazonSocialCliente() != null ? r.getRazonSocialCliente() : "Sin cliente",
                            r.getCuitCliente() != null ? r.getCuitCliente() : "",
                            r.getItems() != null ? r.getItems().size() : 0
                        });
                }
                tablaRemitos.getTableHeader().repaint();
                tablaRemitos.repaint();
            }
                lblCliente.setText(remitosCache != null ? remitosCache.size() + " remitos cargados" : "Sin datos");
                setCursor(Cursor.getDefaultCursor());
            }
        };
        worker.execute();
    }

    private void mostrarDetalleRemito() {
        int row = tablaRemitos.getSelectedRow();
        if (row < 0) return;

        if (remitosCache == null || row >= remitosCache.size()) return;

        remitoSeleccionado = remitosCache.get(row);

        modeloTablaItems.setRowCount(0);
        if (remitoSeleccionado != null && remitoSeleccionado.getItems() != null) {
            for (RemitoReparsoftItem item : remitoSeleccionado.getItems()) {
                modeloTablaItems.addRow(new Object[]{
                    String.valueOf(item.getEls()),
                    item.getEquipoNombre() != null ? item.getEquipoNombre() : "",
                    item.getNumeroSerie() != null ? item.getNumeroSerie() : "",
                    item.getModelo() != null ? item.getModelo() : "",
                    item.getMarca() != null ? item.getMarca() : "",
                    item.getPrecioPeso() != null ? new java.text.DecimalFormat("#,##0.00",
                        java.text.DecimalFormatSymbols.getInstance(new java.util.Locale("es", "AR"))).format(item.getPrecioPeso()) : "0,00",
                    item.isFacturado()
                });
            }
        }

        lblCliente.setText(remitoSeleccionado != null ? remitoSeleccionado.getClienteDisplay() : "Seleccione un remito");
    }

    private RemitoReparsoftItem getItemAtRow(int row) {
        if (remitoSeleccionado == null || remitoSeleccionado.getItems() == null) return null;
        if (row < 0 || row >= remitoSeleccionado.getItems().size()) return null;
        return remitoSeleccionado.getItems().get(row);
    }

    private void importarRemito() {
        if (remitoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un remito para importar", "Importar Remito", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < modeloTablaItems.getRowCount(); i++) {
            boolean checked = Boolean.TRUE.equals(modeloTablaItems.getValueAt(i, 6));
            RemitoReparsoftItem item = getItemAtRow(i);
            if (item != null) {
                item.setSeleccionado(checked);
            }
        }

        List<RemitoReparsoftItem> seleccionados = new java.util.ArrayList<>();
        if (remitoSeleccionado.getItems() != null) {
            for (RemitoReparsoftItem item : remitoSeleccionado.getItems()) {
                if (item.isSeleccionado() && !item.isFacturado()) {
                    seleccionados.add(item);
                }
            }
        }

        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos un item para importar (los items ya facturados no pueden reimportarse)", "Importar Remito", JOptionPane.WARNING_MESSAGE);
            return;
        }

        remitoSeleccionado.setItems(seleccionados);
        setVisible(false);
    }

    public RemitoReparsoftDTO getRemitoSeleccionado() {
        return remitoSeleccionado;
    }

    public static RemitoReparsoftDTO mostrarDialog(JFrame parent) {
        VentanaImportarRemito dialog = new VentanaImportarRemito(parent);
        dialog.setVisible(true);
        return dialog.getRemitoSeleccionado();
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        getContentPane().setBackground(t.bgBase);
        if (panel != null) panel.setBackground(t.bgBase);
        if (panelSuperior != null) panelSuperior.setBackground(t.bgSurface);
        if (panelInferior != null) panelInferior.setBackground(t.bgSurface);
        if (lblBase != null) lblBase.setForeground(t.textPrimary);
        if (lblCliente != null) lblCliente.setForeground(t.textPrimary);
        if (btnRefrescar != null) {
            btnRefrescar.setForeground(t.textPrimary);
            btnRefrescar.setBackground(t.btnBg);
        }
        if (btnCancelar != null) {
            btnCancelar.setForeground(t.textPrimary);
            btnCancelar.setBackground(t.btnBg);
        }
        if (btnImportar != null) {
            btnImportar.setBackground(t.btnBg);
            btnImportar.setForeground(t.textPrimary);
        }
        if (tablaRemitos != null) {
            java.util.Set<Integer> boldRemitos = new java.util.HashSet<>();
            boldRemitos.add(0);
            java.util.Set<Integer> centerRemitos = new java.util.HashSet<>();
            centerRemitos.add(0);
            centerRemitos.add(2);
            centerRemitos.add(3);
            TablaRenderer.applyTo(tablaRemitos, t, java.util.Collections.emptySet(), boldRemitos, centerRemitos, t.bgSurface, t.bgElevated);
            if (tablaRemitos.getTableHeader() != null) {
                styleHeaderBold(tablaRemitos.getTableHeader(), t);
            }
            tablaRemitos.repaint();
        }
        if (tablaItems != null) {
            java.util.Set<Integer> currency = new java.util.HashSet<>();
            currency.add(5);
            java.util.Set<Integer> bold = new java.util.HashSet<>();
            bold.add(0);
            java.util.Set<Integer> center = new java.util.HashSet<>();
            center.add(0);
            center.add(5);
            TablaRenderer.applyTo(tablaItems, t, currency, bold, center, t.bgSurface, t.bgElevated);
            if (tablaItems.getTableHeader() != null) {
                styleHeaderBold(tablaItems.getTableHeader(), t);
            }
        }
        if (scrollRemitos != null) {
            scrollRemitos.getViewport().setBackground(t.bgBase);
            scrollRemitos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(t.brand),
                "REMITOS",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), t.textPrimary
            ));
        }
        if (statusBar != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        }
        if (scrollItems != null) {
            scrollItems.getViewport().setBackground(t.bgBase);
            scrollItems.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(t.brand),
                "ITEMS DEL REMITO",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12), t.textPrimary
            ));
        }
    }

    private static void styleHeaderBold(JTableHeader header, Theme theme) {
        boolean isDark = theme.bgBase.getRed() < 50;
        Color fg = isDark ? Color.WHITE : theme.textPrimary;
        Color headerBg = isDark
            ? new Color(Math.min(255, theme.bgElevated.getRed() + 20), Math.min(255, theme.bgElevated.getGreen() + 20), Math.min(255, theme.bgElevated.getBlue() + 20))
            : new Color(Math.min(255, theme.brand.getRed() + 115), Math.min(255, theme.brand.getGreen() + 110), Math.min(255, theme.brand.getBlue() + 10));
        int avg = (headerBg.getRed() + headerBg.getGreen() + headerBg.getBlue()) / 3;
        Color divider = avg < 80
            ? new Color(Math.min(255, headerBg.getRed() + 60), Math.min(255, headerBg.getGreen() + 60), Math.min(255, headerBg.getBlue() + 60))
            : new Color(Math.max(0, headerBg.getRed() - 60), Math.max(0, headerBg.getGreen() - 60), Math.max(0, headerBg.getBlue() - 60));
        Border colBorder = BorderFactory.createMatteBorder(0, 0, 1, 2, divider);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                  boolean isSelected, boolean hasFocus, int row, int column) {
                DefaultTableCellRenderer c = (DefaultTableCellRenderer)
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(fg);
                c.setBackground(headerBg);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 11));
                c.setText(value != null ? value.toString().toUpperCase() : "");
                c.setBorder(colBorder);
                return c;
            }
        });
    }
}