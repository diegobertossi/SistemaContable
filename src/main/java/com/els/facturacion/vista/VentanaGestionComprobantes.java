package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
import java.awt.BorderLayout;

import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class VentanaGestionComprobantes extends JFrame {

    public VentanaGestionComprobantes() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Comprobantes");
        setSize(1024, 720);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(12f));

        // Tab Facturacion
        VentanaFacturacion facturacionView = new VentanaFacturacion();
        ControladorFacturacion controlador = new ControladorFacturacion(facturacionView);
        controlador.inicializar();
        Container factContent = facturacionView.getContentPane();
        facturacionView.remove(factContent);
        tabbedPane.addTab("Facturaci\u00f3n", factContent);

        // Tab Comprobantes
        VentanaComprobantes comprobantesView = new VentanaComprobantes();
        Container compContent = comprobantesView.getContentPane();
        comprobantesView.remove(compContent);
        tabbedPane.addTab("Comprobantes", compContent);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
