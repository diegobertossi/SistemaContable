package com.els.facturacion.vista;

import com.els.facturacion.service.EmisionAsincronaService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class RetryCountdownDialog extends JDialog {

    private static WindowAdapter FRAME_CLOSE_LISTENER;
    private static EmisionAsincronaService ACTIVE_SERVICE;

    private static final Font FUENTE_COUNTDOWN = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font FUENTE_INFO = new Font("Segoe UI", Font.PLAIN, 11);

    private final JTextArea txtMensaje;
    private final JLabel lblCountdown;
    private final JLabel lblEstado;
    private final JPanel panel;
    private Timer countdownTimer;
    private final EmisionAsincronaService emisionService;
    private Theme theme;
    private int segundosRestantes;

    public RetryCountdownDialog(Window parent, String mensajeInicial,
                                EmisionAsincronaService emisionService, Theme theme) {
        super(parent, "ARCA no responde", ModalityType.MODELESS);
        this.emisionService = emisionService;
        this.theme = theme;
        ACTIVE_SERVICE = emisionService;
        this.segundosRestantes = 600;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);
        setSize(340, 200);
        setLocationRelativeTo(parent);
        VentanaPrincipal.addThemeListener(this);

        txtMensaje = new JTextArea(mensajeInicial);
        txtMensaje.setFont(FUENTE_INFO);
        txtMensaje.setEditable(false);
        txtMensaje.setFocusable(false);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        txtMensaje.setBorder(null);

        lblCountdown = new JLabel(formatearTiempo(segundosRestantes), SwingConstants.CENTER);
        lblCountdown.setFont(FUENTE_COUNTDOWN);

        lblEstado = new JLabel(" ", SwingConstants.CENTER);
        lblEstado.setFont(FUENTE_INFO);

        panel = new JPanel(new BorderLayout(6, 4));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        panel.add(txtMensaje, BorderLayout.NORTH);
        panel.add(lblCountdown, BorderLayout.CENTER);
        panel.add(lblEstado, BorderLayout.SOUTH);
        add(panel);

        applyTheme(theme);

        countdownTimer = new Timer(1000, e -> {
            segundosRestantes--;
            lblCountdown.setText(formatearTiempo(segundosRestantes));
            if (segundosRestantes <= 0) {
                countdownTimer.stop();
                lblEstado.setText("Reintentando...");
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        protegerCierreFrame(parent);
    }

    public void applyTheme(Theme t) {
        if (t == null) return;
        this.theme = t;
        Color bg = t.bgSurface;
        Color fg = t.textPrimary;
        Color fgMuted = t.textTertiary;
        Color danger = t.danger;
        setBackground(bg);
        panel.setBackground(bg);
        txtMensaje.setBackground(bg);
        txtMensaje.setForeground(fg);
        lblCountdown.setForeground(danger);
        lblEstado.setForeground(fgMuted);
        if (getContentPane() != null) {
            getContentPane().setBackground(bg);
        }
    }

    public void setEstado(String texto) {
        lblEstado.setText(texto);
    }

    public void setMensaje(String mensaje) {
        txtMensaje.setText(mensaje);
    }

    public void reiniciarCountdown() {
        applyTheme(VentanaPrincipal.getCurrentTheme());
        segundosRestantes = 600;
        lblCountdown.setText(formatearTiempo(segundosRestantes));
        countdownTimer.stop();
        countdownTimer.start();
    }

    public void empezar() {
        applyTheme(theme);
        countdownTimer.start();
        setVisible(true);
    }

    public void detener() {
        countdownTimer.stop();
        ACTIVE_SERVICE = null;
        dispose();
    }

    private String formatearTiempo(int segs) {
        if (segs < 0) segs = 0;
        int min = segs / 60;
        int sec = segs % 60;
        return String.format("%02d:%02d", min, sec);
    }

    public static boolean hayProcesoActivo() {
        return ACTIVE_SERVICE != null && ACTIVE_SERVICE.estaEmitiendo();
    }

    public static void confirmarSalida() {
        if (!hayProcesoActivo()) {
            salir();
            return;
        }
        int opcion = JOptionPane.showOptionDialog(
            null,
            "Hay un proceso activo de espera de respuesta de ARCA.\n"
            + "Si cierra el sistema, este proceso se cancelara.\n\n"
            + "Desea salir del sistema o permanecer?",
            "Proceso ARCA activo",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            new String[]{"SALIR DEL SISTEMA", "PERMANECER EN EL SISTEMA"},
            "PERMANECER EN EL SISTEMA"
        );
        if (opcion != 0) return;
        int confirmar = JOptionPane.showConfirmDialog(
            null,
            "SE CANCELARA LA GENERACION DE FACTURA.\n"
            + "Desea continuar?",
            "Cancelar factura",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirmar != JOptionPane.OK_OPTION) return;
        if (ACTIVE_SERVICE != null) {
            ACTIVE_SERVICE.cancelar();
        }
        salir();
    }

    private static void salir() {
        Runtime.getRuntime().halt(0);
    }

    private void protegerCierreFrame(Window parent) {
        if (!(parent instanceof Frame) || FRAME_CLOSE_LISTENER != null) return;

        Frame frame = (Frame) parent;
        FRAME_CLOSE_LISTENER = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        };
        frame.addWindowListener(FRAME_CLOSE_LISTENER);
    }

    public static void limpiarProteccion() {
        FRAME_CLOSE_LISTENER = null;
        ACTIVE_SERVICE = null;
    }
}
