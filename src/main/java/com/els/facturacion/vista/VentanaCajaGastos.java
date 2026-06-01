package com.els.facturacion.vista;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class VentanaCajaGastos extends JFrame {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;

    public VentanaCajaGastos() {
        initComponents();
        VentanaPrincipal.addThemeListener(this);
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (getContentPane() != null) {
            getContentPane().setBackground(t.bgBase);
        }
        if (tabbedPane != null) {
            tabbedPane.setForeground(t.textPrimary);
        }
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
    }
}
