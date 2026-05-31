package com.els.facturacion.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import com.els.facturacion.util.UbicacionSistema;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private List<JButton> botones;
    private BotonEstilizado btnComprobantes;
    private BotonEstilizado btnClientes;
    private BotonEstilizado btnRemitos;
    private BotonEstilizado btnPagosRecibos;
    private BotonEstilizado btnHerramientas;
    private BotonEstilizado btnCajaGastos;
    private BotonEstilizado btnSalir;
    private BotonEstilizado btnCerrarSesion;
    private JTextField textUsuario;
    private JTextField textUbicacion;
    private JTextField textProgramador;
    private JTextField textVersionSoft;
    private JPanel panelDeControl;
    private JPanel headerPanel;
    private JPanel sepPanel;
    private JPanel ubPanel;
    private JComboBox<String> cmbUbicacion;
    private BotonEstilizado btnToggleTema;

    public VentanaPrincipal() {
        super();
        botones = new ArrayList<>();
        setResizable(false);
        setMinimumSize(new Dimension(540, 520));
        initialize();
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Iconosoft.png"));
            setIconImage(icon);
        } catch (Exception e) { }
    }

    private void initialize() {
        setTitle("FacturaSoft v1.0");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);
        getContentPane().setLayout(new GridBagLayout());

        TemaFacturaSoft.onTemaChange(modo -> {
            getContentPane().setBackground(TemaFacturaSoft.BG_APP);
            if (headerPanel != null) headerPanel.setBackground(TemaFacturaSoft.BG_APP);
            if (sepPanel != null) sepPanel.setBackground(TemaFacturaSoft.BG_APP);
            if (panelDeControl != null) panelDeControl.setBackground(TemaFacturaSoft.BG_SURFACE);
            if (ubPanel != null) ubPanel.setBackground(TemaFacturaSoft.BG_APP);
        });

        // ══════ TOP BAR ══════
        JPanel topBar = new JPanel(new GridBagLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(8, 12, 8, 12));

        btnCerrarSesion = new BotonEstilizado("Cerrar sesion", BotonEstilizado.Variante.FANTASMA);
        btnCerrarSesion.setFont(TemaFacturaSoft.FONT_UI.deriveFont(10f));
        btnCerrarSesion.setPreferredSize(new Dimension(140, 30));
        try {
            btnCerrarSesion.setIcono(new ImageIcon(this.getClass().getResource("/img/Icono cerrar sesion.png")));
        } catch (Exception e) { }
        GridBagConstraints gbcCerrar = new GridBagConstraints();
        gbcCerrar.gridx = 0;
        gbcCerrar.gridy = 0;
        gbcCerrar.anchor = GridBagConstraints.WEST;
        gbcCerrar.insets = new Insets(0, 0, 0, 6);
        topBar.add(btnCerrarSesion, gbcCerrar);

        JPanel spacerIzq = new JPanel();
        spacerIzq.setOpaque(false);
        GridBagConstraints gbcSpacerIzq = new GridBagConstraints();
        gbcSpacerIzq.gridx = 1;
        gbcSpacerIzq.gridy = 0;
        gbcSpacerIzq.weightx = 1;
        gbcSpacerIzq.fill = GridBagConstraints.HORIZONTAL;
        topBar.add(spacerIzq, gbcSpacerIzq);

        btnToggleTema = new BotonEstilizado("Tema", BotonEstilizado.Variante.FANTASMA);
        btnToggleTema.setFont(TemaFacturaSoft.FONT_UI.deriveFont(10f));
        btnToggleTema.setPreferredSize(new Dimension(80, 28));
        btnToggleTema.setRadioEsquinas(14);
        btnToggleTema.addActionListener(e -> TemaFacturaSoft.toggleTema());
        GridBagConstraints gbcTema = new GridBagConstraints();
        gbcTema.gridx = 2;
        gbcTema.gridy = 0;
        gbcTema.anchor = GridBagConstraints.CENTER;
        topBar.add(btnToggleTema, gbcTema);

        JPanel spacerDer = new JPanel();
        spacerDer.setOpaque(false);
        GridBagConstraints gbcSpacerDer = new GridBagConstraints();
        gbcSpacerDer.gridx = 3;
        gbcSpacerDer.gridy = 0;
        gbcSpacerDer.weightx = 1;
        gbcSpacerDer.fill = GridBagConstraints.HORIZONTAL;
        topBar.add(spacerDer, gbcSpacerDer);

        btnSalir = new BotonEstilizado("Salir", BotonEstilizado.Variante.FANTASMA);
        btnSalir.setFont(TemaFacturaSoft.FONT_UI.deriveFont(10f));
        btnSalir.setPreferredSize(new Dimension(120, 30));
        try {
            btnSalir.setIcono(new ImageIcon(this.getClass().getResource("/img/Icono salir.png")));
        } catch (Exception e) { }
        btnSalir.addActionListener(e -> System.exit(0));
        GridBagConstraints gbcSalir = new GridBagConstraints();
        gbcSalir.gridx = 4;
        gbcSalir.gridy = 0;
        gbcSalir.anchor = GridBagConstraints.EAST;
        gbcSalir.insets = new Insets(0, 6, 0, 0);
        topBar.add(btnSalir, gbcSalir);

        GridBagConstraints gbcTopBar = new GridBagConstraints();
        gbcTopBar.gridx = 0;
        gbcTopBar.gridy = 0;
        gbcTopBar.fill = GridBagConstraints.HORIZONTAL;
        gbcTopBar.weightx = 1;
        gbcTopBar.insets = new Insets(0, 0, 2, 0);
        getContentPane().add(topBar, gbcTopBar);

        // ══════ HEADER ══════
        headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(TemaFacturaSoft.BG_APP);
        headerPanel.setBorder(new EmptyBorder(8, 40, 4, 40));

        JLabel lblImagen = new JLabel();
        ImageIcon icono = new ImageIcon(getClass().getResource("/img/Inicio facturacion.png"));
        int imgTarget = 130;
        double scale = Math.min((double) imgTarget / icono.getIconWidth(),
                (double) imgTarget / icono.getIconHeight());
        int scaledW = (int) (icono.getIconWidth() * scale);
        int scaledH = (int) (icono.getIconHeight() * scale);
        Image img = icono.getImage().getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
        lblImagen.setIcon(new ImageIcon(img));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        GridBagConstraints gbcImg = new GridBagConstraints();
        gbcImg.gridx = 0;
        gbcImg.gridy = 0;
        gbcImg.anchor = GridBagConstraints.CENTER;
        gbcImg.insets = new Insets(0, 0, 4, 0);
        headerPanel.add(lblImagen, gbcImg);

        JLabel lblFacturaSoft = new JLabel("FACTURASOFT", SwingConstants.CENTER);
        lblFacturaSoft.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(28f));
        lblFacturaSoft.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);
        GridBagConstraints gbcTitulo = new GridBagConstraints();
        gbcTitulo.gridx = 0;
        gbcTitulo.gridy = 1;
        gbcTitulo.anchor = GridBagConstraints.CENTER;
        gbcTitulo.insets = new Insets(2, 0, 2, 0);
        headerPanel.add(lblFacturaSoft, gbcTitulo);

        JLabel lblVersion = new JLabel("Version: 1.0", SwingConstants.CENTER);
        lblVersion.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));
        lblVersion.setForeground(TemaFacturaSoft.TEXT_MUTED);
        GridBagConstraints gbcVersion = new GridBagConstraints();
        gbcVersion.gridx = 0;
        gbcVersion.gridy = 2;
        gbcVersion.anchor = GridBagConstraints.CENTER;
        gbcVersion.insets = new Insets(0, 0, 2, 0);
        headerPanel.add(lblVersion, gbcVersion);

        JLabel lblDescripcion = new JLabel(
                "SISTEMA DE FACTURACION ELECTRONICA Y GESTION CONTABLE",
                SwingConstants.CENTER);
        lblDescripcion.setFont(TemaFacturaSoft.FONT_UI_MEDIUM.deriveFont(11f));
        lblDescripcion.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);
        GridBagConstraints gbcDesc = new GridBagConstraints();
        gbcDesc.gridx = 0;
        gbcDesc.gridy = 3;
        gbcDesc.anchor = GridBagConstraints.CENTER;
        gbcDesc.insets = new Insets(0, 0, 4, 0);
        headerPanel.add(lblDescripcion, gbcDesc);

        GridBagConstraints gbcHeader = new GridBagConstraints();
        gbcHeader.gridx = 0;
        gbcHeader.gridy = 1;
        gbcHeader.fill = GridBagConstraints.HORIZONTAL;
        gbcHeader.weightx = 1;
        gbcHeader.insets = new Insets(0, 0, 4, 0);
        getContentPane().add(headerPanel, gbcHeader);

        // ══════ SEPARADOR ══════
        sepPanel = new JPanel(new GridBagLayout());
        sepPanel.setBackground(TemaFacturaSoft.BG_APP);
        javax.swing.JSeparator sep = new javax.swing.JSeparator();
        sep.setForeground(TemaFacturaSoft.BORDER_SECTION);
        GridBagConstraints gbcSepLine = new GridBagConstraints();
        gbcSepLine.fill = GridBagConstraints.HORIZONTAL;
        gbcSepLine.weightx = 1;
        gbcSepLine.insets = new Insets(0, 50, 0, 50);
        sepPanel.add(sep, gbcSepLine);

        GridBagConstraints gbcSep = new GridBagConstraints();
        gbcSep.gridx = 0;
        gbcSep.gridy = 2;
        gbcSep.fill = GridBagConstraints.HORIZONTAL;
        gbcSep.weightx = 1;
        gbcSep.insets = new Insets(0, 0, 4, 0);
        getContentPane().add(sepPanel, gbcSep);

        // ══════ PANEL DE CONTROL ══════
        panelDeControl = new JPanel(new GridBagLayout());
        panelDeControl.setBackground(TemaFacturaSoft.BG_SURFACE);
        panelDeControl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaFacturaSoft.BORDER_DEFAULT, 1, true),
            new EmptyBorder(16, 20, 14, 20)
        ));

        int btnW = 220;
        int btnH = 48;

        // ── btnComprobantes ──
        btnComprobantes = new BotonEstilizado("COMPROBANTES", BotonEstilizado.Variante.SECUNDARIO);
        btnComprobantes.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        btnComprobantes.setHorizontalAlignment(SwingConstants.CENTER);
        btnComprobantes.setIconTextGap(10);
        btnComprobantes.setPreferredSize(new Dimension(btnW, btnH));
        try {
            ImageIcon icoComp = new ImageIcon(this.getClass().getResource("/img/Comprobantes.png"));
            Image imgComp = icoComp.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnComprobantes.setIcon(new ImageIcon(imgComp));
        } catch (Exception e) { }
        btnComprobantes.addActionListener(e -> {
            VentanaGestionComprobantes v = new VentanaGestionComprobantes();
            v.setVisible(true);
        });
        botones.add(btnComprobantes);
        GridBagConstraints gbcBtn1 = new GridBagConstraints();
        gbcBtn1.gridx = 0;
        gbcBtn1.gridy = 0;
        gbcBtn1.fill = GridBagConstraints.BOTH;
        gbcBtn1.weightx = 1;
        gbcBtn1.weighty = 1;
        gbcBtn1.insets = new Insets(4, 4, 4, 4);
        panelDeControl.add(btnComprobantes, gbcBtn1);

        // ── btnClientes ──
        btnClientes = new BotonEstilizado("CLIENTES", BotonEstilizado.Variante.SECUNDARIO);
        btnClientes.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        btnClientes.setHorizontalAlignment(SwingConstants.CENTER);
        btnClientes.setIconTextGap(10);
        btnClientes.setPreferredSize(new Dimension(btnW, btnH));
        try {
            ImageIcon icoCli = new ImageIcon(this.getClass().getResource("/img/Clientes.png"));
            Image imgCli = icoCli.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnClientes.setIcon(new ImageIcon(imgCli));
        } catch (Exception e) { }
        btnClientes.addActionListener(e -> new VentanaClientes().setVisible(true));
        botones.add(btnClientes);
        GridBagConstraints gbcBtn2 = new GridBagConstraints();
        gbcBtn2.gridx = 1;
        gbcBtn2.gridy = 0;
        gbcBtn2.fill = GridBagConstraints.BOTH;
        gbcBtn2.weightx = 1;
        gbcBtn2.weighty = 1;
        gbcBtn2.insets = new Insets(4, 4, 4, 4);
        panelDeControl.add(btnClientes, gbcBtn2);

        // ── btnRemitos ──
        btnRemitos = new BotonEstilizado("REMITOS", BotonEstilizado.Variante.SECUNDARIO);
        btnRemitos.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        btnRemitos.setHorizontalAlignment(SwingConstants.CENTER);
        btnRemitos.setIconTextGap(10);
        btnRemitos.setPreferredSize(new Dimension(btnW, btnH));
        try {
            ImageIcon icoRem = new ImageIcon(this.getClass().getResource("/img/Remitos.png"));
            Image imgRem = icoRem.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnRemitos.setIcon(new ImageIcon(imgRem));
        } catch (Exception e) { }
        btnRemitos.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo")
        );
        botones.add(btnRemitos);
        GridBagConstraints gbcBtn3 = new GridBagConstraints();
        gbcBtn3.gridx = 0;
        gbcBtn3.gridy = 1;
        gbcBtn3.fill = GridBagConstraints.BOTH;
        gbcBtn3.weightx = 1;
        gbcBtn3.weighty = 1;
        gbcBtn3.insets = new Insets(4, 4, 4, 4);
        panelDeControl.add(btnRemitos, gbcBtn3);

        // ── btnPagosRecibos ──
        btnPagosRecibos = new BotonEstilizado("PAGOS Y RECIBOS", BotonEstilizado.Variante.SECUNDARIO);
        btnPagosRecibos.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        btnPagosRecibos.setHorizontalAlignment(SwingConstants.CENTER);
        btnPagosRecibos.setIconTextGap(10);
        btnPagosRecibos.setPreferredSize(new Dimension(btnW, btnH));
        try {
            ImageIcon icoPag = new ImageIcon(this.getClass().getResource("/img/Pagos y recibos.png"));
            Image imgPag = icoPag.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnPagosRecibos.setIcon(new ImageIcon(imgPag));
        } catch (Exception e) { }
        btnPagosRecibos.addActionListener(e -> {
            VentanaPagosRecibos v = new VentanaPagosRecibos();
            v.setVisible(true);
        });
        botones.add(btnPagosRecibos);
        GridBagConstraints gbcBtn4 = new GridBagConstraints();
        gbcBtn4.gridx = 1;
        gbcBtn4.gridy = 1;
        gbcBtn4.fill = GridBagConstraints.BOTH;
        gbcBtn4.weightx = 1;
        gbcBtn4.weighty = 1;
        gbcBtn4.insets = new Insets(4, 4, 4, 4);
        panelDeControl.add(btnPagosRecibos, gbcBtn4);

        // ── btnHerramientas ──
        btnHerramientas = new BotonEstilizado("HERRAMIENTAS", BotonEstilizado.Variante.SECUNDARIO);
        btnHerramientas.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        btnHerramientas.setHorizontalAlignment(SwingConstants.CENTER);
        btnHerramientas.setIconTextGap(10);
        btnHerramientas.setPreferredSize(new Dimension(btnW, btnH));
        try {
            ImageIcon icoHer = new ImageIcon(this.getClass().getResource("/img/Herramientas.png"));
            Image imgHer = icoHer.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnHerramientas.setIcon(new ImageIcon(imgHer));
        } catch (Exception e) { }
        btnHerramientas.addActionListener(e -> {
            VentanaConfigCertificados v = new VentanaConfigCertificados();
            v.setVisible(true);
        });
        botones.add(btnHerramientas);
        GridBagConstraints gbcBtn5 = new GridBagConstraints();
        gbcBtn5.gridx = 0;
        gbcBtn5.gridy = 2;
        gbcBtn5.fill = GridBagConstraints.BOTH;
        gbcBtn5.weightx = 1;
        gbcBtn5.weighty = 1;
        gbcBtn5.insets = new Insets(4, 4, 4, 4);
        panelDeControl.add(btnHerramientas, gbcBtn5);

        // ── btnCajaGastos ──
        btnCajaGastos = new BotonEstilizado("CAJA Y GASTOS", BotonEstilizado.Variante.SECUNDARIO);
        btnCajaGastos.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(12f));
        btnCajaGastos.setHorizontalAlignment(SwingConstants.CENTER);
        btnCajaGastos.setIconTextGap(10);
        btnCajaGastos.setPreferredSize(new Dimension(btnW, btnH));
        try {
            ImageIcon icoCaja = new ImageIcon(this.getClass().getResource("/img/Caja y gastos.png"));
            Image imgCaja = icoCaja.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btnCajaGastos.setIcon(new ImageIcon(imgCaja));
        } catch (Exception e) { }
        btnCajaGastos.addActionListener(e -> {
            VentanaCajaGastos v = new VentanaCajaGastos();
            v.setVisible(true);
        });
        botones.add(btnCajaGastos);
        GridBagConstraints gbcBtn6 = new GridBagConstraints();
        gbcBtn6.gridx = 1;
        gbcBtn6.gridy = 2;
        gbcBtn6.fill = GridBagConstraints.BOTH;
        gbcBtn6.weightx = 1;
        gbcBtn6.weighty = 1;
        gbcBtn6.insets = new Insets(4, 4, 4, 4);
        panelDeControl.add(btnCajaGastos, gbcBtn6);

        // ══════ UBICACION ══════
        ubPanel = new JPanel(new GridBagLayout());
        ubPanel.setOpaque(false);
        ubPanel.setBackground(TemaFacturaSoft.BG_APP);

        JLabel lblUbicacion = new JLabel("UBICACION:");
        TemaFacturaSoft.aplicarEstilo(lblUbicacion, 13, true);
        lblUbicacion.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);
        GridBagConstraints gbcUbLbl = new GridBagConstraints();
        gbcUbLbl.gridx = 0;
        gbcUbLbl.gridy = 0;
        gbcUbLbl.anchor = GridBagConstraints.EAST;
        gbcUbLbl.insets = new Insets(0, 0, 0, 8);
        ubPanel.add(lblUbicacion, gbcUbLbl);

        cmbUbicacion = new JComboBox<>();
        cmbUbicacion.addItem("");
        cmbUbicacion.addItem(UbicacionSistema.BSAS);
        cmbUbicacion.addItem(UbicacionSistema.BRC);
        TemaFacturaSoft.aplicarEstiloCombo(cmbUbicacion, 13);
        cmbUbicacion.setPreferredSize(new Dimension(200, 30));
        cmbUbicacion.addActionListener(e -> {
            String selected = (String) cmbUbicacion.getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                UbicacionSistema.setUbicacion(selected);
                for (JButton btn : botones) btn.setEnabled(true);
            } else {
                UbicacionSistema.setUbicacion(null);
                for (JButton btn : botones) btn.setEnabled(false);
            }
        });
        GridBagConstraints gbcUbCombo = new GridBagConstraints();
        gbcUbCombo.gridx = 1;
        gbcUbCombo.gridy = 0;
        gbcUbCombo.anchor = GridBagConstraints.WEST;
        ubPanel.add(cmbUbicacion, gbcUbCombo);

        GridBagConstraints gbcUb = new GridBagConstraints();
        gbcUb.gridx = 0;
        gbcUb.gridy = 3;
        gbcUb.gridwidth = 2;
        gbcUb.fill = GridBagConstraints.HORIZONTAL;
        gbcUb.weightx = 1;
        gbcUb.insets = new Insets(8, 30, 0, 30);
        panelDeControl.add(ubPanel, gbcUb);

        for (JButton btn : botones) btn.setEnabled(false);

        GridBagConstraints gbcPanel = new GridBagConstraints();
        gbcPanel.gridx = 0;
        gbcPanel.gridy = 3;
        gbcPanel.fill = GridBagConstraints.BOTH;
        gbcPanel.weightx = 1;
        gbcPanel.weighty = 1;
        gbcPanel.insets = new Insets(0, 0, 4, 0);
        getContentPane().add(panelDeControl, gbcPanel);

        // ══════ BOTTOM BAR ══════
        JPanel bottomBar = new JPanel(new GridBagLayout());
        bottomBar.setBackground(TemaFacturaSoft.BG_STATUS_BAR);
        bottomBar.setBorder(new EmptyBorder(5, 16, 5, 16));

        textProgramador = new JTextField();
        textProgramador.setOpaque(false);
        textProgramador.setEditable(false);
        textProgramador.setBorder(null);
        textProgramador.setHorizontalAlignment(SwingConstants.RIGHT);
        textProgramador.setForeground(new Color(200, 200, 220));
        textProgramador.setFont(TemaFacturaSoft.FONT_UI.deriveFont(10f));
        textProgramador.setPreferredSize(new Dimension(180, 16));
        GridBagConstraints gbcProg = new GridBagConstraints();
        gbcProg.gridx = 0;
        gbcProg.gridy = 0;
        gbcProg.anchor = GridBagConstraints.EAST;
        gbcProg.weightx = 1;
        gbcProg.fill = GridBagConstraints.HORIZONTAL;
        bottomBar.add(textProgramador, gbcProg);

        textVersionSoft = new JTextField();
        textVersionSoft.setOpaque(false);
        textVersionSoft.setEditable(false);
        textVersionSoft.setBorder(null);
        textVersionSoft.setHorizontalAlignment(SwingConstants.RIGHT);
        textVersionSoft.setForeground(new Color(200, 200, 220));
        textVersionSoft.setFont(TemaFacturaSoft.FONT_UI.deriveFont(10f));
        textVersionSoft.setPreferredSize(new Dimension(180, 16));
        GridBagConstraints gbcVer = new GridBagConstraints();
        gbcVer.gridx = 1;
        gbcVer.gridy = 0;
        gbcVer.anchor = GridBagConstraints.EAST;
        gbcVer.insets = new Insets(0, 12, 0, 0);
        bottomBar.add(textVersionSoft, gbcVer);

        GridBagConstraints gbcBottom = new GridBagConstraints();
        gbcBottom.gridx = 0;
        gbcBottom.gridy = 4;
        gbcBottom.fill = GridBagConstraints.HORIZONTAL;
        gbcBottom.weightx = 1;
        getContentPane().add(bottomBar, gbcBottom);

        // ══════ FIELDS EXTERNOS ══════
        textUsuario = new JTextField();
        textUsuario.setOpaque(false);
        textUsuario.setEditable(false);
        textUsuario.setBorder(null);
        textUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        textUsuario.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);
        textUsuario.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(13f));

        textUbicacion = new JTextField();
        textUbicacion.setOpaque(false);
        textUbicacion.setEditable(false);
        textUbicacion.setBorder(null);
        textUbicacion.setHorizontalAlignment(SwingConstants.CENTER);
        textUbicacion.setForeground(TemaFacturaSoft.ACCENT_PRIMARY);
        textUbicacion.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(13f));

        setSize(540, 560);
        setLocationRelativeTo(null);
    }

    // ══════ GETTERS ══════
    public JButton getBotonComprobantes() { return btnComprobantes; }
    public JButton getBotonClientes() { return btnClientes; }
    public JButton getBotonRemitos() { return btnRemitos; }
    public JButton getBotonPagosRecibos() { return btnPagosRecibos; }
    public JButton getBotonHerramientas() { return btnHerramientas; }
    public JButton getBotonCajaGastos() { return btnCajaGastos; }
    public JButton getBtnSalir() { return btnSalir; }
    public JPanel getPanelDeControl() { return panelDeControl; }
    public JTextField getTextUsuario() { return textUsuario; }
    public void setTextUsuario(JTextField t) { this.textUsuario = t; }
    public JTextField getTextUbicacion() { return textUbicacion; }
    public void setTextUbicacion(JTextField t) { this.textUbicacion = t; }
    public JTextField getTextProgramador() { return textProgramador; }
    public void setTextProgramador(JTextField t) { this.textProgramador = t; }
    public JTextField getTextVersionSoft() { return textVersionSoft; }
    public void setTextVersionSoft(JTextField t) { this.textVersionSoft = t; }
    public JComboBox<String> getCmbUbicacion() { return cmbUbicacion; }
    public List<JButton> getBotones() { return botones; }
}
