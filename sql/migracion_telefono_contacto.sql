ALTER TABLE facturacion_db_brc.clientes
  ADD COLUMN telefono_contacto VARCHAR(50) AFTER telefono;

ALTER TABLE facturacion_db_bsas.clientes
  ADD COLUMN telefono_contacto VARCHAR(50) AFTER telefono;
