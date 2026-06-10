# Design Pattern — Sistema de Colores (Tema Claro / Oscuro)

Este patrón aplica a todas las ventanas del proyecto (VentanaClientes, VentanaFacturacion, etc.)

## Principios Generales

- **bgBase**: Fondo general de la ventana y panel izquierdo/derecho.
- **bgSurface**: Fondo de paneles contenedores secundarios (formularios, paneles de botones, panel de búsqueda).
- **bgElevated**: Fondo de elementos que deben destacar.
- **bgInput**: Fondo de campos de texto (JTextField) y JComboBox. Todos los campos de entrada comparten el mismo fondo para estilo unificado.

## Fondos de campos según modo de edición

Los campos de entrada cambian de fondo según si están en **visualización** (solo lectura) o **edición**:

| Modo | Visualización (solo lectura) | Edición (habilitado) |
|------|------------------------------|---------------------|
| Claro | `LIGHT_READONLY_BG = (236, 237, 241)` gris muy claro | `LIGHT_EDITABLE_BG = (255, 253, 230)` amarillo pastel |
| Oscuro | `DARK_READONLY_BG = (28, 33, 55)` azul oscuro | `DARK_EDITABLE_BG = (22, 27, 45)` azul más oscuro |

### Implementación
- `getFieldBg(boolean editing)` retorna el color según modo y tema actual.
- En `setFormEditable(boolean editable)`: se calcula `bg = getFieldBg(editable)` y se aplica a todos los campos (JTextField, JComboBox) vía `setBackground(bg)`.
- En `applyTheme()`: se usa `getFieldBg(campo.isEnabled())` para mantener el color correcto al cambiar de tema.
- Los renderers de combo usan `getFieldBg(combo.isEnabled())` para el fondo del item no seleccionado.

## Colores de Texto

### Textos de etiquetas (JLabel)
- Siempre `textPrimary` independientemente del estado (las etiquetas no se deshabilitan).

### Textos de campos (JTextField, JComboBox, JRadioButton)
- **Habilitado (edición):** `textPrimary`
  - Claro → `(20, 28, 50)` negro fuerte
  - Oscuro → `(235, 240, 250)` blanco fuerte
- **Deshabilitado (solo lectura):** color independiente por modo
  - Claro → `DISABLED_FG_LIGHT = (95, 97, 106)` gris oscuro distinguible
  - Oscuro → `DISABLED_FG_DARK = (210, 207, 190)` blanco amarillento pastel

### Implementación
- **JTextField**: Usar `setDisabledTextColor(getDisabledFg())` + `setForeground(textPrimary)`. Swing alterna automáticamente al deshabilitar/habilitar.
- **JComboBox**: El renderer (DefaultListCellRenderer) chequea `combo.isEnabled() ? textPrimary : getDisabledFg()` tanto en `getListCellRendererComponent` como en `paintComponent`. El fondo del renderer se setea a `bgInput` (consistente con el fondo del botón del combo). Los items seleccionados en la lista mantienen su color de selección por defecto.
- Los combos se crean como `new JComboBox<>()` planos (sin anonymous subclass) para compatibilidad con WindowBuilder. El `CustomComboUI` (inner class que extiende `BasicComboBoxUI`) se instala inmediatamente después vía `installComboUI(combo)`, que llama a `combo.setUI(new CustomComboUI())`. La instalación se repite en `applyTheme()` para proteger contra cambios de LAF. Adicionalmente, se sobreescribe `paintCurrentValueBackground` para usar `comboBox.getBackground()` en lugar del color global de UIManager, y `paintCurrentValue` para evitar que `BasicComboBoxUI` pinte con `UIManager.getColor("ComboBox.disabledBackground")` (blanco) cuando el combo está deshabilitado.
- **JRadioButton**: Override de `paintComponent` chequea `isEnabled() ? textPrimary : getDisabledFg()`.

### Método helper en VentanaClientes
```java
private Color getDisabledFg() {
    return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
}
```

## Bordes de validación

- Error (email inválido): `CompoundBorder(LineBorder(red), EmptyBorder(2,4,2,4))`
- Al corregir el texto, `clearErrorBorder` busca el `LineBorder` rojo dentro del `CompoundBorder` y restaura `normalBorder`.
- Al seleccionar otro cliente o limpiar formulario, se restaura explícitamente `first.setBorder(normalBorder)`.

## Restricción crítica: WindowBuilder (Eclipse GUI Designer)

**NO usar anonymous subclasses que sobreescriban métodos en field initializers.** WindowBuilder no las reconoce y lanza error de constructor inexistente.

### Causa
WindowBuilder parsea el código fuente y no soporta anonymous classes dentro de la inicialización de fields (ej. `new JComboBox<>() { @Override ... }`).

### Solución aplicada
- Todos los componentes se crean con su constructor normal (`new JComboBox<>()`, `new JTextField()`, etc.) sin anonymous subclass.
- Cualquier personalización de UI (como `CustomComboUI` para combos) se aplica **después** de la creación vía un método helper (`installComboUI(combo)`) llamado desde `initComponents()` y `applyTheme()`.

### Regla para nuevas ventanas
Si se agrega un JComboBox a cualquier ventana del proyecto:
1. Declarar `private static class CustomComboUI extends BasicComboBoxUI` con override de `paintCurrentValueBackground` (replicar el patrón de `VentanaClientes.java`).
2. Crear el combo como `new JComboBox<>(items)` — sin anonymous class.
3. Llamar a `combo.setUI(new CustomComboUI())` después de la creación y en `applyTheme()`.
4. Crear método helper `installComboUI(JComboBox<?> combo)` si no existe en la ventana.

## Skill de referencia completa
Para el patrón completo de estilizado de VentanaClientes, VentanaFacturacion y VentanaComprobantes (constantes, helpers, CustomComboUI, themeDateField, themeComboEditor, paneles, problemas conocidos), cargar el skill `swing-estilo-ventanas`.
