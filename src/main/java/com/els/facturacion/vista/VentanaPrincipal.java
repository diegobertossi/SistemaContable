package com.els.facturacion.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import com.els.facturacion.util.UbicacionSistema;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private List<JButton> botones;
    private JButton btnComprobantes;
    private JButton btnClientes;
    private JButton btnRemitos;
    private JButton btnPagosRecibos;
    private JButton btnHerramientas;
    private JButton btnCajaGastos;
    private JButton btnSalir;
    private JTextField textUsuario;
    private JTextField textUbicacion;
    private JTextField textProgramador;
    private JTextField textVersionSoft;
    private JPanel panelDeControl;
    private JComboBox<String> cmbUbicacion;

    public VentanaPrincipal() {
        super();
        botones = new ArrayList<>();
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(219, 227, 246));
        setMinimumSize(new Dimension(500, 400));
        initialize();
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Iconosoft.png"));
            setIconImage(icon);
        } catch (Exception e) {
            // icon not available
        }
    }

    private void initialize() {
        setBounds(100, 10, 557, 525);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(null);

        // Main panel for layout
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBackground(new Color(219, 227, 246));
        panel.setBorder(null);
        panel.setBounds(0, 0, 548, 486);
        getContentPane().add(panel);
        panel.setLayout(null);
        
                // Title and info text below image
                JLabel lblFacturaSoft = new JLabel("FACTURASOFT", SwingConstants.CENTER);
                lblFacturaSoft.setFont(new Font("Cambria", Font.BOLD, 32));
                lblFacturaSoft.setForeground(new Color(65, 105, 225));
                lblFacturaSoft.setBounds(134, 116, 280, 28);
                panel.add(lblFacturaSoft);

        // Close session button
        JButton btnCerrarSesion = new JButton("<html><center>CERRAR SESI\u00d3N</html>");
        btnCerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnCerrarSesion.setForeground(new Color(70, 130, 180));
        btnCerrarSesion.setFont(new Font("Cambria", Font.BOLD, 10));
        btnCerrarSesion.setBounds(10, 10, 110, 33);
        btnCerrarSesion.setIcon(new ImageIcon(this.getClass().getResource("/img/Icono cerrar sesion.png")));
        panel.add(btnCerrarSesion);

        // Salir button
        btnSalir = new JButton("SALIR");
        btnSalir.setBounds(424, 10, 110, 33);
        panel.add(btnSalir);
        btnSalir.setForeground(new Color(255, 0, 51));
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setFont(new Font("Cambria", Font.BOLD, 10));
        btnSalir.setIcon(new ImageIcon(this.getClass().getResource("/img/Icono salir.png")));
        btnSalir.addActionListener(e -> System.exit(0));
        

        // Main image - same dimensions as VistaPrincipal (473x153), maintains aspect ratio
        JLabel lblImagen = new JLabel("");
        ImageIcon icono = new ImageIcon(getClass().getResource("/img/Inicio facturacion.png"));
        int imgTargetW = 473, imgTargetH = 160;
        double scale = Math.min((double) imgTargetW / icono.getIconWidth(),
                (double) imgTargetH / icono.getIconHeight());
        int scaledW = (int) (icono.getIconWidth() * scale);
        int scaledH = (int) (icono.getIconHeight() * scale);
        Image img = icono.getImage().getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
        lblImagen.setIcon(new ImageIcon(img));
        lblImagen.setOpaque(false);
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagen.setBounds(134, 10, 280, 131);
        panel.add(lblImagen);

        JLabel lblVersion = new JLabel("Versi\u00f3n: 1.0", SwingConstants.CENTER);
        lblVersion.setFont(new Font("Cambria", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(105, 105, 105));
        lblVersion.setBounds(234, 144, 80, 14);
        panel.add(lblVersion);

        JLabel lblDescripcion = new JLabel(
                "SISTEMA DE FACTURACI\u00d3N ELECTR\u00d3NICA Y GESTI\u00d3N CONTABLE",
                SwingConstants.CENTER);
        lblDescripcion.setFont(new Font("Cambria", Font.PLAIN, 11));
        lblDescripcion.setForeground(new Color(65, 105, 225));
        lblDescripcion.setBounds(54, 177, 440, 14);
        panel.add(lblDescripcion);

        // Buttons panel
        panelDeControl = new JPanel();
        panelDeControl.setBounds(29, 217, 490, 231);
        panel.add(panelDeControl);
        panelDeControl.setBackground(new Color(176, 196, 222));
        panelDeControl.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        panelDeControl.setLayout(null);

        int btnW = 210;
        int btnH = 45;
        int col1X = 25;
        int col2X = 255;
        int row1Y = 15;
        int row2Y = 68;
        int row3Y = 121;

        btnComprobantes = crearBoton("COMPROBANTES");
        btnComprobantes.setBounds(col1X, row1Y, btnW, btnH);
        btnComprobantes.setIcon(new ImageIcon(this.getClass().getResource("/img/Comprobantes.png")));
        btnComprobantes.addActionListener(e -> {
            VentanaGestionComprobantes v = new VentanaGestionComprobantes();
            v.setVisible(true);
        });
        panelDeControl.add(btnComprobantes);

        btnClientes = crearBoton("             CLIENTES");
        btnClientes.setBounds(col2X, row1Y, btnW, btnH);
        btnClientes.setIcon(new ImageIcon(this.getClass().getResource("/img/Clientes.png")));
       // btnClientes.setHorizontalAlignment(SwingConstants.LEFT);
        btnClientes.addActionListener(e -> new VentanaClientes().setVisible(true));
        panelDeControl.add(btnClientes);

        btnRemitos = crearBoton("            REMITOS");
        btnRemitos.setBounds(col1X, row2Y, btnW, btnH);
        btnRemitos.setIcon(new ImageIcon(this.getClass().getResource("/img/Remitos.png")));
        btnRemitos.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo")
        );
        panelDeControl.add(btnRemitos);

        btnPagosRecibos = crearBoton("PAGOS Y RECIBOS");
        btnPagosRecibos.setIcon(new ImageIcon(this.getClass().getResource("/img/Pagos y recibos.png")));
        btnPagosRecibos.setBounds(col2X, row2Y, btnW, btnH);
        btnPagosRecibos.addActionListener(e -> {
            VentanaPagosRecibos v = new VentanaPagosRecibos();
            v.setVisible(true);
        });
        panelDeControl.add(btnPagosRecibos);

        btnHerramientas = crearBoton(" HERRAMIENTAS");
        btnHerramientas.setBounds(col1X, row3Y, btnW, btnH);
        btnHerramientas.setIcon(new ImageIcon(this.getClass().getResource("/img/Herramientas.png")));
        btnHerramientas.addActionListener(e -> {
            VentanaConfigCertificados v = new VentanaConfigCertificados();
            v.setVisible(true);
        });
        panelDeControl.add(btnHerramientas);

        btnCajaGastos = crearBoton("      CAJA Y GASTOS");
        btnCajaGastos.setBounds(col2X, row3Y, btnW, btnH);
        btnCajaGastos.setIcon(new ImageIcon(this.getClass().getResource("/img/Caja y gastos.png")));
        btnCajaGastos.addActionListener(e -> {
            VentanaCajaGastos v = new VentanaCajaGastos();
            v.setVisible(true);
        });
        panelDeControl.add(btnCajaGastos);

        // Recuadro de ubicacion
        JPanel ubPanel = new JPanel(null);
        ubPanel.setBounds(15, 182, 460, 41);
        ubPanel.setBackground(new Color(176, 196, 222));
        ubPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        panelDeControl.add(ubPanel);

        JLabel lblUbicacion = new JLabel("UBICACI\u00d3N:");
        lblUbicacion.setFont(new Font("Cambria", Font.BOLD, 14));
        lblUbicacion.setForeground(new Color(65, 105, 225));
        lblUbicacion.setBounds(98, 8, 80, 25);
        ubPanel.add(lblUbicacion);

        cmbUbicacion = new JComboBox<>();
        cmbUbicacion.addItem("");
        cmbUbicacion.addItem(UbicacionSistema.BSAS);
        cmbUbicacion.addItem(UbicacionSistema.BRC);
        cmbUbicacion.setFont(new Font("Cambria", Font.BOLD, 14));
        cmbUbicacion.setBounds(184, 6, 185, 28);
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
        ubPanel.add(cmbUbicacion);
        
                // Bottom separators
                JSeparator separatorBottom1 = new JSeparator();
                separatorBottom1.setBounds(24, 455, 500, 2);
                panel.add(separatorBottom1);
                JSeparator separatorBottom2 = new JSeparator();
                separatorBottom2.setBounds(24, 459, 500, 2);
                panel.add(separatorBottom2);
                
                        textVersionSoft = new JTextField();
                        textVersionSoft.setBounds(338, 461, 200, 16);
                        panel.add(textVersionSoft);
                        textVersionSoft.setOpaque(false);
                        textVersionSoft.setHorizontalAlignment(SwingConstants.RIGHT);
                        textVersionSoft.setForeground(new Color(65, 105, 225));
                        textVersionSoft.setFont(new Font("Cambria", Font.PLAIN, 12));
                        textVersionSoft.setEditable(false);
                        textVersionSoft.setColumns(10);
                        textVersionSoft.setBorder(null);
                        textVersionSoft.setBackground(SystemColor.activeCaption);
                        
                                // Bottom info fields
                                textProgramador = new JTextField();
                                textProgramador.setBounds(8, 461, 200, 16);
                                panel.add(textProgramador);
                                textProgramador.setOpaque(false);
                                textProgramador.setHorizontalAlignment(SwingConstants.RIGHT);
                                textProgramador.setForeground(new Color(65, 105, 225));
                                textProgramador.setFont(new Font("Cambria", Font.PLAIN, 12));
                                textProgramador.setEditable(false);
                                textProgramador.setColumns(10);
                                textProgramador.setBorder(null);
                                textProgramador.setBackground(SystemColor.activeCaption);
                                
                                        // Separators above the button panel
                                        JSeparator separatorTop1 = new JSeparator();
                                        separatorTop1.setBounds(74, 206, 400, 2);
                                        panel.add(separatorTop1);
                                        JSeparator separatorTop2 = new JSeparator();
                                        separatorTop2.setBounds(74, 202, 400, 2);
                                        panel.add(separatorTop2);

        for (JButton btn : botones) {
            btn.setEnabled(false);
        }

        // User text field
        textUsuario = new JTextField();
        textUsuario.setOpaque(false);
        textUsuario.setEditable(false);
        textUsuario.setBorder(null);
        textUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        textUsuario.setBackground(SystemColor.activeCaption);
        textUsuario.setForeground(new Color(65, 105, 225));
        textUsuario.setFont(new Font("Cambria", Font.BOLD, 14));
        textUsuario.setBounds(250, 8, 200, 18);
        getContentPane().add(textUsuario);
        textUsuario.setColumns(10);

        // Ubicacion text field
        textUbicacion = new JTextField();
        textUbicacion.setOpaque(false);
        textUbicacion.setHorizontalAlignment(SwingConstants.CENTER);
        textUbicacion.setForeground(new Color(65, 105, 225));
        textUbicacion.setFont(new Font("Cambria", Font.BOLD, 14));
        textUbicacion.setEditable(false);
        textUbicacion.setColumns(10);
        textUbicacion.setBorder(null);
        textUbicacion.setBackground(SystemColor.activeCaption);
        textUbicacion.setBounds(250, 30, 200, 18);
        getContentPane().add(textUbicacion);

        setLocationCenter();
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setForeground(new Color(0, 0, 128));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Cambria", Font.BOLD, 13));
        btn.setFocusPainted(false);
        botones.add(btn);
        return btn;
    }

    public void setLocationCenter() {
        setLocationMove(0, 0);
    }

    public void setLocationMove(int moveWidth, int moveHeight) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();
        frameSize.width = frameSize.width > screenSize.width ? screenSize.width : frameSize.width;
        frameSize.height = frameSize.height > screenSize.height ? screenSize.height : frameSize.height;
        setLocation((screenSize.width - frameSize.width) / 2 + moveWidth,
                (screenSize.height - frameSize.height) / 2 + moveHeight);
    }

    public JButton getBotonComprobantes() {
        return btnComprobantes;
    }

    public JButton getBotonClientes() {
        return btnClientes;
    }

    public JButton getBotonRemitos() {
        return btnRemitos;
    }

    public JButton getBotonPagosRecibos() {
        return btnPagosRecibos;
    }

    public JButton getBotonHerramientas() {
        return btnHerramientas;
    }

    public JButton getBotonCajaGastos() {
        return btnCajaGastos;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }

    public JPanel getPanelDeControl() {
        return panelDeControl;
    }

    public JTextField getTextUsuario() {
        return textUsuario;
    }

    public void setTextUsuario(JTextField textUsuario) {
        this.textUsuario = textUsuario;
    }

    public JTextField getTextUbicacion() {
        return textUbicacion;
    }

    public void setTextUbicacion(JTextField textUbicacion) {
        this.textUbicacion = textUbicacion;
    }

    public JTextField getTextProgramador() {
        return textProgramador;
    }

    public void setTextProgramador(JTextField textProgramador) {
        this.textProgramador = textProgramador;
    }

    public JTextField getTextVersionSoft() {
        return textVersionSoft;
    }

    public void setTextVersionSoft(JTextField textVersionSoft) {
        this.textVersionSoft = textVersionSoft;
    }

    public JComboBox<String> getCmbUbicacion() {
        return cmbUbicacion;
    }

    public List<JButton> getBotones() {
        return botones;
    }
}
