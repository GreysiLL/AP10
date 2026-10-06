$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
function G { & git -c "safe.directory=$($PSScriptRoot.Replace('\','/'))" @args; if ($LASTEXITCODE -ne 0) { throw 'Git no terminó correctamente. No se forzó ninguna subida.' } }
if ((G branch --show-current) -ne 'feature/expect-actual-duran') { throw 'Selecciona feature/expect-actual-duran antes de subir.' }
$url='https://github.com/GreysiLL/Activi-09-pract.git'
$remotes=G remote
if ($remotes -contains 'actividad09') {
    if ((G remote get-url actividad09) -ne $url) { throw 'El remoto actividad09 ya apunta a otra dirección.' }
} else { G remote add actividad09 $url }
$pendiente=G status --porcelain
if ($pendiente) { & "$PSScriptRoot/registrar-sesion09.ps1" }
if (G status --porcelain) { throw 'Quedan cambios sin registrar. Revisa git status.' }
G push -u actividad09 feature/expect-actual-duran
G push actividad09 develop
G push actividad09 feature/expect-actual-duran:main
Write-Host 'Práctica subida: https://github.com/GreysiLL/Activi-09-pract'
