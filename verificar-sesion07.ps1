$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:GRADLE_USER_HOME = Join-Path $env:USERPROFILE '.gradle'
Push-Location $PSScriptRoot
try {
    # Reiniciar el proceso evita reutilizar un daemon con acceso restringido.
    & .\gradlew.bat --stop
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo detener el daemon anterior de Gradle.' }
    & .\gradlew.bat :androidApp:assembleDebug :shared:testAndroidHostTest --no-daemon --no-configuration-cache --console=plain --stacktrace
    if ($LASTEXITCODE -ne 0) { throw 'La compilacion o las pruebas fallaron. Comparte las ultimas lineas del error.' }
    Write-Host 'APK y pruebas Android verificados.'
} finally { Pop-Location }
