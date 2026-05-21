-- Script de creación de base de datos facturacion_db
-- FacturaSoft v1.0

CREATE DATABASE IF NOT EXISTS facturacion_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE facturacion_db;

-- Tabla: cuit_certificados
CREATE TABLE IF NOT EXISTS cuit_certificados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cuit VARCHAR(11) NOT NULL UNIQUE,
    razon_social VARCHAR(200),
    condicion_iva VARCHAR(50),
    punto_venta INT NOT NULL,
    ruta_certificado VARCHAR(500),
    password_cert VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE,
    INDEX idx_cuit (cuit),
    INDEX idx_activo (activo)
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
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_comprobante (cuit_emisor, tipo_comprobante, punto_venta, numero),
    INDEX idx_cuit_emisor (cuit_emisor),
    INDEX idx_fecha_emision (fecha_emision),
    INDEX idx_cae (cae)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS descripcion TEXT AFTER email_enviado;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS concepto VARCHAR(50) AFTER descripcion;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS periodo_desde DATE AFTER concepto;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS periodo_hasta DATE AFTER periodo_desde;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS periodo_vto DATE AFTER periodo_hasta;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS condicion_iva_receptor VARCHAR(60) AFTER periodo_vto;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS tipo_documento VARCHAR(10) AFTER condicion_iva_receptor;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS nro_documento VARCHAR(20) AFTER tipo_documento;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS domicilio_receptor VARCHAR(200) AFTER nro_documento;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS email_receptor VARCHAR(100) AFTER domicilio_receptor;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS condiciones_venta VARCHAR(200) AFTER email_receptor;
ALTER TABLE comprobantes ADD COLUMN IF NOT EXISTS comprobante_asociado VARCHAR(50) AFTER condiciones_venta;

-- Tabla: caja_movimientos
CREATE TABLE IF NOT EXISTS caja_movimientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NULL,
    tipo VARCHAR(20) NOT NULL,
    descripcion VARCHAR(300),
    monto DECIMAL(12,2) NOT NULL,
    cuit_asociado VARCHAR(11),
    comprobante_id INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fecha (fecha),
    INDEX idx_tipo (tipo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: categorias_gastos
CREATE TABLE IF NOT EXISTS categorias_gastos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300),
    activa BOOLEAN DEFAULT TRUE,
    INDEX idx_nombre (nombre),
    INDEX idx_activa (activa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla: gastos
CREATE TABLE IF NOT EXISTS gastos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    categoria_id INT NOT NULL,
    descripcion VARCHAR(300),
    monto DECIMAL(12,2) NOT NULL,
    mes INT,
    anio INT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_fecha (fecha),
    INDEX idx_categoria (categoria_id),
    INDEX idx_mes_anio (mes, anio),
    FOREIGN KEY (categoria_id) REFERENCES categorias_gastos(id)
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
    ('reparsoft.port', '3306', 'Puerto de ReparSoft')
ON DUPLICATE KEY UPDATE valor = valor;