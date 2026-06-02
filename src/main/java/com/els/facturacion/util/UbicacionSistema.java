package com.els.facturacion.util;

public class UbicacionSistema {

    public static final String BSAS = "BUENOS AIRES";
    public static final String BRC = "BARILOCHE";

    private static String ubicacionActual = null;

    private UbicacionSistema() {}

    public static void setUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.equals(BSAS) || ubicacion.equals(BRC)) {
            ubicacionActual = ubicacion;
        } else {
            throw new IllegalArgumentException("Ubicación no válida: " + ubicacion);
        }
    }

    public static String getUbicacion() {
        return ubicacionActual;
    }

    public static boolean isSeleccionado() {
        return ubicacionActual != null;
    }

    public static String getNombreDbFacturacion() {
        if (ubicacionActual == null) {
            throw new IllegalStateException("No se ha seleccionado una ubicación del sistema");
        }
        return ubicacionActual.equals(BSAS) ? "facturacion_db_bsas" : "facturacion_db_brc";
    }

    public static String getNombreDbReparsoft() {
        if (ubicacionActual == null) {
            throw new IllegalStateException("No se ha seleccionado una ubicación del sistema");
        }
        return ubicacionActual.equals(BSAS) ? "ordenesbsas" : "ordenesbrc";
    }
}
