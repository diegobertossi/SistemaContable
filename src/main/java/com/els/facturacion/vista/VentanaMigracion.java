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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

public class VentanaMigracion extends JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_LABEL = new Font("Cambria", Font.BOLD, 12);

    private MigracionExcelController migracionController;
    private JTextField txtRutaArchivo;
    private JComboBox<String> cmbTipoMigracion;
    private JComboBox<String> cmbHoja;
    private JLabel lblEstado;

    private String rutaCarpetaExcel = "F:\\Users\\Diego\\git\\SistemaGestion\\ReparsoftCliente\\Excels";

    public VentanaMigracion() {
        migracionController = new MigracionExcelController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Migracion de Datos desde Excel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panelSuperior = new JPanel();
        panelSuperior.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("MIGRACION DE DATOS DESDE EXCEL", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXTO);
        lblTitulo.setBackground(COLOR_FONDO);

        panelSuperior.add(lblTitulo);

        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTipo = new JLabel("Tipo de Migracion:");
        lblTipo.setFont(FUENTE_LABEL);
        lblTipo.setForeground(COLOR_TEXTO);

        cmbTipoMigracion = new JComboBox<>(new String[]{
            "Caja BRC",
            "Gastos 2026",
            "Ordenes 2026 (Costos Fijos)",
            "Ordenes 2026 (Resumen)"
        });
        cmbTipoMigracion.setFont(FUENTE_BOTON);
        cmbTipoMigracion.setPreferredSize(new java.awt.Dimension(250, 25));

        JLabel lblArchivo = new JLabel("Archivo Excel:");
        lblArchivo.setFont(FUENTE_LABEL);
        lblArchivo.setForeground(COLOR_TEXTO);

        txtRutaArchivo = new JTextField(30);
        txtRutaArchivo.setFont(new Font("Cambria", Font.PLAIN, 11));
        txtRutaArchivo.setEditable(false);

        JButton btnSeleccionar = crearBoton("SELECCIONAR");
        btnSeleccionar.addActionListener(e -> btnSeleccionarAction());

        JLabel lblHoja = new JLabel("Hoja a Migrar:");
        lblHoja.setFont(FUENTE_LABEL);
        lblHoja.setForeground(COLOR_TEXTO);

        cmbHoja = new JComboBox<>(new String[]{"Hoja1", "Hoja2", "Costos Fijos", "Resumen"});
        cmbHoja.setFont(FUENTE_BOTON);
        cmbHoja.setPreferredSize(new java.awt.Dimension(150, 25));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        panelCentro.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panelCentro.add(lblTipo, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panelCentro.add(cmbTipoMigracion, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panelCentro.add(lblArchivo, gbc);
        gbc.gridx = 1;
        panelCentro.add(txtRutaArchivo, gbc);
        gbc.gridx = 2;
        panelCentro.add(btnSeleccionar, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelCentro.add(lblHoja, gbc);
        gbc.gridx = 1;
        panelCentro.add(cmbHoja, gbc);

        lblEstado = new JLabel("Seleccione un archivo y tipo de migracion", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Cambria", Font.PLAIN, 11));
        lblEstado.setForeground(COLOR_TEXTO);
        lblEstado.setBackground(COLOR_FONDO);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        panelCentro.add(lblEstado, gbc);

        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(COLOR_FONDO);

        JButton btnMigrar = crearBoton("MIGRAR");
        btnMigrar.addActionListener(e -> btnMigrarAction());

        JButton btnLimpiar = crearBoton("LIMPIAR");
        btnLimpiar.addActionListener(e -> {
            txtRutaArchivo.setText("");
            lblEstado.setText("Seleccione un archivo y tipo de migracion");
        });

        JButton btnVerificar = crearBoton("VERIFICAR");
        btnVerificar.addActionListener(e -> btnVerificarAction());

        JButton btnNuevoAnio = crearBoton("NUEVO AÑO");
        btnNuevoAnio.addActionListener(e -> btnNuevoAnioAction());

        JButton btnAgregarColumna = crearBoton("AGREGAR COLUMNA");
        btnAgregarColumna.addActionListener(e -> btnAgregarColumnaAction());

        JButton btnCerrar = crearBoton("CERRAR");
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

    private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
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
        lblEstado.setForeground(COLOR_TEXTO);

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