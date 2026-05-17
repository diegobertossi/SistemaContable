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

### Nombres de Reportes
- `factura_a.jrxml`
- `factura_c.jrxml`
- `nota_credito.jrxml`
- `reporte_caja_mensual.jrxml`
- `reporte_gastos_anual.jrxml`

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

## Notas Importantes

1. **Facturación**: El comprobante se registra en ARCA ANTES de guardar en BD local. Si el sistema falla entre ambos pasos, el comprobante ya existe en ARCA.
2. **PDF sin CAE**: El DAO debe poder regenerar PDFs para comprobantes que tienen CAE pero no PDF.
3. **ReparSoft**: Solo lectura, NUNCA escribir en sus bases.
4. **Certificados**: Los archivos .p12 se almacenan en `src/main/resources/certificados/`