package com.els.facturacion.vista;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class VentanaPagosRecibos extends JFrame {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;
    private VentanaRecibos recibosView;

    public VentanaPagosRecibos() {
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Pagos y Recibos");
        setSize(1024, 600);
        setMinimumSize(new Dimension(1024, 600));
        setMaximumSize(new Dimension(1024, 600));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

        // Tab Pagos
        VentanaPagos pagosView = new VentanaPagos();
        Container pagosContent = pagosView.getContentPane();
        pagosView.remove(pagosContent);
        tabbedPane.addTab("Pagos", pagosContent);

        // Tab Recibos
        recibosView = new VentanaRecibos();
        Container recibosContent = recibosView.getContentPane();
        recibosView.remove(recibosContent);
        tabbedPane.addTab("Recibos", recibosContent);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                recibosView.refrescar();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (getContentPane() != null) {
            getContentPane().setBackground(t.bgBase);
        }
        if (tabbedPane != null) {
            tabbedPane.setBackground(t.bgSurface);
            tabbedPane.setForeground(t.textPrimary);
        }
    }
}
