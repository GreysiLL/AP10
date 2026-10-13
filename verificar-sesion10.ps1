$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
Push-Location $PSScriptRoot
try {
    & .\gradlew.bat :androidApp:assembleDebug :shared:testAndroidHostTest --no-daemon --no-configuration-cache --console=plain '-Pkotlin.compiler.execution.strategy=in-process' '-Pkotlin.incremental=false'
    if ($LASTEXITCODE -ne 0) { throw 'Fallaron la compilacion o las pruebas de S10. Comparte las ultimas lineas.' }
    Write-Host 'Sesion 10 compilada y pruebas aprobadas.'
} finally { Pop-Location }
