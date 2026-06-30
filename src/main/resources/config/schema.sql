-- Script de instalación de base de datos facturacion_db
-- FacturaSoft v1.0
-- Uso: mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS facturacion_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE facturacion_db;

-- Eliminar tablas existentes si existen (orden inverso por FK)
DROP TABLE IF EXISTS factura_item_pagos;
DROP TABLE IF EXISTS recibo_facturas;
DROP TABLE IF EXISTS recibo_pagos;
DROP TABLE IF EXISTS factura_items;
DROP TABLE IF EXISTS remito_items;
DROP TABLE IF EXISTS remitos;
DROP TABLE IF EXISTS recibos;
DROP TABLE IF EXISTS factura_pagos;
DROP TABLE IF EXISTS comprobantes;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS token_cache;
DROP TABLE IF EXISTS remito_preimpreso_config;
DROP TABLE IF EXISTS configuraciones;
DROP TABLE IF EXISTS cuit_certificados;

-- Tabla: cuit_certificados
CREATE TABLE IF NOT EXISTS cuit_certificados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cuit VARCHAR(11) NOT NULL UNIQUE,
    razon_social VARCHAR(200),
    condicion_iva VARCHAR(50),
    punto_venta INT NOT NULL,
    ruta_certificado VARCHAR(500),
    password_cert VARCHAR(200),
    domicilio VARCHAR(200),
    ingresos_brutos VARCHAR(50),
    fecha_inicio_actividades DATE,
    activo BOOLEAN DEFAULT TRUE,
    INDEX idx_cuit (cuit),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: remito_preimpreso_config
CREATE TABLE IF NOT EXISTS remito_preimpreso_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    punto_venta INT NOT NULL,
    cai BIGINT NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    desde INT NOT NULL,
    hasta INT NOT NULL,
    INDEX idx_punto_venta (punto_venta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: token_cache
CREATE TABLE IF NOT EXISTS token_cache (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cuit VARCHAR(11) NOT NULL,
    token TEXT NOT NULL,
    sign TEXT NOT NULL,
    expiracion DATETIME NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_cuit_expiracion (cuit, expiracion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: comprobantes
CREATE TABLE IF NOT EXISTS comprobantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cuit_emisor VARCHAR(11) NOT NULL,
    tipo_comprobante INT NOT NULL,
    punto_venta INT NOT NULL,
    numero BIGINT NOT NULL,
    cuit_receptor VARCHAR(11),
    razon_social_rec VARCHAR(200),
    fecha_emision DATE NOT NULL,
    importe_neto DECIMAL(12,2),
    importe_iva DECIMAL(12,2),
    importe_total DECIMAL(12,2),
    cae VARCHAR(20),
    vencimiento_cae DATE,
    els_asociado INT,
    ruta_pdf VARCHAR(500),
    email_enviado BOOLEAN DEFAULT FALSE,
    descripcion TEXT,
    concepto VARCHAR(50),
    periodo_desde DATE,
    periodo_hasta DATE,
    periodo_vto DATE,
    condicion_iva_receptor VARCHAR(60),
    tipo_documento VARCHAR(10),
    nro_documento VARCHAR(50),
    domicilio_receptor VARCHAR(200),
    email_receptor VARCHAR(500),
    condiciones_venta VARCHAR(200),
    comprobante_asociado VARCHAR(50),
    estado_pago VARCHAR(30) DEFAULT 'pendiente',
    otros_impuestos DECIMAL(12,2) DEFAULT 0.00,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_comprobante (cuit_emisor, tipo_comprobante, punto_venta, numero),
    INDEX idx_cuit_emisor (cuit_emisor),
    INDEX idx_fecha_emision (fecha_emision),
    INDEX idx_cae (cae),
    INDEX idx_estado_pago (estado_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: clientes
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(10) DEFAULT 'CUIT',
    nro_documento VARCHAR(50) NOT NULL,
    razon_social VARCHAR(200) NOT NULL,
    condicion_iva VARCHAR(60),
    domicilio VARCHAR(200),
    telefono VARCHAR(50),
    email VARCHAR(500),
    origen VARCHAR(20) DEFAULT 'manual',
    els_referencia INT,
    activo BOOLEAN DEFAULT TRUE,
    tipo_persona VARCHAR(20) DEFAULT 'empresa',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_documento (tipo_documento, nro_documento),
    INDEX idx_razon_social (razon_social),
    INDEX idx_nro_documento (nro_documento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: factura_items
CREATE TABLE IF NOT EXISTS factura_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comprobante_id INT NOT NULL,
    codigo VARCHAR(50),
    descripcion VARCHAR(500) NOT NULL,
    cantidad DECIMAL(12,2) NOT NULL,
    unidad_medida VARCHAR(20) DEFAULT 'Unidad',
    precio_unitario DECIMAL(12,2) NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    alicuota_iva DECIMAL(5,2) DEFAULT 21.00,
    estado_pago VARCHAR(20) DEFAULT 'pendiente',
    els_referencia INT,
    orden INT DEFAULT 0,
    FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id) ON DELETE CASCADE,
    INDEX idx_comprobante (comprobante_id),
    INDEX idx_els (els_referencia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: remitos
CREATE TABLE IF NOT EXISTS remitos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_remito VARCHAR(30) NOT NULL UNIQUE,
    fecha_emision DATE NOT NULL,
    fecha_entrega DATE,
    cuit_emisor VARCHAR(11) NOT NULL,
    razon_social_emisor VARCHAR(200),
    domicilio_emisor VARCHAR(200),
    cuit_receptor VARCHAR(11),
    razon_social_receptor VARCHAR(200),
    domicilio_receptor VARCHAR(200),
    comprobante_id INT,
    reparsoft_remito_id INT,
    estado VARCHAR(30) DEFAULT 'pendiente',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_emisor (cuit_emisor),
    INDEX idx_receptor (cuit_receptor),
    INDEX idx_estado (estado),
    INDEX idx_comprobante (comprobante_id),
    INDEX idx_reparsoft_id (reparsoft_remito_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: remito_items
CREATE TABLE IF NOT EXISTS remito_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    remito_id INT NOT NULL,
    codigo VARCHAR(50),
    descripcion VARCHAR(500) NOT NULL,
    cantidad DECIMAL(12,2) NOT NULL,
    unidad_medida VARCHAR(20) DEFAULT 'Unidad',
    els_referencia INT,
    orden INT DEFAULT 0,
    FOREIGN KEY (remito_id) REFERENCES remitos(id) ON DELETE CASCADE,
    INDEX idx_remito (remito_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: recibos
CREATE TABLE IF NOT EXISTS recibos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_recibo VARCHAR(30) NOT NULL UNIQUE,
    fecha_cobro DATE NOT NULL,
    cliente_id INT,
    cuit_cliente VARCHAR(11),
    razon_social_cliente VARCHAR(200),
    monto_total DECIMAL(12,2) NOT NULL,
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_numero (numero_recibo),
    INDEX idx_cliente (cliente_id),
    INDEX idx_fecha (fecha_cobro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: recibo_pagos
CREATE TABLE IF NOT EXISTS recibo_pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recibo_id INT NOT NULL,
    forma_pago VARCHAR(30) NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    referencia VARCHAR(200),
    datos_adicionales TEXT,
    FOREIGN KEY (recibo_id) REFERENCES recibos(id) ON DELETE CASCADE,
    INDEX idx_recibo (recibo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: recibo_facturas
CREATE TABLE IF NOT EXISTS recibo_facturas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recibo_id INT NOT NULL,
    comprobante_id INT NOT NULL,
    monto_aplicado DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (recibo_id) REFERENCES recibos(id) ON DELETE CASCADE,
    FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id),
    UNIQUE KEY uk_recibo_factura (recibo_id, comprobante_id),
    INDEX idx_comprobante (comprobante_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: factura_pagos
CREATE TABLE IF NOT EXISTS factura_pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comprobante_id INT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    forma_pago VARCHAR(30),
    recibo_id INT,
    observaciones VARCHAR(300),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id),
    INDEX idx_comprobante (comprobante_id),
    INDEX idx_recibo (recibo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: factura_item_pagos
CREATE TABLE IF NOT EXISTS factura_item_pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    factura_item_id INT NOT NULL,
    comprobante_id INT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    fecha_pago DATE NOT NULL,
    recibo_id INT,
    estado VARCHAR(20) DEFAULT 'pendiente',
    FOREIGN KEY (factura_item_id) REFERENCES factura_items(id) ON DELETE CASCADE,
    FOREIGN KEY (comprobante_id) REFERENCES comprobantes(id),
    INDEX idx_item (factura_item_id),
    INDEX idx_recibo (recibo_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: configuraciones
CREATE TABLE IF NOT EXISTS configuraciones (
    clave VARCHAR(100) PRIMARY KEY,
    valor VARCHAR(500),
    descripcion VARCHAR(300)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertar configuraciones por defecto
INSERT INTO configuraciones (clave, valor, descripcion) VALUES
    ('arca.entorno', 'homo', 'Entorno ARCA: homo=homologación, prod=producción'),
    ('smtp.host', '', 'Host del servidor SMTP'),
    ('smtp.port', '587', 'Puerto del servidor SMTP'),
    ('smtp.user', '', 'Usuario SMTP'),
    ('smtp.pass', '', 'Contraseña SMTP'),
    ('reparsoft.host', 'localhost', 'Host de ReparSoft'),
    ('reparsoft.port', '3306', 'Puerto de ReparSoft'),
    ('iva.alicuota.0', '0.00', 'Alicuota IVA 0%'),
    ('iva.alicuota.1', '10.50', 'Alicuota IVA 10.5%'),
    ('iva.alicuota.2', '21.00', 'Alicuota IVA 21%'),
    ('iva.alicuota.3', '27.00', 'Alicuota IVA 27%')
ON DUPLICATE KEY UPDATE valor = valor;

-- Insertar emisores de prueba
INSERT INTO cuit_certificados (cuit, razon_social, condicion_iva, punto_venta,
    ruta_certificado, password_cert, domicilio, ingresos_brutos, fecha_inicio_actividades, activo) VALUES
    ('30678901234', 'FACTURASOFT TEST S.A.', 'IVA Responsable Inscripto', 1,
     'src/main/resources/certificados/Certificado.p12', '123456', '', '', NULL, TRUE),
    ('27123456789', 'FACTURASOFT MONOTRIBUTO', 'Responsable Monotributo', 2,
     'src/main/resources/certificados/Certificado.p12', '123456', '', '', NULL, FALSE)
ON DUPLICATE KEY UPDATE razon_social = VALUES(razon_social), condicion_iva = VALUES(condicion_iva),
    punto_venta = VALUES(punto_venta), ruta_certificado = VALUES(ruta_certificado),
    password_cert = VALUES(password_cert), activo = VALUES(activo);
