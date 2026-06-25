package com.els.facturacion.vista;

import com.els.facturacion.util.UbicacionSistema;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VentanaBackup extends JFrame {

    private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FUENTE_STATUS = new Font("Segoe UI", Font.PLAIN, 11);

    private Theme currentTheme = VentanaPrincipal.getCurrentTheme();

    private JButton btnGenerar;
    private JButton btnImportar;
    private JLabel lblStatus;
    private JLabel lblTitulo;
    private JPanel panelBotones;
    private JPanel wrapperPanel;

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String USER = "root";
    private static final String PASS = "root";

    public VentanaBackup() {
        setTitle("Backup - FacturaSoft");
        setSize(420, 260);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(currentTheme.bgBase);

        initComponents();
        applyTheme(currentTheme);
        VentanaPrincipal.addThemeListener(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));

        lblTitulo = new JLabel("BACKUP DE BASE DE DATOS", SwingConstants.CENTER);
        lblTitulo.setFont(FUENTE_TITULO);
        lblTitulo.setForeground(currentTheme.brand);

        panelBotones = new JPanel(new GridBagLayout());
        panelBotones.setBackground(currentTheme.bgBase);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 30, 6, 30);

        btnGenerar = new JButton("GENERAR BACKUP");
        btnGenerar.setFont(FUENTE_BOTON);
        btnGenerar.setFocusPainted(false);
        btnGenerar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnGenerar.addActionListener(e -> generarBackup());

        g.gridx = 0; g.gridy = 0;
        panelBotones.add(btnGenerar, g);

        btnImportar = new JButton("IMPORTAR BACKUP");
        btnImportar.setFont(FUENTE_BOTON);
        btnImportar.setFocusPainted(false);
        btnImportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImportar.addActionListener(e -> importarBackup());

        g.gridx = 0; g.gridy = 1;
        panelBotones.add(btnImportar, g);

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(FUENTE_STATUS);

        wrapperPanel = new JPanel(new BorderLayout(0, 8));
        wrapperPanel.setBackground(currentTheme.bgBase);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(16, 10, 12, 10));
        wrapperPanel.add(lblTitulo, BorderLayout.NORTH);
        wrapperPanel.add(panelBotones, BorderLayout.CENTER);
        wrapperPanel.add(lblStatus, BorderLayout.SOUTH);

        add(wrapperPanel, BorderLayout.CENTER);
    }

    private String getDbName() {
        return UbicacionSistema.getNombreDbFacturacion();
    }

    private void setStatus(String text, Color color) {
        lblStatus.setText(text);
        lblStatus.setForeground(color != null ? color : currentTheme.textSecondary);
    }

    private void generarBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar backup");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setSelectedFile(new java.io.File( nombreBackup() ));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path destino = chooser.getSelectedFile().toPath();

        setStatus("Generando backup...", null);
        btnGenerar.setEnabled(false);
        btnImportar.setEnabled(false);

        new SwingWorker<Void, Void>() {
            private String error;

            @Override
            protected Void doInBackground() {
                try {
                    String db = getDbName();
                    ProcessBuilder pb = new ProcessBuilder(
                        "mysqldump",
                        "-h", HOST,
                        "-P", PORT,
                        "-u", USER,
                        "-p" + PASS,
                        db
                    );
                    pb.redirectErrorStream(false);

                    Process p = pb.start();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
                         BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }

                    // Leer stderr para errores
                    BufferedReader errReader = new BufferedReader(
                            new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder err = new StringBuilder();
                    String line;
                    while ((line = errReader.readLine()) != null) {
                        err.append(line).append("\n");
                    }

                    int exit = p.waitFor();
                    if (exit != 0) {
                        error = "mysqldump exit\u00f3 con c\u00f3digo " + exit + "\n" + err;
                    }
                } catch (Exception e) {
                    error = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                btnGenerar.setEnabled(true);
                btnImportar.setEnabled(true);
                if (error != null) {
                    setStatus("Error: " + error, currentTheme.danger);
                    JOptionPane.showMessageDialog(VentanaBackup.this,
                        "Error al generar backup:\n" + error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    setStatus("Backup guardado en " + destino, new Color(30, 150, 30));
                    JOptionPane.showMessageDialog(VentanaBackup.this,
                        "Backup generado correctamente:\n" + destino, "Exito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }.execute();
    }

    private void importarBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar archivo de backup");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos SQL (*.sql)", "sql"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        Path origen = chooser.getSelectedFile().toPath();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Se restaurar\u00e1n TODOS los datos desde:\n" + origen
            + "\n\n\u00bfContinuar?", "Confirmar importaci\u00f3n",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        setStatus("Importando backup...", null);
        btnGenerar.setEnabled(false);
        btnImportar.setEnabled(false);

        new SwingWorker<Void, Void>() {
            private String error;

            @Override
            protected Void doInBackground() {
                try {
                    String db = getDbName();

                    ProcessBuilder pb = new ProcessBuilder(
                        "mysql",
                        "-h", HOST,
                        "-P", PORT,
                        "-u", USER,
                        "-p" + PASS,
                        db
                    );
                    pb.redirectErrorStream(false);

                    Process p = pb.start();

                    try (BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(p.getOutputStream(), StandardCharsets.UTF_8));
                         BufferedReader reader = Files.newBufferedReader(origen, StandardCharsets.UTF_8)) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                            writer.newLine();
                        }
                        writer.flush();
                    }

                    // Leer stderr para errores
                    BufferedReader errReader = new BufferedReader(
                            new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8));
                    StringBuilder err = new StringBuilder();
                    String line;
                    while ((line = errReader.readLine()) != null) {
                        err.append(line).append("\n");
                    }

                    int exit = p.waitFor();
                    if (exit != 0) {
                        error = "mysql exit\u00f3 con c\u00f3digo " + exit + "\n" + err;
                    }
                } catch (Exception e) {
                    error = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                btnGenerar.setEnabled(true);
                btnImportar.setEnabled(true);
                if (error != null) {
                    setStatus("Error al importar", currentTheme.danger);
                    JOptionPane.showMessageDialog(VentanaBackup.this,
                        "Error al importar backup:\n" + error, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    setStatus("Datos restaurados correctamente", new Color(30, 150, 30));
                    JOptionPane.showMessageDialog(VentanaBackup.this,
                        "Backup importado correctamente.", "Exito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }.execute();
    }

    private String nombreBackup() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yy"));
        return "Backup Facturasoft " + fecha + ".sql";
    }

    @SuppressWarnings("unused")
    public void applyTheme(Theme t) {
        currentTheme = t;
        getContentPane().setBackground(t.bgBase);
        if (wrapperPanel != null) wrapperPanel.setBackground(t.bgBase);
        if (lblTitulo != null) lblTitulo.setForeground(t.brand);
        if (panelBotones != null) {
            panelBotones.setBackground(t.bgBase);
            for (java.awt.Component c : panelBotones.getComponents()) {
                if (c instanceof JButton) {
                    c.setForeground(t.textPrimary);
                    c.setBackground(t.btnBg);
                }
            }
        }
        if (lblStatus != null && lblStatus.getText() != null && !lblStatus.getText().trim().isEmpty()) {
            String txt = lblStatus.getText();
            if (txt.startsWith("Error")) lblStatus.setForeground(t.danger);
            else if (txt.contains("correctamente") || txt.contains("guardado")) lblStatus.setForeground(new Color(30, 150, 30));
            else lblStatus.setForeground(t.textSecondary);
        }
    }
}
