### Optimized Prompt

```
<role>
Eres un desarrollador Java Swing senior experto en UI y refactorización de estilos. Tu tarea es unificar la apariencia de los botones del proyecto manteniendo la funcionalidad intacta.
</role>

<context>
Actualmente los botones del proyecto tienen estilos inconsistentes entre ventanas. Tres botones específicos (`btnBuscar`, `btnActualizar`, `btnVerPDF` en `VentanaComprobantes`) tienen un estilo deseable que debe servir como referencia para todos los demás botones. Unificar estilos mejora la mantenibilidad y da una experiencia visual cohesiva al usuario.
</context>

<task>
1. Identifica los estilos actuales de `btnBuscar`, `btnActualizar` y `btnVerPDF` en `VentanaComprobantes` (colores, fuente, padding/borde, cursor, hover, foco, etc.) tanto en modo claro como oscuro.

2. Aplica esos mismos estilos a **todos los demás botones del proyecto** respetando estas reglas:
   - El tamaño (`preferredSize` / `font` / `margin`) de cada botón debe mantenerse como está actualmente — solo copia las propiedades visuales (colores, cursor, hover, border, foco).
   - Los cambios deben funcionar correctamente en modo claro y modo oscuro.
   - No alteres ninguna funcionalidad existente (listeners, acciones, lógica de negocio).
   - Preserva la estética actual del proyecto — el resultado debe verse cohesionado, no genérico.

<output_format>
Proporciona:
1. Una tabla con las propiedades de estilo extraídas de los botones de referencia.
2. La lista de todos los botones modificados, con archivo y línea.
3. El código exacto de cada cambio, organizado por archivo.
4. No incluyas explicaciones ni resúmenes fuera de lo pedido.
</output_format>

<example>
Ejemplo de tabla de propiedades esperada:

| Propiedad       | btnBuscar (claro) | btnBuscar (oscuro) |
|----------------|-------------------|--------------------|
| background     | Color(220,230,250)| Color(35,40,58)    |
| foreground     | Color(20,28,50)   | Color(235,240,250) |
| font           | Segoe UI Bold 11  | Segoe UI Bold 11   |
| cursor         | HAND_CURSOR       | HAND_CURSOR        |
| focusPainted   | false             | false              |
| margin/insets  | Insets(8,16,8,16) | Insets(8,16,8,16)  |

Ejemplo de formato de cambio:

VentanaFacturacion.java:338
- Antes: btnLimpiar.setBackground(t.btnBg);
+ Después: btnLimpiar.setBackground(t.btnBg); btnLimpiar.setCursor(HAND_CURSOR);

VentanaPagos.java:121
- Antes: estilizarBoton(btnPagarItem);
+ Después: // sin cambios (ya usa estilizarBoton)
</example>
</task>
```

### Changes Made

- **Added role** — "desarrollador Java Swing senior experto en UI/refactorización" enfoca al modelo en el dominio correcto
- **Added context** — explica *por qué* la consistencia visual importa (mantenibilidad, cohesión), ayudando al modelo a priorizar bien
- **Split task into steps** — 1) identificar estilos de referencia, 2) aplicarlos globalmente con reglas claras
- **Replaced negative** — "NO romper funcionalidad" → "No alteres funcionalidad existente" junto con "preserva la estética actual" (positivo y directo)
- **Disambiguated "estilo"** — especifica qué propiedades copiar (colores, cursor, hover, border, foco) vs. qué preservar (tamaño/font/margin propios)
- **Added output format** — tabla de propiedades + lista de cambios + código exacto, sin explicaciones extra
- **Added example** — muestra el formato exacto de tabla y diff esperados, eliminando ambigüedad
- **XML structure** — role, context, task, output_format, example separados en tags descriptivos
- **Verbosity calibration** — pide solo código y tabla, sin resúmenes ni explicaciones
