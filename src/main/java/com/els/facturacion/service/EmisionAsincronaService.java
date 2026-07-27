package com.els.facturacion.service;

import com.els.facturacion.arca.ServicioWSFEv1;
import com.els.facturacion.dao.ComprobanteDAO;
import com.els.facturacion.dao.FacturaItemDAO;
import com.els.facturacion.modelo.ComprobanteDTO;
import com.els.facturacion.modelo.CuitConfigDTO;
import com.els.facturacion.modelo.ItemFacturaDTO;
import com.els.facturacion.modelo.RespuestaCAE;
import com.els.facturacion.pdf.GestorFacturaPDF;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class EmisionAsincronaService {

    private static final Logger LOG = Logger.getLogger(EmisionAsincronaService.class.getName());
    private static final long RETRY_INTERVAL_MS = 10 * 60 * 1000;
    private static final int MAX_RETRIES = 72;

    private final ServicioWSFEv1 servicioWSFE;
    private final ComprobanteDAO comprobanteDAO;
    private final FacturaItemDAO facturaItemDAO;
    private final GestorFacturaPDF gestorPDF;
    private EmisionWorker worker;

    public EmisionAsincronaService() {
        this(new ServicioWSFEv1());
    }

    public EmisionAsincronaService(ServicioWSFEv1 servicioWSFE) {
        this.servicioWSFE = servicioWSFE;
        this.comprobanteDAO = new ComprobanteDAO();
        this.facturaItemDAO = new FacturaItemDAO();
        this.gestorPDF = new GestorFacturaPDF();
    }

    public void emitir(ComprobanteDTO comprobante, CuitConfigDTO cuitConfig, List<ItemFacturaDTO> items,
                       EmisionCallback callback) {
        if (worker != null && !worker.isDone()) {
            callback.onError("Ya hay una emision en curso");
            return;
        }
        worker = new EmisionWorker(comprobante, cuitConfig, items, callback);
        worker.execute();
    }

    public void cancelar() {
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }
    }

    public boolean estaEmitiendo() {
        return worker != null && !worker.isDone();
    }

    private class EmisionWorker extends SwingWorker<ResultadoEmision, String> {

        private final ComprobanteDTO comprobante;
        private final CuitConfigDTO cuitConfig;
        private final List<ItemFacturaDTO> items;
        private final EmisionCallback callback;
        private int intento;
        private boolean primerError;

        EmisionWorker(ComprobanteDTO comprobante, CuitConfigDTO cuitConfig,
                      List<ItemFacturaDTO> items, EmisionCallback callback) {
            this.comprobante = comprobante;
            this.cuitConfig = cuitConfig;
            this.items = items;
            this.callback = callback;
            this.intento = 0;
            this.primerError = true;
        }

        @Override
        protected ResultadoEmision doInBackground() throws Exception {
            while (intento < MAX_RETRIES && !isCancelled()) {
                intento++;
                if (intento > 1) {
                    final int intentoActual = intento;
                    SwingUtilities.invokeLater(() -> callback.onRetryAttempt(intentoActual));
                }
                publish("Enviando a ARCA (intento " + intento + ")...");

                try {
                    RespuestaCAE respuesta = servicioWSFE.emitirComprobante(
                        comprobante, cuitConfig.getRutaCertificado(), cuitConfig.getPasswordCert());

                    if (respuesta.isExitosa()) {
                        publish("CAE recibido: " + respuesta.getCae());
                        return procesarExito(respuesta);
                    }

                    if ("10016".equals(respuesta.getCodigoError())) {
                        long nuevoNumero = comprobante.getNumero() + 1;
                        comprobante.setNumero(nuevoNumero);
                        LOG.log(Level.INFO, "10016 → reintento {0} con numero: {1}",
                            new Object[]{intento, nuevoNumero});
                        publish("CAE existente (10016), reintentando con numero " + nuevoNumero + "...");
                        continue;
                    }

                    String codigo = respuesta.getCodigoError() != null ? respuesta.getCodigoError() : "?";
                    String msg = "Error ARCA codigo " + codigo + ": " + respuesta.getMensaje();
                    LOG.log(Level.WARNING, msg);
                    publicarError(msg);

                    if (esErrorReintentable(respuesta)) {
                        esperarReintento();
                        continue;
                    }

                    return new ResultadoEmision(false, msg, null);
                } catch (Exception e) {
                    String errorMsg = clasificarError(e);
                    LOG.log(Level.WARNING, "Intento {0} fallo: {1}", new Object[]{intento, errorMsg});
                    publicarError(errorMsg);
                    esperarReintento();
                }
            }

            if (isCancelled()) {
                return new ResultadoEmision(false, "Emision cancelada por el usuario", null);
            }
            return new ResultadoEmision(false,
                "Se agotaron los reintentos (" + MAX_RETRIES + "). " +
                "ARCA no respondio despues de " + (MAX_RETRIES * RETRY_INTERVAL_MS / 60000) + " minutos.", null);
        }

        private void publicarError(String msg) {
            if (primerError) {
                primerError = false;
                String explicacion = msg + "\n\n"
                    + "El sistema debe permanecer abierto.\n"
                    + "Se reintentara automaticamente cada 10 minutos.\n"
                    + "Se le informara cuando se realice un reintento y el resultado del mismo.";
                final String msgFinal = explicacion;
                final int intentoActual = intento;
                SwingUtilities.invokeLater(() -> callback.onRetryScheduled(msgFinal, intentoActual));
            } else {
                final String mensaje = msg;
                SwingUtilities.invokeLater(() -> callback.onProgress(mensaje));
            }
        }

        private ResultadoEmision procesarExito(RespuestaCAE respuesta) {
            try {
                comprobante.setCae(respuesta.getCae());
                comprobante.setVencimientoCae(respuesta.getVencimiento());
                if (respuesta.getNumeroComprobante() != null) {
                    comprobante.setNumero(respuesta.getNumeroComprobante());
                }

                int id = comprobanteDAO.insertar(comprobante);
                if (id <= 0) {
                    return new ResultadoEmision(false, "Error al guardar comprobante en base de datos", respuesta);
                }
                comprobante.setId(id);

                if (items != null && !items.isEmpty()) {
                    facturaItemDAO.insertarItems(id, items);
                }

                String rutaPDF = gestorPDF.generarFactura(comprobante, cuitConfig, items);
                if (rutaPDF != null) {
                    comprobante.setRutaPdf(rutaPDF);
                    comprobanteDAO.actualizar(comprobante);
                }

                return new ResultadoEmision(true, "Factura emitida exitosamente", respuesta);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error en post-procesamiento de emision", e);
                return new ResultadoEmision(true,
                    "CAE obtenido pero error en post-procesamiento: " + e.getMessage(), respuesta);
            }
        }

        private String clasificarError(Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
            }
            if (cause instanceof UnknownHostException) {
                return "Sin conexion a internet. No se pudo contactar ARCA.";
            }
            if (cause instanceof SocketTimeoutException) {
                return "ARCA no responde (timeout). Reintentando en 10 minutos...";
            }
            String msg = e.getMessage();
            if (msg != null) {
                if (msg.contains("timeout") || msg.contains("timed out")) {
                    return "ARCA no responde (timeout). Reintentando en 10 minutos...";
                }
                if (msg.contains("conexion") || msg.contains("connect") || msg.contains("Connection refused")) {
                    return "Error de conexion con ARCA. Reintentando en 10 minutos...";
                }
                return msg;
            }
            return "Error desconocido al contactar ARCA";
        }

        private boolean esErrorReintentable(RespuestaCAE respuesta) {
            String codigo = respuesta.getCodigoError();
            if (codigo == null) return true;
            return "10016".equals(codigo);
        }

        private void esperarReintento() {
            try {
                for (int i = 0; i < RETRY_INTERVAL_MS / 1000 && !isCancelled(); i++) {
                    Thread.sleep(1000);
                    if (i % 60 == 0 && i > 0) {
                        int minsRestantes = (int)((RETRY_INTERVAL_MS / 1000 - i) / 60);
                        publish("Proximo reintento en " + minsRestantes + " minuto(s)...");
                    }
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        protected void process(List<String> chunks) {
            String last = chunks.get(chunks.size() - 1);
            callback.onProgress(last);
        }

        @Override
        protected void done() {
            try {
                ResultadoEmision resultado = get();
                if (resultado.exitosa) {
                    callback.onSuccess(resultado.mensaje, resultado.respuesta);
                } else {
                    callback.onError(resultado.mensaje);
                }
            } catch (InterruptedException | CancellationException e) {
                callback.onError("Emision cancelada");
            } catch (ExecutionException e) {
                callback.onError("Error inesperado: " + e.getCause().getMessage());
                LOG.log(Level.SEVERE, "Error en worker de emision", e.getCause());
            }
        }
    }

    public static class ResultadoEmision {
        public final boolean exitosa;
        public final String mensaje;
        public final RespuestaCAE respuesta;

        public ResultadoEmision(boolean exitosa, String mensaje, RespuestaCAE respuesta) {
            this.exitosa = exitosa;
            this.mensaje = mensaje;
            this.respuesta = respuesta;
        }
    }

    public interface EmisionCallback {
        void onProgress(String mensaje);
        void onRetryScheduled(String mensaje, int intento);
        void onRetryAttempt(int intento);
        void onSuccess(String mensaje, RespuestaCAE respuesta);
        void onError(String mensaje);
    }
}
