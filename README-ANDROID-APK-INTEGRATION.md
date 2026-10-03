# Control Financiero — Android APK B1.7

Wrapper Android para `https://controlfinanciero.cl/`.

## Objetivo de B1.7

La primera pantalla de la aplicación debe ser el login real del sistema CCF (el `ccf-auth-gate` existente), no el landing público.

`MainActivity.kt` carga el sitio productivo y activa el botón existente `data-b230-open="login"`. No se duplica la autenticación ni se modifica Supabase.

## Corrección B1.7

El workflow verifica los archivos fundamentales y, si por una carga manual desde Android falta `app/src/main/AndroidManifest.xml`, lo crea automáticamente antes de ejecutar Gradle. Esto evita el error de `processDebugMainManifest` por manifiesto inexistente.

## Estructura raíz

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

No se requiere carpeta `res/`.

## Compilación

GitHub Actions → **Build Control Financiero APK** → **Run workflow**.

El Artifact generado es `Control-Financiero-APK` y contiene `app-debug.apk`.
