package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
import com.els.facturacion.controlador.ControladorReparsoft;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import com.els.facturacion.modelo.RespuestaCAE;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VentanaFacturacion extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Cambria", Font.BOLD, 11);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    private ControladorFacturacion controlador;
    private ControladorReparsoft controladorReparsoft;

    private CardLayout cardLayout;
    private JPanel panelPrincipal;

    private JComboBox<String> cmbPuntoVenta;
    private JComboBox<String> cmbTipoComprobante;

    private JTextField txtFecha;
    private JComboBox<String> cmbConcepto;
    private JTextField txtActividades;
    private JTextField txtPeriodoDesde;
    private JButton btnFecha, btnPeriodoDesde, btnPeriodoHasta, btnPeriodoVto;
    private JTextField txtPeriodoHasta;
    private JTextField txtPeriodoVto;

    private JComboBox<String> cmbCondicionIva;
    private JComboBox<String> cmbTipoDoc;
    private JTextField txtNroDoc;
    private JTextField txtRazonSocial;
    private JTextField txtDomicilio;
    private JTextField txtEmail;
    private JCheckBox chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra;
    private JTextField txtComprobanteAsoc;

    private JTable tablaItems;
    private DefaultTableModel modeloTablaItems;
    private JLabel lblTotal;
    private JTextField txtImporteNeto;
    private JTextField txtImporteIva;
    private JTextField txtImporteTotal;

    private JButton btnSiguiente;
    private JButton btnAnterior;
    private JButton btnEmitir;

    public VentanaFacturacion() {
        controlador = new ControladorFacturacion();
        controladorReparsoft = new ControladorReparsoft();
        initComponents();
        cargarCuits();
    }

    private void initComponents() {
        setTitle("FacturaSoft v1.0 - Sistema de Facturacion Electronica");
        setSize(820, 780);
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

        cardLayout = new CardLayout();
        panelPrincipal = new JPanel(cardLayout);
        panelPrincipal.setBackground(COLOR_FONDO);

        panelPrincipal.add(crearPanelDatos(), "datos");
        panelPrincipal.add(crearPanelOperacion(), "operacion");

        add(panelPrincipal);
    }

    private JPanel crearPanelDatos() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(crearSeccionPuntoVenta(), gbc);

        gbc.gridy++;
        panel.add(crearSeccionEmision(), gbc);

        gbc.gridy++;
        panel.add(crearSeccionReceptor(), gbc);

        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel panelNav = new JPanel();
        panelNav.setBackground(COLOR_FONDO);

        JCheckBox chkModoPrueba = new JCheckBox("MODO PRUEBA");
        chkModoPrueba.setFont(new Font("Cambria", Font.BOLD, 12));
        chkModoPrueba.setForeground(new Color(200, 0, 0));
        chkModoPrueba.setBackground(COLOR_FONDO);
        chkModoPrueba.addActionListener(e -> controlador.setModoPrueba(chkModoPrueba.isSelected()));

        btnSiguiente = crearBoton("SIGUIENTE >>");
        btnSiguiente.addActionListener(e -> {
            if (validarDatosReceptor()) {
                cardLayout.show(panelPrincipal, "operacion");
            }
        });
        panelNav.add(chkModoPrueba);
        panelNav.add(btnSiguiente);
        panel.add(panelNav, gbc);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_FONDO);
        wrapper.add(scroll);
        return wrapper;
    }

    private JPanel crearSeccionPuntoVenta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "PUNTO DE VENTA Y TIPO DE COMPROBANTE",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 12), COLOR_TEXTO
        ));
        panel.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbPuntoVenta = new JComboBox<>(new String[]{"00001", "00002", "00003", "00004", "00005"});
        cmbPuntoVenta.setFont(FUENTE_BOTON);
        cmbTipoComprobante = new JComboBox<>(new String[]{
            "Factura C", "Nota de Débito C", "Nota de Crédito C",
            "Recibo C", "Factura de Crédito Electrónica MiPymes (FCE) C",
            "Nota de Débito Electrónica MiPymes (FCE) C",
            "Nota de Crédito Electrónica MiPymes (FCE) C"
        });
        cmbTipoComprobante.setFont(FUENTE_BOTON);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Punto de Venta:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbPuntoVenta, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Tipo Comprobante:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        panel.add(cmbTipoComprobante, gbc);

        return panel;
    }

    private JPanel crearSeccionEmision() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "DATOS DE EMISION",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 12), COLOR_TEXTO
        ));
        panel.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFecha = new JTextField(12);
        txtFecha.setText(LocalDate.now().format(FMT));
        txtFecha.setFont(new Font("Cambria", Font.PLAIN, 11));
        btnFecha = crearBotonDate();
        btnFecha.addActionListener(e -> seleccionarFecha(txtFecha));

        cmbConcepto = new JComboBox<>(new String[]{"Productos", "Servicios", "Productos y Servicios"});
        cmbConcepto.setFont(FUENTE_BOTON);

        txtActividades = new JTextField(30);
        txtActividades.setText("331290 - Reparacion y mantenimiento de maquinaria de uso especial n.c.p.");
        txtActividades.setEditable(false);
        txtActividades.setFont(new Font("Cambria", Font.PLAIN, 10));
        txtActividades.setBackground(new Color(240, 240, 240));

        txtPeriodoDesde = new JTextField(10);
        txtPeriodoDesde.setText(LocalDate.now().format(FMT));
        txtPeriodoDesde.setFont(new Font("Cambria", Font.PLAIN, 11));
        btnPeriodoDesde = crearBotonDate();
        btnPeriodoDesde.addActionListener(e -> seleccionarFecha(txtPeriodoDesde));

        txtPeriodoHasta = new JTextField(10);
        txtPeriodoHasta.setText(LocalDate.now().format(FMT));
        txtPeriodoHasta.setFont(new Font("Cambria", Font.PLAIN, 11));
        btnPeriodoHasta = crearBotonDate();
        btnPeriodoHasta.addActionListener(e -> seleccionarFecha(txtPeriodoHasta));

        txtPeriodoVto = new JTextField(10);
        txtPeriodoVto.setText(LocalDate.now().format(FMT));
        txtPeriodoVto.setFont(new Font("Cambria", Font.PLAIN, 11));
        btnPeriodoVto = crearBotonDate();
        btnPeriodoVto.addActionListener(e -> seleccionarFecha(txtPeriodoVto));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Fecha del comprobante:"), gbc);
        gbc.gridx = 1;
        panel.add(txtFecha, gbc);
        gbc.gridx = 2;
        panel.add(btnFecha, gbc);
        gbc.gridx = 3;
        panel.add(new JLabel("Concepto:"), gbc);
        gbc.gridx = 4; gbc.weightx = 1.0;
        panel.add(cmbConcepto, gbc);

        row++;
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Actividades Asociadas:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 4; gbc.weightx = 1.0;
        panel.add(txtActividades, gbc);

        row++;
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Periodo facturado:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Desde:"), gbc);
        gbc.gridx = 2;
        panel.add(txtPeriodoDesde, gbc);
        gbc.gridx = 3;
        panel.add(btnPeriodoDesde, gbc);
        gbc.gridx = 4;
        panel.add(new JLabel("Hasta:"), gbc);
        gbc.gridx = 5;
        panel.add(txtPeriodoHasta, gbc);
        gbc.gridx = 6;
        panel.add(btnPeriodoHasta, gbc);
        gbc.gridx = 7;
        panel.add(new JLabel("Vto. Pago:"), gbc);
        gbc.gridx = 8;
        panel.add(txtPeriodoVto, gbc);
        gbc.gridx = 9;
        panel.add(btnPeriodoVto, gbc);

        return panel;
    }

    private JPanel crearSeccionReceptor() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_TITULO),
            "DATOS DEL RECEPTOR",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Cambria", Font.BOLD, 12), COLOR_TEXTO
        ));
        panel.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbCondicionIva = new JComboBox<>(new String[]{
            "IVA Responsable Inscripto", "IVA Sujeto Exento", "Consumidor Final",
            "Responsable Monotributo", "Proveedor del Exterior", "Cliente del Exterior",
            "IVA Liberado - Ley 19.640", "Monotributista Social", "IVA No Alcanzado"
        });
        cmbCondicionIva.setFont(FUENTE_BOTON);

        cmbTipoDoc = new JComboBox<>(new String[]{"CUIT", "DNI"});
        cmbTipoDoc.setFont(FUENTE_BOTON);

        txtNroDoc = new JTextField(15);
        txtNroDoc.setFont(new Font("Cambria", Font.PLAIN, 11));

        txtRazonSocial = new JTextField(25);
        txtRazonSocial.setFont(new Font("Cambria", Font.PLAIN, 11));

        txtDomicilio = new JTextField(25);
        txtDomicilio.setFont(new Font("Cambria", Font.PLAIN, 11));

        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Cambria", Font.PLAIN, 11));

        txtComprobanteAsoc = new JTextField(15);
        txtComprobanteAsoc.setFont(new Font("Cambria", Font.PLAIN, 11));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Condicion frente al IVA:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        panel.add(cmbCondicionIva, gbc);

        row++;
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tipo y Nro Documento:"), gbc);
        gbc.gridx = 1;
        panel.add(cmbTipoDoc, gbc);
        gbc.gridx = 2;
        panel.add(txtNroDoc, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Nombre o Razon Social:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        panel.add(txtRazonSocial, gbc);

        row++;
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Domicilio:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        panel.add(txtDomicilio, gbc);

        row++;
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Condiciones de Venta:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 4; gbc.weightx = 1.0;

        JPanel panelChecks = new JPanel(new GridLayout(2, 4, 5, 2));
        panelChecks.setBackground(COLOR_FONDO);
        chkContado = new JCheckBox("Contado");
        chkTarjetaDeb = new JCheckBox("Tarjeta Debito");
        chkTarjetaCred = new JCheckBox("Tarjeta Credito");
        chkCC = new JCheckBox("Cuenta Corriente");
        chkCheque = new JCheckBox("Cheque");
        chkTransf = new JCheckBox("Transf. Bancaria");
        chkOtra = new JCheckBox("Otra");
        for (JCheckBox c : new JCheckBox[]{chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra}) {
            c.setBackground(COLOR_FONDO);
            c.setFont(FUENTE_BOTON);
            c.setForeground(COLOR_TEXTO);
        }
        panelChecks.add(chkContado);
        panelChecks.add(chkTarjetaDeb);
        panelChecks.add(chkTarjetaCred);
        panelChecks.add(chkCC);
        panelChecks.add(chkCheque);
        panelChecks.add(chkTransf);
        panelChecks.add(chkOtra);
        panel.add(panelChecks, gbc);

        row++;
        gbc.gridwidth = 1; gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Comp. Asociado/Remito:"), gbc);
        gbc.gridx = 1;
        panel.add(txtComprobanteAsoc, gbc);

        return panel;
    }

    private JPanel crearPanelOperacion() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_FONDO);
        btnAnterior = crearBoton("<< ANTERIOR");
        btnAnterior.addActionListener(e -> cardLayout.show(panelPrincipal, "datos"));
        JLabel lblTitulo = new JLabel("DATOS DE LA OPERACION", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_TEXTO);
        panelSuperior.add(btnAnterior, BorderLayout.WEST);
        panelSuperior.add(lblTitulo, BorderLayout.CENTER);

        String[] columnas = {"Codigo", "Producto/Servicio", "Cantidad", "U. Medida", "P. Unitario", "Subtotal"};
        modeloTablaItems = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 5;
            }
        };
        tablaItems = new JTable(modeloTablaItems);
        tablaItems.setFont(new Font("Cambria", Font.PLAIN, 11));
        tablaItems.getTableHeader().setFont(new Font("Cambria", Font.BOLD, 11));
        tablaItems.getColumnModel().getColumn(0).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaItems.getColumnModel().getColumn(2).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(3).setPreferredWidth(60);
        tablaItems.getColumnModel().getColumn(4).setPreferredWidth(100);
        tablaItems.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollTabla = new JScrollPane(tablaItems);

        JButton btnAgregarItem = crearBoton("+ AGREGAR ITEM");
        btnAgregarItem.addActionListener(e -> agregarItem());

        JPanel panelSur = new JPanel(new BorderLayout(10, 5));
        panelSur.setBackground(COLOR_FONDO);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(COLOR_FONDO);
        panelBotones.add(btnAgregarItem);

        JPanel panelTotales = new JPanel(new GridBagLayout());
        panelTotales.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblTotal = new JLabel("$ 0.00");
        lblTotal.setFont(new Font("Cambria", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 100, 0));

        gbc.gridx = 0; gbc.gridy = 0;
        panelTotales.add(new JLabel("Importe Neto:"), gbc);
        txtImporteNeto = new JTextField(12);
        txtImporteNeto.setEditable(false);
        txtImporteNeto.setFont(new Font("Cambria", Font.BOLD, 12));
        txtImporteNeto.setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1;
        panelTotales.add(txtImporteNeto, gbc);
        gbc.gridx = 2;
        panelTotales.add(new JLabel("IVA 21%:"), gbc);
        txtImporteIva = new JTextField(12);
        txtImporteIva.setEditable(false);
        txtImporteIva.setFont(new Font("Cambria", Font.BOLD, 12));
        txtImporteIva.setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 3;
        panelTotales.add(txtImporteIva, gbc);
        gbc.gridx = 4;
        panelTotales.add(new JLabel("Total:"), gbc);
        txtImporteTotal = new JTextField(12);
        txtImporteTotal.setEditable(false);
        txtImporteTotal.setFont(new Font("Cambria", Font.BOLD, 14));
        txtImporteTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtImporteTotal.setForeground(new Color(0, 100, 0));
        gbc.gridx = 5;
        panelTotales.add(txtImporteTotal, gbc);

        JButton btnCalcular = crearBoton("CALCULAR");
        btnCalcular.addActionListener(e -> recalcularTotales());
        gbc.gridx = 6;
        panelTotales.add(btnCalcular, gbc);

        JPanel panelEmitir = new JPanel();
        panelEmitir.setBackground(COLOR_FONDO);
        btnEmitir = crearBoton("EMITIR COMPROBANTE");
        btnEmitir.setFont(new Font("Cambria", Font.BOLD, 13));
        btnEmitir.setBackground(new Color(100, 180, 100));
        btnEmitir.addActionListener(e -> btnEmitirAction());
        JButton btnLimpiar = crearBoton("LIMPIAR");
        btnLimpiar.addActionListener(e -> limpiarTodo());

        panelEmitir.add(btnEmitir);
        panelEmitir.add(btnLimpiar);

        panelSur.add(panelBotones, BorderLayout.NORTH);
        panelSur.add(panelTotales, BorderLayout.CENTER);
        panelSur.add(panelEmitir, BorderLayout.SOUTH);

        panel.add(panelSuperior, BorderLayout.NORTH);
        panel.add(scrollTabla, BorderLayout.CENTER);
        panel.add(panelSur, BorderLayout.SOUTH);

        return panel;
    }

    private void agregarItem() {
        modeloTablaItems.addRow(new Object[]{"", "", "1", "Unidad", "0,00", "0,00"});
    }

    private void recalcularTotales() {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < modeloTablaItems.getRowCount(); i++) {
            try {
                Object cantObj = modeloTablaItems.getValueAt(i, 2);
                Object precioObj = modeloTablaItems.getValueAt(i, 4);
                double cant = Double.parseDouble(cantObj.toString().replace(",", "."));
                double precio = Double.parseDouble(precioObj.toString().replace(",", ".").replace("$", "").trim());
                BigDecimal subtotal = BigDecimal.valueOf(cant * precio).setScale(2, BigDecimal.ROUND_HALF_UP);
                modeloTablaItems.setValueAt(DF.format(subtotal), i, 5);
                total = total.add(subtotal);
            } catch (Exception e) {
                modeloTablaItems.setValueAt("0,00", i, 5);
            }
        }
        BigDecimal iva = total.multiply(BigDecimal.valueOf(0.21)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal totalConIva = total.add(iva);
        txtImporteNeto.setText(DF.format(total));
        txtImporteIva.setText(DF.format(iva));
        txtImporteTotal.setText(DF.format(totalConIva));
        lblTotal.setText("$ " + DF.format(totalConIva));
    }

    private boolean validarDatosReceptor() {
        if (txtNroDoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese numero de documento", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese razon social", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void btnEmitirAction() {
        if (modeloTablaItems.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un item", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        recalcularTotales();

        try {
            CuitConfigDTO cuiSelected = obtenerCuitSeleccionado();
            if (cuiSelected == null) {
                JOptionPane.showMessageDialog(this, "Configure un CUIT emisor en Herramientas > Configurar Certificados", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ComprobanteDTO comprobante = new ComprobanteDTO();
            comprobante.setCuitEmisor(cuiSelected.getCuit());
            comprobante.setTipoComprobante(obtenerTipoCodigo());
            comprobante.setPuntoVenta(Integer.parseInt((String) cmbPuntoVenta.getSelectedItem()));
            comprobante.setCuitReceptor(obtenerDocReceptor());
            comprobante.setRazonSocialRec(txtRazonSocial.getText().trim());
            comprobante.setFechaEmision(parseFecha(txtFecha.getText()));
            comprobante.setImporteNeto(new BigDecimal(txtImporteNeto.getText().replace(",", "")));
            comprobante.setImporteIva(new BigDecimal(txtImporteIva.getText().replace(",", "")));
            comprobante.setImporteTotal(new BigDecimal(txtImporteTotal.getText().replace(",", "")));
            comprobante.setConcepto((String) cmbConcepto.getSelectedItem());
            comprobante.setPeriodoDesde(parseFecha(txtPeriodoDesde.getText()));
            comprobante.setPeriodoHasta(parseFecha(txtPeriodoHasta.getText()));
            comprobante.setPeriodoVto(parseFecha(txtPeriodoVto.getText()));
            comprobante.setCondicionIvaReceptor((String) cmbCondicionIva.getSelectedItem());
            comprobante.setTipoDocumento((String) cmbTipoDoc.getSelectedItem());
            comprobante.setNroDocumento(txtNroDoc.getText().trim());
            comprobante.setDomicilioReceptor(txtDomicilio.getText().trim());
            comprobante.setEmailReceptor(txtEmail.getText().trim());
            comprobante.setCondicionesVenta(obtenerCondicionesVenta());
            comprobante.setComprobanteAsociado(txtComprobanteAsoc.getText().trim());
            comprobante.setDescripcion(obtenerDescripcionItems());

            btnEmitir.setEnabled(false);
            btnEmitir.setText("Emitiendo...");

            List<ItemFacturaDTO> items = obtenerItems();
            RespuestaCAE respuesta = controlador.emitirFactura(comprobante, items);

            if (respuesta.isExitosa()) {
                String modo = controlador.isModoPrueba() ? " (MODO PRUEBA)" : "";
                JOptionPane.showMessageDialog(this,
                    "Comprobante emitido exitosamente!" + modo + "\n\n"
                    + "CAE: " + respuesta.getCae() + "\n"
                    + "Numero: " + respuesta.getNumeroComprobante(),
                    "Exito", JOptionPane.INFORMATION_MESSAGE);
                limpiarTodo();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al emitir comprobante:\n" + respuesta.getMensaje(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            btnEmitir.setEnabled(true);
            btnEmitir.setText("EMITIR COMPROBANTE");
        }
    }

    private int obtenerTipoCodigo() {
        String sel = (String) cmbTipoComprobante.getSelectedItem();
        if (sel.startsWith("Factura C")) return 11;
        if (sel.startsWith("Nota de Debito")) return 12;
        if (sel.startsWith("Nota de Credito")) return 13;
        if (sel.startsWith("Recibo")) return 10;
        if (sel.contains("FCE")) return 11;
        return 11;
    }

    private String obtenerDocReceptor() {
        String tipo = (String) cmbTipoDoc.getSelectedItem();
        String nro = txtNroDoc.getText().trim().replaceAll("[^0-9]", "");
        if ("CUIT".equals(tipo)) {
            while (nro.length() < 11) nro = "0" + nro;
        }
        return nro;
    }

    private List<ItemFacturaDTO> obtenerItems() {
        List<ItemFacturaDTO> lista = new ArrayList<>();
        for (int i = 0; i < modeloTablaItems.getRowCount(); i++) {
            try {
                String codigo = (String) modeloTablaItems.getValueAt(i, 0);
                String descripcion = (String) modeloTablaItems.getValueAt(i, 1);
                BigDecimal cantidad = new BigDecimal(modeloTablaItems.getValueAt(i, 2).toString().replace(",", "."));
                String unidad = (String) modeloTablaItems.getValueAt(i, 3);
                BigDecimal precio = new BigDecimal(modeloTablaItems.getValueAt(i, 4).toString().replace(",", "."));
                BigDecimal subtotal = cantidad.multiply(precio).setScale(2, BigDecimal.ROUND_HALF_UP);
                lista.add(new ItemFacturaDTO(codigo, descripcion, cantidad, unidad, precio, subtotal));
            } catch (Exception e) {
                System.err.println("Error leyendo item fila " + i + ": " + e.getMessage());
            }
        }
        return lista;
    }

    private String obtenerDescripcionItems() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < modeloTablaItems.getRowCount(); i++) {
            if (i > 0) sb.append(" | ");
            sb.append(modeloTablaItems.getValueAt(i, 1));
        }
        return sb.toString();
    }

    private CuitConfigDTO obtenerCuitSeleccionado() {
        List<CuitConfigDTO> cuits = controlador.getCuitsActivos();
        if (cuits.isEmpty()) return null;
        return cuits.get(0);
    }

    private String obtenerCondicionesVenta() {
        StringBuilder sb = new StringBuilder();
        JCheckBox[] checks = {chkContado, chkTarjetaDeb, chkTarjetaCred, chkCC, chkCheque, chkTransf, chkOtra};
        String[] labels = {"Contado", "Tarjeta Debito", "Tarjeta Credito", "Cuenta Corriente", "Cheque", "Transf. Bancaria", "Otra"};
        for (int i = 0; i < checks.length; i++) {
            if (checks[i].isSelected()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(labels[i]);
            }
        }
        return sb.toString();
    }

    private void cargarCuits() {
    }

    private void limpiarTodo() {
        modeloTablaItems.setRowCount(0);
        txtImporteNeto.setText("");
        txtImporteIva.setText("");
        txtImporteTotal.setText("");
        lblTotal.setText("$ 0.00");
        cardLayout.show(panelPrincipal, "datos");
    }

    private void seleccionarFecha(JTextField campo) {
        String input = JOptionPane.showInputDialog(this, "Fecha (dd/MM/yyyy):", campo.getText());
        if (input != null) {
            try {
                LocalDate.parse(input, FMT);
                campo.setText(input);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Fecha invalida. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private LocalDate parseFecha(String str) {
        try {
            return LocalDate.parse(str, FMT);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton crearBotonDate() {
        JButton btn = new JButton("...");
        btn.setFont(new Font("Cambria", Font.BOLD, 10));
        btn.setMargin(new Insets(0, 4, 0, 4));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
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

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaFacturacion().setVisible(true));
    }
}
