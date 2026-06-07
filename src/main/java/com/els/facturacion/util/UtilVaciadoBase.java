package com.els.facturacion.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilVaciadoBase {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String USER = "root";
    private static final String PASS = "root";

    public static void vaciarAmbasBases() throws Exception {
        ejecutarScriptEnDb("facturacion_db_bsas", "/config/schema_bsas.sql");
        ejecutarScriptEnDb("facturacion_db_brc", "/config/schema_brc.sql");
    }

    private static void ejecutarScriptEnDb(String dbName, String scriptPath) throws Exception {
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + dbName
                + "?useUnicode=true&characterEncoding=UTF-8"
                + "&connectionCollation=utf8mb4_unicode_ci"
                + "&serverTimezone=UTC&useSSL=false"
                + "&allowPublicKeyRetrieval=true";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection conn = DriverManager.getConnection(url, USER, PASS);
                Statement stmt = conn.createStatement()) {

            String sql = leerScript(scriptPath);
            List<String> statements = parseStatements(sql);

            for (String st : statements) {
                stmt.execute(st);
            }
        }
    }

    private static String leerScript(String path) throws IOException {
        InputStream is = UtilVaciadoBase.class.getResourceAsStream(path);
        if (is == null) {
            throw new IOException("Script no encontrado: " + path);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            boolean skippingCreateDb = false;
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                String upper = trimmed.toUpperCase();
                if (upper.startsWith("CREATE DATABASE")) {
                    skippingCreateDb = true;
                }
                if (skippingCreateDb) {
                    if (trimmed.endsWith(";")) {
                        skippingCreateDb = false;
                    }
                    continue;
                }
                if (upper.startsWith("USE ") && trimmed.endsWith(";")) {
                    continue;
                }
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private static List<String> parseStatements(String sql) {
        List<String> statements = new ArrayList<>();
        sql = sql.replaceAll("--[^\n]*", "");
        String[] parts = sql.split(";");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                statements.add(trimmed);
            }
        }
        return statements;
    }
}
