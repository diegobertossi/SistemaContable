package com.els.facturacion.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.els.facturacion.util.UbicacionSistema;
import com.els.facturacion.util.UtilVaciadoBase;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    // ─── Font and color constants ────────────────────────────────────
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

    private Theme currentTheme;
    private boolean isDarkMode = false;

    // ─── Static theme propagation ────────────────────────────────────
    private static Theme staticTheme = Theme.LIGHT;
    private static final java.util.List<java.lang.ref.WeakReference<Object>> themeListeners = new java.util.ArrayList<>();

    public static Theme getCurrentTheme() { return staticTheme; }

    public static void addThemeListener(Object window) {
        themeListeners.add(new java.lang.ref.WeakReference<>(window));
    }

    private static void notifyThemeListeners(Theme t) {
        themeListeners.removeIf(ref -> {
            Object w = ref.get();
            if (w == null) return true;
            try {
                java.lang.reflect.Method m = w.getClass().getDeclaredMethod("applyTheme", Theme.class);
                m.setAccessible(true);
                m.invoke(w, t);
            } catch (Exception e) { }
            return false;
        });
    }

    // ─── Controls ──────────────────────────────────────────────────────

    private List<JButton> botones;
    private JButton btnComprobantes;
    private JButton btnClientes;
    private JButton btnRemitos;
    private JButton btnPagosRecibos;
    private JButton btnConfiguracion;
    private JButton btnCajaGastos;
    private JButton btnSalir;
    private JButton btnCerrarSesion;
    private JButton btnVaciarBases;
    private JButton btnThemeToggle;
    private JTextField textUsuario;
    private JTextField textUbicacion;
    private JTextField textProgramador;
    private JTextField textVersionSoft;
    private JPanel panelDeControl;
    private JComboBox<String> cmbUbicacion;
    private JPanel panel;
    private JLabel lblFacturaSoft;
    private JLabel lblVersion;
    private JLabel lblDescripcion;
    private JLabel lblImagen;
    private JLabel lblUbicacion;
    private JSeparator separatorBottom1;
    private JSeparator separatorBottom2;
    private JSeparator separatorTop1;
    private JSeparator separatorTop2;

    // ─── Constructor ───────────────────────────────────────────────────

    public VentanaPrincipal() {
        super();
        currentTheme = staticTheme;
        botones = new ArrayList<>();
        setResizable(false);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(500, 400));
        initialize();
        {
            boolean isDark = currentTheme.bgBase.getRed() < 50;
            Color hdrFg = isDark ? Color.WHITE : currentTheme.textPrimary;
            UIManager.put("TableHeader.foreground", hdrFg);
        }
        UIManager.put("TableHeader.background", currentTheme.bgElevated);
        applyTheme(currentTheme);
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Iconosoft.png"));
            setIconImage(icon);
        } catch (Exception e) { }
    }

    // ─── Initialization ────────────────────────────────────────────────

    private void initialize() {
        setBounds(100, 10, 520, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(null);

        panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(null);
        panel.setBounds(0, 0, 504, 461);
        getContentPane().add(panel);
        panel.setLayout(null);

        // ── Theme toggle ────────────────────────────────────────────
        btnThemeToggle = new JButton("");
        btnThemeToggle.setBounds(217, 429, 70, 32);
        btnThemeToggle.setFont(FUENTE_BOTON);
        ImageIcon sunIcon = loadToggleIcon("/img/sol.png", "\u2600");
        btnThemeToggle.setIcon(sunIcon);
        btnThemeToggle.setFocusPainted(false);
        btnThemeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnThemeToggle.setToolTipText("Cambiar a tema oscuro");
        btnThemeToggle.addActionListener(e -> toggleTheme());
        panel.add(btnThemeToggle);

        // ── Title ───────────────────────────────────────────────────
        lblFacturaSoft = new JLabel("FACTURASOFT", SwingConstants.CENTER);
        lblFacturaSoft.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblFacturaSoft.setBounds(112, 112, 280, 28);
        panel.add(lblFacturaSoft);

        // ── Close session ───────────────────────────────────────────
        btnCerrarSesion = new JButton("<html><center>CERRAR SESI\u00d3N</html>");
        btnCerrarSesion.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnCerrarSesion.setFont(FUENTE_BOTON);
        btnCerrarSesion.setForeground(currentTheme.textPrimary);
        btnCerrarSesion.setBackground(currentTheme.btnBg);
        btnCerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBounds(10, 10, 100, 35);
        try {
            btnCerrarSesion.setIcon(new ImageIcon(getClass().getResource("/img/Icono cerrar sesion.png")));
        } catch (Exception e) { }
        panel.add(btnCerrarSesion);

        // ── Exit button ─────────────────────────────────────────────
        btnSalir = new JButton("SALIR");
        btnSalir.setBounds(399, 10, 100, 35);
        btnSalir.setFont(FUENTE_BOTON);
        btnSalir.setForeground(currentTheme.textPrimary);
        btnSalir.setBackground(currentTheme.btnBg);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setFocusPainted(false);
        try {
            btnSalir.setIcon(new ImageIcon(getClass().getResource("/img/Icono salir.png")));
        } catch (Exception e) { }
        btnSalir.addActionListener(e -> System.exit(0));
        panel.add(btnSalir);

        // ── Image ───────────────────────────────────────────────────
        lblImagen = new JLabel("");
        try {
            ImageIcon icono = new ImageIcon(getClass().getResource("/img/Inicio facturacion.png"));
            int imgTargetW = 473, imgTargetH = 160;
            double scale = Math.min((double) imgTargetW / icono.getIconWidth(),
                    (double) imgTargetH / icono.getIconHeight());
            int scaledW = (int) (icono.getIconWidth() * scale);
            int scaledH = (int) (icono.getIconHeight() * scale);
            Image img = icono.getImage().getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(img));
        } catch (Exception e) { }
        lblImagen.setOpaque(false);
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setBounds(112, 10, 280, 125);
        panel.add(lblImagen);

        // ── Version ─────────────────────────────────────────────────
        lblVersion = new JLabel("Versi\u00f3n: 1.0", SwingConstants.CENTER);
        lblVersion.setFont(FUENTE_LABEL);
        lblVersion.setForeground(currentTheme.textPrimary);
        lblVersion.setBounds(215, 140, 80, 14);
        panel.add(lblVersion);

        // ── Description ─────────────────────────────────────────────
        lblDescripcion = new JLabel(
                "SISTEMA DE FACTURACI\u00d3N ELECTR\u00d3NICA Y GESTI\u00d3N CONTABLE",
                SwingConstants.CENTER);
        lblDescripcion.setFont(FUENTE_LABEL);
        lblDescripcion.setForeground(currentTheme.textPrimary);
        lblDescripcion.setBounds(35, 162, 440, 14);
        panel.add(lblDescripcion);

        // ── Separators above panel ──────────────────────────────────
        separatorTop2 = new JSeparator();
        separatorTop2.setBounds(52, 186, 400, 2);
        panel.add(separatorTop2);
        separatorTop1 = new JSeparator();
        separatorTop1.setBounds(52, 190, 400, 2);
        panel.add(separatorTop1);

        // ── Control panel ───────────────────────────────────────────
        panelDeControl = new JPanel();
        panelDeControl.setBounds(10, 198, 490, 215);
        panelDeControl.setLayout(null);
        panel.add(panelDeControl);

        int btnW = 210;
        int btnH = 48;
        int col1X = 25;
        int col2X = 255;
        int row1Y = 12;
        int row2Y = 64;
        int row3Y = 116;

        btnComprobantes = crearBoton("COMPROBANTES");
        btnComprobantes.setBounds(col1X, row1Y, btnW, btnH);
        try { btnComprobantes.setIcon(new ImageIcon(getClass().getResource("/img/Comprobantes.png"))); } catch (Exception e) { }
        btnComprobantes.addActionListener(e -> new VentanaGestionComprobantes().setVisible(true));
        panelDeControl.add(btnComprobantes);

        btnClientes = crearBoton("             CLIENTES");
        btnClientes.setBounds(col2X, row1Y, btnW, btnH);
        try { btnClientes.setIcon(new ImageIcon(getClass().getResource("/img/Clientes.png"))); } catch (Exception e) { }
        btnClientes.addActionListener(e -> new VentanaClientes().setVisible(true));
        panelDeControl.add(btnClientes);

        btnRemitos = crearBoton("            REMITOS");
        btnRemitos.setBounds(col1X, row2Y, btnW, btnH);
        try { btnRemitos.setIcon(new ImageIcon(getClass().getResource("/img/Remitos.png"))); } catch (Exception e) { }
        btnRemitos.addActionListener(e -> new VentanaRemitos().setVisible(true));
        panelDeControl.add(btnRemitos);

        btnPagosRecibos = crearBoton("PAGOS Y RECIBOS");
        btnPagosRecibos.setBounds(col2X, row2Y, btnW, btnH);
        try { btnPagosRecibos.setIcon(new ImageIcon(getClass().getResource("/img/Pagos y recibos.png"))); } catch (Exception e) { }
        btnPagosRecibos.addActionListener(e -> new VentanaPagosRecibos().setVisible(true));
        panelDeControl.add(btnPagosRecibos);

        btnConfiguracion = crearBoton(" CONFIGURACIÓN");
        btnConfiguracion.setBounds(col1X, row3Y, btnW, btnH);
        try { btnConfiguracion.setIcon(new ImageIcon(getClass().getResource("/img/Herramientas.png"))); } catch (Exception e) { }
        btnConfiguracion.addActionListener(e -> new VentanaConfiguracion().setVisible(true));
        panelDeControl.add(btnConfiguracion);

        btnCajaGastos = crearBoton("      CAJA Y GASTOS");
        btnCajaGastos.setBounds(col2X, row3Y, btnW, btnH);
        try { btnCajaGastos.setIcon(new ImageIcon(getClass().getResource("/img/Caja y gastos.png"))); } catch (Exception e) { }
        btnCajaGastos.addActionListener(e -> new VentanaCajaGastos().setVisible(true));
        panelDeControl.add(btnCajaGastos);

        // ── Location ─────────────────────────────────────────────────
        lblUbicacion = new JLabel("UBICACI\u00d3N:");
        lblUbicacion.setFont(FUENTE_LABEL);
        lblUbicacion.setForeground(currentTheme.textPrimary);
        lblUbicacion.setBounds(127, 182, 74, 20);
        panelDeControl.add(lblUbicacion);

        cmbUbicacion = new JComboBox<>();
        cmbUbicacion.addItem("");
        cmbUbicacion.addItem(UbicacionSistema.BSAS);
        cmbUbicacion.addItem(UbicacionSistema.BRC);
        cmbUbicacion.setFont(FUENTE_INPUT_BOLD);
        cmbUbicacion.setBounds(198, 182, 108, 20);
        cmbUbicacion.addActionListener(e -> {
            String selected = (String) cmbUbicacion.getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                UbicacionSistema.setUbicacion(selected);
                for (JButton btn : botones) btn.setEnabled(true);
            } else {
                UbicacionSistema.setUbicacion(null);
                for (JButton btn : botones) btn.setEnabled(false);
            }
        });
        installComboUI(cmbUbicacion);
        cmbUbicacion.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? list.getSelectionBackground() : getFieldBg(cmbUbicacion.isEnabled()));
                setForeground(isSelected ? list.getSelectionForeground() : (cmbUbicacion.isEnabled() ? currentTheme.textPrimary : getDisabledFg()));
                setFont(cmbUbicacion.getFont());
                return this;
            }
        });
        panelDeControl.add(cmbUbicacion);

        btnVaciarBases = new JButton("VACIAR BASES");
        btnVaciarBases.setBounds(313, 178, 157, 26);
        btnVaciarBases.setFont(FUENTE_BOTON);
        btnVaciarBases.setForeground(currentTheme.textPrimary);
        btnVaciarBases.setBackground(currentTheme.btnBg);
        btnVaciarBases.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVaciarBases.setFocusPainted(false);
        btnVaciarBases.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Esta acci\u00f3n eliminar\u00e1 TODOS los datos en ambas bases:\n"
                    + "  - facturacion_db_bsas\n"
                    + "  - facturacion_db_brc\n\n"
                    + "Las tablas se recrear\u00e1n con datos de prueba por defecto.\n\n"
                    + "Esta operaci\u00f3n NO SE PUEDE DESHACER.\n\n"
                    + "\u00bfEst\u00e1 seguro de continuar?",
                    "VACIAR BASES - Confirmaci\u00f3n",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                btnVaciarBases.setEnabled(false);
                btnVaciarBases.setText("VACIANDO...");
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        UtilVaciadoBase.vaciarAmbasBases();
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            get();
                            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                    "Ambas bases fueron vaciadas y recreadas correctamente.",
                                    "VACIAR BASES - \u00c9xito",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                    "Error al vaciar las bases:\n" + ex.getMessage(),
                                    "VACIAR BASES - Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } finally {
                            btnVaciarBases.setEnabled(true);
                            btnVaciarBases.setText("VACIAR BASES");
                        }
                    }
                }.execute();
            }
        });
        panelDeControl.add(btnVaciarBases);

        // ── Bottom separators ───────────────────────────────────────
        separatorBottom1 = new JSeparator();
        separatorBottom1.setBounds(52, 418, 400, 2);
        panel.add(separatorBottom1);
        separatorBottom2 = new JSeparator();
        separatorBottom2.setBounds(52, 422, 400, 2);
        panel.add(separatorBottom2);

        // ── Footer fields ───────────────────────────────────────────
        textVersionSoft = new JTextField();
        textVersionSoft.setBounds(301, 422, 200, 16);
        textVersionSoft.setOpaque(false);
        textVersionSoft.setHorizontalAlignment(SwingConstants.RIGHT);
        textVersionSoft.setFont(FUENTE_INPUT);
        textVersionSoft.setEditable(false);
        textVersionSoft.setColumns(10);
        textVersionSoft.setBorder(null);
        textVersionSoft.setDisabledTextColor(getDisabledFg());
        panel.add(textVersionSoft);

        textProgramador = new JTextField();
        textProgramador.setBounds(10, 422, 200, 16);
        textProgramador.setOpaque(false);
        textProgramador.setHorizontalAlignment(SwingConstants.RIGHT);
        textProgramador.setFont(FUENTE_INPUT);
        textProgramador.setEditable(false);
        textProgramador.setColumns(10);
        textProgramador.setBorder(null);
        textProgramador.setDisabledTextColor(getDisabledFg());
        panel.add(textProgramador);

        // ── User fields ─────────────────────────────────────────────
        textUsuario = new JTextField();
        textUsuario.setOpaque(false);
        textUsuario.setEditable(false);
        textUsuario.setBorder(null);
        textUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        textUsuario.setFont(FUENTE_INPUT_BOLD);
        textUsuario.setBounds(210, 8, 180, 18);
        textUsuario.setColumns(10);
        textUsuario.setDisabledTextColor(getDisabledFg());
        textUsuario.setForeground(currentTheme.textPrimary);
        getContentPane().add(textUsuario);

        textUbicacion = new JTextField();
        textUbicacion.setOpaque(false);
        textUbicacion.setHorizontalAlignment(SwingConstants.CENTER);
        textUbicacion.setFont(FUENTE_INPUT_BOLD);
        textUbicacion.setEditable(false);
        textUbicacion.setColumns(10);
        textUbicacion.setBorder(null);
        textUbicacion.setBounds(210, 30, 180, 18);
        textUbicacion.setDisabledTextColor(getDisabledFg());
        textUbicacion.setForeground(currentTheme.textPrimary);
        getContentPane().add(textUbicacion);

        // ── Disable buttons until location selected ─────────────────
        for (JButton btn : botones) btn.setEnabled(false);

        setLocationCenter();
    }

    // ─── Button factory ────────────────────────────────────────────────

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(currentTheme.textPrimary);
        btn.setBackground(currentTheme.btnBg);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        botones.add(btn);
        return btn;
    }

    // ─── Theme toggle icon (PNG resources) ───────────────────────────

    private ImageIcon loadToggleIcon(String resourcePath, String fallbackText) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url != null) {
            return new ImageIcon(url);
        }
        BufferedImage img = new BufferedImage(18, 18, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        int x = (18 - fm.stringWidth(fallbackText)) / 2;
        int y = (18 - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(fallbackText, x, y);
        g.dispose();
        return new ImageIcon(img);
    }

    // ─── Helper methods ──────────────────────────────────────────────

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

    private void applyTheme(Theme t) {
        currentTheme = t;

        // 1. Panel backgrounds
        getContentPane().setBackground(t.bgBase);
        if (panel != null) panel.setBackground(t.bgBase);
        if (panelDeControl != null) {
            panelDeControl.setBackground(t.bgSurface);
            panelDeControl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(t.borderLight, 1),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        }

        // 2. Theme labels recursively
        themeLabels(getContentPane(), t);
        if (panelDeControl != null) themeLabels(panelDeControl, t);

        // 3. Restore labels with special styling (overrides themeLabels)
        if (lblFacturaSoft != null) {
            lblFacturaSoft.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblFacturaSoft.setForeground(t.brand);
        }
        if (lblVersion != null) lblVersion.setForeground(t.textTertiary);
        if (lblDescripcion != null) {
            lblDescripcion.setFont(FUENTE_LABEL);
            lblDescripcion.setForeground(t.brand);
        }
        if (lblUbicacion != null) lblUbicacion.setForeground(t.brand);

        // 4. Buttons
        for (JButton btn : botones) {
            btn.setForeground(t.textPrimary);
            btn.setBackground(t.btnBg);
        }
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setForeground(t.textPrimary);
            btnCerrarSesion.setBackground(t.btnBg);
        }
        if (btnVaciarBases != null) {
            btnVaciarBases.setForeground(t.textPrimary);
            btnVaciarBases.setBackground(t.btnBg);
        }
        if (btnSalir != null) {
            btnSalir.setForeground(t.textPrimary);
            btnSalir.setBackground(t.btnBg);
        }
        if (btnThemeToggle != null) {
            btnThemeToggle.setBackground(t.btnBg);
            btnThemeToggle.setForeground(t.textPrimary);
            btnThemeToggle.setToolTipText(isDarkMode
                ? "Cambiar a tema claro"
                : "Cambiar a tema oscuro");
        }

        // 5. Text fields with getFieldBg
        if (textUsuario != null) {
            textUsuario.setBackground(getFieldBg(textUsuario.isEnabled()));
            textUsuario.setForeground(t.textPrimary);
            textUsuario.setDisabledTextColor(getDisabledFg());
            textUsuario.setCaretColor(t.textPrimary);
        }
        if (textUbicacion != null) {
            textUbicacion.setBackground(getFieldBg(textUbicacion.isEnabled()));
            textUbicacion.setForeground(t.textPrimary);
            textUbicacion.setDisabledTextColor(getDisabledFg());
            textUbicacion.setCaretColor(t.textPrimary);
        }
        if (textVersionSoft != null) {
            textVersionSoft.setBackground(getFieldBg(textVersionSoft.isEnabled()));
            textVersionSoft.setForeground(t.textPrimary);
            textVersionSoft.setDisabledTextColor(getDisabledFg());
            textVersionSoft.setCaretColor(t.textPrimary);
        }
        if (textProgramador != null) {
            textProgramador.setBackground(getFieldBg(textProgramador.isEnabled()));
            textProgramador.setForeground(t.textPrimary);
            textProgramador.setDisabledTextColor(getDisabledFg());
            textProgramador.setCaretColor(t.textPrimary);
        }

        // 6. Combo
        if (cmbUbicacion != null) {
            cmbUbicacion.setBackground(getFieldBg(cmbUbicacion.isEnabled()));
            cmbUbicacion.setForeground(cmbUbicacion.isEnabled() ? t.textPrimary : getDisabledFg());
            installComboUI(cmbUbicacion);
        }

        // 7. Separators
        Color sepColor = t.borderLight;
        if (separatorBottom1 != null) separatorBottom1.setForeground(sepColor);
        if (separatorBottom2 != null) separatorBottom2.setForeground(sepColor);
        if (separatorTop1 != null) separatorTop1.setForeground(sepColor);
        if (separatorTop2 != null) separatorTop2.setForeground(sepColor);
    }

    // ─── Theme toggle ───────────────────────────────────────────────────

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        Theme t = isDarkMode ? Theme.DARK : Theme.LIGHT;
        {
            boolean isDark = t.bgBase.getRed() < 50;
            Color hdrFg = isDark ? Color.WHITE : t.textPrimary;
            UIManager.put("TableHeader.foreground", hdrFg);
        }
        UIManager.put("TableHeader.background", t.bgElevated);
        btnThemeToggle.setIcon(loadToggleIcon(
            isDarkMode ? "/img/luna.png" : "/img/sol.png",
            isDarkMode ? "\u263E" : "\u2600"));
        applyTheme(t);
        staticTheme = t;
        notifyThemeListeners(t);
    }

    // ─── Centering ──────────────────────────────────────────────────────

    public void setLocationCenter() {
        setLocationMove(0, 0);
    }

    public void setLocationMove(int moveWidth, int moveHeight) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        frameSize.width = Math.min(frameSize.width, screenSize.width);
        frameSize.height = Math.min(frameSize.height, screenSize.height);
        setLocation((screenSize.width - frameSize.width) / 2 + moveWidth,
                (screenSize.height - frameSize.height) / 2 + moveHeight);
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

    // ─── Getters / setters ──────────────────────────────────────────────

    public JButton getBotonComprobantes() { return btnComprobantes; }
    public JButton getBotonClientes() { return btnClientes; }
    public JButton getBotonRemitos() { return btnRemitos; }
    public JButton getBotonPagosRecibos() { return btnPagosRecibos; }
    public JButton getBotonConfiguracion() { return btnConfiguracion; }
    public JButton getBotonCajaGastos() { return btnCajaGastos; }
    public JButton getBtnSalir() { return btnSalir; }
    public JPanel getPanelDeControl() { return panelDeControl; }
    public JTextField getTextUsuario() { return textUsuario; }
    public void setTextUsuario(JTextField textUsuario) { this.textUsuario = textUsuario; }
    public JTextField getTextUbicacion() { return textUbicacion; }
    public void setTextUbicacion(JTextField textUbicacion) { this.textUbicacion = textUbicacion; }
    public JTextField getTextProgramador() { return textProgramador; }
    public void setTextProgramador(JTextField textProgramador) { this.textProgramador = textProgramador; }
    public JTextField getTextVersionSoft() { return textVersionSoft; }
    public void setTextVersionSoft(JTextField textVersionSoft) { this.textVersionSoft = textVersionSoft; }
    public JComboBox<String> getCmbUbicacion() { return cmbUbicacion; }
    public List<JButton> getBotones() { return botones; }
    public boolean isDarkMode() { return isDarkMode; }
}
