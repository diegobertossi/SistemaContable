package com.els.facturacion.vista;

import com.els.facturacion.controlador.ControladorFacturacion;
import com.els.facturacion.modelo.ComprobanteDTO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaComprobantes extends javax.swing.JFrame {

    private ControladorFacturacion controlador;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscarCAE;
    private DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VentanaComprobantes() {
        controlador = new ControladorFacturacion();
        initComponents();
        cargarComprobantes();
    }

    private void initComponents() {
        setTitle("Historial de Comprobantes");
        setSize(950, 520);
        setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(TemaFacturaSoft.BG_APP);

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(TemaFacturaSoft.BG_APP);

        JLabel lblTitulo = new JLabel("HISTORIAL DE COMPROBANTES", SwingConstants.CENTER);
        TemaFacturaSoft.aplicarEstilo(lblTitulo, 18, true);

        GridBagConstraints gbc_titulo = new GridBagConstraints();
        gbc_titulo.insets = new Insets(5, 5, 5, 5);
        gbc_titulo.fill = GridBagConstraints.HORIZONTAL;
        gbc_titulo.gridx = 0; gbc_titulo.gridy = 0; gbc_titulo.gridwidth = 5;
        panelSuperior.add(lblTitulo, gbc_titulo);

        JLabel lblBuscar = new JLabel("Buscar por CAE:");
        TemaFacturaSoft.aplicarEstiloLabel(lblBuscar, "Buscar por CAE:");

        txtBuscarCAE = new JTextField(20);
        TemaFacturaSoft.aplicarEstiloInput(txtBuscarCAE);

        JButton btnBuscar = new JButton("BUSCAR");
        btnBuscar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnBuscar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnBuscar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> btnBuscarAction());

        JButton btnActualizar = new JButton("ACTUALIZAR");
        btnActualizar.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnActualizar.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnActualizar.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnActualizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarComprobantes());

        JButton btnVerPDF = new JButton("VER PDF");
        btnVerPDF.setFont(TemaFacturaSoft.FONT_UI_BOLD.deriveFont(11f));
        btnVerPDF.setForeground(TemaFacturaSoft.TEXT_PRIMARY);
        btnVerPDF.setBackground(TemaFacturaSoft.BG_SURFACE);
        btnVerPDF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVerPDF.setFocusPainted(false);
        btnVerPDF.addActionListener(e -> btnVerPDFAction());

        GridBagConstraints gbc_lblBuscar = new GridBagConstraints();
        gbc_lblBuscar.insets = new Insets(5, 5, 5, 5);
        gbc_lblBuscar.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblBuscar.gridwidth = 1;
        gbc_lblBuscar.gridx = 0; gbc_lblBuscar.gridy = 1;
        panelSuperior.add(lblBuscar, gbc_lblBuscar);

        GridBagConstraints gbc_txtBuscarCAE = new GridBagConstraints();
        gbc_txtBuscarCAE.insets = new Insets(5, 5, 5, 5);
        gbc_txtBuscarCAE.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtBuscarCAE.gridwidth = 1;
        gbc_txtBuscarCAE.gridx = 1; gbc_txtBuscarCAE.gridy = 1;
        panelSuperior.add(txtBuscarCAE, gbc_txtBuscarCAE);

        GridBagConstraints gbc_btnBuscar = new GridBagConstraints();
        gbc_btnBuscar.insets = new Insets(5, 5, 5, 5);
        gbc_btnBuscar.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnBuscar.gridwidth = 1;
        gbc_btnBuscar.gridx = 2; gbc_btnBuscar.gridy = 1;
        panelSuperior.add(btnBuscar, gbc_btnBuscar);

        GridBagConstraints gbc_btnActualizar = new GridBagConstraints();
        gbc_btnActualizar.insets = new Insets(5, 5, 5, 5);
        gbc_btnActualizar.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnActualizar.gridwidth = 1;
        gbc_btnActualizar.gridx = 3; gbc_btnActualizar.gridy = 1;
        panelSuperior.add(btnActualizar, gbc_btnActualizar);

        GridBagConstraints gbc_btnVerPDF = new GridBagConstraints();
        gbc_btnVerPDF.insets = new Insets(5, 5, 5, 5);
        gbc_btnVerPDF.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnVerPDF.gridwidth = 1;
        gbc_btnVerPDF.gridx = 4; gbc_btnVerPDF.gridy = 1;
        panelSuperior.add(btnVerPDF, gbc_btnVerPDF);

        String[] columnas = {"ID", "Tipo", "PtoVta", "Numero", "Fecha", "CUIT Receptor", "Total", "Estado Pago", "CAE", "Vto CAE", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        TemaFacturaSoft.aplicarEstiloTabla(tabla);
        JScrollPane scrollPane = new JScrollPane(tabla);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarComprobantes() {
        modeloTabla.setRowCount(0);
        List<ComprobanteDTO> lista = controlador.listarComprobantes();
        for (ComprobanteDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                dto.getId(),
                dto.getTipoComprobanteStr(),
                dto.getPuntoVenta(),
                dto.getNumero(),
                dto.getFechaEmision() != null ? dto.getFechaEmision().format(fechaFormatter) : "",
                dto.getCuitReceptor(),
                dto.getImporteTotal() != null ? dto.getImporteTotal().toString() : "",
                obtenerEstadoPagoDisplay(dto.getEstadoPago()),
                dto.getCae(),
                dto.getVencimientoCae() != null ? dto.getVencimientoCae().format(fechaFormatter) : "",
                dto.getEmailEnviado() ? "Si" : "No"
            });
        }
    }

    private String obtenerEstadoPagoDisplay(String estado) {
        if (estado == null) return "Pendiente";
        switch (estado) {
            case "pendiente": return "Pendiente";
            case "pagada_parcial": return "Pagada Parcial";
            case "pagada_total": return "Pagada Total";
            case "anulada": return "Anulada";
            default: return estado;
        }
    }

    private void btnBuscarAction() {
        String cae = txtBuscarCAE.getText().trim();
        if (cae.isEmpty()) {
            cargarComprobantes();
            return;
        }

        modeloTabla.setRowCount(0);
        ComprobanteDTO comp = controlador.buscarComprobante(cae);
        if (comp != null) {
            modeloTabla.addRow(new Object[]{
                comp.getId(),
                comp.getTipoComprobanteStr(),
                comp.getPuntoVenta(),
                comp.getNumero(),
                comp.getFechaEmision() != null ? comp.getFechaEmision().format(fechaFormatter) : "",
                comp.getCuitReceptor(),
                comp.getImporteTotal() != null ? comp.getImporteTotal().toString() : "",
                obtenerEstadoPagoDisplay(comp.getEstadoPago()),
                comp.getCae(),
                comp.getVencimientoCae() != null ? comp.getVencimientoCae().format(fechaFormatter) : "",
                comp.getEmailEnviado() ? "Si" : "No"
            });
        } else {
            JOptionPane.showMessageDialog(this, "No se encontro comprobante con ese CAE", "Buscar", JOptionPane.INFORMATION_MESSAGE);
            cargarComprobantes();
        }
    }

    private void btnVerPDFAction() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un comprobante", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloTabla.getValueAt(selectedRow, 0);
        ComprobanteDTO comp = controlador.buscarComprobante(id);

        if (comp == null) {
            JOptionPane.showMessageDialog(this, "Error al cargar comprobante", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (comp.getRutaPdf() == null || comp.getRutaPdf().isEmpty()) {
            int opcion = JOptionPane.showConfirmDialog(this,
                "El comprobante no tiene PDF generado. Desea generarlo ahora?",
                "Generar PDF", JOptionPane.YES_NO_OPTION);

            if (opcion == JOptionPane.YES_OPTION) {
                String ruta = controlador.regenerarPDF(comp);
                if (ruta != null) {
                    JOptionPane.showMessageDialog(this, "PDF generado: " + ruta, "Exito", JOptionPane.INFORMATION_MESSAGE);
                    abrirPDF(ruta);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al generar PDF", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            File pdfFile = new File(comp.getRutaPdf());
            if (pdfFile.exists()) {
                abrirPDF(comp.getRutaPdf());
            } else {
                int opcion = JOptionPane.showConfirmDialog(this,
                    "El archivo PDF no existe en la ruta indicada. Desea regenerarlo?",
                    "PDF no encontrado", JOptionPane.YES_NO_OPTION);
                if (opcion == JOptionPane.YES_OPTION) {
                    String ruta = controlador.regenerarPDF(comp);
                    if (ruta != null) {
                        JOptionPane.showMessageDialog(this, "PDF generado: " + ruta, "Exito", JOptionPane.INFORMATION_MESSAGE);
                        abrirPDF(ruta);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al generar PDF", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void abrirPDF(String ruta) {
        try {
            File pdfFile = new File(ruta);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al abrir PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new VentanaComprobantes().setVisible(true));
    }
}
