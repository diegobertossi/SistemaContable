package com.els.facturacion.vista;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

public class RoundedBorder extends AbstractBorder {
    private final Color color;
    private final int radius;
    private final int thickness;
    private final int padH;
    private final int padV;

    public RoundedBorder(Color color, int radius, int thickness, int padH, int padV) {
        this.color = color;
        this.radius = radius;
        this.thickness = thickness;
        this.padH = padH;
        this.padV = padV;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness + padV, thickness + padH, thickness + padV, thickness + padH);
    }
}
