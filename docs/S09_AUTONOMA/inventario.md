# Inventario de capacidades nativas

Compartidor es una interfaz común con inyección, no un expect. Se registran todos los expect propios, incluido Atrás en detalle.

## Moneda

`expect fun formatearSoles(valor: Double): String`

- commonMain: `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.kt`
- androidMain: `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.android.kt`; API: NumberFormat + Locale(es, PE)
- iosMain: `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.ios.kt`; API: NSNumberFormatter + NSLocale(es_PE)

## Información del dispositivo

`expect class InfoDispositivo() { val sistema: String; val version: String }`

- commonMain: `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/platform/InfoDispositivo.kt`
- androidMain: `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/InfoDispositivo.android.kt`; API: Build.VERSION.RELEASE
- iosMain: `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/InfoDispositivo.ios.kt`; API: UIDevice.systemName y systemVersion

## Inyección y red

`expect val platformModule: Module`

- commonMain: `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/di/AppModule.kt`
- androidMain: `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/di/PlatformModule.android.kt`; API: Koin androidContext, OkHttp, 10.0.2.2
- iosMain: `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/di/PlatformModule.ios.kt`; API: Koin sin Context, Darwin, localhost

## Atrás en detalle

`@Composable expect fun AtrasDetalle(habilitado: Boolean, volver: () -> Unit)`

- commonMain: `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/platform/AtrasDetalle.kt`
- androidMain: `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/AtrasDetalle.android.kt`; API: BackHandler de Activity Compose
- iosMain: `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/AtrasDetalle.ios.kt`; API: Sin interceptor: botón Volver común

## Compartir (interfaz con DI)

`interface Compartidor { fun compartir(texto: String) }`

- commonMain: `shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/domain/platform/Compartidor.kt`
- androidMain: `shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorAndroid.kt`; API: Intent.ACTION_SEND, Context, EXTRA_TEXT
- iosMain: `shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorIos.kt`; API: UIActivityViewController, UIWindowScene
