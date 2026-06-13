package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorRemitos;
import com.els.facturacion.dao.RemitoReparsoftLecturaDAO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.RemitoDTO;
import com.els.facturacion.modelo.RemitoItemDTO;
import com.els.facturacion.util.AutoCompleteComboBox;
import com.els.facturacion.util.UbicacionSistema;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VentanaRemitos extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_INPUT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FUENTE_INPUT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FUENTE_TABLA = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Color DISABLED_FG_LIGHT = new Color(95, 97, 106);
    private static final Color DISABLED_FG_DARK = new Color(210, 207, 190);
    private static final Color LIGHT_READONLY_BG = new Color(236, 237, 241);
    private static final Color LIGHT_EDITABLE_BG = new Color(255, 253, 230);
    private static final Color DARK_READONLY_BG = new Color(28, 33, 55);
    private static final Color DARK_EDITABLE_BG = new Color(22, 27, 45);

    private static final String[] COLUMNAS = {
        "ELS", "EQUIPO", "MARCA", "MODELO", "N\u00b0 SERIE", "AVISO", "ESTADO TEC", "ESTADO COM", "AGREGAR A REMITO"
    };
    private static final int[] ANCHOS = { 60, 200, 120, 120, 120, 60, 100, 100, 130 };

    private Theme currentTheme;
    private RemitoReparsoftLecturaDAO dao;
    private String baseDatos;

    private JPanel panel;
    private JPanel panelFiltro;
    private JPanel panelSuperior;
    private JLabel lblTitulo;
    private JLabel lblCliente;
    private AutoCompleteComboBox cmbCliente;
    private JLabel lblSucursal;
    private AutoCompleteComboBox cmbSucursal;
    private JScrollPane scrollPane;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JSeparator separator;
    private JLabel lblSeleccionRemito;
    private JTextField txtTipoRemito;
    private JButton btnCambiarN;
    private JPanel panelDatosRemito;
    private JLabel lblUbicacion;
    private JComboBox<String> cmbUbicacion;
    private JLabel lblNroRemito;
    private JTextField txtNumeroRemito;
    private JLabel lblCantBultos;
    private JTextField txtCantBultos;
    private JPanel panelAcciones;
    private JLabel lblRemitoConformado;
    private JTextField txtRemitoConformado;
    private JButton btnVisualizarRemito;
    private JButton btnGuardarRemito;

    private JTabbedPane tabbedPane;
    private VentanaVisualizacionRemitos visView;

    private boolean cargandoDatos = false;
    private boolean remitoGenerado = false;
    private int idClienteActual = -1;
    private int idSucursalActual = -1;
    private String clienteNombre = "";
    private String sucursalNombre = "";
    private String part1 = "";
    private ControladorRemitos controladorRemitos;

    public VentanaRemitos() {
        super();
        this.currentTheme = VentanaPrincipal.getCurrentTheme();
        this.dao = new RemitoReparsoftLecturaDAO();
        this.controladorRemitos = new ControladorRemitos();
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
        cargarClientes();
        setLocationCenter();
    }

    private void initComponents() {
        setTitle("M\u00d3DULO REMITOS");
        setSize(1024, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Iconosoft.png"));
            setIconImage(icon);
        } catch (Exception e) { }

        // ── Tabbed Pane ─────────────────────────────────────────────
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // ── Tab 0: Generar Remito ───────────────────────────────────
        JPanel tabGenerar = new JPanel(new BorderLayout());
        tabGenerar.setOpaque(false);

        // ── Title panel ────────────────────────────────────────────
        panelSuperior = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        panelSuperior.setBackground(currentTheme.bgSurface);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        lblTitulo = new JLabel("M\u00d3DULO REMITOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.brand);
        panelSuperior.add(lblTitulo);
        tabGenerar.add(panelSuperior, BorderLayout.NORTH);

        panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(null);
        panel.setLayout(null);
        tabGenerar.add(panel, BorderLayout.CENTER);

        // ── Filter panel ────────────────────────────────────────────
        panelFiltro = new JPanel();
        panelFiltro.setBounds(10, 10, 988, 36);
        panelFiltro.setLayout(null);
        panel.add(panelFiltro);

        lblCliente = new JLabel("CLIENTE:");
        lblCliente.setFont(FUENTE_LABEL);
        lblCliente.setForeground(currentTheme.textPrimary);
        lblCliente.setBounds(10, 8, 60, 20);
        panelFiltro.add(lblCliente);

        cmbCliente = new AutoCompleteComboBox();
        cmbCliente.setFont(FUENTE_INPUT_BOLD);
        cmbCliente.setBounds(75, 8, 320, 22);
        themeComboEditor(cmbCliente, currentTheme);
        panelFiltro.add(cmbCliente);

        lblSucursal = new JLabel("SUCURSAL:");
        lblSucursal.setFont(FUENTE_LABEL);
        lblSucursal.setForeground(currentTheme.textPrimary);
        lblSucursal.setBounds(420, 8, 70, 20);
        panelFiltro.add(lblSucursal);

        cmbSucursal = new AutoCompleteComboBox();
        cmbSucursal.setFont(FUENTE_INPUT_BOLD);
        cmbSucursal.setBounds(495, 8, 480, 22);
        themeComboEditor(cmbSucursal, currentTheme);
        panelFiltro.add(cmbSucursal);

        cmbCliente.addActionListener(e -> onClienteChanged());
        cmbSucursal.addActionListener(e -> onSucursalChanged());

        // ── Table ───────────────────────────────────────────────────
        scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 56, 988, 280);
        panel.add(scrollPane);

        modeloTabla = new DefaultTableModel(null, COLUMNAS) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 8 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8 && !remitoGenerado;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(FUENTE_TABLA);
        tabla.setRowHeight(22);
        tabla.setIntercellSpacing(new Dimension(3, 2));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setResizingAllowed(false);
        for (int i = 0; i < ANCHOS.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(ANCHOS[i]);
        }
        scrollPane.setViewportView(tabla);

        // ── Separator ───────────────────────────────────────────────
        separator = new JSeparator();
        separator.setBounds(10, 346, 988, 2);
        panel.add(separator);

        // ── Seleccion de remito label ───────────────────────────────
        lblSeleccionRemito = new JLabel("SELECCI\u00d3N DE REMITO:", SwingConstants.LEFT);
        lblSeleccionRemito.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSeleccionRemito.setForeground(currentTheme.textPrimary);
        lblSeleccionRemito.setBounds(79, 355, 140, 22);
        panel.add(lblSeleccionRemito);

        // ── tipo remito text ────────────────────────────────────────
        txtTipoRemito = new JTextField();
        txtTipoRemito.setHorizontalAlignment(SwingConstants.CENTER);
        txtTipoRemito.setFont(FUENTE_INPUT_BOLD);
        txtTipoRemito.setEditable(false);
        txtTipoRemito.setBorder(null);
        txtTipoRemito.setOpaque(false);
        txtTipoRemito.setBounds(550, 355, 140, 22);
        panel.add(txtTipoRemito);

        // ── Datos remito panel (ubicacion, nro, bultos) ─────────────
        panelDatosRemito = new JPanel();
        panelDatosRemito.setBounds(79, 380, 350, 99);
        panelDatosRemito.setLayout(null);
        panel.add(panelDatosRemito);

        lblUbicacion = new JLabel("UBICACI\u00d3N:");
        lblUbicacion.setFont(FUENTE_LABEL);
        lblUbicacion.setForeground(currentTheme.textPrimary);
        lblUbicacion.setBounds(10, 6, 80, 20);
        panelDatosRemito.add(lblUbicacion);

        cmbUbicacion = new JComboBox<>();
        cmbUbicacion.setFont(FUENTE_INPUT_BOLD);
        cmbUbicacion.setBounds(115, 6, 200, 20);
        panelDatosRemito.add(cmbUbicacion);
        installComboUI(cmbUbicacion);
        cmbUbicacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(getFieldBg(cmbUbicacion.isEnabled()));
                setForeground(cmbUbicacion.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                setFont(cmbUbicacion.getFont());
                return this;
            }
            @Override
            public void paintComponent(Graphics g) {
                setBackground(getFieldBg(cmbUbicacion.isEnabled()));
                setForeground(cmbUbicacion.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
                super.paintComponent(g);
            }
        });

        lblNroRemito = new JLabel("N\u00b0 DE REMITO:");
        lblNroRemito.setFont(FUENTE_LABEL);
        lblNroRemito.setForeground(currentTheme.textPrimary);
        lblNroRemito.setBounds(10, 36, 85, 20);
        panelDatosRemito.add(lblNroRemito);

        txtNumeroRemito = new JTextField();
        txtNumeroRemito.setFont(FUENTE_INPUT_BOLD);
        txtNumeroRemito.setEditable(false);
        txtNumeroRemito.setBounds(115, 36, 200, 20);
        txtNumeroRemito.setDisabledTextColor(getDisabledFg());
        panelDatosRemito.add(txtNumeroRemito);

        lblCantBultos = new JLabel("CANT. DE BULTOS:");
        lblCantBultos.setFont(FUENTE_INPUT);
        lblCantBultos.setForeground(currentTheme.textPrimary);
        lblCantBultos.setBounds(10, 66, 100, 20);
        panelDatosRemito.add(lblCantBultos);

        txtCantBultos = new JTextField();
        txtCantBultos.setFont(FUENTE_INPUT_BOLD);
        txtCantBultos.setBounds(115, 66, 200, 20);
        txtCantBultos.setDisabledTextColor(getDisabledFg());
        panelDatosRemito.add(txtCantBultos);

        cmbUbicacion.addActionListener(e -> onUbicacionChanged());

        // ── Acciones panel (conformado + botones) ──────────────────
        panelAcciones = new JPanel();
        panelAcciones.setBounds(550, 380, 370, 99);
        panelAcciones.setLayout(null);
        panelAcciones.setVisible(false);
        panel.add(panelAcciones);

        lblRemitoConformado = new JLabel("REMITO CONFORMADO");
        lblRemitoConformado.setFont(FUENTE_LABEL);
        lblRemitoConformado.setForeground(currentTheme.textPrimary);
        lblRemitoConformado.setBounds(10, 10, 150, 20);
        panelAcciones.add(lblRemitoConformado);

        txtRemitoConformado = new JTextField();
        txtRemitoConformado.setFont(FUENTE_INPUT_BOLD);
        txtRemitoConformado.setEditable(false);
        txtRemitoConformado.setHorizontalAlignment(SwingConstants.CENTER);
        txtRemitoConformado.setBounds(10, 35, 150, 22);
        txtRemitoConformado.setDisabledTextColor(getDisabledFg());
        panelAcciones.add(txtRemitoConformado);

        btnVisualizarRemito = new JButton("VISUALIZAR REMITO");
        btnVisualizarRemito.setFont(FUENTE_BOTON);
        btnVisualizarRemito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVisualizarRemito.setFocusPainted(false);
        btnVisualizarRemito.setBounds(175, 10, 180, 35);
        btnVisualizarRemito.addActionListener(e -> visualizarRemito());
        panelAcciones.add(btnVisualizarRemito);

        btnGuardarRemito = new JButton("GUARDAR REMITO");
        btnGuardarRemito.setFont(FUENTE_BOTON);
        btnGuardarRemito.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGuardarRemito.setFocusPainted(false);
        btnGuardarRemito.setBounds(175, 55, 180, 35);
        btnGuardarRemito.addActionListener(e -> guardarRemito());
        panelAcciones.add(btnGuardarRemito);

        // ── Cambiar nro button ─────────────────────────────────────
        btnCambiarN = new JButton("CAMBIAR N°");
        btnCambiarN.setFont(FUENTE_BOTON);
        btnCambiarN.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCambiarN.setFocusPainted(false);
        btnCambiarN.setBounds(439, 424, 99, 20);
        btnCambiarN.addActionListener(e -> {
            txtNumeroRemito.setEditable(true);
            txtNumeroRemito.requestFocus();
        });
        panel.add(btnCambiarN);

        cargarUbicaciones();

        tabbedPane.addTab("Generar Remito", tabGenerar);

        // ── Tab 1: Visualizar Remitos ──────────────────────────────
        visView = new VentanaVisualizacionRemitos();
        Container visContent = visView.getContentPane();
        visView.remove(visContent);
        tabbedPane.addTab("Visualizar Remitos", visContent);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1 && visView != null) {
                visView.cargarRemitos();
            }
        });

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    // ─── Data loading ──────────────────────────────────────────────────

    private void cargarClientes() {
        if (!UbicacionSistema.isSeleccionado()) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar una ubicaci\u00f3n primero.",
                "Sin ubicaci\u00f3n", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }
        baseDatos = UbicacionSistema.getNombreDbReparsoft();
        cargandoDatos = true;
        List<String> clientes = dao.listarClientes(baseDatos);
        cmbCliente.setData(clientes);
        cargandoDatos = false;
    }

    private String getComboText(JComboBox<String> combo) {
        Object sel = combo.getSelectedItem();
        if (sel != null) {
            String s = sel.toString();
            if (s != null && !s.trim().isEmpty()) return s.trim();
        }
        return ((JTextField) combo.getEditor().getEditorComponent()).getText();
    }

    private void onClienteChanged() {
        if (cargandoDatos) return;
        if (remitoGenerado) resetForm();
        String sel = getComboText(cmbCliente);
        if (sel == null || sel.trim().isEmpty()) return;
        clienteNombre = sel.trim();
        idClienteActual = dao.idClienteporNombre(baseDatos, clienteNombre);
        if (idClienteActual < 0) return;

        cargandoDatos = true;
        Map<Integer, String> sucursales = dao.listarSucursalesPorCliente(baseDatos, idClienteActual);
        List<String> nomSuc = new ArrayList<>(sucursales.values());
        cmbSucursal.setData(nomSuc);
        cargandoDatos = false;

        idSucursalActual = -1;
        sucursalNombre = "";
        cmbSucursal.setSelectedIndex(-1);
        cmbSucursal.setEditorText("");
        cargarEquipos();
    }

    private void onSucursalChanged() {
        if (cargandoDatos) return;
        if (idClienteActual < 0) return;
        String sel = getComboText(cmbSucursal);
        if (sel == null || sel.trim().isEmpty()) return;
        sucursalNombre = sel.trim();
        idSucursalActual = dao.idSucursalporNombre(baseDatos, sucursalNombre, idClienteActual);
        if (idSucursalActual < 0) return;
        cargarEquipos();
    }

    private void cargarEquipos() {
        if (idClienteActual < 0) return;
        List<Map<String, Object>> equipos = dao.listarEquiposParaRemito(baseDatos, idClienteActual, idSucursalActual);
        modeloTabla.setRowCount(0);
        for (Map<String, Object> eq : equipos) {
            modeloTabla.addRow(new Object[] {
                eq.get("els"),
                eq.get("equipo"),
                eq.get("marca"),
                eq.get("modelo"),
                eq.get("serie"),
                eq.get("aviso"),
                eq.get("estadoTec"),
                eq.get("estadoCom"),
                false
            });
        }
    }

    private void cargarUbicaciones() {
        if (!UbicacionSistema.isSeleccionado()) return;
        String db = UbicacionSistema.getNombreDbReparsoft();
        List<String> ubicaciones = dao.listarUbicaciones(db);
        cmbUbicacion.removeAllItems();
        cmbUbicacion.addItem("--Seleccionar Ubicaci\u00f3n--");
        for (String u : ubicaciones) {
            cmbUbicacion.addItem(u);
        }
    }

    private void onUbicacionChanged() {
        if (remitoGenerado) resetForm();
        if (cmbUbicacion.getSelectedIndex() <= 0) {
            txtTipoRemito.setText("");
            txtTipoRemito.setVisible(false);
            panelAcciones.setVisible(false);
            txtNumeroRemito.setText("");
            txtRemitoConformado.setText("");
            return;
        }
        String selected = (String) cmbUbicacion.getSelectedItem();
        String[] parts = selected.split(" - ");
        if (parts.length > 0) {
            try {
                int codigo = Integer.parseInt(parts[0].trim());
                int idx = cmbUbicacion.getSelectedIndex();
                if (idx == 1 || idx == 2 || idx == 3 || idx == 4) {
                    txtTipoRemito.setText("REMITO PREIMPRESO");
                } else {
                    txtTipoRemito.setText("REMITO COM\u00daN");
                }
                txtTipoRemito.setVisible(true);
                panelAcciones.setVisible(true);

                int nextNro = dao.obtenerNumeroRemito(baseDatos, codigo);
                String nroStr = String.format("%08d", nextNro);
                part1 = parts[0].trim();
                txtNumeroRemito.setText(nroStr);
                txtRemitoConformado.setText(part1 + " - " + nroStr);
            } catch (NumberFormatException e) { }
        }
    }

    // ─── Remito generation ─────────────────────────────────────────────

    private void visualizarRemito() {
        int filas = modeloTabla.getRowCount();
        int cont = 0;
        for (int i = 0; i < filas; i++) {
            Boolean val = (Boolean) modeloTabla.getValueAt(i, 8);
            if (val != null && val) cont++;
        }
        if (txtCantBultos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la 'CANTIDAD DE BULTOS'");
            return;
        }
        if (cont == 0) {
            JOptionPane.showMessageDialog(this, "Debe agregar al menos un equipo al remito");
            return;
        }
        JOptionPane.showMessageDialog(this,
            "Funcionalidad de visualizaci\u00f3n de remito en desarrollo.\n"
            + "Equipos seleccionados: " + cont);
    }

    private void guardarRemito() {
        int filas = modeloTabla.getRowCount();
        List<Integer> elsSeleccionados = new ArrayList<>();
        List<RemitoItemDTO> items = new ArrayList<>();
        for (int i = 0; i < filas; i++) {
            Boolean val = (Boolean) modeloTabla.getValueAt(i, 8);
            if (val != null && val) {
                int els = (int) modeloTabla.getValueAt(i, 0);
                String equipo = (String) modeloTabla.getValueAt(i, 1);
                String marca = (String) modeloTabla.getValueAt(i, 2);
                String modelo = (String) modeloTabla.getValueAt(i, 3);
                String serie = (String) modeloTabla.getValueAt(i, 4);
                String descripcion = String.format("%s %s %s s/n: %s",
                        equipo != null ? equipo : "",
                        marca != null ? marca : "",
                        modelo != null ? modelo : "",
                        serie != null ? serie : "").trim();
                elsSeleccionados.add(els);
                items.add(new RemitoItemDTO(String.valueOf(els), descripcion,
                        BigDecimal.ONE, "Unidad", els));
            }
        }
        if (txtCantBultos.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar la 'CANTIDAD DE BULTOS'");
            return;
        }
        if (elsSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe agregar al menos un equipo al remito");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "\u00bfDesea generar un remito para este/os equipos?",
            "Confirmaci\u00f3n", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        btnGuardarRemito.setEnabled(false);
        btnGuardarRemito.setText("GUARDANDO...");

        CuitConfigDTO emisor = controladorRemitos.getCuitActivo();
        if (emisor == null) {
            JOptionPane.showMessageDialog(this,
                "No hay un CUIT emisor configurado.\nConfigure uno en Herramientas > Configurar Certificados.",
                "Error", JOptionPane.ERROR_MESSAGE);
            btnGuardarRemito.setEnabled(true);
            btnGuardarRemito.setText("GUARDAR REMITO");
            return;
        }

        RemitoDTO remito = new RemitoDTO();
        remito.setNumeroRemito(txtRemitoConformado.getText().trim());
        remito.setFechaEmision(LocalDate.now());
        remito.setCuitEmisor(emisor.getCuit());
        remito.setRazonSocialEmisor(emisor.getRazonSocial());
        remito.setDomicilioEmisor("");
        remito.setCuitReceptor("");
        remito.setRazonSocialReceptor(clienteNombre);
        remito.setDomicilioReceptor("");
        remito.setEstado("pendiente");
        remito.setObservaciones("Cant. bultos: " + txtCantBultos.getText().trim());
        remito.setItems(items);

        int codigoUbicacion;
        try {
            codigoUbicacion = Integer.parseInt(part1);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error al determinar ubicaci\u00f3n del remito",
                "Error", JOptionPane.ERROR_MESSAGE);
            btnGuardarRemito.setEnabled(true);
            btnGuardarRemito.setText("GUARDAR REMITO");
            return;
        }

        new SwingWorker<Void, Void>() {
            private int remitoId;

            @Override
            protected Void doInBackground() throws Exception {
                remitoId = controladorRemitos.guardarRemito(remito, elsSeleccionados, codigoUbicacion);
                return null;
            }
            @Override
            protected void done() {
                if (remitoId > 0) {
                    JOptionPane.showMessageDialog(VentanaRemitos.this,
                        "Remito guardado correctamente: " + remito.getNumeroRemito());
                    setFormEditable(false);
                    remitoGenerado = true;
                } else {
                    JOptionPane.showMessageDialog(VentanaRemitos.this,
                        "Error al guardar el remito",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    btnGuardarRemito.setEnabled(true);
                    btnGuardarRemito.setText("GUARDAR REMITO");
                }
            }
        }.execute();
    }

    // ─── Form state ────────────────────────────────────────────────────

    private void setFormEditable(boolean editable) {
        cmbCliente.setEnabled(editable);
        cmbSucursal.setEnabled(editable);
        cmbUbicacion.setEnabled(editable);
        txtCantBultos.setEnabled(editable);
        btnCambiarN.setEnabled(editable);
        btnGuardarRemito.setEnabled(editable);
        btnVisualizarRemito.setEnabled(editable);
        if (!editable) {
            txtNumeroRemito.setEnabled(false);
        }
    }

    private void resetForm() {
        remitoGenerado = false;
        setFormEditable(true);
        txtCantBultos.setText("");
        txtNumeroRemito.setText("");
        txtRemitoConformado.setText("");
        txtTipoRemito.setVisible(false);
        panelAcciones.setVisible(false);
        cmbUbicacion.setSelectedIndex(0);
        modeloTabla.setRowCount(0);
    }

    // ─── Helpers ───────────────────────────────────────────────────────

    private Color getDisabledFg() {
        return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
    }

    private Color getFieldBg(boolean editing) {
        return currentTheme.bgBase.getRed() > 128
            ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
            : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
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

    private void themeLabels(Container container, Theme t) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                c.setFont(FUENTE_LABEL);
                c.setForeground(t.textPrimary);
            } else if (c instanceof Container) {
                themeLabels((Container) c, t);
            }
        }
    }

    // ─── Theme application ─────────────────────────────────────────────

    public void applyTheme(Theme t) {
        currentTheme = t;
        getContentPane().setBackground(t.bgBase);
        if (tabbedPane != null) {
            tabbedPane.setBackground(t.bgSurface);
            tabbedPane.setForeground(t.textPrimary);
        }
        if (panel != null) panel.setBackground(t.bgBase);
        if (panelFiltro != null) {
            panelFiltro.setBackground(t.bgSurface);
            panelFiltro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.borderLight, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)));
        }
        if (panelSuperior != null) panelSuperior.setBackground(t.bgSurface);

        themeLabels(getContentPane(), t);

        // Restore special labels
        if (lblTitulo != null) {
            lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitulo.setForeground(t.brand);
        }
        if (lblSeleccionRemito != null) {
            lblSeleccionRemito.setFont(FUENTE_LABEL);
            lblSeleccionRemito.setForeground(t.brand);
        }
        if (lblUbicacion != null) lblUbicacion.setForeground(t.brand);
        if (lblNroRemito != null) lblNroRemito.setForeground(t.brand);
        if (lblCantBultos != null) lblCantBultos.setForeground(t.brand);
        if (lblRemitoConformado != null) lblRemitoConformado.setForeground(t.brand);

        // Panel datos remito
        if (panelDatosRemito != null) {
            panelDatosRemito.setBackground(t.bgSurface);
            panelDatosRemito.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.borderLight, 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        }

        // Panel acciones
        if (panelAcciones != null) {
            panelAcciones.setBackground(t.bgSurface);
            panelAcciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.borderLight, 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        }

        // Buttons
        if (btnVisualizarRemito != null) {
            btnVisualizarRemito.setBackground(t.btnBg);
            btnVisualizarRemito.setForeground(t.textPrimary);
        }
        if (btnGuardarRemito != null) {
            btnGuardarRemito.setBackground(t.btnBg);
            btnGuardarRemito.setForeground(t.textPrimary);
        }
        if (btnCambiarN != null) {
            btnCambiarN.setBackground(t.btnBg);
            btnCambiarN.setForeground(t.textPrimary);
        }

        // Text fields
        if (txtNumeroRemito != null) {
            txtNumeroRemito.setBackground(getFieldBg(txtNumeroRemito.isEnabled()));
            txtNumeroRemito.setForeground(t.textPrimary);
            txtNumeroRemito.setDisabledTextColor(getDisabledFg());
            txtNumeroRemito.setCaretColor(t.textPrimary);
        }
        if (txtCantBultos != null) {
            txtCantBultos.setBackground(getFieldBg(txtCantBultos.isEnabled()));
            txtCantBultos.setForeground(t.textPrimary);
            txtCantBultos.setDisabledTextColor(getDisabledFg());
            txtCantBultos.setCaretColor(t.textPrimary);
        }
        if (txtRemitoConformado != null) {
            txtRemitoConformado.setBackground(getFieldBg(txtRemitoConformado.isEnabled()));
            txtRemitoConformado.setForeground(t.textPrimary);
            txtRemitoConformado.setDisabledTextColor(getDisabledFg());
            txtRemitoConformado.setCaretColor(t.textPrimary);
        }
        if (txtTipoRemito != null) {
            txtTipoRemito.setForeground(t.textPrimary);
        }

        // Combos
        if (cmbCliente != null) themeComboEditor(cmbCliente, t);
        if (cmbSucursal != null) themeComboEditor(cmbSucursal, t);
        if (cmbUbicacion != null) {
            cmbUbicacion.setBackground(getFieldBg(cmbUbicacion.isEnabled()));
            cmbUbicacion.setForeground(cmbUbicacion.isEnabled() ? t.textPrimary : getDisabledFg());
            installComboUI(cmbUbicacion);
        }

        // Table
        if (tabla != null) {
            TablaRenderer.applyTo(tabla, t);
            if (tabla.getTableHeader() != null) {
                Theme.styleTableHeader(tabla.getTableHeader(), t);
            }
        }

        // Separator
        if (separator != null) separator.setForeground(t.borderLight);
    }

    // ─── Centering ──────────────────────────────────────────────────────

    private void setLocationCenter() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frame = getSize();
        setLocation((screen.width - frame.width) / 2, (screen.height - frame.height) / 2);
    }

    // ─── CustomComboUI ──────────────────────────────────────────────────

    private static class CustomComboUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(comboBox.getBackground());
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            ListCellRenderer<Object> renderer = comboBox.getRenderer();
            Component c;
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
}
