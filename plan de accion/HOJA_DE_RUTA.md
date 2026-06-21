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

## Hito 10: Validación de CUIT receptor vía ws_sr_padron_a5 (postergado)

**Objetivo:** Antes de emitir una factura, consultar el padrón de AFIP/ARCA (`ws_sr_padron_a5`) para validar que el CUIT del receptor existe y autocompletar sus datos (razón social, domicilio, condición IVA).

**Qué permite:**
- Validar si un CUIT existe antes de emitir (evita rechazo de ARCA).
- Autocompletar datos del receptor (razón social, domicilio, cond. IVA) al ingresar un CUIT.
- Consulta solo por CUIT (no permite búsqueda por razón social, DNI, etc.).

**Qué se necesita:**

| Ítem | Detalle |
|------|---------|
| Token WSAA extra | Mismo mecanismo que `wsfe`, pero con `service = "ws_sr_padron_a5"` |
| Certificado autorizado | El mismo certificado .p12 debe estar habilitado para `ws_sr_padron_a5` en ARCA |
| WSDL HOMO | `https://awshomo.afip.gov.ar/sr-padron/webservices/personaServiceA5?WSDL` |
| WSDL PROD | `https://aws.afip.gov.ar/sr-padron/webservices/personaServiceA5?WSDL` |
| Método | `getPersona` (parámetro: CUIT, retorna datos fiscales completos) |

**Patrón de implementación:** Idéntico a `ServicioWSFEv1`: cliente SOAP directo con POST HTTP + envelope XML + parseo de respuesta.

**Archivos a crear:**

| Archivo | Propósito |
|---------|-----------|
| `src/main/java/com/els/facturacion/arca/ServicioPadronA5.java` | Cliente SOAP para ws_sr_padron_a5 (mismo patrón que ServicioWSFEv1) |
| `src/main/java/com/els/facturacion/modelo/DatosReceptorARCA.java` | DTO con denominación, domicilio, cond. IVA, etc. |

**Archivos a modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/main/java/com/els/facturacion/controlador/ControladorFacturacion.java` | Al cambiar CUIT receptor o al hacer focus out del campo, disparar consulta a `ServicioPadronA5`. Si existe, autocompletar campos. |
| `src/main/java/com/els/facturacion/vista/VentanaFacturacion.java` | Mostrar indicador visual (check/cruz) al lado del campo CUIT según resultado de la consulta. |
| `src/main/java/com/els/facturacion/arca/ServicioWSAA.java` | Agregar soporte para token multi-servicio (un token por service, no compartido) — el token de `wsfe` no sirve para `ws_sr_padron_a5`. |

**Dependencias:** Hito 6 (refactor TokenCache multi-CUIT) — necesario porque ws_sr_padron_a5 requiere su propio token independiente.

**Criterio de aceptación:**
- Al ingresar un CUIT válido en el formulario de facturación, se autocompletan razón social, domicilio y condición IVA.
- Si el CUIT no existe en padrón, se muestra advertencia visual y se bloquea la emisión hasta que se corrija.
- El token de `ws_sr_padron_a5` se cachea independientemente del de `wsfe` (tiempo de vida: 12h).

**Esfuerzo:** Alto

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

---

## Hito 6: Optimización y robustez de conexión WSFE (ARCA)

**Objetivo:** Fortalecer la integración actual WSFEv1 directo para producción: multi-CUIT, retry, logging, manejo de errores ARCA tipado, y consultas de estado de comprobantes.

**Archivos a modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/main/java/com/els/facturacion/arca/TokenCache.java` | Refactor: cambiar campos escalares a `Map<String, TokenInfo>` por CUIT. Métodos: `tieneTokenValido(cuit)`, `guardarToken(cuit, ...)`, `getToken(cuit)`, `getSign(cuit)`. Mantener persistencia BD existente. |
| `src/main/java/com/els/facturacion/arca/ServicioWSAA.java` | Actualizar llamadas a `tokenCache.getToken(cuit)` / `getSign(cuit)`. |
| `src/main/java/com/els/facturacion/arca/ServicioWSFEv1.java` | Agregar retry con exponential backoff (3 intentos, 1s/3s/10s) en `enviarWSFE()` y `enviarWSAA()`. Reemplazar parsing `indexOf` por `XPath`/`DocumentBuilder`. Mapear `<Errors>` con código y mensaje. |
| `src/main/java/com/els/facturacion/modelo/RespuestaCAE.java` | Agregar `codigoError` (int/string) + `setErrorCode()`. Mantener compatibilidad. |
| `src/main/java/com/els/facturacion/controlador/ControladorFacturacion.java` | Nuevo método `verificarCAE(String cae)` que usa `consultarComprobante()` existente. Idempotencia: antes de emitir, verificar si `(cuit+pv+tipo+numero)` ya existe con CAE en BD local. |
| `pom.xml` | Agregar `ch.qos.logback:logback-classic:1.4.14` (SLF4J impl). Opcional: `javax.xml.xpath` ya está en JDK. |
| `src/main/java/...` (varias clases) | Reemplazar `System.err.println` por `Logger logger = LoggerFactory.getLogger(Clase.class)`; niveles ERROR/WARN/INFO. |

**Dependencias:** Ninguna (se puede arrancar independientemente).

**Criterio de aceptación:**
- Dos CUITs configurados en BD: al alternar entre ellos en la UI, ambos conservan token válido sin re-autenticar.
- Si ARCA responde con timeout/error 5xx, el sistema reintenta 3 veces antes de fallar.
- Errores de ARCA llegan a `RespuestaCAE` con `codigoError` y `mensaje` parseados del XML `<Errors>`.
- Logging unificado con niveles configurables (archivo `logback.xml` en resources).
- `verificarCAE(cae)` consulta ARCA y actualiza BD si el estado cambió.
- Emisión idempotente: re-emitir mismo comprobante retorna el CAE ya guardado sin llamar ARCA.

**Esfuerzo:** Alto

---

## Hito 7: Soporte multi-Punto de Venta

**Objetivo:** Permitir múltiples Puntos de Venta (PV) por CUIT. Hoy el sistema asume 1 PV por CUIT.

**Archivos a modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/main/resources/config/schema.sql` | Quitar `UNIQUE` de `cuit_certificados.punto_venta` → índice simple. Agregar tabla `puntos_venta` (id, cuit, numero, activo) si se quiere normalizar. |
| `src/main/java/com/els/facturacion/dao/CuitDAO.java` | `listarPuntosVenta(cuit)` → `List<Integer>`. |
| `src/main/java/com/els/facturacion/controlador/ControladorFacturacion.java` | `cargarEmisorActivo()`: cargar todos los PV del CUIT activo en `cmbPuntoVenta`. |
| `src/main/java/com/els/facturacion/vista/VentanaFacturacion.java` | `cmbPuntoVenta` ya existe; poblar con lista de PV del CUIT. |

**Dependencias:** Hito 6 (recomendado, no obligatorio).

**Criterio de aceptación:**
- Un CUIT con 3 PV (00001, 00002, 00003) en BD → combo muestra las 3 opciones.
- `getUltimoNumero()` filtra por PV correcto.
- Facturas emitidas usan el PV seleccionado por el usuario.

**Esfuerzo:** Medio

---

## Hito 8: Configuración de certificado por entorno

**Objetivo:** Eliminar path hardcodeado `src/main/resources/certificados/Certificado.p12`. Permitir configurar ruta del certificado por CUIT vía UI.

**Archivos a modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/main/java/com/els/facturacion/vista/VentanaPrincipal.java` / nueva `VentanaConfiguracion` | Panel "Configurar Certificados" existente: agregar validación `Files.exists(path)` al guardar. Mostrar warning si no existe. |
| `src/main/java/com/els/facturacion/dao/CuitDAO.java` | Validar ruta al insertar/actualizar (opcional, solo warning). |
| `src/main/resources/config/schema.sql` | Insertar datos de prueba con path relativo configurable, no absoluto. |

**Dependencias:** Ninguna.

**Criterio de aceptación:**
- Al guardar un CUIT con ruta de certificado inexistente → warning visible pero permite guardar (para casos donde cert está en otra máquina).
- En producción, cada máquina puede tener el `.p12` en path distinto sin tocar código.

**Esfuerzo:** Bajo

---

## Hito 9: Tests unitarios e integración WSFE

**Objetivo:** Cobertura de pruebas para la cadena crítica de emisión.

**Archivos a crear/modificar:**

| Archivo | Cambio |
|---------|--------|
| `src/test/java/com/els/facturacion/arca/ServicioWSFEv1Test.java` | Mock `ServicioWSAA` y `HttpURLConnection`. Tests: emisión exitosa, retry en timeout, error autenticación, parsing XML con namespaces. |
| `src/test/java/com/els/facturacion/arca/TokenCacheTest.java` | Tests: token válido/expirado, multi-CUIT, persistencia BD. |
| `src/test/java/com/els/facturacion/controlador/ControladorFacturacionTest.java` | Test integración: `emitirFactura()` flow completo con mocks. |

**Dependencias:** Hito 6 (código testeable tras refactor).

**Criterio de aceptación:**
- `mvn test` pasa en CI.
- Cobertura > 70% en paquetes `arca.*` y `controlador.ControladorFacturacion`.

**Esfuerzo:** Alto

---

## Apéndice: Requisitos ARCA (Checklist de Puesta en Producción)

> Referencia para cuando el sistema pase a entorno productivo.

### Certificados y Credenciales Necesarios

| Ítem | Descripción | Dónde se obtiene |
|------|-------------|------------------|
| **Certificado .p12 (PKCS#12)** | Contiene clave privada + certificado público firmado por AFIP. Válido 1 año. | Portal AFIP → **Administrador de Certificados** → Solicitar para "Web Services Factura Electrónica" |
| **Contraseña del .p12** | Definida al generar el certificado. Se guarda en `CuitConfigDTO.passwordCert` | La define el usuario al descargar el .p12 |
| **CUIT del emisor** | Debe tener actividad comercial declarada en AFIP | Ya existe en `cuit_certificados.cuit` |
| **Punto(s) de Venta** | Cada PV debe estar dado de alta y autorizado para WSFE | Portal AFIP → **Facturación** → **Alta Punto de Venta** → **Autorizar Web Service** |
| **Entorno** | `homo` (homologación) o `prod` (producción) | `configuraciones.arca.entorno` |

### Entornos y URLs

| Entorno | WSFE URL | WSAA URL |
|---------|----------|----------|
| Homologación | `https://wswhomo.afip.gov.ar/wsfev1/service.asmx` | `https://wsaahomo.afip.gov.ar/ws/services/LoginCms` |
| Producción | `https://servicios1.afip.gov.ar/wsfev1/service.asmx` | `https://wsaa.afip.gov.ar/ws/services/LoginCms` |

### Pasos para pasar a Producción

1. Obtener certificado `.p12` de **producción** (el de homologación no sirve en prod).
2. Configurar en BD: `cuit_certificados` con ruta al .p12 de prod + password.
3. Cambiar `configuraciones` → `arca.entorno = 'prod'`.
4. Verificar que los PV estén autorizados en producción.
5. Probar emisión de 1 factura real (Factura B a Consumidor Final).

### Renovación Anual del Certificado

- El certificado vence a **1 año** de su emisión.
- **30 días antes**: AFIP permite generar uno nuevo sin revocar el anterior (superposición).
- Proceso: Generar nuevo .p12 → Actualizar `ruta_certificado` y `password_cert` en BD → Probar en homologación → Cambiar a producción.
- **Automatización futura**: Agregar job/recordatorio que alerte 45 días antes del vencimiento (leer `expiracion` del certificado con BouncyCastle).

---

## Resumen de dependencias actualizado

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

Hito 6 (WSFE robustez) ── sin dependencias previas, CRÍTICO para prod
    │
    ├──▶ Hito 7 (Multi-PV) ── requiere Hito 6
    │
    ├──▶ Hito 9 (Tests WSFE) ── requiere Hito 6
    │
    └──▶ Hito 10 (Padrón A5) ── requiere Hito 6 (token multi-servicio)

Hito 8 (Config cert) ── independiente
```

---

## Riesgos y decisiones pendientes (actualizado)

1. **ReparSoft `cliente`**: El ALTER TABLE agrega columnas NULL, pero ReparSoft (el otro sistema) podría tener un mapeo rígido que no espere esas columnas. Probablemente no haya problema porque son NULL, pero hay que verificarlo.
2. **Escritura concurrente**: Si alguien usa ReparSoft al mismo tiempo que FacturaSoft escribe en `cliente`, puede haber conflictos. No hay lock distribuido.
3. **Importación dual**: La importación desde ReparSoft debe actualizar `els_referencia` en clientes ya existentes. Actualmente solo inserta nuevos.
4. **OC en visualización**: El campo `comprobante_asociado` debe incluirse en la tabla de comprobantes y en el PDF de factura si tiene valor.
5. **TokenCache multi-CUIT (Hito 6)**: Cambio crítico. Si falla, multi-emisor no funciona. Testear exhaustivamente con 2+ CUITs.
6. **Parsing XML con XPath (Hito 6)**: Reemplaza `indexOf` frágil. Verificar que `javax.xml.xpath` está disponible en JDK 8 target (sí, desde JDK 1.5).
7. **Idempotencia emisión (Hito 6)**: Si ARCA responde OK pero falla INSERT local, el comprobante queda en ARCA con CAE. La verificación previa mitiga, pero no elimina race condition. Considerar tabla `comprobantes_pendientes_sync`.
8. **Versión del plan**: v1.3 — 20/06/2026
