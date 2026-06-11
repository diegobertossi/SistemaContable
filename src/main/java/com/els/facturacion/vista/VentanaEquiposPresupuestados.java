package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorReparsoft;
import com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem;
import com.els.facturacion.util.AutoCompleteComboBox;
import com.els.facturacion.util.UbicacionSistema;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VentanaEquiposPresupuestados extends JDialog {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

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

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 11);
    private static final Color DISABLED_FG_LIGHT = new Color(95, 97, 106);
    private static final Color DISABLED_FG_DARK = new Color(210, 207, 190);
    private static final Color LIGHT_READONLY_BG = new Color(236, 237, 241);
    private static final Color LIGHT_EDITABLE_BG = new Color(255, 253, 230);
    private static final Color DARK_READONLY_BG = new Color(28, 33, 55);
    private static final Color DARK_EDITABLE_BG = new Color(22, 27, 45);

    private ControladorReparsoft controlador;
    private JTable tablaEquipos;
    private DefaultTableModel modeloTablaEquipos;
    private JLabel lblCliente;
    private JButton btnSeleccionar;
    private JButton btnCancelar;
    private JPanel panel;
    private JPanel panelInferior;
    private JScrollPane scrollEquipos;
    private JLabel lblBase;
    private JButton btnRefrescar;
    private JPanel statusBar;
    private JLabel lblStatus;

    private JPanel panelFiltro;
    private JLabel lblFiltroCliente;
    private AutoCompleteComboBox cmbFiltroCliente;
    private JLabel lblFiltroSucursal;
    private AutoCompleteComboBox cmbFiltroSucursal;

    private List<RemitoReparsoftItem> allEquiposCache;
    private List<RemitoReparsoftItem> seleccionados;
    private final String clienteNombreInicial;
    private boolean cargandoDatos = false;

    public VentanaEquiposPresupuestados(JFrame parent, String clienteNombre) {
        super(parent, "Equipos Presupuestados" + (clienteNombre != null && !clienteNombre.isEmpty() ? " - " + clienteNombre : ""), true);
        this.controlador = new ControladorReparsoft();
        this.clienteNombreInicial = clienteNombre;
        this.seleccionados = new ArrayList<>();
        initComponents();
        applyTheme(currentTheme);
        cargarEquipos();
        setLocationRelativeTo(parent);
        VentanaPrincipal.addThemeListener(this);
    }

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
    }

    private void initComponents() {
        setSize(850, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                seleccionados = null;
            }
        });
        getContentPane().setBackground(currentTheme.bgBase);

        panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(currentTheme.bgBase);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
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
        btnRefrescar.addActionListener(e -> cargarEquipos());
        panelSuperior.add(btnRefrescar);

        lblCliente = new JLabel("Cargando...");
        lblCliente.setFont(FUENTE_LABEL);
        lblCliente.setForeground(currentTheme.textPrimary);
        panelSuperior.add(lblCliente);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setOpaque(false);
        northWrapper.add(panelSuperior, BorderLayout.NORTH);

        // ── Filter panel ──
        lblFiltroCliente = new JLabel("CLIENTE:");
        lblFiltroCliente.setFont(FUENTE_LABEL);
        lblFiltroCliente.setForeground(currentTheme.textPrimary);

        cmbFiltroCliente = new AutoCompleteComboBox();
        cmbFiltroCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbFiltroCliente.setMaximumRowCount(12);
        cmbFiltroCliente.addActionListener(e -> onClienteChanged());
        themeComboEditor(cmbFiltroCliente, currentTheme);

        lblFiltroSucursal = new JLabel("SUCURSAL:");
        lblFiltroSucursal.setFont(FUENTE_LABEL);
        lblFiltroSucursal.setForeground(currentTheme.textPrimary);

        cmbFiltroSucursal = new AutoCompleteComboBox();
        cmbFiltroSucursal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cmbFiltroSucursal.setMaximumRowCount(12);
        cmbFiltroSucursal.addActionListener(e -> onSucursalChanged());
        themeComboEditor(cmbFiltroSucursal, currentTheme);

        panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panelFiltro.setBackground(currentTheme.bgSurface);
        panelFiltro.add(lblFiltroCliente);
        panelFiltro.add(cmbFiltroCliente);
        panelFiltro.add(Box.createRigidArea(new Dimension(15, 0)));
        panelFiltro.add(lblFiltroSucursal);
        panelFiltro.add(cmbFiltroSucursal);
        northWrapper.add(panelFiltro, BorderLayout.CENTER);

        panel.add(northWrapper, BorderLayout.NORTH);

        String[] colEquipos = {"ELS", "Equipo", "Modelo", "CLIENTE", "SUCURSAL", "REMITO", "Precio", "SEL"};
        modeloTablaEquipos = new DefaultTableModel(colEquipos, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 7 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 7) {
                    RemitoReparsoftItem item = getItemAtRow(row);
                    return item != null && !item.isFacturado();
                }
                return false;
            }
        };
        tablaEquipos = new JTable(modeloTablaEquipos);
        tablaEquipos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaEquipos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaEquipos.setRowHeight(22);
        tablaEquipos.setShowGrid(true);
        tablaEquipos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarEquipos();
                }
            }
        });
        tablaEquipos.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaEquipos.getColumnModel().getColumn(1).setPreferredWidth(130);
        tablaEquipos.getColumnModel().getColumn(2).setPreferredWidth(70);
        tablaEquipos.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaEquipos.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaEquipos.getColumnModel().getColumn(5).setPreferredWidth(90);
        tablaEquipos.getColumnModel().getColumn(6).setPreferredWidth(80);
        tablaEquipos.getColumnModel().getColumn(7).setPreferredWidth(30);
        scrollEquipos = new JScrollPane(tablaEquipos);
        scrollEquipos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(currentTheme.brand),
            "EQUIPOS PRESUPUESTADOS",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), currentTheme.textPrimary
        ));

        panel.add(scrollEquipos, BorderLayout.CENTER);

        panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelInferior.setBackground(currentTheme.bgSurface);

        btnSeleccionar = new JButton("SELECCIONAR");
        btnSeleccionar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSeleccionar.setForeground(currentTheme.textPrimary);
        btnSeleccionar.setBackground(currentTheme.btnBg);
        btnSeleccionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.addActionListener(e -> seleccionarEquipos());
        panelInferior.add(btnSeleccionar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(FUENTE_BOTON);
        btnCancelar.setForeground(currentTheme.textPrimary);
        btnCancelar.setBackground(currentTheme.btnBg);
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> {
            seleccionados = null;
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

    private void cargarEquipos() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        modeloTablaEquipos.setRowCount(0);
        lblCliente.setText("Cargando...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                String baseSel = UbicacionSistema.getNombreDbReparsoft();
                allEquiposCache = controlador.listarTodosEquiposConPrecio(baseSel);
                return null;
            }

            @Override
            protected void done() {
                if (allEquiposCache != null) {
                    cargandoDatos = true;
                    // Populate client combo
                    Set<String> clientes = new LinkedHashSet<>();
                    for (RemitoReparsoftItem item : allEquiposCache) {
                        String cli = item.getNombreCliente();
                        if (cli != null && !cli.isEmpty()) clientes.add(cli);
                    }
                    List<String> listaClientes = new ArrayList<>(clientes);
                    java.util.Collections.sort(listaClientes);
                    List<String> cliData = new ArrayList<>();
                    cliData.add("Todos");
                    cliData.addAll(listaClientes);
                    cmbFiltroCliente.setData(cliData);
                    
                    // If initial client was provided, select it; otherwise keep "Todos"
                    if (clienteNombreInicial != null && !clienteNombreInicial.isEmpty() && clientes.contains(clienteNombreInicial)) {
                        cmbFiltroCliente.setSelectedItem(clienteNombreInicial);
                    } else {
                        cmbFiltroCliente.setSelectedItem("Todos");
                    }
                    actualizarSucursales();
                    cargandoDatos = false;
                }
                aplicarFiltro();
            }
        };
        worker.execute();
    }

    private void onClienteChanged() {
        if (cargandoDatos) return;
        String sel = getComboText(cmbFiltroCliente);
        if (sel == null || sel.trim().isEmpty() || "Todos".equals(sel)) {
            actualizarSucursales();
            aplicarFiltro();
            return;
        }
        
        cargandoDatos = true;
        actualizarSucursales();
        cargandoDatos = false;
        aplicarFiltro();
    }

    private void onSucursalChanged() {
        if (cargandoDatos) return;
        aplicarFiltro();
    }

    private void actualizarSucursales() {
        String selCli = getComboText(cmbFiltroCliente);
        if (selCli == null || selCli.trim().isEmpty()) selCli = "Todos";
        Set<String> sucursales = new LinkedHashSet<>();
        for (RemitoReparsoftItem item : allEquiposCache) {
            String cli = item.getNombreCliente();
            if (!"Todos".equals(selCli) && !selCli.equals(cli)) continue;
            String suc = item.getSucursalDisplay();
            if (suc != null && !suc.isEmpty()) sucursales.add(suc);
        }
        String selSuc = getComboText(cmbFiltroSucursal);
        List<String> listaSuc = new ArrayList<>(sucursales);
        java.util.Collections.sort(listaSuc);
        List<String> sucData = new ArrayList<>();
        sucData.add("Todos");
        sucData.addAll(listaSuc);
        cmbFiltroSucursal.setData(sucData);
        cmbFiltroSucursal.setSelectedItem(!selSuc.isEmpty() && sucursales.contains(selSuc) ? selSuc : "Todos");
    }

    private void aplicarFiltro() {
        modeloTablaEquipos.setRowCount(0);
        if (allEquiposCache == null) return;

        String filtroCli = getComboText(cmbFiltroCliente);
        if (filtroCli == null || filtroCli.trim().isEmpty()) filtroCli = "Todos";
        String filtroSuc = getComboText(cmbFiltroSucursal);
        if (filtroSuc == null || filtroSuc.trim().isEmpty()) filtroSuc = "Todos";

        java.text.DecimalFormat precioFmt = new java.text.DecimalFormat("#,##0.00",
            java.text.DecimalFormatSymbols.getInstance(new java.util.Locale("es", "AR")));

        for (RemitoReparsoftItem item : allEquiposCache) {
            String cli = item.getNombreCliente();
            String suc = item.getSucursalDisplay();
            if (!"Todos".equals(filtroCli) && !filtroCli.equals(cli)) continue;
            if (!"Todos".equals(filtroSuc) && !filtroSuc.equals(suc)) continue;
            modeloTablaEquipos.addRow(new Object[]{
                String.valueOf(item.getEls()),
                item.getEquipoNombre() != null ? item.getEquipoNombre() : "",
                item.getModelo() != null ? item.getModelo() : "",
                item.getNombreCliente() != null ? item.getNombreCliente() : "",
                item.getSucursalDisplay() != null ? item.getSucursalDisplay() : "",
                item.getNumeroRemito() != null ? item.getNumeroRemito() : "",
                item.getPrecioPeso() != null ? precioFmt.format(item.getPrecioPeso()) : "0,00",
                item.isFacturado()
            });
        }
        tablaEquipos.getTableHeader().repaint();
        tablaEquipos.repaint();
        lblCliente.setText(modeloTablaEquipos.getRowCount() + " equipos cargados");
    }

    private RemitoReparsoftItem getItemAtRow(int row) {
        if (allEquiposCache == null) return null;
        String filtroCli = getComboText(cmbFiltroCliente);
        if (filtroCli == null || filtroCli.trim().isEmpty()) filtroCli = "Todos";
        String filtroSuc = getComboText(cmbFiltroSucursal);
        if (filtroSuc == null || filtroSuc.trim().isEmpty()) filtroSuc = "Todos";
        int idx = 0;
        for (RemitoReparsoftItem item : allEquiposCache) {
            String cli = item.getNombreCliente();
            String suc = item.getSucursalDisplay();
            if (!"Todos".equals(filtroCli) && !filtroCli.equals(cli)) continue;
            if (!"Todos".equals(filtroSuc) && !filtroSuc.equals(suc)) continue;
            if (idx == row) return item;
            idx++;
        }
        return null;
    }

    private void seleccionarEquipos() {
        List<RemitoReparsoftItem> seleccionadosTemp = new ArrayList<>();
        for (int i = 0; i < modeloTablaEquipos.getRowCount(); i++) {
            boolean checked = Boolean.TRUE.equals(modeloTablaEquipos.getValueAt(i, 7));
            RemitoReparsoftItem item = getItemAtRow(i);
            if (item != null && checked && !item.isFacturado()) {
                seleccionadosTemp.add(item);
            }
        }

        if (seleccionadosTemp.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Seleccione al menos un equipo para importar",
                "Seleccionar Equipos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.seleccionados = seleccionadosTemp;
        setVisible(false);
    }

    public List<RemitoReparsoftItem> getEquiposSeleccionados() {
        return seleccionados;
    }

    public static List<RemitoReparsoftItem> mostrarDialog(JFrame parent, String clienteNombre) {
        VentanaEquiposPresupuestados dialog = new VentanaEquiposPresupuestados(parent, clienteNombre);
        dialog.setVisible(true);
        return dialog.getEquiposSeleccionados();
    }

    // ── Combo helpers ──

    private String getComboText(AutoCompleteComboBox combo) {
        Object sel = combo.getSelectedItem();
        if (sel != null) {
            String s = sel.toString();
            if (s != null && !s.trim().isEmpty()) return s.trim();
        }
        return combo.getEditorText();
    }

    private void installComboUI(JComboBox<?> combo) {
        combo.setUI(new CustomComboUI());
    }

    private void themeComboEditor(JComboBox<?> combo, Theme t) {
        Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            JTextField ed = (JTextField) editorComp;
            ed.setBackground(getFieldBg(combo.isEnabled()));
            ed.setForeground(combo.isEnabled() ? t.textPrimary : getDisabledFg());
            ed.setDisabledTextColor(getDisabledFg());
            ed.setCaretColor(t.textPrimary);
        }
    }

    private static class CustomComboUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(comboBox.getBackground());
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            javax.swing.ListCellRenderer<Object> renderer = comboBox.getRenderer();
            java.awt.Component c;
            if (hasFocus && !isPopupVisible(comboBox)) {
                c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
            } else {
                c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
            }
            c.setFont(comboBox.getFont());
            if (hasFocus && !isPopupVisible(comboBox)) {
                c.setForeground(listBox.getSelectionForeground());
                c.setBackground(listBox.getSelectionBackground());
            } else {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            }
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        getContentPane().setBackground(t.bgBase);
        if (panel != null) panel.setBackground(t.bgBase);
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
        if (btnSeleccionar != null) {
            btnSeleccionar.setBackground(t.btnBg);
            btnSeleccionar.setForeground(t.textPrimary);
        }
        if (panelFiltro != null) panelFiltro.setBackground(t.bgSurface);
        if (lblFiltroCliente != null) lblFiltroCliente.setForeground(t.textPrimary);
        if (cmbFiltroCliente != null) {
            cmbFiltroCliente.setBackground(getFieldBg(cmbFiltroCliente.isEnabled()));
            cmbFiltroCliente.setForeground(cmbFiltroCliente.isEnabled() ? t.textPrimary : getDisabledFg());
            themeComboEditor(cmbFiltroCliente, t);
        }
        if (lblFiltroSucursal != null) lblFiltroSucursal.setForeground(t.textPrimary);
        if (cmbFiltroSucursal != null) {
            cmbFiltroSucursal.setBackground(getFieldBg(cmbFiltroSucursal.isEnabled()));
            cmbFiltroSucursal.setForeground(cmbFiltroSucursal.isEnabled() ? t.textPrimary : getDisabledFg());
            themeComboEditor(cmbFiltroSucursal, t);
        }
        if (tablaEquipos != null) {
            Set<Integer> currency = new HashSet<>();
            currency.add(6);
            Set<Integer> bold = new HashSet<>();
            bold.add(0);
            Set<Integer> center = new HashSet<>();
            center.add(0);
            center.add(4);
            center.add(5);
            center.add(6);
            TablaRenderer.applyTo(tablaEquipos, t, currency, bold, center, t.bgSurface, t.bgElevated);
            if (tablaEquipos.getTableHeader() != null) {
                styleHeaderBold(tablaEquipos.getTableHeader(), t);
            }
        }
        if (scrollEquipos != null) {
            scrollEquipos.getViewport().setBackground(t.bgBase);
            scrollEquipos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(t.brand),
                "EQUIPOS PRESUPUESTADOS",
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
    }
}
