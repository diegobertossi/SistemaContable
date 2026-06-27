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

public class VentanaCajaGastos extends JFrame {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;
    private JPanel statusBar;
    private JLabel lblStatus;

    public VentanaCajaGastos() {
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
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

    private void initComponents() {
        setTitle("Caja y Gastos");
        setSize(1000, 640);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

        // Tab Caja
        VentanaCaja cajaView = new VentanaCaja();
        Container cajaContent = cajaView.getContentPane();
        cajaView.remove(cajaContent);
        tabbedPane.addTab("Caja", cajaContent);

        // Tab Gastos
        VentanaGastos gastosView = new VentanaGastos();
        Container gastosContent = gastosView.getContentPane();
        gastosView.remove(gastosContent);
        tabbedPane.addTab("Gastos", gastosContent);

        // Tab Migracion
        VentanaMigracion migracionView = new VentanaMigracion();
        Container migracionContent = migracionView.getContentPane();
        migracionView.remove(migracionContent);
        tabbedPane.addTab("Migraci\u00f3n", migracionContent);

        add(tabbedPane, BorderLayout.CENTER);

        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica", SwingConstants.LEFT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(currentTheme.statusBarFg);

        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(currentTheme.statusBarBg);
        statusBar.add(lblStatus);
        add(statusBar, BorderLayout.SOUTH);
    }
}
