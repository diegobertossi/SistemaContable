package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorReparsoft;
import com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem;
import com.els.facturacion.util.UbicacionSistema;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class VentanaEquiposPresupuestados extends JDialog {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 11);

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

    private List<RemitoReparsoftItem> equiposCache;
    private List<RemitoReparsoftItem> seleccionados;
    private final String clienteNombre;

    public VentanaEquiposPresupuestados(JFrame parent, String clienteNombre) {
        super(parent, "Equipos Presupuestados - " + clienteNombre, true);
        this.controlador = new ControladorReparsoft();
        this.clienteNombre = clienteNombre;
        this.seleccionados = new ArrayList<>();
        initComponents();
        applyTheme(currentTheme);
        cargarEquipos();
        setLocationRelativeTo(parent);
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setSize(750, 500);
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

        lblCliente = new JLabel("Cliente: " + clienteNombre);
        lblCliente.setFont(FUENTE_LABEL);
        lblCliente.setForeground(currentTheme.textPrimary);
        panelSuperior.add(lblCliente);

        panel.add(panelSuperior, BorderLayout.NORTH);

        String[] colEquipos = {"Seleccionar", "ELS", "Equipo", "Nro Serie", "Modelo", "Marca", "Precio"};
        modeloTablaEquipos = new DefaultTableModel(colEquipos, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
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
        tablaEquipos.getColumnModel().getColumn(0).setPreferredWidth(20);
        tablaEquipos.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaEquipos.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaEquipos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaEquipos.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaEquipos.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaEquipos.getColumnModel().getColumn(6).setPreferredWidth(80);
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
        String base = UbicacionSistema.getNombreDbReparsoft();
        if (base == null) return;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        modeloTablaEquipos.setRowCount(0);
        lblCliente.setText("Cargando...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                equiposCache = controlador.listarEquiposPorCliente(base, clienteNombre);
                return null;
            }

            @Override
            protected void done() {
                if (equiposCache != null) {
                    for (RemitoReparsoftItem item : equiposCache) {
                        modeloTablaEquipos.addRow(new Object[]{
                            item.isFacturado(),
                            item.getEls(),
                            item.getEquipoNombre() != null ? item.getEquipoNombre() : "",
                            item.getNumeroSerie() != null ? item.getNumeroSerie() : "",
                            item.getModelo() != null ? item.getModelo() : "",
                            item.getMarca() != null ? item.getMarca() : "",
                            item.getPrecioPeso() != null ? "$ " + String.format("%.2f", item.getPrecioPeso()) : "$ 0.00"
                        });
                    }
                    tablaEquipos.getTableHeader().repaint();
                    tablaEquipos.repaint();
                }
                lblCliente.setText(equiposCache != null ? equiposCache.size() + " equipos cargados para: " + clienteNombre : "Sin datos");
                setCursor(Cursor.getDefaultCursor());
            }
        };
        worker.execute();
    }

    private RemitoReparsoftItem getItemAtRow(int row) {
        if (equiposCache == null || row < 0 || row >= equiposCache.size()) return null;
        return equiposCache.get(row);
    }

    private void seleccionarEquipos() {
        List<RemitoReparsoftItem> seleccionadosTemp = new ArrayList<>();
        for (int i = 0; i < modeloTablaEquipos.getRowCount(); i++) {
            boolean checked = Boolean.TRUE.equals(modeloTablaEquipos.getValueAt(i, 0));
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
        if (tablaEquipos != null) {
            TablaRenderer.applyTo(tablaEquipos, t);
            if (tablaEquipos.getTableHeader() != null) {
                Theme.styleTableHeader(tablaEquipos.getTableHeader(), t);
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
