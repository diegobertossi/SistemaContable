package com.els.facturacion.vista;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class VentanaPagosRecibos extends JFrame {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;
    private VentanaRecibos recibosView;
    private JPanel statusBar;
    private JLabel lblStatus;

    public VentanaPagosRecibos() {
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("MÓDULO PAGOS Y RECIBOS");
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
        tabbedPane.addTab("Historial y Recibos", recibosContent);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                recibosView.refrescar();
            }
        });

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica", SwingConstants.LEFT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(currentTheme.statusBarFg);

        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(currentTheme.statusBarBg);
        statusBar.add(lblStatus);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
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
        if (statusBar != null) statusBar.setBackground(t.statusBarBg);
        if (lblStatus != null) lblStatus.setForeground(t.statusBarFg);
    }
}
