package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorRemitos;
import com.els.facturacion.dao.RemitoReparsoftLecturaDAO;
import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.reportes.GestorReportes;
import com.els.facturacion.util.AutoCompleteComboBox;
import com.els.facturacion.util.UbicacionSistema;

import javax.swing.BorderFactory;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.border.MatteBorder;


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
    private AutoCompleteComboBox cmbFiltroCliente;
    private List<Map<String, Object>> allRemitosData;
    private boolean loadingData = false;

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
        // El content pane se reparentea al tabbedPane de VentanaRemitos,
        // lo que dispara addNotify y el LAF pisa fuente/color del lblTitulo.
        // Re-aplicamos el theme luego de que la jerarquía se estabilice.
        javax.swing.SwingUtilities.invokeLater(() -> applyTheme(currentTheme));
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
        getContentPane().setLayout(new BorderLayout());

        // ── Panel superior (solo título) ──
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        lblTitulo = new JLabel("HISTORIAL DE REMITOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        panelSuperior.add(lblTitulo);

        // --- FILTER ---
        lblFiltroCliente = new JLabel("CLIENTE:");
        lblFiltroCliente.setFont(FUENTE_LABEL);
        lblFiltroCliente.setForeground(currentTheme.textPrimary);

        cmbFiltroCliente = new AutoCompleteComboBox();
        cmbFiltroCliente.setFont(FUENTE_INPUT_BOLD);
        cmbFiltroCliente.setPreferredSize(new Dimension(160, 22));

        themeComboEditor(cmbFiltroCliente, currentTheme);
        addLiveFilter(cmbFiltroCliente);
        cmbFiltroCliente.addActionListener(e -> {
            if (!loadingData && allRemitosData != null) aplicarFiltro();
        });
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

        FlowLayout fl_panelFiltro = new FlowLayout(FlowLayout.LEFT, 10, 7);
        panelFiltro = new JPanel(fl_panelFiltro);
        panelFiltro.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(169, 169, 169)));
        panelFiltro.setBackground(currentTheme.bgSurface);
        panelFiltro.add(lblFiltroCliente);
        panelFiltro.add(cmbFiltroCliente);

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

        // ── Panel central (null layout como VentanaRemitos) ──
        panelCentro = new JPanel();
        panelCentro.setBackground(new Color(200, 212, 235));
        panelCentro.setBorder(null);
        panelCentro.setLayout(null);
        panelFiltro.setBounds(10, 10, 988, 36);
        scrollPane.setBounds(10, 56, 988, 434);
        panelBotones.setBounds(10, 515, 1004, 40);
        panelCentro.add(panelFiltro);
        panelCentro.add(scrollPane);
        panelCentro.add(panelBotones);
        getContentPane().add(panelSuperior, BorderLayout.NORTH);
        getContentPane().add(panelCentro, BorderLayout.CENTER);

        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    public void cargarRemitos() {
        if (baseDatos == null) {
            if (UbicacionSistema.isSeleccionado()) {
                baseDatos = UbicacionSistema.getNombreDbReparsoft();
            } else {
                return;
            }
        }

        // Limpiar remitos huérfanos (eliminados de ReparSoft pero no de FacturaSoft)
        int limpiados = controlador.limpiarRemitosHuerfanos();
        if (limpiados > 0) {
            System.out.println("Se limpiaron " + limpiados + " remitos huérfanos");
        }

        // Cargar desde ReparSoft
        allRemitosData = dao.listarRemitosConFechas(baseDatos);
        if (allRemitosData == null) allRemitosData = new ArrayList<>();

        // Cargar desde FacturaSoft local y fusionar
        List<RemitoDTO> locales = controlador.listarTodos();
        if (locales != null) {
            Map<String, RemitoDTO> localesPorNro = new java.util.HashMap<>();
            for (RemitoDTO r : locales) {
                if (r.getNumeroRemito() != null) {
                    localesPorNro.put(r.getNumeroRemito(), r);
                }
            }
            // Corregir fecha de emision con la local (correcta) para los que ya existen
            for (Map<String, Object> item : allRemitosData) {
                String nro = (String) item.get("numeroRemito");
                if (nro != null && localesPorNro.containsKey(nro)) {
                    RemitoDTO local = localesPorNro.get(nro);
                    item.put("fechaEmision", java.sql.Date.valueOf(local.getFechaEmision()));
                    if (local.getFechaEntrega() != null) {
                        item.put("fechaEntrega", java.sql.Date.valueOf(local.getFechaEntrega()));
                    }
                }
            }
            // Agregar los que solo existen en local (no están en ReparSoft)
            for (RemitoDTO r : locales) {
                boolean existe = false;
                for (Map<String, Object> item : allRemitosData) {
                    if (r.getNumeroRemito().equals(item.get("numeroRemito"))) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("numeroRemito", r.getNumeroRemito());
                    item.put("cliente", r.getRazonSocialReceptor());
                    item.put("items", r.getItems() != null ? r.getItems().size() : 0);
                    item.put("fechaEmision", java.sql.Date.valueOf(r.getFechaEmision()));
                    item.put("fechaEntrega", r.getFechaEntrega() != null ? java.sql.Date.valueOf(r.getFechaEntrega()) : null);
                    allRemitosData.add(item);
                }
            }
        }

        // Populate filter combo
        Object selObj = cmbFiltroCliente.getSelectedItem();
        String seleccionActual = selObj != null ? selObj.toString() : "";
        java.util.Set<String> clientes = new java.util.LinkedHashSet<>();
        for (Map<String, Object> r : allRemitosData) {
            String cli = (String) r.get("cliente");
            if (cli != null && !cli.isEmpty()) clientes.add(cli);
        }
        java.util.List<String> listaClientes = new ArrayList<>(clientes);
        java.util.Collections.sort(listaClientes);
        listaClientes.add(0, "--Todos--");
        loadingData = true;
        cmbFiltroCliente.setData(listaClientes);
        cmbFiltroCliente.setEditorText("");
        if (!seleccionActual.isEmpty() && listaClientes.contains(seleccionActual)) {
            cmbFiltroCliente.setSelectedItem(seleccionActual);
        } else {
            cmbFiltroCliente.setSelectedItem("--Todos--");
        }
        loadingData = false;

        aplicarFiltro();
    }

    private String getComboText(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        String editorText = ((JTextField) combo.getEditor().getEditorComponent()).getText();
        if (sel != null) {
            String s = sel.toString();
            if ("--Todos--".equals(s)) return editorText;
            if (s != null && !s.trim().isEmpty()) return s.trim();
        }
        return editorText;
    }

    private void addLiveFilter(JComboBox<?> combo) {
        Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            ((JTextField) editorComp).getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { onFilterTextChanged(); }
                public void removeUpdate(DocumentEvent e) { onFilterTextChanged(); }
                public void changedUpdate(DocumentEvent e) { onFilterTextChanged(); }
            });
        }
    }

    private void onFilterTextChanged() {
        if (loadingData) return;
        aplicarFiltro();
    }

    private void aplicarFiltro() {
        modeloTabla.setRowCount(0);
        listaNrosRemito = new ArrayList<>();
        String filtro = getComboText(cmbFiltroCliente);
        boolean filtrar = filtro != null && !filtro.trim().isEmpty()
            && !"--Todos--".equals(filtro.trim());
        String filtroLower = filtrar ? filtro.trim().toLowerCase() : "";
        for (Map<String, Object> r : allRemitosData) {
            String cli = (String) r.get("cliente");
            if (filtrar && !cli.toLowerCase().contains(filtroLower)) continue;
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

    private void themeLabels(java.awt.Container container, Theme t) {
        for (java.awt.Component comp : container.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(FUENTE_LABEL);
                comp.setForeground(t.textPrimary);
            } else if (comp instanceof java.awt.Container) {
                themeLabels((java.awt.Container) comp, t);
            }
        }
    }

    public void applyTheme(Theme t) {
        currentTheme = t;
        boolean isLight = t.bgBase.getRed() > 128;
        Color bg = isLight ? new Color(200, 212, 235) : t.bgBase;
        Color surface = isLight ? new Color(200, 212, 235) : t.bgSurface;
        if (getContentPane() != null) getContentPane().setBackground(bg);
        if (panelSuperior != null) panelSuperior.setBackground(surface);
        if (panelCentro != null) panelCentro.setBackground(bg);
        if (panelFiltro != null) panelFiltro.setBackground(surface);
        if (panelBotones != null) panelBotones.setBackground(bg);
        if (btnActualizar != null) { btnActualizar.setBackground(t.btnBg); btnActualizar.setForeground(t.textPrimary); }
        if (btnVerPDF != null) { btnVerPDF.setBackground(t.btnBg); btnVerPDF.setForeground(t.textPrimary); }
        if (scrollPane != null) scrollPane.getViewport().setBackground(bg);
        if (tabla != null) {
            boolean isDarkTheme = t.bgBase.getRed() < 50;
            Color evenBg = isDarkTheme ? new Color(30, 40, 62) : new Color(210, 222, 242);
            Color oddBg  = isDarkTheme ? new Color(45, 58, 80) : new Color(235, 242, 252);
            Set<Integer> bold = new HashSet<>(Arrays.asList(0));
            Set<Integer> center = new HashSet<>(Arrays.asList(0, 2, 3, 4));
            TablaRenderer.applyTo(tabla, t, new HashSet<>(), bold, center, evenBg, oddBg);
            // +1pt for N° REMITO column
            javax.swing.table.TableCellRenderer base = tabla.getDefaultRenderer(Object.class);
            tabla.getColumnModel().getColumn(0).setCellRenderer((javax.swing.table.TableCellRenderer)
                (javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
                    java.awt.Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    ((javax.swing.JLabel) c).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                    return c;
                });
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
        }
        // Apply theme to all labels, then restore special ones (como VentanaRemitos)
        if (getContentPane() != null) themeLabels(getContentPane(), t);
        if (lblTitulo != null) {
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitulo.setForeground(t.brand);
        }
        if (lblFiltroCliente != null) lblFiltroCliente.setForeground(t.textPrimary);
        if (cmbFiltroCliente != null) {
            cmbFiltroCliente.setBackground(getFieldBg(cmbFiltroCliente.isEnabled()));
            cmbFiltroCliente.setForeground(cmbFiltroCliente.isEnabled() ? t.textPrimary : getDisabledFg());
            themeComboEditor(cmbFiltroCliente, t);
        }
        if (statusBar != null) {
            statusBar.setBackground(bg);
        }
        if (lblStatus != null) {
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        }
    }
}
