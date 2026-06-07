# FacturaSoft v2.0 — Hoja de Ruta

> Sistema de facturación electrónica Java + Swing + MySQL.
> Proyecto: `SistemaContable_ramas`

---

## Hito 1: AGENTS.md + esquema DB + ClienteDTO.tipoPersona

**Objetivo:** Preparar la base para los cambios de clientes. Se actualiza la documentación, se agrega la columna `tipo_persona` a la tabla `clientes` y al DTO, y se expande la tabla `cliente` de ReparSoft con las nuevas columnas (nullable, sin impacto visual en ese sistema).

**Archivos a modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/main/resources/AGENTS.md` | Actualizar regla ReparSoft: de "solo lectura" a "sincronización bidireccional en tabla cliente" |
| `src/main/java/com/els/facturacion/modelo/ClienteDTO.java` | Agregar campo `tipoPersona` (String, default `"empresa"`) |
| `src/main/java/com/els/facturacion/dao/ClienteDAO.java` | Agregar `tipo_persona` en INSERT, UPDATE y `mapear()` |
| `src/main/resources/config/schema_brc.sql` | Agregar `tipo_persona VARCHAR(20) DEFAULT 'empresa'` al CREATE TABLE clientes + migración ALTER TABLE |
| `src/main/resources/config/schema_bsas.sql` | Ídem |
| `src/main/resources/config/alter_reparsoft_cliente.sql` | **Nuevo**: ALTER TABLE para `ordenesbsas.cliente` y `ordenesbrc.cliente` agregando `tipo_documento`, `condicion_iva`, `tipo_persona` (NULL, con defaults) |

**Dependencias:** Ninguna.

**Criterio de aceptación:**
- `AGENTS.md` refleja la nueva política de sincronización.
- `ClienteDTO` tiene getter/setter para `tipoPersona`.
- `ClienteDAO` persiste y recupera `tipo_persona` correctamente.
- Las tablas `clientes` en ambas bases de facturación tienen la columna.
- Las tablas `cliente` en `ordenesbrc`/`ordenesbsas` tienen las nuevas columnas (todo NULL permitido).
- El script `alter_reparsoft_cliente.sql` está documentado para ejecución manual.

**Esfuerzo:** Bajo

---

## Hito 2: RadioButton Particular/Empresa + validación condicional

**Objetivo:** Agregar un selector de tipo de persona (Particular / Empresa) en la ventana de clientes, con validación de campos obligatorios según el tipo.

**Archivos a modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/main/java/com/els/facturacion/vista/VentanaClientes.java` | Agregar `JRadioButton` group (rdParticular, rdEmpresa) en el formulario. Validación condicional al guardar: Particular → solo nombre y teléfono obligatorios; Empresa → todos menos email. Mostrar/ocultar indicadores visuales. |
| `src/main/java/com/els/facturacion/controlador/ControladorClientes.java` | Pasar `tipoPersona` desde la vista al DTO. |

**Dependencias:** Hito 1.

**Criterio de aceptación:**
- Al crear un cliente, se puede seleccionar Particular o Empresa.
- Si es Particular: solo `razonSocial` (nombre) y `telefono` son obligatorios. El resto puede estar vacío.
- Si es Empresa: `razonSocial`, `tipoDocumento`, `nroDocumento`, `condicionIva`, `telefono`, `domicilio` son obligatorios. `email` es opcional.
- La grilla muestra el tipo de persona.
- Al cargar un cliente existente, el RadioButton refleja su tipo.
- Clientes existentes sin `tipo_persona` se muestran como Empresa (default).

**Esfuerzo:** Bajo

---

## Hito 3: Sincronización bidireccional FacturaSoft ↔ ReparSoft (Clientes)

**Objetivo:** Las operaciones CRUD de clientes en FacturaSoft impactan automáticamente en la tabla `cliente` de ReparSoft (base según ubicación actual). La importación existente también actualiza registros vinculados.

**Archivos a modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/main/java/com/els/facturacion/controlador/ControladorReparsoft.java` | Nuevos métodos: `insertarClienteEnReparsoft(ClienteDTO)`, `actualizarClienteEnReparsoft(ClienteDTO)`, `eliminarClienteEnReparsoft(int elsReferencia)` |
| `src/main/java/com/els/facturacion/controlador/ControladorClientes.java` | `guardarCliente()`: después de insertar/actualizar en FacturaSoft, llama al método de sync en ReparSoft. Si es INSERT, guarda `idCliente` devuelto en `els_referencia`. `eliminarCliente()`: también elimina en ReparSoft. |
| `src/main/java/com/els/facturacion/dao/ClienteDAO.java` | `importarDesdeReparsoft()`: actualizar registros existentes que ya tienen match (no solo insertar nuevos). Guardar `idCliente` en `els_referencia`. |

**Dependencias:** Hito 2.

**Criterio de aceptación:**
- Al crear un cliente en FacturaSoft, aparece también en `ordenes_{ubicacion}.cliente`.
- Al modificar un cliente en FacturaSoft, se actualiza en ReparSoft.
- Al eliminar un cliente en FacturaSoft, se elimina (o desactiva) en ReparSoft.
- `els_referencia` almacena el `idCliente` de ReparSoft para mantener el vínculo.
- La importación desde ReparSoft actualiza registros vinculados (no solo inserta nuevos).

**Riesgo:** ReparSoft `cliente` tiene `idCliente` auto-incremental. FacturaSoft necesita guardar ese ID en `els_referencia` para futuras actualizaciones.

**Esfuerzo:** Alto

---

## Hito 4: Campo OC (Orden de Compra) en factura — COMPLETO ✅

**Objetivo:** Agregar un campo de texto simple "N° OC" en el formulario de facturación, que se guarda en el campo `comprobante_asociado` del `ComprobanteDTO`. No hay tabla separada de OC, el usuario ingresa el número manualmente.

**Cambios realizados:**

| Archivo | Cambio |
|---------|--------|
| `src/main/java/com/els/facturacion/vista/VentanaFacturacion.java` | Agregar campo `txtComprobanteAsoc` (JTextField), label "N° OC:" en sección "Datos del Receptor" (entre email y Cond. Venta), getter `getTxtComprobanteAsoc()`, y theme styling. |
| `src/main/java/com/els/facturacion/controlador/ControladorFacturacion.java` | Descomentar `comprobante.setComprobanteAsociado(...)` en `btnEmitirAction()` y `view.getTxtComprobanteAsoc().setText("")` en `limpiarTodo()`. |
| `src/main/java/com/els/facturacion/vista/VentanaClientes.java` | Eliminar botón "IMPORTAR DE REPARSOFT". Auto-import silencioso al abrir ventana mediante `sincronizarConReparsoft()`. |

**Criterio de aceptación:**
- ✅ El campo "N° OC" aparece en el formulario de facturación (entre email y Cond. Venta).
- ✅ El usuario puede escribir un número de OC (texto libre, no obligatorio).
- ✅ Al emitir la factura, el número de OC se guarda en `comprobantes.comprobante_asociado`.
- ✅ Si no se ingresa OC, el campo queda vacío y la factura se emite normalmente.
- ✅ Campo vacío al limpiar formulario.
- ✅ Sincronización con tema activo.

**Esfuerzo:** Bajo

---

## Hito 5: Módulo Remitos (postergado)

**Objetivo:** Completar la ventana de gestión de remitos. Los DTOs (`RemitoDTO`, `RemitoItemDTO`) y el DAO (`RemitoDAO`) ya están implementados. Falta la interfaz gráfica `VentanaRemitos.java` y el `ControladorRemitos.java`.

**Dependencias:** Ninguna (se puede arrancar independientemente).

**Estado actual:**
- `RemitoDTO` — COMPLETO (55 líneas, con todos los campos)
- `RemitoItemDTO` — COMPLETO (44 líneas)
- `RemitoDAO` — COMPLETO (164 líneas con insertar, listar, buscar, actualizarEstado, getUltimoNumero)
- `ControladorRemitos` — PENDIENTE (clase mencionada en AGENTS.md pero sin archivo)
- `VentanaRemitos` — PENDIENTE (botón en VentanaPrincipal muestra "Funcionalidad en desarrollo")

**Esfuerzo:** Medio (cuando se retome)

---

## Resumen de dependencias

```
Hito 1 (AGENTS + DB + DTO)
    │
    ▼
Hito 2 (RadioButton + validación)
    │
    ▼
Hito 3 (Sincronización ReparSoft)
    
Hito 4 (Campo OC) ── COMPLETO ✅
    
Hito 5 (Remitos) ── postergado, sin dependencias
```

---

## Riesgos y decisiones pendientes

1. **ReparSoft `cliente`**: El ALTER TABLE agrega columnas NULL, pero ReparSoft (el otro sistema) podría tener un mapeo rígido que no espere esas columnas. Probablemente no haya problema porque son NULL, pero hay que verificarlo.
2. **Escritura concurrente**: Si alguien usa ReparSoft al mismo tiempo que FacturaSoft escribe en `cliente`, puede haber conflictos. No hay lock distribuido.
3. **Importación dual**: La importación desde ReparSoft debe actualizar `els_referencia` en clientes ya existentes. Actualmente solo inserta nuevos.
4. **OC en visualización**: El campo `comprobante_asociado` debe incluirse en la tabla de comprobantes y en el PDF de factura si tiene valor.
5. **Versión del plan**: v1.1 — 07/06/2026
