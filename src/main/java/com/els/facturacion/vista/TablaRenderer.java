package com.els.facturacion.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class TablaRenderer extends DefaultTableCellRenderer {

    private Theme theme;
    private Set<Integer> currencyColumns;
    private Set<Integer> boldColumns;
    private Set<Integer> centerColumns;
    private Color evenBg;
    private Color oddBg;

    public TablaRenderer(Theme theme) {
        this(theme, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(),
            theme.bgSurface, theme.bgElevated);
    }

    public TablaRenderer(Theme theme, Set<Integer> currencyColumns, Set<Integer> boldColumns) {
        this(theme, currencyColumns, boldColumns, Collections.emptySet(),
            theme.bgSurface, theme.bgElevated);
    }

    public TablaRenderer(Theme theme, Set<Integer> currencyColumns, Set<Integer> boldColumns,
          Set<Integer> centerColumns, Color evenBg, Color oddBg) {
        this.theme = theme;
        this.currencyColumns = currencyColumns;
        this.boldColumns = boldColumns;
        this.centerColumns = centerColumns;
        this.evenBg = evenBg;
        this.oddBg = oddBg;
        setFont(new Font("Segoe UI", Font.PLAIN, 10));
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    private static final DecimalFormat CURRENCY_FMT = new DecimalFormat("$ #,##0.00");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        int modelColumn = table.convertColumnIndexToModel(column);

        if (currencyColumns.contains(modelColumn) && value != null) {
            try {
                String clean = value.toString().replace("$", "").replace(".", "").replace(",", ".");
                BigDecimal num = new BigDecimal(clean);
                setText(CURRENCY_FMT.format(num));
            } catch (Exception ignored) {}
        }

        if (boldColumns.contains(modelColumn)) {
            setFont(new Font("Segoe UI", Font.BOLD, 10));
        } else {
            setFont(new Font("Segoe UI", Font.PLAIN, 10));
        }

        if (centerColumns.contains(modelColumn)) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else if (currencyColumns.contains(modelColumn)) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEADING);
        }

        if (!isSelected) {
            setBackground(row % 2 == 0 ? evenBg : oddBg);
            setForeground(theme.textPrimary);
        } else {
            setBackground(theme.brandDark);
            setForeground(Color.WHITE);
        }

        return this;
    }

    public static void applyTo(JTable table, Theme theme) {
        applyTo(table, theme, Collections.emptySet(), Collections.emptySet(),
            Collections.emptySet(), theme.bgSurface, theme.bgElevated);
    }

    public static void applyTo(JTable table, Theme theme,
          Set<Integer> currencyColumns, Set<Integer> boldColumns) {
        applyTo(table, theme, currencyColumns, boldColumns,
            Collections.emptySet(), theme.bgSurface, theme.bgElevated);
    }

    public static void applyTo(JTable table, Theme theme,
          Set<Integer> currencyColumns, Set<Integer> boldColumns,
          Set<Integer> centerColumns, Color evenBg, Color oddBg) {
        UIManager.put("Table.background", theme.bgBase);
        table.setBackground(theme.bgBase);
        table.setForeground(theme.textPrimary);
        int avg = (theme.borderLight.getRed() + theme.borderLight.getGreen() + theme.borderLight.getBlue()) / 3;
        if (avg < 100) {
            table.setGridColor(new Color(
                Math.min(255, theme.borderLight.getRed() + 45),
                Math.min(255, theme.borderLight.getGreen() + 45),
                Math.min(255, theme.borderLight.getBlue() + 45)));
        } else {
            table.setGridColor(theme.borderLight);
        }
        table.setShowGrid(true);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class,
            new TablaRenderer(theme, currencyColumns, boldColumns, centerColumns, evenBg, oddBg));
    }
}
