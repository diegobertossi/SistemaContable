-- Migración: expandir tabla cliente de ReparSoft con columnas de FacturaSoft
-- Ejecutar manualmente contra el servidor MySQL antes de usar la sincronización bidireccional
-- Las columnas son NULL por defecto para no romper el sistema ReparSoft existente

ALTER TABLE ordenesbsas.cliente
    ADD COLUMN tipo_documento VARCHAR(10) NULL DEFAULT 'CUIT' AFTER idCliente,
    ADD COLUMN condicion_iva VARCHAR(60) NULL AFTER CorreoElectronico,
    ADD COLUMN tipo_persona VARCHAR(20) NULL DEFAULT 'empresa' AFTER condicion_iva;

ALTER TABLE ordenesbrc.cliente
    ADD COLUMN tipo_documento VARCHAR(10) NULL DEFAULT 'CUIT' AFTER idCliente,
    ADD COLUMN condicion_iva VARCHAR(60) NULL AFTER CorreoElectronico,
    ADD COLUMN tipo_persona VARCHAR(20) NULL DEFAULT 'empresa' AFTER condicion_iva;
