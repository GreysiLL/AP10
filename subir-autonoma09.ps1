$ErrorActionPreference = 'Stop'
$gitPractica = 'C:\Program Files\Git\cmd\git.exe'
Set-Location $PSScriptRoot
$urlPractica = & $gitPractica remote get-url autonoma09
if ($urlPractica -ne 'https://github.com/GreysiLL/SE3AA.git') { throw 'El remoto no corresponde a SE3AA.' }
$estadoPractica = & $gitPractica status --porcelain
if ($estadoPractica) { throw 'Hay cambios sin registrar. No se sube una entrega incompleta.' }
& $gitPractica -c http.sslBackend=openssl push -u autonoma09 feature/autonoma09-duran
if ($LASTEXITCODE -ne 0) { throw 'No se pudo subir la actividad.' }
Write-Host 'Actividad subida a https://github.com/GreysiLL/SE3AA/tree/feature/autonoma09-duran'
