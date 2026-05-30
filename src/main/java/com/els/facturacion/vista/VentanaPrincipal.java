package com.els.facturacion.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.els.facturacion.util.UbicacionSistema;

public class VentanaPrincipal extends JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 16);

    private List<JButton> botones;

    public VentanaPrincipal() {
        botones = new ArrayList<>();
        initComponents();

    }

    private void initComponents() {
        setTitle("FacturaSoft v1.0 \u2014 Sistema de Facturaci\u00f3n Electr\u00f3nica");
        setSize(640, 560);
        setMinimumSize(new Dimension(500, 440));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_FONDO);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel lblTitulo = new JLabel("FACTURASOFT", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_TITULO);

        JLabel lblSubTitulo = new JLabel("Sistema de Facturaci\u00f3n Electr\u00f3nica", SwingConstants.CENTER);
        lblSubTitulo.setFont(new Font("Cambria", Font.PLAIN, 14));
        lblSubTitulo.setForeground(COLOR_TITULO);

        JLabel lblUbicacion = new JLabel("Ubicaci\u00f3n del sistema:", SwingConstants.CENTER);
        lblUbicacion.setFont(new Font("Cambria", Font.PLAIN, 13));
        lblUbicacion.setForeground(COLOR_TITULO);

        JComboBox<String> cmbUbicacion = new JComboBox<>();
        cmbUbicacion.addItem("");
        cmbUbicacion.addItem(UbicacionSistema.BSAS);
        cmbUbicacion.addItem(UbicacionSistema.BRC);
        cmbUbicacion.setFont(new Font("Cambria", Font.PLAIN, 13));
        cmbUbicacion.setPreferredSize(new Dimension(250, 28));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(COLOR_FONDO);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(
                EtchedBorder.RAISED,
                new Color(188, 205, 235), new Color(130, 165, 215)
            ),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel buttonWrapper = new JPanel(new BorderLayout());
        buttonWrapper.setBackground(COLOR_FONDO);
        buttonWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(8, 8, 8, 8),
            BorderFactory.createLineBorder(new Color(155, 182, 222), 2)
        ));
        buttonWrapper.add(buttonPanel, BorderLayout.CENTER);

        JButton btnComprobantes = crearBoton("GESTI\u00d3N DE COMPROBANTES");
        btnComprobantes.addActionListener(e -> {
            VentanaGestionComprobantes v = new VentanaGestionComprobantes();
            v.setVisible(true);
        });

        JButton btnClientes = crearBoton("GESTI\u00d3N DE CLIENTES");
        btnClientes.addActionListener(e -> new VentanaClientes().setVisible(true));

        JButton btnRemitos = crearBoton("GESTI\u00d3N DE REMITOS");
        btnRemitos.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo")
        );

        JButton btnPagosRecibos = crearBoton("GESTI\u00d3N DE PAGOS Y RECIBOS");
        btnPagosRecibos.addActionListener(e -> {
            VentanaPagosRecibos v = new VentanaPagosRecibos();
            v.setVisible(true);
        });

        JButton btnHerramientas = crearBoton("HERRAMIENTAS");
        btnHerramientas.addActionListener(e -> {
            VentanaConfigCertificados v = new VentanaConfigCertificados();
            v.setVisible(true);
        });

        JButton btnCajaGastos = crearBoton("CAJA Y GASTOS");
        btnCajaGastos.addActionListener(e -> {
            VentanaCajaGastos v = new VentanaCajaGastos();
            v.setVisible(true);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.gridx = 0; gbc.gridy = 0;
        buttonPanel.add(btnComprobantes, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        buttonPanel.add(btnClientes, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        buttonPanel.add(btnRemitos, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        buttonPanel.add(btnPagosRecibos, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        buttonPanel.add(btnHerramientas, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        buttonPanel.add(btnCajaGastos, gbc);

        cmbUbicacion.addActionListener(e -> {
            String selected = (String) cmbUbicacion.getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                UbicacionSistema.setUbicacion(selected);
                for (JButton btn : botones) {
                    btn.setEnabled(true);
                }
            } else {
                UbicacionSistema.setUbicacion(null);
                for (JButton btn : botones) {
                    btn.setEnabled(false);
                }
            }
        });

        GridBagConstraints gbcTitulo = new GridBagConstraints();
        gbcTitulo.insets = new Insets(5, 0, 8, 0);
        gbcTitulo.gridx = 0; gbcTitulo.gridy = 0;
        gbcTitulo.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(lblTitulo, gbcTitulo);

        GridBagConstraints gbcSub = new GridBagConstraints();
        gbcSub.insets = new Insets(0, 0, 5, 0);
        gbcSub.gridx = 0; gbcSub.gridy = 1;
        gbcSub.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(lblSubTitulo, gbcSub);

        GridBagConstraints gbcUbicacionLabel = new GridBagConstraints();
        gbcUbicacionLabel.insets = new Insets(8, 0, 2, 0);
        gbcUbicacionLabel.gridx = 0; gbcUbicacionLabel.gridy = 2;
        gbcUbicacionLabel.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(lblUbicacion, gbcUbicacionLabel);

        GridBagConstraints gbcUbicacionCombo = new GridBagConstraints();
        gbcUbicacionCombo.insets = new Insets(0, 0, 10, 0);
        gbcUbicacionCombo.gridx = 0; gbcUbicacionCombo.gridy = 3;
        mainPanel.add(cmbUbicacion, gbcUbicacionCombo);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.insets = new Insets(8, 0, 0, 0);
        gbcButtons.gridx = 0; gbcButtons.gridy = 4;
        gbcButtons.fill = GridBagConstraints.BOTH;
        gbcButtons.weighty = 1;
        mainPanel.add(buttonWrapper, gbcButtons);

        add(mainPanel);

        for (JButton btn : botones) {
            btn.setEnabled(false);
        }
    }

    private JButton crearBoton(String texto) {
        Color hoverBg = new Color(155, 180, 215);
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(18, 20, 18, 20));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.setForeground(COLOR_TEXTO);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_BOTON);
                btn.setForeground(COLOR_TEXTO);
            }
        });
        botones.add(btn);
        return btn;
    }
}
