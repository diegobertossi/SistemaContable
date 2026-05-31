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
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

public class VentanaMigracion extends JFrame {

    private MigracionExcelController migracionController;
    private JTextField txtRutaArchivo;
    private JComboBox<String> cmbTipoMigracion;
    private JComboBox<String> cmbHoja;
    private JLabel lblEstado;

    private String rutaCarpetaExcel = "F:\\Users\\Diego\\git\\SistemaGestion\\ReparsoftCliente\\Excels";

    public VentanaMigracion() {
        migracionController = new MigracionExcelController();
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);
        initComponents();
    }

    private void initComponents() {
        setTitle("Migracion de Datos desde Excel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(TemaFacturaSoft.BG_APP);

        JLabel lblTitulo = new JLabel("MIGRACION DE DATOS DESDE EXCEL", SwingConstants.CENTER);
        lblTitulo.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(18f));
        lblTitulo.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        lblTitulo.setBackground(TemaFacturaSoft.BG_APP);

        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setBackground(TemaFacturaSoft.BG_APP);
        JLabel lblTipo = new JLabel("Tipo de Migracion:");
        lblTipo.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(12f));
        lblTipo.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        cmbTipoMigracion = new JComboBox<>(new String[]{
            "Caja BRC",
            "Gastos 2026",
            "Ordenes 2026 (Costos Fijos)",
            "Ordenes 2026 (Resumen)"
        });
        TemaFacturaSoft.aplicarEstiloCombo(cmbTipoMigracion, 11);
        cmbTipoMigracion.setPreferredSize(new java.awt.Dimension(250, 25));

        JLabel lblArchivo = new JLabel("Archivo Excel:");
        lblArchivo.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(12f));
        lblArchivo.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        txtRutaArchivo = new JTextField(30);
        txtRutaArchivo.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));
        txtRutaArchivo.setEditable(false);

        JButton btnSeleccionar = new JButton("SELECCIONAR");
        btnSeleccionar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnSeleccionar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnSeleccionar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnSeleccionar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSeleccionar.setFocusPainted(false);
        btnSeleccionar.addActionListener(e -> btnSeleccionarAction());

        JLabel lblHoja = new JLabel("Hoja a Migrar:");
        lblHoja.setFont(TemaFacturaSoft.FONT_UI_SEMIBOLD.deriveFont(12f));
        lblHoja.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

        cmbHoja = new JComboBox<>(new String[]{"Hoja1", "Hoja2", "Costos Fijos", "Resumen"});
        TemaFacturaSoft.aplicarEstiloCombo(cmbHoja, 11);
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
        lblEstado.setFont(TemaFacturaSoft.FONT_UI.deriveFont(11f));
        lblEstado.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        lblEstado.setBackground(TemaFacturaSoft.BG_APP);

        GridBagConstraints gbc_estado = new GridBagConstraints();
        gbc_estado.insets = new Insets(10, 10, 10, 10);
        gbc_estado.fill = GridBagConstraints.HORIZONTAL;
        gbc_estado.gridx = 0; gbc_estado.gridy = 4; gbc_estado.gridwidth = 3;
        panelCentro.add(lblEstado, gbc_estado);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(TemaFacturaSoft.BG_APP);

        JButton btnMigrar = new JButton("MIGRAR");
        btnMigrar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnMigrar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnMigrar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnMigrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnMigrar.setFocusPainted(false);
        btnMigrar.addActionListener(e -> btnMigrarAction());

        JButton btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnLimpiar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnLimpiar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnLimpiar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(e -> {
            txtRutaArchivo.setText("");
            lblEstado.setText("Seleccione un archivo y tipo de migracion");
        });

        JButton btnVerificar = new JButton("VERIFICAR");
        btnVerificar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnVerificar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnVerificar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnVerificar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerificar.setFocusPainted(false);
        btnVerificar.addActionListener(e -> btnVerificarAction());

        JButton btnNuevoAnio = new JButton("NUEVO AÑO");
        btnNuevoAnio.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnNuevoAnio.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnNuevoAnio.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnNuevoAnio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevoAnio.setFocusPainted(false);
        btnNuevoAnio.addActionListener(e -> btnNuevoAnioAction());

        JButton btnAgregarColumna = new JButton("AGREGAR COLUMNA");
        btnAgregarColumna.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnAgregarColumna.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnAgregarColumna.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnAgregarColumna.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAgregarColumna.setFocusPainted(false);
        btnAgregarColumna.addActionListener(e -> btnAgregarColumnaAction());

        JButton btnCerrar = new JButton("CERRAR");
        btnCerrar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnCerrar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnCerrar.setBackground(TemaFacturaSoft.BG_SURFACE);
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
        add(panelBotones, BorderLayout.SOUTH);

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
        lblEstado.setForeground(TemaFacturaSoft.TEXT_PRIMARY);

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

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaMigracion().setVisible(true));
    }
}
