This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…## Capacidades nativas — Sesión 9

La práctica agrega formato de soles con `expect/actual` y compartir un producto mediante una interfaz común.

### Cómo probar en Android

1. Ejecutar `androidApp` en Android Studio.
2. Abrir **Productos**, registrar Paracetamol con precio 24.50 y stock 10.
3. Tocar la tarjeta del producto: abre **Detalle del producto**.
4. Verificar el precio con moneda peruana y pulsar **Compartir**.
5. Android debe abrir su selector de aplicaciones con nombre, precio y stock. Volver al detalle y luego a la lista.

### Archivos para explicar el código

- `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.kt`: declara `expect fun formatearSoles`.
- `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.android.kt`: `actual` con NumberFormat y locale es_PE.
- `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.ios.kt`: `actual` con NSNumberFormatter.
- `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/presentation/producto/ProductoUi.kt`: transforma el precio antes de enviarlo a la pantalla.
- `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/domain/platform/Compartidor.kt`: contrato compartido para compartir texto.
- `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/domain/usecase/TextoParaCompartir.kt`: construye el mensaje del producto como indica la guía.
- `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorAndroid.kt`: Intent ACTION_SEND y selector nativo.
- `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorIos.kt`: UIActivityViewController.
- `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/presentation/detalle/`: ViewModel, estado y pantalla de detalle.
- `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/di/AppModule.kt` y los PlatformModule de Android/iOS: inyección de las implementaciones.

### Evidencias y límites

`docs/SESION09/01-error-sin-actual.log` conserva la compilación inicial, incluido el error real de falta de actual. `02-verificacion-android.log` contiene la compilación y las pruebas posteriores.

Esta base usa un repositorio en memoria, con registrar y listar: no contiene el CRUD REST de la sesión 8. Para cumplir ese prerrequisito falta conectar PharmaSoft y verificar GET /api/v1/productos. La implementación de iOS está preparada, pero no está compilada ni ejecutada: requiere Mac con Xcode. Las capturas de ejecución de ambos sistemas deben obtenerse en sus dispositivos; no se sustituyen por las pruebas unitarias.

Rama solicitada: `feature/expect-actual-duran`, desde `develop`. Ejecutar `iniciar-sesion09.ps1` desde la terminal del usuario para crearla; después de verificar los cambios, `registrar-sesion09.ps1` registra tres commits separados. No realiza push.

### Prueba manual Android confirmada

La ejecución del usuario confirmó la instalación de PharmaMobil y el selector nativo con el texto «galleta — S/ 3.50 — Stock: 3». Evidencia: [selector Android](docs/SESION09/android-compartir.png). Esto confirma compartir en Android. Este registro es anterior a las pruebas REST de S07 documentadas más abajo; iOS sigue sin ejecutarse.


## Sesión 7: consumo GET con Ktor

Ktor 3.6.0 y kotlinx.serialization consumen PharmaSoft con un HttpClient único inyectado por Koin.

| Plataforma | Motor | URL base |
|---|---|---|
| Emulador Android | OkHttp | http://10.0.2.2:8080/api/v1/ |
| Simulador iOS en Mac | Darwin | http://localhost:8080/api/v1/ |

Endpoint: `GET productos?pagina=0&tamanio=20`. La sesión carga la primera página (20 productos).
La respuesta se deserializa como `PaginaResponseDto<ProductoResponseDto>`: contenido, pagina, tamanio, totalElementos, totalPaginas y ultima.
ProductoResponseDto: id, nombre, precio, stock, estado, categoriaId y categoriaNombre. Se ignoran campos adicionales, como las fechas.
El mapper entrega Producto al dominio sin dependencias de Ktor ni anotaciones de serialización.

Mantener Docker (Oracle healthy) y PharmaSoft en el puerto 8080 abiertos. Ejecutar Android, entrar a Productos y comprobar los tres productos de prueba.
En Logcat filtrar `Ktor:` para ver GET y respuesta 200. Crear otro producto en Swagger y volver a entrar a Productos.
Desactivar la conexión del emulador y pulsar Reintentar para verificar el mensaje de error.
El registro desde la app sigue guardando en memoria y no aparece en el listado REST: el POST corresponde a la sesión 8.
HTTP se autoriza únicamente para los hosts locales indicados. En producción usar HTTPS.
La verificación de iOS requiere Mac y Xcode; preparar el código en Windows no acredita ejecución en iOS.

Verificación en Windows: powershell -NoProfile -ExecutionPolicy Bypass -File .\verificar-sesion07.ps1
OkHttp se fija en 5.3.2 con su BOM para mantener compileSdk 36: OkHttp 5.5 requiere SDK 37.



## Conectividad REST

Actividad autónoma S07: [catálogo, DTO y bitácora](docs/S07_AUTONOMA/actividad-autonoma.md).
Los cinco endpoints de productos se ejecutaron contra PharmaSoft; [resultados HTTP completos](docs/S07_AUTONOMA/endpoints-ejecutados.json).
Base Android: http://10.0.2.2:8080/api/v1/. Base iOS: http://localhost:8080/api/v1/ en la Mac del backend.
GET /productos devuelve página; POST devuelve 201; PUT devuelve 200; DELETE devuelve 204 y aplica baja lógica.
La Activity debug PruebasConexionActivity permite reproducir éxito, 404, desconexión, timeout de 1 ms y JSON estricto/tolerante.
Abrir después de instalar debug: `adb shell am start -n pe.edu.upeu.pharmamobil/.PruebasConexionActivity`.
Para PC-03 activar modo avión y desactivar Wi-Fi antes de ejecutar. Guardar Logcat filtrado por `Ktor:` y las capturas.
Los registros deben distinguir pruebas HTTP del backend, pruebas Ktor automatizadas y ejecución visual del emulador.
La evidencia iOS requiere una ejecución real en Mac/Xcode.

Repositorio de entrega S07 autónoma: https://github.com/GreysiLL/AA

Las cinco pruebas de conexión pasaron en Android (Pixel 9, API 36). [Bitácora real](docs/S07_AUTONOMA/bitacora-android.json), [registro Ktor](docs/S07_AUTONOMA/ktor-android.log) e [informe PDF](docs/S07_AUTONOMA/S07_ActividadAutonoma_Duran_Castro.pdf).

iOS: no ejecutado por falta de Mac/Xcode. Esta evidencia queda pendiente.
