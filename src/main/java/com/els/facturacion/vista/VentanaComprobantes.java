package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.util.AutoCompleteComboBox;
import javax.swing.BorderFactory;
import javax.swing.Box;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VentanaComprobantes extends javax.swing.JFrame {

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
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private ControladorFacturacion controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTitulo;
    private JButton btnActualizar;
    private JButton btnVerPDF;
    private JPanel panelSuperior;
    private JScrollPane scrollPane;


    private JPanel panelBotones;
    private JPanel panelCentro;
    private JLabel lblFiltroCliente;
    private AutoCompleteComboBox cmbFiltroCliente;
    private JLabel lblFiltroEstado;
    private JComboBox<String> cmbFiltroEstado;
    private JPanel panelFiltroComprobantes;

    private List<ComprobanteDTO> allComprobantes;
    private List<String> allClientes;
    private List<Integer> listaIds;

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
    }

    public VentanaComprobantes() {
        controlador = new ControladorFacturacion();
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
        cargarComprobantes();
    }

    private static String formatearTipo(String tipoStr) {
        if (tipoStr == null || tipoStr.isEmpty()) return "";
        if (tipoStr.contains("FCE")) return "FCE";
        String[] parts = tipoStr.split(" ");
        if (parts.length < 2) return tipoStr;
        String abbr;
        switch (parts[0]) {
            case "Factura":       abbr = "F";  break;
            case "Nota":
                boolean isCredito = false;
                for (String p : parts) if (p.startsWith("C") || p.startsWith("Cr")) { isCredito = true; break; }
                abbr = isCredito ? "NC" : "ND";
                break;
            case "Recibo":        abbr = "R";  break;
            default:              abbr = parts[0]; break;
        }
        String letra = parts[parts.length - 1];
        return abbr + "-" + letra;
    }

    private void ajustarAnchoColumnas(JTable table, int... priorizar) {
        Set<Integer> prio = new HashSet<>();
        for (int p : priorizar) prio.add(p);
        for (int i = 0; i < table.getColumnCount(); i++) {
            javax.swing.table.TableColumn col = table.getColumnModel().getColumn(i);
            int width = 60;
            javax.swing.table.TableCellRenderer hr = table.getTableHeader().getDefaultRenderer();
            java.awt.Component hc = hr.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, i);
            width = Math.max(width, hc.getPreferredSize().width + 6);
            for (int r = 0; r < Math.min(table.getRowCount(), 30); r++) {
                javax.swing.table.TableCellRenderer rnd = table.getCellRenderer(r, i);
                java.awt.Component cmp = table.prepareRenderer(rnd, r, i);
                width = Math.max(width, cmp.getPreferredSize().width + 6);
            }
            if (prio.contains(i)) width = (int) (width * 1.35);
            col.setPreferredWidth(width);
        }
    }

    private void initComponents() {
        setTitle("Historial de Comprobantes");
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

        lblTitulo = new JLabel("HISTORIAL DE COMPROBANTES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelSuperior.add(lblTitulo);
        panelSuperior.add(Box.createRigidArea(new Dimension(0, 6)));

        // --- FILTER ---
        lblFiltroCliente = new JLabel("CLIENTE:");
        lblFiltroCliente.setFont(FUENTE_LABEL);
        lblFiltroCliente.setForeground(currentTheme.textPrimary);

        cmbFiltroCliente = new AutoCompleteComboBox();
        cmbFiltroCliente.setFont(FUENTE_INPUT_BOLD);
        cmbFiltroCliente.setMaximumRowCount(12);
        cmbFiltroCliente.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(cmbFiltroCliente.isEnabled()));
                setForeground(isSelected ? list.getSelectionForeground() :
                    (cmbFiltroCliente.isEnabled() ? currentTheme.textPrimary : getDisabledFg()));
                return this;
            }
        });
        installComboUI(cmbFiltroCliente);
        themeComboEditor(cmbFiltroCliente, currentTheme);
        ((JTextField) cmbFiltroCliente.getEditor().getEditorComponent()).setFont(FUENTE_INPUT);
        cmbFiltroCliente.setOnFilter(() -> aplicarFiltro());
        cmbFiltroCliente.addActionListener(e -> aplicarFiltro());

        // --- Estado filter ---
        lblFiltroEstado = new JLabel("ESTADO:");
        lblFiltroEstado.setFont(FUENTE_LABEL);
        lblFiltroEstado.setForeground(currentTheme.textPrimary);

        cmbFiltroEstado = new JComboBox<>(new String[]{"--Todos--", "Pendiente", "Parcial", "Pagada"});
        cmbFiltroEstado.setFont(FUENTE_INPUT_BOLD);
        cmbFiltroEstado.setMaximumRowCount(12);
        cmbFiltroEstado.setPreferredSize(new Dimension(110, 24));
        installComboUI(cmbFiltroEstado);
        cmbFiltroEstado.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                  int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(true));
                setForeground(isSelected ? list.getSelectionForeground() : currentTheme.textPrimary);
                return this;
            }
        });
        cmbFiltroEstado.addActionListener(e -> aplicarFiltro());

        panelFiltroComprobantes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panelFiltroComprobantes.setBackground(currentTheme.bgSurface);
        panelFiltroComprobantes.add(lblFiltroCliente);
        panelFiltroComprobantes.add(cmbFiltroCliente);
        panelFiltroComprobantes.add(lblFiltroEstado);
        panelFiltroComprobantes.add(cmbFiltroEstado);
        panelSuperior.add(panelFiltroComprobantes);

        // --- TABLE ---
        String[] columnas = {"N\u00famero", "Tipo", "Fecha", "Cliente", "Total", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.getTableHeader().setBackground(currentTheme.btnBg);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setRowHeight(22);
        tabla.setShowGrid(true);
        tabla.setAutoCreateRowSorter(false);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollPane = new JScrollPane(tabla);

        // --- BOTTOM BUTTONS ---
        btnActualizar = new JButton("ACTUALIZAR");
        btnActualizar.setFont(FUENTE_BOTON);
        btnActualizar.setForeground(currentTheme.textPrimary);
        btnActualizar.setBackground(currentTheme.btnBg);
        btnActualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarComprobantes());

        btnVerPDF = new JButton("VER PDF");
        btnVerPDF.setFont(FUENTE_BOTON);
        btnVerPDF.setForeground(currentTheme.textPrimary);
        btnVerPDF.setBackground(currentTheme.btnBg);
        btnVerPDF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerPDF.setFocusPainted(false);
        btnVerPDF.addActionListener(e -> btnVerPDFAction());

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

    }

    public void cargarComprobantes() {
        allComprobantes = controlador.listarComprobantes();
        if (allComprobantes == null) allComprobantes = new ArrayList<>();

        allClientes = new ArrayList<>();
        allClientes.add("--Todos--");
        for (ComprobanteDTO c : allComprobantes) {
            String cli = c.getRazonSocialRec();
            if (cli != null && !cli.isEmpty() && !allClientes.contains(cli)) {
                allClientes.add(cli);
            }
        }

        cmbFiltroCliente.setData(allClientes);
        cmbFiltroCliente.setSelectedItem("--Todos--");
        cmbFiltroEstado.setSelectedItem("--Todos--");
        aplicarFiltro();

        tabla.clearSelection();
    }

    private void aplicarFiltro() {
        modeloTabla.setRowCount(0);
        listaIds = new ArrayList<>();
        if (allComprobantes == null) return;

        String filtro = obtenerTextoFiltro();
        String filtroLower = filtro.toLowerCase();

        String filtroEstado = (String) cmbFiltroEstado.getSelectedItem();
        boolean filtrarEstado = filtroEstado != null && !"--Todos--".equals(filtroEstado);

        for (ComprobanteDTO c : allComprobantes) {
            String cli = c.getRazonSocialRec() != null ? c.getRazonSocialRec() : "";
            if (!filtro.isEmpty() && !cli.toLowerCase().contains(filtroLower)) {
                continue;
            }
            if (filtrarEstado) {
                String est = c.getEstadoPago();
                String estDisp = "pendiente".equals(est) ? "Pendiente"
                    : "pagada_parcial".equals(est) ? "Parcial"
                    : "pagada_total".equals(est) ? "Pagada" : est;
                if (!filtroEstado.equals(estDisp)) {
                    continue;
                }
            }
            listaIds.add(c.getId());
            String estado = c.getEstadoPago();
            String estadoDisplay = "pendiente".equals(estado) ? "Pendiente"
                : "pagada_parcial".equals(estado) ? "Parcial"
                : "pagada_total".equals(estado) ? "Pagada" : estado;
            String totalStr = c.getImporteTotal() != null ? "$ " + DF.format(c.getImporteTotal()) : "";
            modeloTabla.addRow(new Object[]{
                String.format("%04d-%08d", c.getPuntoVenta(), c.getNumero()),
                formatearTipo(c.getTipoComprobanteStr()),
                c.getFechaEmision() != null ? c.getFechaEmision().format(FMT) : "",
                cli,
                totalStr,
                estadoDisplay
            });
        }
        ajustarAnchoColumnas(tabla, 0, 4);
    }

    private String obtenerTextoFiltro() {
        String text = cmbFiltroCliente.getEditorText().trim();
        return "--Todos--".equals(text) ? "" : text;
    }



    private void btnVerPDFAction() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un comprobante", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= listaIds.size()) return;
        int id = listaIds.get(modelRow);
        ComprobanteDTO comp = controlador.buscarComprobante(id);

        if (comp == null) {
            JOptionPane.showMessageDialog(this, "Error al cargar comprobante", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (comp.getRutaPdf() == null || comp.getRutaPdf().isEmpty()) {
            int opcion = JOptionPane.showConfirmDialog(this,
                "El comprobante no tiene PDF generado. Desea generarlo ahora?",
                "Generar PDF", JOptionPane.YES_NO_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                String ruta = controlador.regenerarPDF(comp);
                if (ruta != null) {
                    JOptionPane.showMessageDialog(this, "PDF generado: " + ruta, "Exito", JOptionPane.INFORMATION_MESSAGE);
                    abrirPDF(ruta);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al generar PDF", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            File pdfFile = new File(comp.getRutaPdf());
            if (pdfFile.exists()) {
                abrirPDF(comp.getRutaPdf());
            } else {
                int opcion = JOptionPane.showConfirmDialog(this,
                    "El archivo PDF no existe en la ruta indicada. Desea regenerarlo?",
                    "PDF no encontrado", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    String ruta = controlador.regenerarPDF(comp);
                    if (ruta != null) {
                        JOptionPane.showMessageDialog(this, "PDF generado: " + ruta, "Exito", JOptionPane.INFORMATION_MESSAGE);
                        abrirPDF(ruta);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al generar PDF", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void abrirPDF(String ruta) {
        try {
            File pdfFile = new File(ruta);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al abrir PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (getContentPane() != null) getContentPane().setBackground(t.bgBase);
        if (panelSuperior != null) panelSuperior.setBackground(t.bgSurface);
        if (panelFiltroComprobantes != null) panelFiltroComprobantes.setBackground(t.bgSurface);
        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (lblFiltroCliente != null) {
            lblFiltroCliente.setForeground(t.textPrimary);
            lblFiltroCliente.setFont(FUENTE_LABEL);
        }
        if (cmbFiltroCliente != null) {
            installComboUI(cmbFiltroCliente);
            cmbFiltroCliente.setBackground(getFieldBg(cmbFiltroCliente.isEnabled()));
            cmbFiltroCliente.setForeground(cmbFiltroCliente.isEnabled() ? t.textPrimary : getDisabledFg());
            themeComboEditor(cmbFiltroCliente, t);
        }
        if (lblFiltroEstado != null) {
            lblFiltroEstado.setForeground(t.textPrimary);
            lblFiltroEstado.setFont(FUENTE_LABEL);
        }
        if (cmbFiltroEstado != null) {
            installComboUI(cmbFiltroEstado);
            cmbFiltroEstado.setBackground(getFieldBg(true));
            cmbFiltroEstado.setForeground(t.textPrimary);
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
            Set<Integer> currency = new HashSet<>(Arrays.asList(4));
            Set<Integer> bold     = new HashSet<>(Arrays.asList(0));
            Set<Integer> center   = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5));
            TablaRenderer.applyTo(tabla, t, currency, bold, center, evenBg, oddBg);
            // Estado column color coding
            boolean dark = t.bgBase.getRed() < 50;
            Color pagadaBg   = dark ? new Color(30, 70, 40)   : new Color(220, 248, 220);
            Color pendienteBg = dark ? new Color(70, 30, 30)   : new Color(255, 200, 200);
            Color parcialBg   = dark ? new Color(75, 58, 25)   : new Color(255, 220, 165);
            javax.swing.table.TableCellRenderer base = tabla.getDefaultRenderer(Object.class);
            tabla.getColumnModel().getColumn(5).setCellRenderer(
                (javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) -> {
                    java.awt.Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                    if (!isSelected && value != null) {
                        String v = value.toString();
                        if ("Pagada".equals(v))            c.setBackground(pagadaBg);
                        else if ("Pendiente".equals(v))    c.setBackground(pendienteBg);
                        else if ("Parcial".equals(v))      c.setBackground(parcialBg);
                    }
                    return c;
                });
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
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

    private void installComboUI(JComboBox<?> combo) {
        combo.setUI(new CustomComboUI());
        if (combo instanceof AutoCompleteComboBox) {
            ((AutoCompleteComboBox) combo).refreshListeners();
        }
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

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaComprobantes().setVisible(true));
    }
}
