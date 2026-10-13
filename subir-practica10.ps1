$ErrorActionPreference = 'Stop'
$gitPractica = 'C:\Program Files\Git\cmd\git.exe'
Set-Location $PSScriptRoot
$urlPractica = & $gitPractica remote get-url practica10
if ($urlPractica -ne 'https://github.com/GreysiLL/AP10.git') { throw 'El remoto no corresponde a AP10.' }
$estadoPractica = & $gitPractica status --porcelain
if ($estadoPractica) { throw 'Hay cambios sin registrar. No se sube una entrega incompleta.' }
& $gitPractica -c http.sslBackend=openssl push -u practica10 feature/auth-jwt-duran
if ($LASTEXITCODE -ne 0) { throw 'No se pudo subir la practica.' }
Write-Host 'Practica subida a https://github.com/GreysiLL/AP10/tree/feature/auth-jwt-duran'
