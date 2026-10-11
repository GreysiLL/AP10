$ErrorActionPreference = 'Stop'
$taskGit = 'C:\Program Files\Git\cmd\git.exe'
$taskUrl = 'https://github.com/GreysiLL/Activi-09-pract.git'
$taskRemoto = & $taskGit -C $PSScriptRoot remote get-url actividad09
if ($LASTEXITCODE -ne 0 -or $taskRemoto -ne $taskUrl) { throw 'El remoto actividad09 no coincide con el repositorio indicado.' }
$taskEstado = & $taskGit -C $PSScriptRoot status --porcelain
if ($taskEstado) { throw 'Hay cambios locales sin registrar. Comparte git status antes de subir.' }
& $taskGit -C $PSScriptRoot -c http.sslBackend=openssl push -u actividad09 feature/expect-actual-duran
if ($LASTEXITCODE -ne 0) { throw 'No se pudo subir la practica 09. Comparte las ultimas lineas.' }
Write-Host 'Practica subida: https://github.com/GreysiLL/Activi-09-pract/tree/feature/expect-actual-duran'
