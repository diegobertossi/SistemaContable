package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class VentanaGestionComprobantes extends JFrame {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;

    public VentanaGestionComprobantes() {
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Gesti\u00f3n de Comprobantes");
        setSize(1024, 720);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));

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
