# Control Financiero — Android APK B2.0

Wrapper Android nativo que abre la producción en https://controlfinanciero.cl/.

## Objetivos B2.0
- Punto de entrada: portal real de **Iniciar sesión**, no el landing.
- Fuerza la carga de `CCF-AUTH-BOOT-FINAL.js` si la página no lo ha inicializado todavía.
- Conserva autenticación Supabase y lógica financiera existentes.
- Icono CCF empaquetado como launcher icon.
- Compacta la vista del sistema a escala aproximada 90% después de autenticar.
- No modifica Supabase, tablas, RLS ni cálculos financieros.

## Estructura del repositorio
Subir el **contenido de este paquete en la raíz** del repositorio Android, de modo que `.github/` y `app/` queden directamente en la raíz.

## Compilación
GitHub Actions → `Build Control Financiero APK` → `Run workflow`.

El APK de prueba se publica como artifact `Control-Financiero-APK`.
