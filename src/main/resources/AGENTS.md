# AGENTS.md - FacturaSoft v1.0

## Convenciones de Código

### Paquetes Java
- Estructura: `com.els.facturacion.{modulo}`
- Módulos: conexion, arca, modelo, dao, controlador, pdf, email, vista

### Nombres de Clases
- **DAOs**: `NombreEntidadDAO.java` (ej: `ComprobanteDAO.java`)
- **DTOs**: `NombreEntidadDTO.java` (ej: `ComprobanteDTO.java`)
- **Controladores**: `ControladorNombre.java` (ej: `ControladorFacturacion.java`)
- **Vistas**: `VentanaNombre.java` (ej: `VentanaFacturacion.java`)
- **Servicios**: `ServicioNombre.java` (ej: `ServicioWSAA.java`)

### Convenciones de Código
- **Clases**: PascalCase (ej: `ConexionFacturacion`)
- **Métodos**: camelCase (ej: `getConexion()`)
- **Constantes**: MAYUSCULAS_CON_GUION_BAJO
- **Variables**: camelCase (ej: `cuitEmisor`)
- **Interfaces**: Prefijo `I` opcional (ej: `IComprobanteDAO`)
- **Java 8 target**: NO usar `List.of()`, `Set.of()`, `Map.of()`, `Map.ofEntries()` ni otras API de Java 9+. Usar `Arrays.asList(...)`, `Collections.singletonList(...)`, `Collections.unmodifiableList(...)` o `new ArrayList<>(Arrays.asList(...))` según corresponda. El proyecto compila con `maven.compiler.source=8` y esas APIs no existen en Java 8.

### Propiedades Java
- Longitud máxima de línea: 120 caracteres
- Indentación: 4 espacios (no tabs)
- Encodings: UTF-8

---

## Patrones de Diseño

### Singleton
Usar para conexiones a bases de datos:
```java
private static InstanciaClase instancia;
private InstanciaClase() {}
public static InstanciaClase getInstancia() {
    if (instancia == null) {
        synchronized (InstanciaClase.class) {
            if (instancia == null) {
                instancia = new InstanciaClase();
            }
        }
    }
    return instancia;
}
```

### DAO (Data Access Object)
Cada tabla tiene su propio DAO con métodos:
- `insertar(EntidadDTO)`
- `actualizar(EntidadDTO)`
- `buscarPorId(int id)`
- `listarTodos()`
- `buscarPorCondicion(String where, Object... params)`

### DTO (Data Transfer Object)
Clases POJO con:
- Campos privados
- Getters y Setters
- Constructores vacío y con parámetros

### Controlador
Orquesta la lógica de negocio:
- Maneja DAO y servicios
- Valida datos
- Coordina flujo UI → BD

---

## Estructura de Excepciones

### Manejo de Errores
- Usar `try-catch` en métodos de DAO y servicios
- Registrar errores con `System.err.println()`
- Lanzar `RuntimeException` para errores críticos
- Mensajes de error descriptivos

---

## Convenciones de Base de Datos

### Tablas
- Nombre en singular (ej: `comprobante`, no `comprobantes`)
- Todas con `id INT PK AUTO_INCREMENT`
- Timestamps: `fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP`

### Índices
- FK siempre con índice
- Columns frecuentemente consultadas con índice

### Consultas SQL
- Usar PreparedStatement para evitar inyección
- Nombres de columnas en minúsculas con guiones bajos

---

## Integración ARCA

### Endpoints
- Homologación: `https://wswhomo.afip.gov.ar/...`
- Producción: `https://wsfe.afip.gov.ar/...`

### Token Cache
- Renovación automática cuando faltan < 10 min para vencer
- Almacenar en tabla `token_cache` con expiración

### QR ARCA (obligatorio)
Formato JSON encodeado en Base64:
```json
{"ver":1,"fecha":"YYYY-MM-DD","cuit":12345678,"ptoVta":1,
 "tipoCmp":1,"nroCmp":1,"importe":100.00,"moneda":"PES",
 "ctz":1,"tipoDocRec":80,"nroDocRec":20123456789,
 "tipoCodAuth":"E","codAut":12345678901234}
```

---

## Convenciones de PDF (JasperReports)

### Formato Factura A4
- Tamaño: A4 (210mm x 297mm)
- Margenes: 2cm
- Encabezado: datos_emisor + logo
- Detalle: tabla_items
- Pie: CAE + vencimiento + QR

### Carga de reportes compilados vs. fuente
- `.jasper` = binario compilado → usar `JasperFillManager.fillReport(inputStream, params, dataSource)` o `JRLoader.loadObject(inputStream)`. **NO** usar `JasperCompileManager.compileReport()` (espera `.jrxml` XML y falla con "Byte no válido 1 de la secuencia UTF-8").
- `.jrxml` = XML fuente → usar `JasperCompileManager.compileReport(inputStream)`.

### Nombres de Reportes
- `factura_a.jrxml`
- `factura_c.jrxml`
- `nota_credito.jrxml`
- `reporte_caja_mensual.jrxml`
- `reporte_gastos_anual.jrxml`

### Orden XSD en JRXML
El XSD de JasperReports exige orden estricto dentro de `<jasperReport>`:
`property* → propertyExpression* → import* → template* → reportFont* → style* → subDataset* → scriptlet* → **parameter*** → queryString? → **field*** → sortField* → variable* → filterExpression? → group* → background? → title? → pageHeader? → columnHeader? → detail? → columnFooter? → pageFooter? → lastPageFooter? → summary? → noData?`
- `<parameter>` va SIEMPRE **antes** que `<queryString>` y `<field>`, no después. (Error común: asumir que `parameter` va después de `field` porque es menos usado; el XSD de JasperReports 6.21.3 lo pone antes de `queryString`.)

---

## Dependencias Principales

| Librería | Versión | Uso |
|----------|---------|-----|
| Apache CXF | 3.5.9 | Cliente SOAP ARCA |
| BouncyCastle | 1.70 | Firma digital CMS |
| JasperReports | 6.21.3 | PDF y reportes |
| JavaMail | 1.6.2 | Envío de emails |
| MySQL Connector | 8.0.33 | Conexión MySQL |
| Apache POI | 5.2.5 | Lectura Excel |

---

## Reglas de Commit (futuro)

- Mensajes claros y descriptivos
- Prefijos: `feat:`, `fix:`, `refactor:`, `docs:`
- No commits de archivos binarios (.class, .jar)

---

## Nuevos Módulos v2.0

### 1. MÓDULO DE CLIENTES (`com.els.facturacion.vista.VentanaClientes`)
- Tabla: `clientes`
- DTO: `ClienteDTO`, DAO: `ClienteDAO`, Controller: `ControladorClientes`
- Importación desde ReparSoft (tabla `reparaciones` en `ordenesbrc`/`ordenesbsas`)
- CRUD completo con búsqueda por razón social
- Diálogo de selección: `VentanaSeleccionCliente`

### 2. MÓDULO DE COMPROBANTES (mejoras)
- Items de factura persistidos en `factura_items` (antes solo en memoria)
- IVA discriminado por alícuota configurable por ítem (0%, 10.5%, 21%, 27%)
- Campo `otros_impuestos` para percepciones/IIBB
- Estado de pago: `pendiente`, `pagada_parcial`, `pagada_total`, `anulada`
- Botón eliminar ítem en grilla de items
- Punto de venta único (00001 por defecto)

### 3. MÓDULO DE REMITOS (`com.els.facturacion.vista.VentanaRemitos`)
- Tablas: `remitos`, `remito_items`
- DTO: `RemitoDTO`, `RemitoItemDTO`, DAO: `RemitoDAO`, Controller: `ControladorRemitos`
- Numeración automática: R 0001-XXXXXXXX
- Estados: pendiente, entregado, anulado
- Items sin valores económicos (documento no fiscal)

### 4. MÓDULO DE RECIBOS (`com.els.facturacion.vista.VentanaRecibos`)
- Tablas: `recibos`, `recibo_pagos`, `recibo_facturas`
- DTO: `ReciboDTO`, `ReciboPagoDTO`, `ReciboFacturaDTO`
- DAO: `ReciboDAO`, Controller: `ControladorRecibos`
- Múltiples formas de pago por recibo
- Referencia a facturas que cancela (parcial o totalmente)
- Numeración automática: RE 0001-XXXXXXXX

### 5. MÓDULO DE PAGOS (`com.els.facturacion.vista.VentanaPagos`)
- Tablas: `factura_pagos`, `factura_item_pagos`
- DTO: `FacturaPagoDTO`, `FacturaItemPagoDTO`
- DAO: `FacturaPagoDAO`, Controller: `ControladorPagos`
- Pago total o parcial de facturas
- Pago individual por ítem (ELS)
- Actualización automática del estado de la factura

### 6. MÓDULO DE REPORTES (`com.els.facturacion.reportes.GestorReportes`)
- Generación de reportes JasperReports para:
  - Listado de facturas
  - Remitos imprimibles
  - Recibos de pago
  - Cuentas corrientes por cliente

### 7. CONFIGURACIÓN DEL SISTEMA
- Alicuotas de IVA configurables en tabla `configuraciones`
- Configuración de conexión ReparSoft

## Relaciones entre Entidades
```
Cliente          →  tiene muchas →  Facturas (comprobantes)
Factura          →  tiene muchos →  FacturaItem (cada uno referencia un ELS)
Factura          →  tiene muchos →  Pagos (factura_pagos)
FacturaItem      →  puede tener →  pago individual (factura_item_pagos)
Factura          →  puede tener →  Remito vinculado
Recibo           →  referencia  →  Factura(s) (recibo_facturas)
Recibo           →  tiene muchos →  Formas de pago (recibo_pagos)
```

## Notas Importantes

1. **Facturación**: El comprobante se registra en ARCA ANTES de guardar en BD local. Si el sistema falla entre ambos pasos, el comprobante ya existe en ARCA.
2. **PDF sin CAE**: El DAO debe poder regenerar PDFs para comprobantes que tienen CAE pero no PDF.
3. **ReparSoft**: Sincronización bidireccional en tabla `cliente`. Las modificaciones de clientes en FacturaSoft se reflejan en ReparSoft (tabla `cliente`, base según ubicación) y viceversa. No escribir en otras tablas de ReparSoft sin autorización.
4. **Certificados**: Los archivos .p12 se almacenan en `src/main/resources/certificados/`
5. **Items de factura**: Se persisten en `factura_items` después de emitir el comprobante en ARCA.
6. **Estado de pago**: Se actualiza automáticamente al registrar pagos en el módulo de Pagos o Recibos.