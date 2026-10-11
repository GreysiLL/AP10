$ErrorActionPreference = 'Stop'
$taskGit = 'C:\Program Files\Git\cmd\git.exe'
& $taskGit -C $PSScriptRoot -c http.sslBackend=openssl push -u practica08 feature/crud-productos-duran
if ($LASTEXITCODE -ne 0) { throw 'No se pudo subir la practica. Comparte las ultimas lineas.' }
Write-Host 'Practica S08 subida a https://github.com/GreysiLL/act.prac-2/tree/feature/crud-productos-duran'
