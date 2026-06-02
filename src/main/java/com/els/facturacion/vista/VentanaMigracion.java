package com.els.facturacion.vista;

import com.els.facturacion.controlador.MigracionExcelController;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

public class VentanaMigracion extends JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.BOLD, 12);

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private MigracionExcelController migracionController;
    private JTextField txtRutaArchivo;
    private JComboBox<String> cmbTipoMigracion;
    private JComboBox<String> cmbHoja;
    private JLabel lblEstado;

    private String rutaCarpetaExcel = "F:\\Users\\Diego\\git\\SistemaGestion\\ReparsoftCliente\\Excels";

    private JPanel panelSuperior;
    private JPanel panelCentro;
    private JPanel panelBotones;
    private JLabel lblTitulo;
    private JLabel lblTipo;
    private JLabel lblArchivo;
    private JLabel lblHoja;
    private JButton btnSeleccionar;
    private JButton btnMigrar;
    private JButton btnLimpiar;
    private JButton btnVerificar;
    private JButton btnNuevoAnio;
    private JButton btnAgregarColumna;
    private JButton btnCerrar;
    private JPanel statusBar;
    private JLabel lblStatus;

    public VentanaMigracion() {
        migracionController = new MigracionExcelController();
        initComponents();
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setTitle("Migracion de Datos desde Excel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        panelSuperior = new JPanel();
        panelSuperior.setBackground(currentTheme.bgBase);

        lblTitulo = new JLabel("MIGRACION DE DATOS DESDE EXCEL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(currentTheme.textPrimary);
        lblTitulo.setBackground(currentTheme.bgBase);

        panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setBackground(currentTheme.bgBase);
        lblTipo = new JLabel("Tipo de Migracion:");
        lblTipo.setFont(FUENTE_LABEL);
        lblTipo.setForeground(currentTheme.textPrimary);

        cmbTipoMigracion = new JComboBox<>(new String[]{
            "Caja BRC",
            "Gastos 2026",
            "Ordenes 2026 (Costos Fijos)",
            "Ordenes 2026 (Resumen)"
        });
        cmbTipoMigracion.setFont(FUENTE_BOTON);
        cmbTipoMigracion.setPreferredSize(new java.awt.Dimension(250, 25));

        lblArchivo = new JLabel("Archivo Excel:");
        lblArchivo.setFont(FUENTE_LABEL);
        lblArchivo.setForeground(currentTheme.textPrimary);

        txtRutaArchivo = new JTextField(30);
        txtRutaArchivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtRutaArchivo.setEditable(false);

        btnSeleccionar = new JButton("SELECCIONAR");
        btnSeleccionar.setFont(FUENTE_BOTON);
        btnSeleccionar.setForeground(currentTheme.textPrimary);
        btnSeleccionar.setBackground(currentTheme.btnBg);
        btnSeleccionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.addActionListener(e -> btnSeleccionarAction());

        lblHoja = new JLabel("Hoja a Migrar:");
        lblHoja.setFont(FUENTE_LABEL);
        lblHoja.setForeground(currentTheme.textPrimary);

        cmbHoja = new JComboBox<>(new String[]{"Hoja1", "Hoja2", "Costos Fijos", "Resumen"});
        cmbHoja.setFont(FUENTE_BOTON);
        cmbHoja.setPreferredSize(new java.awt.Dimension(150, 25));

        GridBagConstraints gbc_titulo = new GridBagConstraints();
        gbc_titulo.insets = new Insets(10, 10, 10, 10);
        gbc_titulo.fill = GridBagConstraints.HORIZONTAL;
        gbc_titulo.gridx = 0; gbc_titulo.gridy = 0; gbc_titulo.gridwidth = 3;
        panelCentro.add(lblTitulo, gbc_titulo);

        GridBagConstraints gbc_tipo = new GridBagConstraints();
        gbc_tipo.insets = new Insets(10, 10, 10, 10);
        gbc_tipo.fill = GridBagConstraints.HORIZONTAL;
        gbc_tipo.gridwidth = 1;
        gbc_tipo.gridx = 0; gbc_tipo.gridy = 1;
        panelCentro.add(lblTipo, gbc_tipo);

        GridBagConstraints gbc_cmbTipo = new GridBagConstraints();
        gbc_cmbTipo.insets = new Insets(10, 10, 10, 10);
        gbc_cmbTipo.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbTipo.gridx = 1; gbc_cmbTipo.gridwidth = 2;
        panelCentro.add(cmbTipoMigracion, gbc_cmbTipo);

        GridBagConstraints gbc_archivo = new GridBagConstraints();
        gbc_archivo.insets = new Insets(10, 10, 10, 10);
        gbc_archivo.fill = GridBagConstraints.HORIZONTAL;
        gbc_archivo.gridwidth = 1;
        gbc_archivo.gridx = 0; gbc_archivo.gridy = 2;
        panelCentro.add(lblArchivo, gbc_archivo);

        GridBagConstraints gbc_txtRuta = new GridBagConstraints();
        gbc_txtRuta.insets = new Insets(10, 10, 10, 10);
        gbc_txtRuta.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtRuta.gridx = 1;
        panelCentro.add(txtRutaArchivo, gbc_txtRuta);

        GridBagConstraints gbc_btnSeleccionar = new GridBagConstraints();
        gbc_btnSeleccionar.insets = new Insets(10, 10, 10, 10);
        gbc_btnSeleccionar.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSeleccionar.gridx = 2;
        panelCentro.add(btnSeleccionar, gbc_btnSeleccionar);

        GridBagConstraints gbc_hoja = new GridBagConstraints();
        gbc_hoja.insets = new Insets(10, 10, 10, 10);
        gbc_hoja.fill = GridBagConstraints.HORIZONTAL;
        gbc_hoja.gridx = 0; gbc_hoja.gridy = 3;
        panelCentro.add(lblHoja, gbc_hoja);

        GridBagConstraints gbc_cmbHoja = new GridBagConstraints();
        gbc_cmbHoja.insets = new Insets(10, 10, 10, 10);
        gbc_cmbHoja.fill = GridBagConstraints.HORIZONTAL;
        gbc_cmbHoja.gridx = 1;
        panelCentro.add(cmbHoja, gbc_cmbHoja);

        lblEstado = new JLabel("Seleccione un archivo y tipo de migracion", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEstado.setForeground(currentTheme.textPrimary);
        lblEstado.setBackground(currentTheme.bgBase);

        GridBagConstraints gbc_estado = new GridBagConstraints();
        gbc_estado.insets = new Insets(10, 10, 10, 10);
        gbc_estado.fill = GridBagConstraints.HORIZONTAL;
        gbc_estado.gridx = 0; gbc_estado.gridy = 4; gbc_estado.gridwidth = 3;
        panelCentro.add(lblEstado, gbc_estado);

        panelBotones = new JPanel();
        panelBotones.setBackground(currentTheme.bgBase);

        btnMigrar = new JButton("MIGRAR");
        btnMigrar.setFont(FUENTE_BOTON);
        btnMigrar.setForeground(currentTheme.textPrimary);
        btnMigrar.setBackground(currentTheme.btnBg);
        btnMigrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMigrar.setFocusPainted(false);
        btnMigrar.addActionListener(e -> btnMigrarAction());

        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setFont(FUENTE_BOTON);
        btnLimpiar.setForeground(currentTheme.textPrimary);
        btnLimpiar.setBackground(currentTheme.btnBg);
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> {
            txtRutaArchivo.setText("");
            lblEstado.setText("Seleccione un archivo y tipo de migracion");
        });

        btnVerificar = new JButton("VERIFICAR");
        btnVerificar.setFont(FUENTE_BOTON);
        btnVerificar.setForeground(currentTheme.textPrimary);
        btnVerificar.setBackground(currentTheme.btnBg);
        btnVerificar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerificar.setFocusPainted(false);
        btnVerificar.addActionListener(e -> btnVerificarAction());

        btnNuevoAnio = new JButton("NUEVO AÑO");
        btnNuevoAnio.setFont(FUENTE_BOTON);
        btnNuevoAnio.setForeground(currentTheme.textPrimary);
        btnNuevoAnio.setBackground(currentTheme.btnBg);
        btnNuevoAnio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevoAnio.setFocusPainted(false);
        btnNuevoAnio.addActionListener(e -> btnNuevoAnioAction());

        btnAgregarColumna = new JButton("AGREGAR COLUMNA");
        btnAgregarColumna.setFont(FUENTE_BOTON);
        btnAgregarColumna.setForeground(currentTheme.textPrimary);
        btnAgregarColumna.setBackground(currentTheme.btnBg);
        btnAgregarColumna.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregarColumna.setFocusPainted(false);
        btnAgregarColumna.addActionListener(e -> btnAgregarColumnaAction());

        btnCerrar = new JButton("CERRAR");
        btnCerrar.setFont(FUENTE_BOTON);
        btnCerrar.setForeground(currentTheme.textPrimary);
        btnCerrar.setBackground(currentTheme.btnBg);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnMigrar);
        panelBotones.add(btnVerificar);
        panelBotones.add(btnNuevoAnio);
        panelBotones.add(btnAgregarColumna);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCerrar);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setBackground(currentTheme.bgBase);
        southWrapper.add(panelBotones, BorderLayout.CENTER);
        boolean barIsLight = currentTheme.bgBase.getRed() > 128;
        statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        statusBar.setBackground(barIsLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        lblStatus = new JLabel("  FacturaSoft v1.0  |  Sistema de Facturaci\u00f3n Electr\u00f3nica");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(barIsLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        statusBar.add(lblStatus);
        southWrapper.add(statusBar, BorderLayout.SOUTH);
        add(southWrapper, BorderLayout.SOUTH);

        cmbTipoMigracion.addActionListener(e -> actualizarHojasDisponibles());
    }

    private void actualizarHojasDisponibles() {
        String tipo = (String) cmbTipoMigracion.getSelectedItem();
        cmbHoja.removeAllItems();

        switch (tipo) {
            case "Caja BRC":
                cmbHoja.addItem("2026");
                txtRutaArchivo.setText(rutaCarpetaExcel + "\\Caja BRC.xlsx");
                break;
            case "Gastos 2026":
                cmbHoja.addItem("Hoja1");
                txtRutaArchivo.setText(rutaCarpetaExcel + "\\Detalle gastos 2026.xls");
                break;
            case "Ordenes 2026 (Costos Fijos)":
                cmbHoja.addItem("Costos Fijos");
                txtRutaArchivo.setText(rutaCarpetaExcel + "\\ReparBRC_Mysql.xlsx");
                break;
            case "Ordenes 2026 (Resumen)":
                cmbHoja.addItem("Resumen");
                txtRutaArchivo.setText(rutaCarpetaExcel + "\\ReparBRC_Mysql.xlsx");
                break;
        }
    }

    private void btnSeleccionarAction() {
        JFileChooser fileChooser = new JFileChooser(new File(rutaCarpetaExcel));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            txtRutaArchivo.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void btnVerificarAction() {
        String ruta = txtRutaArchivo.getText().trim();
        if (ruta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File archivo = new File(ruta);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(this, "El archivo no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String resultado = migracionController.getCabecerasExcel(ruta);
            JOptionPane.showMessageDialog(this, resultado, "Cabeceras del Excel", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void btnNuevoAnioAction() {
        String[] opciones = {"2027", "2028", "2029", "2030"};
        String nuevoAnio = (String) JOptionPane.showInputDialog(
            this,
            "Seleccione el nuevo año a crear:",
            "Nuevo Año",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (nuevoAnio != null) {
            cmbHoja.addItem(nuevoAnio);
            cmbHoja.setSelectedItem(nuevoAnio);
            JOptionPane.showMessageDialog(this, "Año " + nuevoAnio + " agregado. Configure la migración para este año.", "Nuevo Año", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void btnAgregarColumnaAction() {
        JOptionPane.showMessageDialog(this, "Función para agregar columnas del año en el Excel.\nEsta función permitirá crear una nueva hoja con los datos del año seleccionado.", "Agregar Columna", JOptionPane.INFORMATION_MESSAGE);
    }

    private void btnMigrarAction() {
        String ruta = txtRutaArchivo.getText().trim();
        if (ruta.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File archivo = new File(ruta);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(this, "El archivo no existe", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo = (String) cmbTipoMigracion.getSelectedItem();
        lblEstado.setText("Migrando...");
        lblEstado.setForeground(currentTheme.textPrimary);

        try {
            int count = 0;
            String mensaje = "";

            String anioSeleccionado = (String) cmbHoja.getSelectedItem();

            switch (tipo) {
                case "Caja BRC":
                    count = migracionController.migrarCajaBRC(ruta, anioSeleccionado);
                    mensaje = "movimientos de caja " + anioSeleccionado;
                    break;
                case "Gastos 2026":
                    count = migracionController.migrarGastos(ruta);
                    mensaje = "gastos";
                    break;
                case "Ordenes 2026 (Costos Fijos)":
                    lblEstado.setText("Migracion de Ordenes en desarrollo...");
                    JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    return;
                case "Ordenes 2026 (Resumen)":
                    lblEstado.setText("Migracion de Ordenes en desarrollo...");
                    JOptionPane.showMessageDialog(this, "Funcionalidad en desarrollo", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                    return;
            }

            lblEstado.setText("Se migraron " + count + " " + mensaje);
            JOptionPane.showMessageDialog(this, "Migracion completada: " + count + " registros", "Exito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            lblEstado.setText("Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error en migracion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void applyTheme(Theme t) {
        currentTheme = t;
        if (t == null) return;
        getContentPane().setBackground(t.bgBase);
        if (panelSuperior != null) panelSuperior.setBackground(t.bgBase);
        if (panelCentro != null) panelCentro.setBackground(t.bgBase);
        if (panelBotones != null) panelBotones.setBackground(t.bgBase);
        if (statusBar != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            statusBar.setBackground(isLight ? new Color(200, 208, 225) : new Color(50, 58, 80));
        }
        if (lblStatus != null) {
            boolean isLight = t.bgBase.getRed() > 128;
            lblStatus.setForeground(isLight ? new Color(80, 90, 110) : new Color(160, 175, 200));
        }
        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (lblTipo != null) lblTipo.setForeground(t.textPrimary);
        if (lblArchivo != null) lblArchivo.setForeground(t.textPrimary);
        if (lblHoja != null) lblHoja.setForeground(t.textPrimary);
        if (lblEstado != null) lblEstado.setForeground(t.textPrimary);
        if (btnSeleccionar != null) {
            btnSeleccionar.setBackground(t.btnBg);
            btnSeleccionar.setForeground(t.textPrimary);
        }
        if (btnMigrar != null) {
            btnMigrar.setBackground(t.btnBg);
            btnMigrar.setForeground(t.textPrimary);
        }
        if (btnLimpiar != null) {
            btnLimpiar.setBackground(t.btnBg);
            btnLimpiar.setForeground(t.textPrimary);
        }
        if (btnVerificar != null) {
            btnVerificar.setBackground(t.btnBg);
            btnVerificar.setForeground(t.textPrimary);
        }
        if (btnNuevoAnio != null) {
            btnNuevoAnio.setBackground(t.btnBg);
            btnNuevoAnio.setForeground(t.textPrimary);
        }
        if (btnAgregarColumna != null) {
            btnAgregarColumna.setBackground(t.btnBg);
            btnAgregarColumna.setForeground(t.textPrimary);
        }
        if (btnCerrar != null) {
            btnCerrar.setBackground(t.btnBg);
            btnCerrar.setForeground(t.textPrimary);
        }
        if (txtRutaArchivo != null) {
            txtRutaArchivo.setForeground(t.textPrimary);
            txtRutaArchivo.setBackground(t.bgInput);
        }
        if (cmbTipoMigracion != null) {
            cmbTipoMigracion.setForeground(t.textPrimary);
            cmbTipoMigracion.setBackground(t.bgElevated);
        }
        if (cmbHoja != null) {
            cmbHoja.setForeground(t.textPrimary);
            cmbHoja.setBackground(t.bgElevated);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaMigracion().setVisible(true));
    }
}
