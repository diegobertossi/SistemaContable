package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
import com.els.facturacion.controlador.ControladorReparsoft;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.RespuestaCAE;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaFacturacion extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 13);
    private static final Font FUENTE_TITULO = new Font("Cambria", Font.BOLD, 14);

    private ControladorFacturacion controlador;
    private ControladorReparsoft controladorReparsoft;

    private JComboBox<CuitConfigDTO> cmbCuitEmisor;
    private JComboBox<String> cmbTipoComprobante;
    private JTextField txtPuntoVenta;
    private JTextField txtNumero;
    private JTextField txtCuitReceptor;
    private JTextField txtRazonSocial;
    private JTextField txtImporteNeto;
    private JTextField txtImporteIva;
    private JTextField txtImporteTotal;
    private JTextField txtFecha;
    private JTextField txtELS;
    private JButton btnEmitir;
    private JButton btnLimpiar;
    private JButton btnVerComprobantes;
    private JButton btnConfigurar;

    private DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VentanaFacturacion() {
        controlador = new ControladorFacturacion();
        controladorReparsoft = new ControladorReparsoft();
        initComponents();
        cargarCuits();
    }

    private void initComponents() {
        setTitle("FacturaSoft v1.0 - Sistema de Facturacion Electronica");
        setSize(700, 500);
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemSalir);

        JMenu menuHerramientas = new JMenu("Herramientas");
        JMenuItem itemConfig = new JMenuItem("Configurar Certificados");
        itemConfig.addActionListener(e -> abrirConfiguracion());
        JMenuItem itemHistorial = new JMenuItem("Ver Comprobantes");
        itemHistorial.addActionListener(e -> abrirHistorial());
        JMenuItem itemCaja = new JMenuItem("Caja");
        itemCaja.addActionListener(e -> abrirCaja());
        JMenuItem itemGastos = new JMenuItem("Gastos");
        itemGastos.addActionListener(e -> abrirGastos());
        JMenuItem itemMigrar = new JMenuItem("Migrar desde Excel");
        itemMigrar.addActionListener(e -> abrirMigracion());
        menuHerramientas.add(itemConfig);
        menuHerramientas.add(itemHistorial);
        menuHerramientas.addSeparator();
        menuHerramientas.add(itemCaja);
        menuHerramientas.add(itemGastos);
        menuHerramientas.addSeparator();
        menuHerramientas.add(itemMigrar);

        menuBar.add(menuArchivo);
        menuBar.add(menuHerramientas);
        setJMenuBar(menuBar);

        JPanel panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(COLOR_FONDO);
        GridBagConstraints gbcBotones = new GridBagConstraints();
        gbcBotones.insets = new Insets(8, 8, 8, 8);

        JButton btnFacturacion = crearBotonMenu("FACTURACION", 120, 50);
        btnFacturacion.addActionListener(e -> this.toFront());

        JButton btnCaja = crearBotonMenu("CAJA", 120, 50);
        btnCaja.addActionListener(e -> abrirCaja());

        JButton btnGastos = crearBotonMenu("GASTOS", 120, 50);
        btnGastos.addActionListener(e -> abrirGastos());

        JButton btnComprobantes = crearBotonMenu("COMPROBANTES", 120, 50);
        btnComprobantes.addActionListener(e -> abrirHistorial());

        JButton btnConfig = crearBotonMenu("CONFIGURACION", 120, 50);
        btnConfig.addActionListener(e -> abrirConfiguracion());

        JButton btnMigrar = crearBotonMenu("MIGRAR", 120, 50);
        btnMigrar.addActionListener(e -> abrirMigracion());

        gbcBotones.gridx = 0;
        panelBotones.add(btnFacturacion, gbcBotones);
        gbcBotones.gridx = 1;
        panelBotones.add(btnCaja, gbcBotones);
        gbcBotones.gridx = 2;
        panelBotones.add(btnGastos, gbcBotones);
        gbcBotones.gridx = 3;
        panelBotones.add(btnComprobantes, gbcBotones);
        gbcBotones.gridx = 4;
        panelBotones.add(btnConfig, gbcBotones);
        gbcBotones.gridx = 5;
        panelBotones.add(btnMigrar, gbcBotones);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.add(panelBotones, BorderLayout.NORTH);

        JLabel lblTitulo = new JLabel("FacturaSoft v1.0", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_TEXTO);
        panelPrincipal.add(lblTitulo, BorderLayout.CENTER);

        JLabel lblSubtitulo = new JLabel("Sistema de Facturacion Electronica", SwingConstants.CENTER);
        lblSubtitulo.setFont(FUENTE_TITULO);
        lblSubtitulo.setForeground(COLOR_TITULO);
        panelPrincipal.add(lblSubtitulo, BorderLayout.SOUTH);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        cmbCuitEmisor = new JComboBox<>();
        cmbTipoComprobante = new JComboBox<>(new String[]{
            "1 - Factura A", "6 - Factura B", "11 - Factura C",
            "3 - Nota Crédito A", "8 - Nota Crédito B", "13 - Nota Crédito C"
        });
        cmbTipoComprobante.setSelectedIndex(0);

        txtPuntoVenta = new JTextField(10);
        txtNumero = new JTextField(10);
        txtCuitReceptor = new JTextField(15);
        txtRazonSocial = new JTextField(20);
        txtImporteNeto = new JTextField(12);
        txtImporteIva = new JTextField(12);
        txtImporteTotal = new JTextField(12);
        txtFecha = new JTextField(10);
        txtELS = new JTextField(10);

        txtFecha.setText(LocalDate.now().format(fechaFormatter));
        txtFecha.setEditable(false);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("CUIT Emisor:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelFormulario.add(cmbCuitEmisor, gbc);

        row++;
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Tipo Comprobante:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(cmbTipoComprobante, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Punto de Venta:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtPuntoVenta, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Número:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtNumero, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("CUIT Receptor:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtCuitReceptor, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Razón Social:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelFormulario.add(txtRazonSocial, gbc);

        row++;
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Importe Neto:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtImporteNeto, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Importe IVA:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtImporteIva, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Importe Total:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtImporteTotal, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("Fecha Emisión:"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtFecha, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panelFormulario.add(new JLabel("ELS (ReparSoft):"), gbc);
        gbc.gridx = 1;
        panelFormulario.add(txtELS, gbc);

        JButton btnCargarELS = new JButton("Cargar desde ELS");
        btnCargarELS.addActionListener(e -> btnCargarELSAction());
        gbc.gridx = 2;
        panelFormulario.add(btnCargarELS, gbc);

        JPanel panelBotones1 = new JPanel();
        btnEmitir = new JButton("Emitir Comprobante");
        btnLimpiar = new JButton("Limpiar");
        btnEmitir.addActionListener(e -> btnEmitirAction());
        btnLimpiar.addActionListener(e -> btnLimpiar());

        panelBotones1.add(btnEmitir);
        panelBotones1.add(btnLimpiar);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelBotones1, BorderLayout.CENTER);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarCuits() {
        List<CuitConfigDTO> cuits = controlador.getCuitsActivos();
        cmbCuitEmisor.removeAllItems();
        for (CuitConfigDTO cuit : cuits) {
            cmbCuitEmisor.addItem(cuit);
        }
    }

    private void btnEmitirAction() {
        if (!validarCampos()) return;

        try {
            CuitConfigDTO cuiSelected = (CuitConfigDTO) cmbCuitEmisor.getSelectedItem();
            if (cuiSelected == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un CUIT emisor", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tipoStr = (String) cmbTipoComprobante.getSelectedItem();
            int tipoComprobante = Integer.parseInt(tipoStr.split(" - ")[0]);

            ComprobanteDTO comprobante = new ComprobanteDTO();
            comprobante.setCuitEmisor(cuiSelected.getCuit());
            comprobante.setTipoComprobante(tipoComprobante);
            comprobante.setPuntoVenta(cuiSelected.getPuntoVenta());
            comprobante.setCuitReceptor(txtCuitReceptor.getText().trim());
            comprobante.setRazonSocialRec(txtRazonSocial.getText().trim());
            comprobante.setImporteNeto(new BigDecimal(txtImporteNeto.getText().trim()));
            String ivaStr = txtImporteIva.getText().trim();
            if (!ivaStr.isEmpty()) {
                comprobante.setImporteIva(new BigDecimal(ivaStr));
            }
            String totalStr = txtImporteTotal.getText().trim();
            if (!totalStr.isEmpty()) {
                comprobante.setImporteTotal(new BigDecimal(totalStr));
            }

            String elsStr = txtELS.getText().trim();
            if (!elsStr.isEmpty()) {
                comprobante.setElsAsociado(Integer.parseInt(elsStr));
            }

            btnEmitir.setEnabled(false);
            btnEmitir.setText("Emitiendo...");

            RespuestaCAE respuesta = controlador.emitirFactura(comprobante);

            if (respuesta.isExitosa()) {
                JOptionPane.showMessageDialog(this,
                    "Comprobante emitido exitosamente!\n\n"
                    + "CAE: " + respuesta.getCae() + "\n"
                    + "Número: " + respuesta.getNumeroComprobante() + "\n"
                    + "Vencimiento: " + respuesta.getVencimiento().format(fechaFormatter),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                btnLimpiar();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al emitir comprobante:\n" + respuesta.getMensaje(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            btnEmitir.setEnabled(true);
            btnEmitir.setText("Emitir Comprobante");
        }
    }

    private void btnLimpiar() {
        txtCuitReceptor.setText("");
        txtRazonSocial.setText("");
        txtImporteNeto.setText("");
        txtImporteIva.setText("");
        txtImporteTotal.setText("");
        txtELS.setText("");
    }

    private void btnCargarELSAction() {
        String elsStr = txtELS.getText().trim();
        if (elsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el número de ELS", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int els = Integer.parseInt(elsStr);

            if (!controladorReparsoft.verificarELS(els)) {
                JOptionPane.showMessageDialog(this, "ELS " + els + " no encontrada en ReparSoft", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ComprobanteDTO comprobante = controladorReparsoft.crearComprobanteDesdeELS(els);
            if (comprobante != null) {
                txtCuitReceptor.setText(comprobante.getCuitReceptor() != null ? comprobante.getCuitReceptor() : "");
                txtRazonSocial.setText(comprobante.getRazonSocialRec() != null ? comprobante.getRazonSocialRec() : "");

                if (comprobante.getImporteNeto() != null) {
                    txtImporteNeto.setText(comprobante.getImporteNeto().toString());
                }
                if (comprobante.getImporteIva() != null) {
                    txtImporteIva.setText(comprobante.getImporteIva().toString());
                }
                if (comprobante.getImporteTotal() != null) {
                    txtImporteTotal.setText(comprobante.getImporteTotal().toString());
                }

                JOptionPane.showMessageDialog(this, "Datos cargados desde ELS " + els, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al cargar datos del ELS", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ELS debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (cmbCuitEmisor.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un CUIT emisor", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtCuitReceptor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese CUIT del receptor", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese razón social del receptor", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtImporteNeto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese importe neto", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            new BigDecimal(txtImporteNeto.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Importe neto inválido", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void abrirConfiguracion() {
        VentanaConfigCertificados ventana = new VentanaConfigCertificados();
        ventana.setVisible(true);
    }

    private void abrirHistorial() {
        VentanaComprobantes ventana = new VentanaComprobantes();
        ventana.setVisible(true);
    }

    private void abrirCaja() {
        VentanaCaja ventana = new VentanaCaja();
        ventana.setVisible(true);
    }

    private void abrirGastos() {
        VentanaGastos ventana = new VentanaGastos();
        ventana.setVisible(true);
    }

    private void abrirMigracion() {
        VentanaMigracion ventana = new VentanaMigracion();
        ventana.setVisible(true);
    }

    private JButton crearBotonMenu(String texto, int ancho, int alto) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new java.awt.Dimension(ancho, alto));
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaFacturacion().setVisible(true));
    }
}