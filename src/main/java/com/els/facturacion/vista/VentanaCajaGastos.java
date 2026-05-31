package com.els.facturacion.vista;

import java.awt.BorderLayout;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class VentanaCajaGastos extends JFrame {

    public VentanaCajaGastos() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Caja y Gastos");
        setSize(1000, 640);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(12f));

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
