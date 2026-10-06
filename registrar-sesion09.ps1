$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
function G { & git -c "safe.directory=$($PSScriptRoot.Replace('\','/'))" @args; if ($LASTEXITCODE -ne 0) { throw 'Git no terminó correctamente.' } }
if ((G branch --show-current) -ne 'feature/expect-actual-duran') { throw 'Primero ejecuta iniciar-sesion09.ps1.' }
G add shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.kt shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.android.kt shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/Formato.ios.kt shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/presentation/producto/ProductoUi.kt docs/SESION09/01-error-sin-actual.log
G commit -m 'feat(moneda): implementa formato de soles con expect actual'
G add shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/domain/platform shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/domain/usecase/TextoParaCompartir.kt shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorAndroid.kt shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/CompartidorIos.kt shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/di shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/di shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/di shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/presentation/detalle shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/presentation/producto/ProductoScreen.kt shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/App.kt shared/src/commonMain/kotlin/pe/edu/upeu/pharmamobil/platform/AtrasDetalle.kt shared/src/androidMain/kotlin/pe/edu/upeu/pharmamobil/platform/AtrasDetalle.android.kt shared/src/iosMain/kotlin/pe/edu/upeu/pharmamobil/platform/AtrasDetalle.ios.kt
G commit -m 'feat(detalle): conecta compartir nativo mediante Koin'
G add shared/src/commonTest README.md settings.gradle.kts docs/SESION09 iniciar-sesion09.ps1 registrar-sesion09.ps1 subir-practica09.ps1
G commit -m 'test(sesion09): verifica compartir y documenta capacidades nativas'
G status --short

