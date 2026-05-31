package com.els.facturacion.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class TemaFacturaSoft {

    public enum ModoTema { CLARO, OSCURO }

    private static ModoTema modoActual = ModoTema.CLARO;
    private static final List<Consumer<ModoTema>> listeners = new ArrayList<>();

    // ── Fuentes ──────────────────────────────────────────────
    public static Font FONT_UI;
    public static Font FONT_UI_BOLD;
    public static Font FONT_UI_SEMIBOLD;
    public static Font FONT_UI_MEDIUM;
    public static Font FONT_MONO;
    public static Font FONT_MONO_BOLD;
    public static Font FONT_MONO_MEDIUM;

    // ── Colores base (se actualizan al cambiar tema) ─────────
    public static Color BG_APP;
    public static Color BG_SURFACE;
    public static Color BG_SURFACE_HOVER;
    public static Color BG_INPUT;
    public static Color BG_INPUT_DISABLED;
    public static Color BG_TABLE_HEADER;
    public static Color BG_TABLE_ALT;
    public static Color BG_TABLE_SELECTION;
    public static Color BG_STATUS_BAR;

    public static Color TEXT_PRIMARY;
    public static Color TEXT_SECONDARY;
    public static Color TEXT_MUTED;
    public static Color TEXT_ON_PRIMARY;
    public static Color TEXT_ON_ACCENT;

    public static Color ACCENT_PRIMARY;
    public static Color ACCENT_PRIMARY_HOVER;
    public static Color ACCENT_PRIMARY_PRESSED;
    public static Color ACCENT_SUCCESS;
    public static Color ACCENT_SUCCESS_HOVER;
    public static Color ACCENT_DANGER;
    public static Color ACCENT_DANGER_HOVER;

    public static Color BORDER_DEFAULT;
    public static Color BORDER_FOCUS;
    public static Color BORDER_SECTION;

    public static Color SHADOW;

    // ── Constantes de diseño ─────────────────────────────────
    public static final int RADIUS_SM = 16;
    public static final int RADIUS_MD = 30;
    public static final int RADIUS_LG = 60;

    public static final int BTN_HEIGHT = 36;
    public static final int BTN_PADDING_H = 20;
    public static final int INPUT_HEIGHT = 32;

    public static final InsetsU INSETS_BUTTON = new InsetsU(6, BTN_PADDING_H, 6, BTN_PADDING_H);
    public static final InsetsU INSETS_INPUT = new InsetsU(6, 10, 6, 10);
    public static final InsetsU INSETS_SECTION = new InsetsU(12, 14, 12, 14);
    public static final InsetsU INSETS_GAP = new InsetsU(0, 8, 0, 8);

    static {
        cargarFuentes();
        aplicarTema(ModoTema.CLARO);
    }

    // ── Carga de fuentes ─────────────────────────────────────
    private static void cargarFuentes() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            Font interRegular = cargarFuente("/fonts/Inter-Regular.ttf", 12f);
            Font interMedium = cargarFuente("/fonts/Inter-Medium.ttf", 12f);
            Font interSemiBold = cargarFuente("/fonts/Inter-SemiBold.ttf", 12f);
            Font interBold = cargarFuente("/fonts/Inter-Bold.ttf", 12f);

            Font jbMono = cargarFuente("/fonts/JetBrainsMono-Regular.ttf", 12f);
            Font jbMonoMedium = cargarFuente("/fonts/JetBrainsMono-Medium.ttf", 12f);
            Font jbMonoBold = cargarFuente("/fonts/JetBrainsMono-Bold.ttf", 12f);

            FONT_UI = interRegular != null ? interRegular : new Font("SansSerif", Font.PLAIN, 12);
            FONT_UI_MEDIUM = interMedium != null ? interMedium : new Font("SansSerif", Font.PLAIN, 12);
            FONT_UI_SEMIBOLD = interSemiBold != null ? interSemiBold : new Font("SansSerif", Font.BOLD, 12);
            FONT_UI_BOLD = interBold != null ? interBold : new Font("SansSerif", Font.BOLD, 12);

            FONT_MONO = jbMono != null ? jbMono : new Font("Monospaced", Font.PLAIN, 12);
            FONT_MONO_MEDIUM = jbMonoMedium != null ? jbMonoMedium : new Font("Monospaced", Font.PLAIN, 12);
            FONT_MONO_BOLD = jbMonoBold != null ? jbMonoBold : new Font("Monospaced", Font.BOLD, 12);

            if (interRegular != null) ge.registerFont(interRegular);
            if (interMedium != null) ge.registerFont(interMedium);
            if (interSemiBold != null) ge.registerFont(interSemiBold);
            if (interBold != null) ge.registerFont(interBold);
            if (jbMono != null) ge.registerFont(jbMono);
            if (jbMonoMedium != null) ge.registerFont(jbMonoMedium);
            if (jbMonoBold != null) ge.registerFont(jbMonoBold);

        } catch (Exception e) {
            FONT_UI = new Font("SansSerif", Font.PLAIN, 12);
            FONT_UI_BOLD = new Font("SansSerif", Font.BOLD, 12);
            FONT_UI_SEMIBOLD = FONT_UI_BOLD;
            FONT_UI_MEDIUM = FONT_UI;
            FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);
            FONT_MONO_BOLD = new Font("Monospaced", Font.BOLD, 12);
            FONT_MONO_MEDIUM = FONT_MONO;
        }
    }

    private static Font cargarFuente(String ruta, float tamaño) {
        try {
            InputStream is = TemaFacturaSoft.class.getResourceAsStream(ruta);
            if (is == null) return null;
            Font base = Font.createFont(Font.TRUETYPE_FONT, is);
            return base.deriveFont(tamaño);
        } catch (Exception e) {
            return null;
        }
    }

    // ── Aplicación de tema ───────────────────────────────────
    public static void aplicarTema(ModoTema modo) {
        modoActual = modo;
        if (modo == ModoTema.CLARO) {
            aplicarTemaClaro();
        } else {
            aplicarTemaOscuro();
        }
        for (Consumer<ModoTema> l : listeners) l.accept(modo);
    }

    public static void toggleTema() {
        aplicarTema(modoActual == ModoTema.CLARO ? ModoTema.OSCURO : ModoTema.CLARO);
    }

    public static ModoTema getModoActual() { return modoActual; }

    public static void onTemaChange(Consumer<ModoTema> listener) {
        listeners.add(listener);
    }

    // ── Tema Claro ───────────────────────────────────────────
    private static void aplicarTemaClaro() {
        BG_APP              = new Color(237, 242, 250);
        BG_SURFACE          = new Color(195, 212, 238);
        BG_SURFACE_HOVER    = new Color(175, 196, 225);
        BG_INPUT            = Color.WHITE;
        BG_INPUT_DISABLED   = new Color(241, 245, 249);
        BG_TABLE_HEADER     = new Color(190, 208, 232);
        BG_TABLE_ALT        = new Color(237, 242, 250);
        BG_TABLE_SELECTION  = new Color(170, 200, 245);
        BG_STATUS_BAR       = new Color(15, 23, 42);

        TEXT_PRIMARY         = new Color(30, 41, 59);
        TEXT_SECONDARY       = new Color(100, 116, 139);
        TEXT_MUTED           = new Color(148, 163, 184);
        TEXT_ON_PRIMARY      = Color.WHITE;
        TEXT_ON_ACCENT       = Color.WHITE;

        ACCENT_PRIMARY       = new Color(37, 99, 235);
        ACCENT_PRIMARY_HOVER = new Color(29, 78, 216);
        ACCENT_PRIMARY_PRESSED = new Color(30, 64, 175);
        ACCENT_SUCCESS       = new Color(22, 163, 74);
        ACCENT_SUCCESS_HOVER = new Color(21, 128, 61);
        ACCENT_DANGER        = new Color(220, 38, 38);
        ACCENT_DANGER_HOVER  = new Color(185, 28, 28);

        BORDER_DEFAULT       = new Color(226, 232, 240);
        BORDER_FOCUS         = ACCENT_PRIMARY;
        BORDER_SECTION       = new Color(203, 213, 225);

        SHADOW               = new Color(0, 0, 0, 20);
    }

    // ── Tema Oscuro ──────────────────────────────────────────
    private static void aplicarTemaOscuro() {
        BG_APP              = new Color(8, 12, 24);
        BG_SURFACE          = new Color(18, 25, 42);
        BG_SURFACE_HOVER    = new Color(30, 42, 65);
        BG_INPUT            = new Color(18, 25, 42);
        BG_INPUT_DISABLED   = new Color(18, 25, 42);
        BG_TABLE_HEADER     = new Color(18, 25, 42);
        BG_TABLE_ALT        = new Color(12, 18, 32);
        BG_TABLE_SELECTION  = new Color(30, 58, 138);
        BG_STATUS_BAR       = new Color(4, 6, 14);

        TEXT_PRIMARY         = new Color(226, 232, 240);
        TEXT_SECONDARY       = new Color(148, 163, 184);
        TEXT_MUTED           = new Color(100, 116, 139);
        TEXT_ON_PRIMARY      = Color.WHITE;
        TEXT_ON_ACCENT       = Color.WHITE;

        ACCENT_PRIMARY       = new Color(96, 165, 250);
        ACCENT_PRIMARY_HOVER = new Color(59, 130, 246);
        ACCENT_PRIMARY_PRESSED = new Color(37, 99, 235);
        ACCENT_SUCCESS       = new Color(74, 222, 128);
        ACCENT_SUCCESS_HOVER = new Color(34, 197, 94);
        ACCENT_DANGER        = new Color(248, 113, 113);
        ACCENT_DANGER_HOVER  = new Color(239, 68, 68);

        BORDER_DEFAULT       = new Color(51, 65, 85);
        BORDER_FOCUS         = ACCENT_PRIMARY;
        BORDER_SECTION       = new Color(71, 85, 105);

        SHADOW               = new Color(0, 0, 0, 60);
    }

    // ── Utilidades de estilos ────────────────────────────────
    public static void aplicarEstilo(JLabel lbl, int tamaño, boolean bold) {
        lbl.setFont(bold ? FONT_UI_BOLD.deriveFont((float) tamaño) : FONT_UI.deriveFont((float) tamaño));
        lbl.setForeground(TEXT_PRIMARY);
    }

    public static void aplicarEstiloSecundario(JLabel lbl, int tamaño) {
        lbl.setFont(FONT_UI.deriveFont((float) tamaño));
        lbl.setForeground(TEXT_SECONDARY);
    }

    public static void aplicarEstiloMono(JTextField txt, int tamaño) {
        txt.setFont(FONT_MONO.deriveFont((float) tamaño));
        txt.setBackground(BG_INPUT);
        txt.setForeground(TEXT_PRIMARY);
        txt.setCaretColor(TEXT_PRIMARY);
        txt.setBorder(new CompoundBorder(
            new LineBorder(BORDER_DEFAULT, 1, true),
            new EmptyBorder(5, 8, 5, 8)
        ));
    }

    public static void aplicarEstiloCombo(JComboBox<?> combo, int tamaño) {
        combo.setFont(FONT_UI.deriveFont((float) tamaño));
        combo.setBackground(BG_INPUT);
        combo.setForeground(TEXT_PRIMARY);
    }

    public static Border crearBordeSeccion() {
        return new CompoundBorder(
            new LineBorder(BORDER_SECTION, 1, true),
            new EmptyBorder(INSETS_SECTION.top, INSETS_SECTION.left, INSETS_SECTION.bottom, INSETS_SECTION.right)
        );
    }

    public static Border crearBordeInput() {
        return new CompoundBorder(
            new LineBorder(BORDER_DEFAULT, 1, true),
            new EmptyBorder(5, 8, 5, 8)
        );
    }

    public static Border crearBordeInputFocus() {
        return new CompoundBorder(
            new LineBorder(BORDER_FOCUS, 2, true),
            new EmptyBorder(4, 7, 4, 7)
        );
    }

    public static void aplicarCursorMano(JComponent c) {
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── Estilo de Tablas ─────────────────────────────────────
    public static void aplicarEstiloTabla(JTable tabla) {
        tabla.setFont(FONT_UI.deriveFont(11f));
        tabla.setRowHeight(28);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tabla.setSelectionBackground(BG_TABLE_SELECTION);
        tabla.setSelectionForeground(TEXT_PRIMARY);
        tabla.setBackground(BG_SURFACE);
        tabla.setForeground(TEXT_PRIMARY);
        tabla.setBorder(null);

        // Header
        tabla.getTableHeader().setFont(FONT_UI_BOLD.deriveFont(11f));
        tabla.getTableHeader().setBackground(BG_TABLE_HEADER);
        tabla.getTableHeader().setForeground(TEXT_PRIMARY);
        tabla.getTableHeader().setBorder(new LineBorder(BORDER_DEFAULT, 1));
        tabla.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 32));
        tabla.getTableHeader().setReorderingAllowed(false);

        // Alternating row colors
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? BG_SURFACE : BG_TABLE_ALT);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    public static void aplicarEstiloTablaHeaderCentrado(JTable tabla) {
        javax.swing.table.DefaultTableCellRenderer headerRenderer =
            (javax.swing.table.DefaultTableCellRenderer) tabla.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static void centrarColumnas(JTable tabla, int... columnas) {
        javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col : columnas) {
            if (col < tabla.getColumnCount()) {
                tabla.getColumnModel().getColumn(col).setCellRenderer(center);
            }
        }
    }

    // ── Estilo de Formularios ────────────────────────────────
    public static void aplicarEstiloInput(JTextField txt) {
        txt.setFont(FONT_UI.deriveFont(12f));
        txt.setBackground(BG_INPUT);
        txt.setForeground(TEXT_PRIMARY);
        txt.setCaretColor(TEXT_PRIMARY);
        txt.setBorder(crearBordeInput());
        txt.setPreferredSize(new Dimension(txt.getPreferredSize().width, INPUT_HEIGHT));

        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                txt.setBorder(crearBordeInputFocus());
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                txt.setBorder(crearBordeInput());
            }
        });
    }

    public static void aplicarEstiloInputNoEditable(JTextField txt) {
        txt.setFont(FONT_UI.deriveFont(12f));
        txt.setBackground(BG_INPUT_DISABLED);
        txt.setForeground(TEXT_SECONDARY);
        txt.setEditable(false);
        txt.setBorder(crearBordeInput());
        txt.setPreferredSize(new Dimension(txt.getPreferredSize().width, INPUT_HEIGHT));
    }

    public static void aplicarEstiloLabel(JLabel lbl, String texto) {
        lbl.setText(texto);
        lbl.setFont(FONT_UI_SEMIBOLD.deriveFont(12f));
        lbl.setForeground(TEXT_PRIMARY);
    }

    public static void aplicarEstiloLabelSecundario(JLabel lbl, String texto) {
        lbl.setText(texto);
        lbl.setFont(FONT_UI.deriveFont(11f));
        lbl.setForeground(TEXT_SECONDARY);
    }

    public static TitledBorder crearSeccion(String titulo) {
        return BorderFactory.createTitledBorder(
            new LineBorder(BORDER_SECTION, 1, true),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            FONT_UI_BOLD.deriveFont(11f),
            TEXT_SECONDARY
        );
    }

    // ── Insets helper ────────────────────────────────────────
    public static class InsetsU {
        public final int top, left, bottom, right;
        public InsetsU(int top, int left, int bottom, int right) {
            this.top = top; this.left = left; this.bottom = bottom; this.right = right;
        }
        public java.awt.Insets toInsets() {
            return new java.awt.Insets(top, left, bottom, right);
        }
    }
}
