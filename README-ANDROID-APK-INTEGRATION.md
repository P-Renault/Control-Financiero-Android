# Control Financiero — Android APK B1.4

## Corrección del workflow

La ejecución B1.3 falló con:

`Process completed with exit code 127`

en el paso `Set up Android SDK`.

La causa fue la llamada a `sdkmanager`, que no estaba disponible en el PATH del runner.

B1.4 elimina completamente esa llamada. El workflow usa directamente el Android SDK preinstalado por GitHub en `ubuntu-24.04` y verifica que exista Android platform 35 antes de compilar.

## Despliegue

Sube TODO el contenido de este ZIP a la raíz del repositorio `Control-Financiero-Android`, reemplazando el contenido anterior.

Estructura:

```text
.github/workflows/android.yml
app/
build.gradle.kts
gradle.properties
settings.gradle.kts
README-ANDROID-APK-INTEGRATION.md
```

Luego:

1. GitHub → Actions.
2. Build Control Financiero APK.
3. Run workflow.
4. Esperar el resultado.
5. Si termina verde, abrir Artifacts.
6. Descargar `Control-Financiero-APK`.

No ejecutes `sdkmanager` manualmente.
No agregues `android-actions/setup-android`.
No cambies el proyecto web ni Supabase.

## Release

Para publicar automáticamente el APK en GitHub Releases, crea un tag:

`v1.0.0`

y súbelo al repositorio.

## APK

La compilación genera una versión debug para pruebas directas en Android.

Una vez validada, se puede generar una versión release firmada para distribución.
