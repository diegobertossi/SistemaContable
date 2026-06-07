package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorPagos;
import com.els.facturacion.modelo.FacturaPagoDTO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class VentanaRecibos extends javax.swing.JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private ControladorPagos controlador;

    private JTable tablaPagos;
    private DefaultTableModel modeloTablaPagos;
    private JScrollPane scrollPagos;
    private JPanel panelPagos;
    private JPanel panelAcciones;
    private JButton btnGenerarRecibo;
    private JButton btnVerRecibo;
    private JPanel statusBar;
    private JLabel lblStatus;
    private JLabel lblTitulo;
    private JLabel lblFiltroCliente;
    private JComboBox<String> cmbFiltroCliente;
    private JPanel panelFiltro;
    private JPanel panelTitulo;

    private List<Integer> pagosIds;
    private List<String> reciboOriginalNums;
    private List<FacturaPagoDTO> allPagos;
    private List<String> allClientes;
    private boolean actualizandoCombo;

    public VentanaRecibos() {
        controlador = new ControladorPagos();
        initComponents();
        applyTheme(currentTheme);
        cargarHistorialCompleto();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Recibos");
        setSize(1024, 768);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgSurface);

        lblTitulo = new JLabel("GESTI\u00d3N DE RECIBOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);

        panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(currentTheme.bgSurface);
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(6, 10, 4, 10));
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);

        lblFiltroCliente = new JLabel("CLIENTE:");
        lblFiltroCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFiltroCliente.setForeground(currentTheme.textPrimary);

        cmbFiltroCliente = new JComboBox<>();
        cmbFiltroCliente.setEditable(true);
        cmbFiltroCliente.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFiltroCliente.setPreferredSize(new Dimension(260, 26));
        cmbFiltroCliente.setMaximumRowCount(12);

        panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panelFiltro.setBackground(currentTheme.bgSurface);
        panelFiltro.setBorder(BorderFactory.createEmptyBorder(0, 14, 6, 10));
        panelFiltro.add(lblFiltroCliente);
        panelFiltro.add(cmbFiltroCliente);

        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(currentTheme.bgSurface);
        panelNorte.add(panelTitulo);
        panelNorte.add(panelFiltro);

        JTextField editorFiltro = (JTextField) cmbFiltroCliente.getEditor().getEditorComponent();
        editorFiltro.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onFiltroCambiado(); }
            @Override public void removeUpdate(DocumentEvent e) { onFiltroCambiado(); }
            @Override public void changedUpdate(DocumentEvent e) { onFiltroCambiado(); }
        });
        cmbFiltroCliente.addActionListener(e -> {
            if (actualizandoCombo) return;
            Object sel = cmbFiltroCliente.getSelectedItem();
            if (sel != null) {
                String txt = sel.toString();
                if (!txt.equals(editorFiltro.getText())) {
                    editorFiltro.setText(txt);
                }
            }
        });

        panelPagos = new JPanel(new BorderLayout(5, 5));
        panelPagos.setBackground(currentTheme.bgSurface);
        panelPagos.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(6, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(currentTheme.brand),
                "PAGOS Y RECIBOS",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), currentTheme.textPrimary
            )
        ));

        String[] colPagos = {"Fecha", "Cliente", "Factura", "Recibo", "Monto", "Forma de Pago", "SEL"};
        modeloTablaPagos = new DefaultTableModel(colPagos, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                if (col != 6) return false;
                Object reciboVal = getValueAt(row, 3);
                return reciboVal == null || reciboVal.toString().isEmpty();
            }
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 6 ? Boolean.class : Object.class;
            }
        };
        tablaPagos = new JTable(modeloTablaPagos);
        tablaPagos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablaPagos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaPagos.getTableHeader().setBackground(currentTheme.btnBg);
        tablaPagos.setRowHeight(22);
        tablaPagos.setShowGrid(true);
        tablaPagos.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollPagos = new JScrollPane(tablaPagos);
        panelPagos.add(scrollPagos, BorderLayout.CENTER);

        btnGenerarRecibo = new JButton("GENERAR RECIBO");
        estilizarBoton(btnGenerarRecibo);
        btnGenerarRecibo.setPreferredSize(new java.awt.Dimension(140, 28));
        btnGenerarRecibo.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "\u00bfEst\u00e1 seguro de generar el recibo?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                generarReciboDesdePago();
        });

        btnVerRecibo = new JButton("VER RECIBO");
        estilizarBoton(btnVerRecibo);
        btnVerRecibo.setPreferredSize(new java.awt.Dimension(110, 28));
        btnVerRecibo.addActionListener(e -> verReciboPagoSeleccionado());

        panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelAcciones.setBackground(bgAzulado(currentTheme));
        panelAcciones.add(btnGenerarRecibo);
        panelAcciones.add(btnVerRecibo);
        panelPagos.add(panelAcciones, BorderLayout.SOUTH);

        getContentPane().add(panelNorte, BorderLayout.NORTH);
        getContentPane().add(panelPagos);

        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    public void refrescar() {
        cargarHistorialCompleto();
    }

    private void cargarHistorialCompleto() {
        allPagos = controlador.getTodosLosPagos();
        if (allPagos == null) allPagos = new ArrayList<>();

        allClientes = new ArrayList<>();
        for (FacturaPagoDTO p : allPagos) {
            String cli = p.getClienteRazonSocial();
            if (cli != null && !cli.isEmpty() && !allClientes.contains(cli)) {
                allClientes.add(cli);
            }
        }

        aplicarFiltro();
        actualizarSugerenciasCombo();
    }

    private void aplicarFiltro() {
        modeloTablaPagos.setRowCount(0);
        pagosIds = new ArrayList<>();
        reciboOriginalNums = new ArrayList<>();
        if (allPagos == null) return;

        String filtro = obtenerTextoFiltro();
        String filtroLower = filtro.toLowerCase();

        for (FacturaPagoDTO p : allPagos) {
            String cli = p.getClienteRazonSocial() != null ? p.getClienteRazonSocial() : "";
            if (!filtro.isEmpty() && !cli.toLowerCase().contains(filtroLower)) {
                continue;
            }
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
                p.getFechaPago() != null ? p.getFechaPago().format(FMT) : "",
                cli,
                compStr != null ? compStr : "",
                p.getReciboId() != null ? reciboStr : "",
                p.getMonto() != null ? "$ " + DF.format(p.getMonto()) : "",
                p.getFormaPago() != null ? p.getFormaPago() : "",
                p.getReciboId() == null ? Boolean.FALSE : Boolean.TRUE
            });
        }
        ajustarAnchoColumnas(tablaPagos);
    }

    private void onFiltroCambiado() {
        if (actualizandoCombo) return;
        aplicarFiltro();
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (!actualizandoCombo) {
                actualizarSugerenciasCombo();
            }
        });
    }

    private String obtenerTextoFiltro() {
        Component c = cmbFiltroCliente.getEditor().getEditorComponent();
        if (c instanceof JTextField) {
            return ((JTextField) c).getText().trim();
        }
        Object sel = cmbFiltroCliente.getSelectedItem();
        return sel != null ? sel.toString().trim() : "";
    }

    private void actualizarSugerenciasCombo() {
        if (allClientes == null) return;
        JTextField editor = (JTextField) cmbFiltroCliente.getEditor().getEditorComponent();
        String texto = editor.getText();
        String textoLower = texto.toLowerCase();

        actualizandoCombo = true;
        try {
            cmbFiltroCliente.hidePopup();
            cmbFiltroCliente.removeAllItems();
            cmbFiltroCliente.addItem("");
            int agregados = 0;
            for (String cli : allClientes) {
                if (texto.isEmpty() || cli.toLowerCase().contains(textoLower)) {
                    cmbFiltroCliente.addItem(cli);
                    agregados++;
                    if (agregados >= 15) break;
                }
            }
            String textoActualEditor = editor.getText();
            if (!textoActualEditor.equals(texto)) {
                editor.setText(texto);
            }
            boolean tieneFoco = editor.hasFocus();
            if (tieneFoco && !texto.isEmpty()) {
                cmbFiltroCliente.setPopupVisible(true);
            }
        } finally {
            actualizandoCombo = false;
        }
    }

    private void generarReciboDesdePago() {
        if (pagosIds == null || pagosIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay pagos en el historial", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> idsSeleccionados = new ArrayList<>();
        for (int i = 0; i < modeloTablaPagos.getRowCount(); i++) {
            Boolean checked = (Boolean) modeloTablaPagos.getValueAt(i, 6);
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
            cargarHistorialCompleto();
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

    private void estilizarBoton(JButton btn) {
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(currentTheme.textPrimary);
        btn.setBackground(currentTheme.btnBg);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
    }

    private void ajustarAnchoColumnas(JTable table, int... priorizar) {
        java.util.Set<Integer> prio = new java.util.HashSet<>();
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
            if (prio.contains(i)) width = (int)(width * 1.35);
            col.setPreferredWidth(width);
        }
    }

    private static Color bgAzulado(Theme t) {
        int r = t.bgBase.getRed();
        int g = t.bgBase.getGreen();
        int b = t.bgBase.getBlue();
        if (r < 50) {
            return new Color(
                Math.max(0, r - 3),
                Math.max(0, g - 3),
                Math.min(255, b + 5)
            );
        }
        return new Color(
            Math.max(0, r - 18),
            Math.max(0, g - 16),
            Math.max(0, b - 6)
        );
    }

    private void applyTheme(Theme t) {
        currentTheme = t;

        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (panelTitulo != null) panelTitulo.setBackground(t.bgSurface);
        if (panelFiltro != null) panelFiltro.setBackground(t.bgSurface);
        if (lblFiltroCliente != null) lblFiltroCliente.setForeground(t.textPrimary);
        if (cmbFiltroCliente != null) {
            cmbFiltroCliente.setBackground(t.bgInput);
            cmbFiltroCliente.setForeground(t.textPrimary);
        }

        if (panelPagos != null) {
            panelPagos.setBackground(t.bgSurface);
            panelPagos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(6, 10, 10, 10),
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(t.brand),
                    "PAGOS Y RECIBOS",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 13), t.textPrimary)));
        }
        if (panelAcciones != null) panelAcciones.setBackground(bgAzulado(t));
        if (btnGenerarRecibo != null) { btnGenerarRecibo.setBackground(t.btnBg); btnGenerarRecibo.setForeground(t.textPrimary); }
        if (btnVerRecibo != null) { btnVerRecibo.setBackground(t.btnBg); btnVerRecibo.setForeground(t.textPrimary); }
        if (scrollPagos != null && scrollPagos.getViewport() != null)
            scrollPagos.getViewport().setBackground(t.bgSurface);

        if (tablaPagos != null) {
            TablaRenderer.applyTo(tablaPagos, t,
                new HashSet<>(Arrays.asList(4)),
                Collections.emptySet(),
                new HashSet<>(Arrays.asList(0, 3)),
                t.bgSurface, t.bgElevated);
            if (tablaPagos.getTableHeader() != null) {
                Theme.styleTableHeader(tablaPagos.getTableHeader(), t);
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
