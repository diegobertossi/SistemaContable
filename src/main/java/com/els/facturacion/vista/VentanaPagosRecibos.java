package com.els.facturacion.vista;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class VentanaPagosRecibos extends JFrame {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;

    public VentanaPagosRecibos() {
        initComponents();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Pagos y Recibos");
        setSize(1000, 640);
        setMinimumSize(new Dimension(800, 500));
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
        VentanaRecibos recibosView = new VentanaRecibos();
        Container recibosContent = recibosView.getContentPane();
        recibosView.remove(recibosContent);
        tabbedPane.addTab("Recibos", recibosContent);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (tabbedPane != null) {
            tabbedPane.setBackground(t.bgSurface);
            tabbedPane.setForeground(t.textPrimary);
        }
    }
}
