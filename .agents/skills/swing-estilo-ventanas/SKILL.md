---
name: swing-estilo-ventanas
description: Patrón completo de estilizado para ventanas Java Swing del proyecto SistemaContable. Usar cuando se mencionen VentanaClientes, VentanaFacturacion, VentanaComprobantes, o se pida crear/modificar ventanas "segun el estilo de". Trigger: "estilo de ventana", "estilo de", "segun el estilo", "copiar el estilo", "misma paleta".
---

# Swing Estilo Ventanas — SistemaContable

Este skill documenta el patrón completo de estilizado (tema claro/oscuro) que comparten las tres ventanas del proyecto (`VentanaClientes`, `VentanaFacturacion`, `VentanaComprobantes`). Cuando el usuario pida crear una ventana nueva o modificar una existente "según el estilo de X", estas son las reglas a seguir.

## Archivos de referencia

| Ventana | Ruta |
|---|---|---|
| VentanaClientes | `src/main/java/com/els/facturacion/vista/VentanaClientes.java` |
| VentanaFacturacion | `src/main/java/com/els/facturacion/vista/VentanaFacturacion.java` |
| VentanaComprobantes | `src/main/java/com/els/facturacion/vista/VentanaComprobantes.java` |
| VentanaPagos | `src/main/java/com/els/facturacion/vista/VentanaPagos.java` |
| VentanaRecibos | `src/main/java/com/els/facturacion/vista/VentanaRecibos.java` |
| VentanaPagosRecibos | `src/main/java/com/els/facturacion/vista/VentanaPagosRecibos.java` |
| VentanaGestionComprobantes | `src/main/java/com/els/facturacion/vista/VentanaGestionComprobantes.java` |
| TablaRenderer | `src/main/java/com/els/facturacion/vista/TablaRenderer.java` |
| Theme | `src/main/java/com/els/facturacion/vista/Theme.java` |
| AutoCompleteComboBox | `src/main/java/com/els/facturacion/util/AutoCompleteComboBox.java` |

## Constantes obligatorias (copiar textual en cada ventana)

```java
private static final Font FUENTE_BOTON = new Font("Segoe UI", Font.BOLD, 11);
private static final Font FUENTE_INPUT = new Font("Segoe UI", Font.PLAIN, 12);
private static final Font FUENTE_INPUT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
private static final Font FUENTE_LABEL = new Font("Segoe UI", Font.PLAIN, 11);
private static final Color DISABLED_FG_LIGHT = new Color(95, 97, 106);
private static final Color DISABLED_FG_DARK = new Color(210, 207, 190);
private static final Color LIGHT_READONLY_BG = new Color(236, 237, 241);
private static final Color LIGHT_EDITABLE_BG = new Color(255, 253, 230);
private static final Color DARK_READONLY_BG = new Color(28, 33, 55);
private static final Color DARK_EDITABLE_BG = new Color(22, 27, 45);
```

`FUENTE_TABLA` se define siempre: `new Font("Segoe UI", Font.PLAIN, 12)`. Si la ventana no tiene JTable igual no da error, y facilita agregar una después.

## Métodos helper obligatorios

```java
private Color getDisabledFg() {
    return currentTheme.bgBase.getRed() > 128 ? DISABLED_FG_LIGHT : DISABLED_FG_DARK;
}

private Color getFieldBg(boolean editing) {
    return currentTheme.bgBase.getRed() > 128
        ? (editing ? LIGHT_EDITABLE_BG : LIGHT_READONLY_BG)
        : (editing ? DARK_EDITABLE_BG : DARK_READONLY_BG);
}
```

## CustomComboUI (inner class)

```java
private static class CustomComboUI extends BasicComboBoxUI {
    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        g.setColor(comboBox.getBackground());
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        ListCellRenderer<Object> renderer = comboBox.getRenderer();
        Component c;
        if (hasFocus && !isPopupVisible(comboBox)) {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
        } else {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
        }
        c.setFont(comboBox.getFont());
        if (hasFocus && !isPopupVisible(comboBox)) {
            c.setForeground(listBox.getSelectionForeground());
            c.setBackground(listBox.getSelectionBackground());
        } else {
            c.setForeground(comboBox.getForeground());
            c.setBackground(comboBox.getBackground());
        }
        currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
```

## Métodos helper para combos

```java
private void installComboUI(JComboBox<?> combo) {
    combo.setUI(new CustomComboUI());
}

private void themeComboEditor(JComboBox<?> combo, Theme t) {
    Component editorComp = combo.getEditor().getEditorComponent();
    if (editorComp instanceof JTextField) {
        JTextField ed = (JTextField) editorComp;
        ed.setBackground(getFieldBg(combo.isEnabled()));
        ed.setForeground(combo.isEnabled() ? t.textPrimary : getDisabledFg());
        ed.setDisabledTextColor(getDisabledFg());
        ed.setCaretColor(t.textPrimary);
    }
}
```

## Método helper para JDateChooser

```java
private void themeDateField(JComponent comp, Theme t) {
    if (comp instanceof JTextField) {
        JTextField tf = (JTextField) comp;
        tf.setBackground(getFieldBg(true));
        tf.setForeground(t.textPrimary);
        tf.setDisabledTextColor(getDisabledFg());
        tf.setCaretColor(t.textPrimary);
        tf.setFont(FUENTE_INPUT_BOLD);
    } else {
        for (Component c : comp.getComponents()) {
            if (c instanceof JTextField) {
                JTextField tf = (JTextField) c;
                tf.setBackground(getFieldBg(true));
                tf.setForeground(t.textPrimary);
                tf.setDisabledTextColor(getDisabledFg());
                tf.setCaretColor(t.textPrimary);
                tf.setFont(FUENTE_INPUT_BOLD);
            }
            if (c instanceof Container) {
                themeDateField((JComponent) c, t);
            }
        }
    }
}

private void installDateFocusListener(JComponent chooser) {
    for (Component c : chooser.getComponents()) {
        if (c instanceof JTextField) {
            JTextField tf = (JTextField) c;
            tf.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    tf.setForeground(currentTheme.textPrimary);
                }
            });
            return;
        }
        if (c instanceof java.awt.Container) {
            installDateFocusListener((JComponent) c);
        }
    }
}
```

## Reglas de creación de componentes

### JTextField
- `new JTextField()` — sin anonymous subclass
- Font: `FUENTE_INPUT_BOLD`
- Excepciones: email y teléfono usan `FUENTE_INPUT` (PLAIN)
- En `initComponents`: `setFont(...)`, `setDisabledTextColor(getDisabledFg())`, `setCaretColor(currentTheme.textPrimary)`
- En `applyTheme`: `setBackground(getFieldBg(isEnabled()))`, `setForeground(t.textPrimary)`, `setDisabledTextColor(getDisabledFg())`, `setCaretColor(t.textPrimary)`

### JComboBox
- `new JComboBox<>(items)` — sin anonymous subclass (crítico para WindowBuilder)
- Font: `FUENTE_INPUT_BOLD`
- Llamar `installComboUI(combo)` inmediatamente después de crear
- Renderer personalizado inline en `initComponents`:
```java
combo.setRenderer(new DefaultListCellRenderer() {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setBackground(getFieldBg(combo.isEnabled()));
        setForeground(combo.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
        setFont(combo.getFont());
        return this;
    }
    @Override
    public void paintComponent(Graphics g) {
        setBackground(getFieldBg(combo.isEnabled()));
        setForeground(combo.isEnabled() ? currentTheme.textPrimary : getDisabledFg());
        super.paintComponent(g);
    }
});
```
### AutoCompleteComboBox (combo editable con filtro)
- Después de crearlo, llamar `themeComboEditor(combo, currentTheme)` en `initComponents`
- En `applyTheme`: llamar `themeComboEditor(combo, t)`
- El combo editable tiene un editor JTextField aparte; `themeComboEditor` lo estiliza por separado del renderer

### JDateChooser (carga vía reflexión)
```java
private JComponent crearDateChooser() {
    try {
        Class<?> clazz = Class.forName("com.toedter.calendar.JDateChooser");
        JComponent chooser = (JComponent) clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("setDateFormatString", String.class).invoke(chooser, "dd/MM/yyyy");
        clazz.getMethod("setDate", java.util.Date.class).invoke(chooser,
            java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        chooser.setPreferredSize(new Dimension(110, 24));
        chooser.addPropertyChangeListener("date", e -> themeDateField(chooser, currentTheme));
        installDateFocusListener(chooser);
        return chooser;
    } catch (Exception e) {
        JTextField tf = new JTextField(LocalDate.now().format(FMT));
        tf.setPreferredSize(new Dimension(110, 24));
        tf.setEditable(false);
        return tf;
    }
}
```
- Llamar `themeDateField(chooser, currentTheme)` después de crear en `initComponents`
- En `applyTheme`: llamar `themeDateField(chooser, t)`
- `installDateFocusListener` se llama UNA SOLA VEZ en `crearDateChooser` (no en applyTheme) para evitar FocusListener duplicados
- **Problema conocido**: JDateChooser resetea el foreground a negro al perder foco → `installDateFocusListener` lo corrige

### JRadioButton
- Override de `paintComponent`:
```java
JRadioButton radio = new JRadioButton(text) {
    @Override
    protected void paintComponent(Graphics g) {
        setForeground(isEnabled() ? currentTheme.textPrimary : getDisabledFg());
        super.paintComponent(g);
    }
};
```

### JCheckBox
- Font: `FUENTE_LABEL`
- En `initComponents`: `setFont(FUENTE_LABEL)`, `setBackground(currentTheme.bgBase)`, `setForeground(currentTheme.textPrimary)`
- En `applyTheme`: `setBackground(t.bgBase)`, `setForeground(t.textPrimary)`

### JButton
- Font: `FUENTE_BOTON`
- En `initComponents`: `setFont(FUENTE_BOTON)`, `setForeground(currentTheme.textPrimary)`, `setBackground(currentTheme.btnBg)`, `setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))`, `setFocusPainted(false)`
- En `applyTheme`: `setBackground(t.btnBg)`, `setForeground(t.textPrimary)`

### JTable
- Font de tabla: `FUENTE_TABLA`
- Configuración de tabla:
```java
tabla.setFont(FUENTE_TABLA);
tabla.setRowHeight(22);
tabla.setIntercellSpacing(new Dimension(3, 2));
tabla.setDefaultRenderer(Object.class, new TablaRenderer(tabla, columnasNegrita));
```
- `TablaRenderer` usa `table.getFont()` y aplica `deriveFont(Font.BOLD)` a columnas configuradas como negrita
- `columnasNegrita` es un `Set<Integer>` con índices de columnas que deben ir en negrita
- En `applyTheme` de VentanaClientes (referencia):
```java
tabla.setForeground(t.textPrimary);
tabla.setBackground(t.bgInput);
tabla.setSelectionForeground(t.textPrimary);
tabla.setSelectionBackground(t.brand);
tabla.setGridColor(t.bgBase);
if (tabla.getTableHeader() != null) {
    tabla.getTableHeader().setBackground(t.bgSurface);
    tabla.getTableHeader().setForeground(t.textPrimary);
}
```

### JLabel
- Font: `FUENTE_LABEL` (labels de campo) o `FUENTE_BOTON` (labels en pestañas)
- Color: siempre `textPrimary` (nunca se deshabilitan)
- **TODO label debe ser field** (no anonymous) para poder themearlo en `applyTheme`. Buscar `new JLabel(...)` en `initComponents` y asegurarse de que todos sean asignados a fields.
- En `initComponents`: `setFont(FUENTE_LABEL)`, `setForeground(currentTheme.textPrimary)`
- En `applyTheme`: `setForeground(t.textPrimary)` (salvo labels especiales como `lblTitulo` que usan `t.brand`)
- `themeLabels(container, t)` recorre recursivamente y setea font/LAF; se llama desde `applyTheme`
- **Títulos importantes**: después de `themeLabels`, restaurar font y foreground de:
  - `lblTituloDatos`: `FUENTE_INPUT_BOLD.deriveFont(18f)` (o BOLD 18), foreground `t.brand`
  - `lblTituloOp`: foreground `t.brand`
  - `lblSubtituloOp`: actualizar texto
  - Cualquier label con font especial debe restaurarse DESPUÉS de themeLabels

## Reglas de paneles (applyTheme)

| Panel | Color | Notas |
|---|---|---|
| panelIzquierdo | `t.bgBase` | — |
| panelCentro | `t.bgBase` | — |
| panelDerecho | `t.bgBase` | — |
| panelBusqueda | `t.bgSurface` | — |
| panelTituloDatos | `t.bgSurface` | — |
| panelBotonesReceptor | `t.bgSurface` | VentanaFacturacion |
| panelPrincipalWrapper | `t.bgSurface` | — |
| panelPrincipal | `t.bgSurface` | — |
| datosCard / datosWrapper | `t.bgSurface` | — |
| panelOperacion | `t.bgSurface` | — |
| panelSuperiorOp | `t.bgSurface` | — |
| panelSur | `t.bgBase` | — |
| panelItems | `t.bgBase` | — |
| panelTotales | `t.bgBase` | — |
| panelEmitir | `t.bgBase` | — |
| panelAnterior | `t.bgBase` | — |
| panelNav | `t.bgSurface` | — |
| centerCol | `t.bgSurface` + `CompoundBorder(LineBorder(t.brand), EmptyBorder(...))` | VentanaFacturacion |
| panelBotones (Comprobantes) | `t.bgBase` | VentanaComprobantes |
| statusBar | claro: `200,208,225` / oscuro: `50,58,80` | — |
| secPuntoVenta, secEmision, secReceptor | `t.bgSurface` + `TitledBorder(t.brand, ...)` | — |

## Combo renderer

El renderer se instala inline (no como clase separada). Usa `getFieldBg(combo.isEnabled())` para fondo y `combo.isEnabled() ? currentTheme.textPrimary : getDisabledFg()` para foreground. El `paintComponent` override repite la lógica porque el renderer se recicla y podría tener colores residuales de selección.

## applyTheme — orden de operaciones

1. Asignar colores a todos los paneles (con null-check)
2. Llamar `themeLabels(contenedorRaíz, t)` para recorrer JLabels
3. Restaurar títulos que themeLabels sobreescribe
4. Asignar colores a botones
5. Asignar colores a campos (JTextField, JComboBox, JRadioButton, JCheckBox) con `getFieldBg(campo.isEnabled())`
6. Llamar `themeDateField` para cada JDateChooser
7. Llamar `themeComboEditor` para cada AutoCompleteComboBox
8. Re-instalar `installComboUI` para cada combo (protege contra cambios de LAF)
9. Asignar colores de tabla

## Regla de oro: todo componente visible debe ser field

- **Paneles**: todo `JPanel` que contenga otros componentes debe ser field. Los paneles creados como locales (`JPanel x = new JPanel(...)`) en un factory method no se pueden theme.
- **Labels**: todo `JLabel` debe ser field, incluso los labels simples como `new JLabel("IMPORTE:")`.
- **Excepción**: paneles temporales que son contenedores de layout sin color propio (ej: `panelNorte` en VentanaRecibos que agrupa titulo+filtro) pueden ser locales si no necesitan color distinto al padre.

Para detectar componentes faltantes en una ventana nueva:
1. Buscar todos los `new JLabel(` en `initComponents` — cada uno debe ser field y estar en `applyTheme`
2. Buscar todos los `new JPanel(` en `initComponents` — cada panel visible debe ser field y estar en `applyTheme`
3. Buscar todos los `new JTextField(` y `new JComboBox(` — deben tener estilizado completo

## Theme listener / reflexión

- `VentanaPrincipal.addThemeListener(this)` se registra en el constructor de cada ventana
- `VentanaPrincipal.notifyThemeListeners` usa `getDeclaredMethod("applyTheme", Theme.class)` vía reflexión
- **El método applyTheme debe estar DECLARADO en la clase exacta** (no heredado) o `getDeclaredMethod` falla

## Paleta de colores (Theme.java)

Los colores están en la clase `com.els.facturacion.vista.Theme`. Campos relevantes:
- `bgBase` — fondo general
- `bgSurface` — fondo secundario
- `bgElevated` — fondo elevado
- `bgInput` — fondo de inputs
- `textPrimary` — texto principal
- `textSecondary` — texto secundario
- `brand` — color de acento/borde
- `btnBg` — fondo de botón
- `danger` — color de peligro/error
- `success` — color de éxito

## DocumentFilter en JTextField con formato ($, espacios)

Si un JTextField tiene un `DocumentFilter` que valida el texto completo combinando el existente con el nuevo (`sb.insert(offset, text)`), ese filtro **falla en `replace`** (cuando el usuario selecciona todo y teclea). En vez de simular el resultado, validar solo el texto entrante:

```java
private boolean esNumeroValido(String text) {
    if (text == null || text.isEmpty()) return true;
    return text.matches("[\\d,]*");
}
```

Las asignaciones programáticas (`setText()`) deben usar un flag `boolean actualizandoCampo` para saltarse el filtro.

## Contenedor padre con contentPane extraído (tabs)

Cuando una ventana se usa como tab dentro de otra (vía `getContentPane()` + `remove()` + `addTab()`), el contentPane original se muestra pero el JFrame se descarta:
- `getContentPane()` en `applyTheme` devuelve un contentPane **nuevo** (porque el original fue removido del JFrame). **No usar `getContentPane()` en applyTheme** para ventanas que son tabs.
- En su lugar, themeer los paneles hijos directamente via fields.
- El tab container (VentanaPagosRecibos, VentanaGestionComprobantes) no necesita fields ni applyTheme complejo; solo themea el JTabbedPane.

## Reglas adicionales para ventanas tipo tab

Una ventana que será tab dentro de otra:
- No debe cerrar la aplicación al cerrarse (`DISPOSE_ON_CLOSE`)
- El constructor debe llamar `VentanaPrincipal.addThemeListener(this)` **y** `applyTheme(currentTheme)` al final
- Todos los paneles visibles deben ser fields (no locales) para poder themearlos en `applyTheme`
- La ventana se crea, se extrae su contentPane y se agrega al JTabbedPane:
```java
VentanaPagos pagosView = new VentanaPagos();
Container pagosContent = pagosView.getContentPane();
pagosView.remove(pagosContent);
tabbedPane.addTab("Pagos", pagosContent);
```

## Problemas conocidos y soluciones

| Problema | Síntoma | Solución |
|---|---|---|
| Combo muestra fondo blanco al deshabilitar | El LAF pinta con `UIManager.getColor("ComboBox.disabledBackground")` | `CustomComboUI.paintCurrentValueBackground` usa `comboBox.getBackground()` |
| Combo muestra foreground negro al deshabilitar | `BasicComboBoxUI` pinta con el color por defecto | `CustomComboUI.paintCurrentValue` usa `comboBox.getForeground()` |
| JDateChooser texto se vuelve negro al perder foco | El date editor resetea foreground | `installDateFocusListener` re-aplica `currentTheme.textPrimary` en focusLost |
| JDateChooser resetea foreground al cambiar fecha | PropertyChangeListener("date") → themeDateField | Listener en crearDateChooser |
| AutoCompleteComboBox no se themea | El editor es un JTextField aparte del combo | `themeComboEditor` themea el editor por separado |
| themeLabels sobreescribe fonts de títulos | Pone todos los labels con FUENTE_LABEL | Restaurar font/foreground DESPUÉS de themeLabels |
| WindowBuilder no reconoce el constructor | Anonymous subclasses en field initializers | Crear componentes con `new JComboBox<>()` plano, personalizar después con helpers |
| DocumentFilter bloquea escritura | `esNumeroValido` usa `sb.insert()` en vez de `sb.replace()` | Validar solo el texto entrante (`text.matches("[\\d,]*")`), no el combinado |
| JLabel anónimo no cambia de tema | `new JLabel(...)` sin field → no se puede theme | TODO label debe ser field y agregarse a applyTheme |
| Panel local no cambia de tema | `JPanel xxx = new JPanel(...)` → no se puede theme | TODO panel visible debe ser field y agregarse a applyTheme |
| contentPane no se themea en tabs | `getContentPane()` devuelve un container nuevo tras el strip | No usar getContentPane() en applyTheme; themear paneles hijos como fields |
