$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
function G { & git -c "safe.directory=$($PSScriptRoot.Replace('\','/'))" @args; if($LASTEXITCODE -ne 0){throw 'Git no completo la operacion'} }
if(!(G branch --list develop)){ G branch develop main }
if(!(G branch --list feature/expect-actual-duran)){ G switch -c feature/expect-actual-duran develop } else { G switch feature/expect-actual-duran }
