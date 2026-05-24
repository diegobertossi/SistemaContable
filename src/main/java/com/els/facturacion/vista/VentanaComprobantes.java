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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaComprobantes extends javax.swing.JFrame {

    private static final Color COLOR_FONDO = new Color(219, 227, 246);
    private static final Color COLOR_BOTON = new Color(176, 196, 222);
    private static final Color COLOR_TEXTO = new Color(0, 0, 128);
    private static final Color COLOR_TITULO = new Color(65, 105, 225);
    private static final Font FUENTE_BOTON = new Font("Cambria", Font.BOLD, 11);
    private static final Font FUENTE_TITULO = new Font("Cambria", Font.BOLD, 14);

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
        getContentPane().setBackground(COLOR_FONDO);

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBackground(COLOR_FONDO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("HISTORIAL DE COMPROBANTES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Cambria", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_TEXTO);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 5;
        panelSuperior.add(lblTitulo, gbc);

        JLabel lblBuscar = new JLabel("Buscar por CAE:");
        lblBuscar.setFont(FUENTE_BOTON);
        lblBuscar.setForeground(COLOR_TEXTO);

        txtBuscarCAE = new JTextField(20);
        txtBuscarCAE.setFont(new Font("Cambria", Font.PLAIN, 11));

        JButton btnBuscar = crearBoton("BUSCAR");
        btnBuscar.addActionListener(e -> btnBuscarAction());

        JButton btnActualizar = crearBoton("ACTUALIZAR");
        btnActualizar.addActionListener(e -> cargarComprobantes());

        JButton btnVerPDF = crearBoton("VER PDF");
        btnVerPDF.addActionListener(e -> btnVerPDFAction());

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panelSuperior.add(lblBuscar, gbc);

        gbc.gridx = 1;
        panelSuperior.add(txtBuscarCAE, gbc);

        gbc.gridx = 2;
        panelSuperior.add(btnBuscar, gbc);

        gbc.gridx = 3;
        panelSuperior.add(btnActualizar, gbc);

        gbc.gridx = 4;
        panelSuperior.add(btnVerPDF, gbc);

        String[] columnas = {"ID", "Tipo", "PtoVta", "Numero", "Fecha", "CUIT Receptor", "Total", "Estado Pago", "CAE", "Vto CAE", "Email"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Cambria", Font.PLAIN, 10));
        JScrollPane scrollPane = new JScrollPane(tabla);

        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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