package com.els.facturacion.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Theme {
    public final String name;
    public final Color bgBase;
    public final Color bgSurface;
    public final Color bgElevated;
    public final Color bgInput;
    public final Color textPrimary;
    public final Color textSecondary;
    public final Color textTertiary;
    public final Color textMuted;
    public final Color brand;
    public final Color brandDark;
    public final Color borderLight;
    public final Color borderStrong;
    public final Color btnBg;
    public final Color hoverBg;
    public final Color pressedBg;
    public final Color danger;

    public Theme(String name, Color bgBase, Color bgSurface, Color bgElevated, Color bgInput,
          Color textPrimary, Color textSecondary, Color textTertiary, Color textMuted,
          Color brand, Color brandDark, Color borderLight, Color borderStrong,
          Color btnBg, Color hoverBg, Color pressedBg, Color danger) {
        this.name = name;
        this.bgBase = bgBase;
        this.bgSurface = bgSurface;
        this.bgElevated = bgElevated;
        this.bgInput = bgInput;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.textTertiary = textTertiary;
        this.textMuted = textMuted;
        this.brand = brand;
        this.brandDark = brandDark;
        this.borderLight = borderLight;
        this.borderStrong = borderStrong;
        this.btnBg = btnBg;
        this.hoverBg = hoverBg;
        this.pressedBg = pressedBg;
        this.danger = danger;
    }

    public static final Theme LIGHT = new Theme(
        "Claro",
        /* bgBase    */ new Color(226, 232, 245),
        /* bgSurface */ new Color(200, 212, 235),
        /* bgElevated*/ new Color(240, 244, 252),
        /* bgInput   */ new Color(240, 244, 252),
        /* textPrimary  */ new Color(20, 28, 50),
        /* textSecondary*/ new Color(65, 105, 225),
        /* textTertiary */ new Color(105, 105, 115),
        /* textMuted    */ new Color(160, 165, 175),
        /* brand     */ new Color(65, 105, 225),
        /* brandDark */ new Color(42, 82, 190),
        /* borderLight  */ new Color(180, 192, 215),
        /* borderStrong */ new Color(65, 105, 225, 120),
        /* btnBg     */ new Color(220, 230, 250),
        /* hoverBg   */ new Color(198, 212, 240),
        /* pressedBg */ new Color(178, 195, 230),
        /* danger    */ new Color(200, 40, 60)
    );

    public static final Theme DARK = new Theme(
        "Oscuro",
        /* bgBase    */ new Color(15, 18, 30),
        /* bgSurface */ new Color(25, 30, 48),
        /* bgElevated*/ new Color(35, 40, 58),
        /* bgInput   */ new Color(30, 35, 52),
        /* textPrimary  */ new Color(235, 240, 250),
        /* textSecondary*/ new Color(140, 175, 245),
        /* textTertiary */ new Color(130, 140, 160),
        /* textMuted    */ new Color(80, 90, 110),
        /* brand     */ new Color(100, 145, 245),
        /* brandDark */ new Color(130, 170, 250),
        /* borderLight  */ new Color(50, 58, 80),
        /* borderStrong */ new Color(100, 145, 245, 100),
        /* btnBg     */ new Color(35, 40, 58),
        /* hoverBg   */ new Color(40, 48, 68),
        /* pressedBg */ new Color(50, 58, 78),
        /* danger    */ new Color(220, 80, 90)
    );

    /**
     * Installs a custom header renderer using the given theme.
     *
     * In light mode uses a pastel blue background derived from the brand color.
     * In dark mode uses a slightly elevated background.
     * Applies: uppercase, Segoe UI Bold 11, centered, column divider borders.
     *
     * @param header the table header to style
     * @param theme  the current theme (for colors and mode detection)
     */
    public static void styleTableHeader(JTableHeader header, Theme theme) {
        boolean isDark = theme.bgBase.getRed() < 50;
        Color fg = isDark ? Color.WHITE : theme.textPrimary;
        Color headerBg;
        if (isDark) {
            headerBg = new Color(
                Math.min(255, theme.bgElevated.getRed() + 20),
                Math.min(255, theme.bgElevated.getGreen() + 20),
                Math.min(255, theme.bgElevated.getBlue() + 20));
        } else {
            headerBg = new Color(
                Math.min(255, theme.brand.getRed() + 115),
                Math.min(255, theme.brand.getGreen() + 110),
                Math.min(255, theme.brand.getBlue() + 10));
        }
        header.setBackground(headerBg);
        int divAvg = (headerBg.getRed() + headerBg.getGreen() + headerBg.getBlue()) / 3;
        Color divider;
        if (divAvg < 80) {
            divider = new Color(
                Math.min(255, headerBg.getRed() + 30),
                Math.min(255, headerBg.getGreen() + 30),
                Math.min(255, headerBg.getBlue() + 30));
        } else {
            divider = new Color(
                Math.max(0, headerBg.getRed() - 25),
                Math.max(0, headerBg.getGreen() - 25),
                Math.max(0, headerBg.getBlue() - 25));
        }
        Border colBorder = BorderFactory.createMatteBorder(0, 0, 1, 1, divider);
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
}
