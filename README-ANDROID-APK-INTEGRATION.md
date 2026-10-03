# Control Financiero — Android B1.5

Proyecto Android mínimo para generar el APK de Control Financiero mediante GitHub Actions.

## Qué cambia en B1.5

- Elimina completamente los recursos XML propios de `res/` para evitar errores por archivos renombrados como `1.txt` al cargar el proyecto desde el navegador de GitHub.
- La interfaz se crea directamente desde Kotlin.
- El tema e icono usan recursos nativos de Android.
- Mantiene la aplicación como un wrapper WebView de `https://controlfinanciero.cl/`.
- Mantiene Supabase, autenticación y lógica financiera en el sistema web de producción.
- Mantiene el workflow B1.4 que evita `sdkmanager` y compila con Android SDK del runner.

## Estructura

```text
.github/workflows/android.yml
app/build.gradle.kts
app/src/main/AndroidManifest.xml
app/src/main/java/cl/controlfinanciero/app/MainActivity.kt
build.gradle.kts
gradle.properties
settings.gradle.kts
README-ANDROID-APK-INTEGRATION.md
```

## GitHub

Subir el contenido de este ZIP directamente a la raíz de `P-Renault/Control-Financiero-Android`. No crear una carpeta intermedia `CCF-ANDROID-B1.5-src`.

Después: Actions → Build Control Financiero APK → Run workflow.
