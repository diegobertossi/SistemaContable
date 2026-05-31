package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorReparsoft;
import com.els.facturacion.modelo.RemitoReparsoftDTO;
import com.els.facturacion.modelo.RemitoReparsoftDTO.RemitoReparsoftItem;
import com.els.facturacion.util.UbicacionSistema;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaImportarRemito extends JDialog {

    private ControladorReparsoft controlador;
    private JTable tablaRemitos;
    private DefaultTableModel modeloTablaRemitos;
    private JTable tablaItems;
    private DefaultTableModel modeloTablaItems;
    private JLabel lblCliente;
    private JButton btnImportar;
    private JButton btnCancelar;

    private RemitoReparsoftDTO remitoSeleccionado;
    private List<RemitoReparsoftDTO> remitosCache;

    public VentanaImportarRemito(JFrame parent) {
        super(parent, "Importar Remito desde ReparSoft", true);
        this.controlador = new ControladorReparsoft();
        initComponents();
        cargarRemitos();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(TemaFacturaSoft.BG_APP);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelSuperior.setBackground(TemaFacturaSoft.BG_APP);
        JLabel lblBase = new JLabel("Base: " + UbicacionSistema.getNombreDbReparsoft());
        TemaFacturaSoft.aplicarEstiloLabel(lblBase, lblBase.getText());
        panelSuperior.add(lblBase);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnRefrescar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnRefrescar.setBackground(TemaFacturaSoft.BG_SURFACE);
        TemaFacturaSoft.aplicarCursorMano(btnRefrescar);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarRemitos());
        panelSuperior.add(btnRefrescar);

        lblCliente = new JLabel("Seleccione un remito");
        TemaFacturaSoft.aplicarEstiloLabel(lblCliente, lblCliente.getText());
        panelSuperior.add(lblCliente);

        panel.add(panelSuperior, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        String[] colRemitos = {"Nro Remito", "Cliente", "CUIT", "Items"};
        modeloTablaRemitos = new DefaultTableModel(colRemitos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRemitos = new JTable(modeloTablaRemitos);
        TemaFacturaSoft.aplicarEstiloTabla(tablaRemitos);
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
        JScrollPane scrollRemitos = new JScrollPane(tablaRemitos);
        scrollRemitos.setBorder(TemaFacturaSoft.crearSeccion("REMITOS"));

        String[] colItems = {"Seleccionar", "ELS", "Equipo", "Nro Serie", "Modelo", "Marca", "Precio"};
        modeloTablaItems = new DefaultTableModel(colItems, 0) {
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
        tablaItems = new JTable(modeloTablaItems);
        TemaFacturaSoft.aplicarEstiloTabla(tablaItems);
        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(20);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaItems.getColumnModel().getColumn(6).setPreferredWidth(80);
        JScrollPane scrollItems = new JScrollPane(tablaItems);
        scrollItems.setBorder(TemaFacturaSoft.crearSeccion("ITEMS DEL REMITO"));

        splitPane.setTopComponent(scrollRemitos);
        splitPane.setBottomComponent(scrollItems);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelInferior.setBackground(TemaFacturaSoft.BG_APP);

        btnImportar = new JButton("IMPORTAR REMITO");
        btnImportar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        btnImportar.setForeground(TemaFacturaSoft.TEXT_ON_PRIMARY);
        btnImportar.setBackground(TemaFacturaSoft.ACCENT_SUCCESS);
        TemaFacturaSoft.aplicarCursorMano(btnImportar);
        btnImportar.setFocusPainted(false);
        btnImportar.addActionListener(e -> importarRemito());
        panelInferior.add(btnImportar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnCancelar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnCancelar.setBackground(TemaFacturaSoft.BG_SURFACE);
        TemaFacturaSoft.aplicarCursorMano(btnCancelar);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> dispose());
        panelInferior.add(btnCancelar);

        panel.add(panelInferior, BorderLayout.SOUTH);

        add(panel);
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
                    item.isFacturado(),
                    item.getEls(),
                    item.getEquipoNombre() != null ? item.getEquipoNombre() : "",
                    item.getNumeroSerie() != null ? item.getNumeroSerie() : "",
                    item.getModelo() != null ? item.getModelo() : "",
                    item.getMarca() != null ? item.getMarca() : "",
                    item.getPrecioPeso() != null ? "$ " + String.format("%.2f", item.getPrecioPeso()) : "$ 0.00"
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
            boolean checked = Boolean.TRUE.equals(modeloTablaItems.getValueAt(i, 0));
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
}
