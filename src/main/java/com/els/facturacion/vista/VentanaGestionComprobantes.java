package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
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

public class VentanaGestionComprobantes extends JFrame {

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();
    private JTabbedPane tabbedPane;
    private VentanaComprobantes comprobantesView;
    private PanelEstadisticas panelEstadisticas;
    private JPanel statusBar;
    private JLabel lblStatus;

    public VentanaGestionComprobantes() {
        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("MÓDULO COMPROBANTES");
        setSize(1024, 600);
        setMinimumSize(new Dimension(1024, 600));
        setMaximumSize(new Dimension(1024, 600));
        setResizable(false);
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
        comprobantesView = new VentanaComprobantes();
        Container compContent = comprobantesView.getContentPane();
        comprobantesView.remove(compContent);
        tabbedPane.addTab("Comprobantes", compContent);

        // Tab Estadisticas
        panelEstadisticas = new PanelEstadisticas();
        tabbedPane.addTab("Estad\u00edsticas", panelEstadisticas);

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == compContent) {
                comprobantesView.cargarComprobantes();
            } else if (tabbedPane.getSelectedComponent() == panelEstadisticas) {
                panelEstadisticas.cargarDatos();
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
        if (panelEstadisticas != null) {
            panelEstadisticas.applyTheme(t);
        }
        if (statusBar != null) statusBar.setBackground(t.statusBarBg);
        if (lblStatus != null) lblStatus.setForeground(t.statusBarFg);
    }
}
