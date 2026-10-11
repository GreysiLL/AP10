$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
Push-Location $PSScriptRoot
try {
    & .\gradlew.bat :androidApp:assembleDebug :shared:testAndroidHostTest --no-daemon --no-configuration-cache --console=plain '-Pkotlin.compiler.execution.strategy=in-process'
    if ($LASTEXITCODE -ne 0) { throw 'Fallaron la compilacion o las pruebas de la practica 09.' }
    Write-Host 'Practica 09: APK y pruebas Android verificados.'
} finally { Pop-Location }
