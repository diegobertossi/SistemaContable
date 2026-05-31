package com.els.facturacion.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

public class BotonEstilizado extends JButton {

    public enum Variante { PRIMARIO, SECUNDARIO, EXITO, PELIGRO, FANTASMA }

    private Variante variante = Variante.SECUNDARIO;
    private Color bgColor;
    private Color fgColor;
    private Color hoverBgColor;
    private Color pressedBgColor;
    private int radioEsquinas = TemaFacturaSoft.RADIUS_LG;
    private boolean hover = false;
    private boolean pressed = false;

    public BotonEstilizado(String texto) {
        this(texto, Variante.SECUNDARIO);
    }

    public BotonEstilizado(String texto, Variante variante) {
        super(texto);
        this.variante = variante;
        init();
    }

    public BotonEstilizado(String texto, Icon icono) {
        this(texto, Variante.SECUNDARIO);
        setIcon(icono);
    }

    private void init() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        setPreferredSize(new Dimension(getPreferredSize().width, TemaFacturaSoft.BTN_HEIGHT));
        setMinimumSize(new Dimension(80, TemaFacturaSoft.BTN_HEIGHT));
        setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        setIconTextGap(10);
        setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        aplicarColoresVariante();

        TemaFacturaSoft.onTemaChange(modo -> aplicarColoresVariante());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                setBackground(hoverBgColor);
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                pressed = false;
                setBackground(bgColor);
                repaint();
            }
            @Override
            public void mousePressed(MouseEvent e) {
                pressed = true;
                setBackground(pressedBgColor);
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                pressed = false;
                setBackground(hover ? hoverBgColor : bgColor);
                repaint();
            }
        });

        TemaFacturaSoft.onTemaChange(modo -> repaint());
    }

    private void aplicarColoresVariante() {
        switch (variante) {
            case PRIMARIO:
                bgColor = TemaFacturaSoft.ACCENT_PRIMARY;
                fgColor = TemaFacturaSoft.TEXT_ON_PRIMARY;
                hoverBgColor = TemaFacturaSoft.ACCENT_PRIMARY_HOVER;
                pressedBgColor = TemaFacturaSoft.ACCENT_PRIMARY_PRESSED;
                break;
            case EXITO:
                bgColor = TemaFacturaSoft.ACCENT_SUCCESS;
                fgColor = TemaFacturaSoft.TEXT_ON_PRIMARY;
                hoverBgColor = TemaFacturaSoft.ACCENT_SUCCESS_HOVER;
                pressedBgColor = TemaFacturaSoft.ACCENT_SUCCESS.darker();
                break;
            case PELIGRO:
                bgColor = TemaFacturaSoft.ACCENT_DANGER;
                fgColor = TemaFacturaSoft.TEXT_ON_PRIMARY;
                hoverBgColor = TemaFacturaSoft.ACCENT_DANGER_HOVER;
                pressedBgColor = TemaFacturaSoft.ACCENT_DANGER.darker();
                break;
            case FANTASMA:
                bgColor = TemaFacturaSoft.BG_APP;
                fgColor = TemaFacturaSoft.TEXT_PRIMARY;
                hoverBgColor = TemaFacturaSoft.BG_SURFACE_HOVER;
                pressedBgColor = TemaFacturaSoft.BORDER_DEFAULT;
                break;
            case SECUNDARIO:
            default:
                bgColor = TemaFacturaSoft.BG_SURFACE;
                fgColor = TemaFacturaSoft.TEXT_PRIMARY;
                hoverBgColor = TemaFacturaSoft.BG_SURFACE_HOVER;
                pressedBgColor = TemaFacturaSoft.BORDER_DEFAULT;
                break;
        }
        setBackground(bgColor);
        setForeground(fgColor);
        setBorder(BorderFactory.createLineBorder(TemaFacturaSoft.BORDER_DEFAULT, 1, true));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        int r = radioEsquinas;

        // Sombra sutil
        g2.setColor(TemaFacturaSoft.SHADOW);
        g2.fillRoundRect(1, 2, w - 2, h - 1, r, r);

        // Fondo
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, w - 1, h - 2, r, r);

        // Borde
        if (variante == Variante.PRIMARIO || variante == Variante.EXITO || variante == Variante.PELIGRO) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(TemaFacturaSoft.BORDER_DEFAULT);
        }
        g2.drawRoundRect(0, 0, w - 2, h - 3, r, r);

        // Focus ring
        if (hasFocus()) {
            g2.setColor(TemaFacturaSoft.ACCENT_PRIMARY);
            g2.setStroke(new java.awt.BasicStroke(2f));
            g2.drawRoundRect(-1, -1, w + 1, h, r + 2, r + 2);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    // ── Setters ──────────────────────────────────────────────
    public void setVariante(Variante v) {
        this.variante = v;
        aplicarColoresVariante();
    }

    public void setRadioEsquinas(int radio) {
        this.radioEsquinas = radio;
        repaint();
    }

    public void setIcono(Icon icono) {
        setIcon(icono);
        if (icono != null) {
            setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
            setIconTextGap(10);
        }
    }

    // ── Métodos estáticos de fábrica ─────────────────────────
    public static BotonEstilizado primario(String texto) {
        return new BotonEstilizado(texto, Variante.PRIMARIO);
    }

    public static BotonEstilizado secundario(String texto) {
        return new BotonEstilizado(texto, Variante.SECUNDARIO);
    }

    public static BotonEstilizado exito(String texto) {
        return new BotonEstilizado(texto, Variante.EXITO);
    }

    public static BotonEstilizado peligro(String texto) {
        return new BotonEstilizado(texto, Variante.PELIGRO);
    }

    public static BotonEstilizado fantasma(String texto) {
        return new BotonEstilizado(texto, Variante.FANTASMA);
    }
}
