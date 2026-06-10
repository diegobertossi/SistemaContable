package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorRemitos;
import com.els.facturacion.dao.RemitoReparsoftLecturaDAO;
import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.reportes.GestorReportes;
import com.els.facturacion.util.UbicacionSistema;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.awt.Rectangle;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VentanaVisualizacionRemitos extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_INPUT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_INPUT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Color DISABLED_FG_LIGHT = new Color(95, 97, 106);
    private static final Color DISABLED_FG_DARK = new Color(210, 207, 190);
    private static final Color LIGHT_READONLY_BG = new Color(236, 237, 241);
    private static final Color LIGHT_EDITABLE_BG = new Color(255, 253, 230);
    private static final Color DARK_READONLY_BG = new Color(28, 33, 55);
    private static final Color DARK_EDITABLE_BG = new Color(22, 27, 45);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] COLUMNAS = {
        "N\u00b0 REMITO", "CLIENTE", "ITEMS (CANTIDAD)", "FECHA DE EMISI\u00d3N", "FECHA DE ENTREGA"
    };

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private ControladorRemitos controlador;
    private RemitoReparsoftLecturaDAO dao;
    private String baseDatos;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTitulo;
    private JButton btnActualizar;
    private JButton btnVerPDF;
    private JPanel panelSuperior;
    private JScrollPane scrollPane;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JPanel panelBotones;
    private JPanel panelCentro;
    private List<String> listaNrosRemito;
    private JPanel panelFiltro;
    private JLabel lblFiltroCliente;
    private JComboBox<String> cmbFiltroCliente;
    private List<Map<String, Object>> allRemitosData;

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
    }

    public VentanaVisualizacionRemitos() {
        controlador = new ControladorRemitos();
        dao = new RemitoReparsoftLecturaDAO();
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
        if (UbicacionSistema.isSeleccionado()) {
            baseDatos = UbicacionSistema.getNombreDbReparsoft();
            cargarRemitos();
        }
    }

    private void initComponents() {
        setTitle("Visualizaci\u00f3n de Remitos");
        setSize(1024, 600);
        setMinimumSize(new Dimension(1024, 600));
        setMaximumSize(new Dimension(1024, 600));
        setResizable(false);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        lblTitulo = new JLabel("HISTORIAL DE REMITOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSuperior.add(lblTitulo);
        panelSuperior.add(javax.swing.Box.createRigidArea(new Dimension(0, 4)));

        // --- FILTER ---
        lblFiltroCliente = new JLabel("CLIENTE:");
        lblFiltroCliente.setFont(FUENTE_LABEL);
        lblFiltroCliente.setForeground(currentTheme.textPrimary);

        cmbFiltroCliente = new JComboBox<>();
        cmbFiltroCliente.setFont(FUENTE_INPUT_BOLD);
        cmbFiltroCliente.setMaximumRowCount(12);
        cmbFiltroCliente.addActionListener(e -> {
            if (allRemitosData != null) aplicarFiltro();
        });
        installComboUI(cmbFiltroCliente);
        cmbFiltroCliente.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(cmbFiltroCliente.isEnabled()));
                setForeground(isSelected ? list.getSelectionForeground() :
                    (cmbFiltroCliente.isEnabled() ? currentTheme.textPrimary : getDisabledFg()));
                return this;
            }
        });

        panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panelFiltro.setBackground(currentTheme.bgSurface);
        panelFiltro.add(lblFiltroCliente);
        panelFiltro.add(cmbFiltroCliente);
        panelSuperior.add(panelFiltro);

        // --- TABLE ---
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.setRowHeight(22);
        tabla.setShowGrid(true);
        tabla.setAutoCreateRowSorter(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getTableHeader().setReorderingAllowed(false);

        scrollPane = new JScrollPane(tabla);

        // --- BOTTOM BUTTONS ---
        btnActualizar = new JButton("ACTUALIZAR");
        btnActualizar.setFont(FUENTE_BOTON);
        btnActualizar.setForeground(currentTheme.textPrimary);
        btnActualizar.setBackground(currentTheme.btnBg);
        btnActualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarRemitos());

        btnVerPDF = new JButton("VER PDF");
        btnVerPDF.setFont(FUENTE_BOTON);
        btnVerPDF.setForeground(currentTheme.textPrimary);
        btnVerPDF.setBackground(currentTheme.btnBg);
        btnVerPDF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerPDF.setFocusPainted(false);
        btnVerPDF.addActionListener(e -> verPDF());

        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        panelBotones.setBackground(currentTheme.bgBase);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVerPDF);

        panelCentro = new JPanel(new BorderLayout(0, 4));
        panelCentro.setBackground(currentTheme.bgBase);
        panelCentro.add(scrollPane, BorderLayout.CENTER);
        panelCentro.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);

        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);
    }

    public void cargarRemitos() {
        if (baseDatos == null) {
            if (UbicacionSistema.isSeleccionado()) {
                baseDatos = UbicacionSistema.getNombreDbReparsoft();
            } else {
                return;
            }
        }
        allRemitosData = dao.listarRemitosConFechas(baseDatos);
        if (allRemitosData == null) allRemitosData = new ArrayList<>();

        // Populate filter combo
        String seleccionActual = cmbFiltroCliente.getSelectedItem() != null
            ? cmbFiltroCliente.getSelectedItem().toString() : "";
        cmbFiltroCliente.removeAllItems();
        cmbFiltroCliente.addItem("Todos");
        java.util.Set<String> clientes = new java.util.LinkedHashSet<>();
        for (Map<String, Object> r : allRemitosData) {
            String cli = (String) r.get("cliente");
            if (cli != null && !cli.isEmpty()) clientes.add(cli);
        }
        java.util.List<String> listaClientes = new ArrayList<>(clientes);
        java.util.Collections.sort(listaClientes);
        for (String c : listaClientes) cmbFiltroCliente.addItem(c);
        cmbFiltroCliente.setSelectedItem(seleccionActual.isEmpty() ? "Todos" : seleccionActual);

        aplicarFiltro();
    }

    private void aplicarFiltro() {
        modeloTabla.setRowCount(0);
        listaNrosRemito = new ArrayList<>();
        String filtro = cmbFiltroCliente.getSelectedItem() != null
            ? cmbFiltroCliente.getSelectedItem().toString() : "Todos";
        for (Map<String, Object> r : allRemitosData) {
            String cli = (String) r.get("cliente");
            if (!"Todos".equals(filtro) && !filtro.equals(cli)) continue;
            String nro = (String) r.get("numeroRemito");
            listaNrosRemito.add(nro);
            String cliente = (String) r.get("cliente");
            String items = String.valueOf(r.get("items"));
            java.sql.Date fechaEm = (java.sql.Date) r.get("fechaEmision");
            java.sql.Date fechaEn = (java.sql.Date) r.get("fechaEntrega");
            String fechaEmStr = fechaEm != null ? FMT.format(fechaEm.toLocalDate()) : "";
            String fechaEnStr = fechaEn != null ? FMT.format(fechaEn.toLocalDate()) : "";
            modeloTabla.addRow(new Object[]{ nro, cliente, items, fechaEmStr, fechaEnStr });
        }
        ajustarAnchoColumnas();
        tabla.clearSelection();
    }

    private void ajustarAnchoColumnas() {
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            javax.swing.table.TableColumn col = tabla.getColumnModel().getColumn(i);
            int width = 60;
            javax.swing.table.TableCellRenderer hr = tabla.getTableHeader().getDefaultRenderer();
            java.awt.Component hc = hr.getTableCellRendererComponent(tabla, col.getHeaderValue(), false, false, 0, i);
            width = Math.max(width, hc.getPreferredSize().width + 6);
            for (int r = 0; r < Math.min(tabla.getRowCount(), 30); r++) {
                javax.swing.table.TableCellRenderer rnd = tabla.getCellRenderer(r, i);
                java.awt.Component cmp = tabla.prepareRenderer(rnd, r, i);
                width = Math.max(width, cmp.getPreferredSize().width + 6);
            }
            if (i == 0 || i == 1) width = (int) (width * 1.35);
            col.setPreferredWidth(width);
        }
    }

    private void verPDF() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un remito", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= listaNrosRemito.size()) return;
        String nroRemito = listaNrosRemito.get(modelRow);
        RemitoDTO remito = buscarRemitoEnLocal(nroRemito);
        if (remito == null) {
            JOptionPane.showMessageDialog(this,
                "El remito no se encuentra en la base local.\n"
                + "Debe generarlo desde la pesta\u00f1a Generar Remito primero.",
                "Remito no encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            GestorReportes gestor = new GestorReportes();
            String nombre = "remito_" + remito.getNumeroRemito().replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";
            String ruta = System.getProperty("java.io.tmpdir") + File.separator + nombre;
            ruta = gestor.generarReporteRemito(remito, ruta);
            if (ruta != null && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(ruta));
            } else {
                JOptionPane.showMessageDialog(this, "Error al generar PDF", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private RemitoDTO buscarRemitoEnLocal(String nroRemito) {
        if (nroRemito == null) return null;
        List<RemitoDTO> todos = controlador.listarTodos();
        if (todos == null) return null;
        for (RemitoDTO r : todos) {
            if (nroRemito.equals(r.getNumeroRemito())) {
                return r;
            }
        }
        return null;
    }

    private void installComboUI(JComboBox<?> combo) {
        combo.setUI(new CustomComboUI());
    }

    private void themeComboEditor(JComboBox<?> combo, Theme t) {
        java.awt.Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            JTextField ed = (JTextField) editorComp;
            ed.setBackground(getFieldBg(combo.isEnabled()));
            ed.setForeground(combo.isEnabled() ? t.textPrimary : getDisabledFg());
            ed.setDisabledTextColor(getDisabledFg());
            ed.setCaretColor(t.textPrimary);
        }
    }

    private static class CustomComboUI extends javax.swing.plaf.basic.BasicComboBoxUI {
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

    public void applyTheme(Theme t) {
        currentTheme = t;
        if (getContentPane() != null) getContentPane().setBackground(t.bgBase);
        if (panelSuperior != null) panelSuperior.setBackground(t.bgSurface);
        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (panelFiltro != null) panelFiltro.setBackground(t.bgSurface);
        if (lblFiltroCliente != null) lblFiltroCliente.setForeground(t.textPrimary);
        if (cmbFiltroCliente != null) {
            installComboUI(cmbFiltroCliente);
            cmbFiltroCliente.setBackground(getFieldBg(cmbFiltroCliente.isEnabled()));
            cmbFiltroCliente.setForeground(cmbFiltroCliente.isEnabled() ? t.textPrimary : getDisabledFg());
        }
        if (panelCentro != null) panelCentro.setBackground(t.bgBase);
        if (panelBotones != null) panelBotones.setBackground(t.bgBase);
        if (btnActualizar != null) { btnActualizar.setBackground(t.btnBg); btnActualizar.setForeground(t.textPrimary); }
        if (btnVerPDF != null) { btnVerPDF.setBackground(t.btnBg); btnVerPDF.setForeground(t.textPrimary); }
        if (scrollPane != null) scrollPane.getViewport().setBackground(t.bgBase);
        if (tabla != null) {
            boolean isDarkTheme = t.bgBase.getRed() < 50;
            Color evenBg = isDarkTheme ? new Color(30, 40, 62) : new Color(210, 222, 242);
            Color oddBg  = isDarkTheme ? new Color(45, 58, 80) : new Color(235, 242, 252);
            Set<Integer> bold = new HashSet<>(Arrays.asList(0));
            Set<Integer> center = new HashSet<>(Arrays.asList(0, 2, 3, 4));
            TablaRenderer.applyTo(tabla, t, new HashSet<>(), bold, center, evenBg, oddBg);
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
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
