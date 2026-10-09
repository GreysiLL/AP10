$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:GRADLE_USER_HOME = Join-Path $env:USERPROFILE '.gradle'
Push-Location $PSScriptRoot
try {
    & .\gradlew.bat :androidApp:assembleDebug :shared:testAndroidHostTest --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'La compilacion o las pruebas fallaron. Comparte las ultimas lineas del error.' }
    Write-Host 'APK y pruebas Android verificados.'
} finally { Pop-Location }
