package com.els.facturacion.vista;

import java.awt.BorderLayout;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class VentanaPagosRecibos extends JFrame {

    public VentanaPagosRecibos() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Pagos y Recibos");
        setSize(1000, 640);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(12f));

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
}
