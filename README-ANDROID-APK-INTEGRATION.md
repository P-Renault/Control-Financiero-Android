# Control Financiero — Android APK B1.0

## Qué es

Aplicación Android ligera que ejecuta la versión de producción de Control Financiero dentro de un WebView seguro:

`https://controlfinanciero.cl/`

No duplica la lógica financiera ni la base de datos. Supabase continúa siendo el backend de producción.

## Compilación

El proyecto está preparado para GitHub Actions.

1. Crear/subir este proyecto a un repositorio GitHub.
2. Ir a **Actions**.
3. Ejecutar **Build Control Financiero APK** con `workflow_dispatch`.
4. Descargar el artefacto `Control-Financiero-Android`.
5. Instalar `Control-Financiero.apk` en Android.

Para publicar automáticamente una descarga en GitHub Releases, crear un tag como:

`v1.0.0`

El workflow compilará el APK y lo adjuntará al Release.

## URL de descarga para el landing

Una vez creado el Release `v1.0.0`, el enlace estable puede ser:

`https://github.com/p-renault/Finanzas/releases/latest/download/Control-Financiero.apk`

El botón del landing debe apuntar a ese recurso **solo después de que el APK haya sido publicado en Releases**.

## Seguridad y limitaciones

- El APK no contiene credenciales de Supabase.
- El APK requiere Internet.
- Los datos permanecen en el backend existente.
- No se modifica la aplicación web.
- No requiere Google Play Store.
- La instalación manual de APK requiere que Android permita instalar desde la fuente utilizada.

## Estado de esta entrega

Este paquete es el **proyecto Android compilable**, no un APK ya compilado. El entorno de trabajo utilizado para preparar esta entrega no dispone del Android SDK/Gradle necesario para producir y verificar un binario APK localmente. GitHub Actions queda configurado para realizar la compilación reproducible.
