$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$expectedRemote = 'https://github.com/AmandhaYumi/Cp-Java.git'
$actualRemote = & git remote get-url origin
if ($LASTEXITCODE -ne 0 -or $actualRemote -ne $expectedRemote) {
    throw 'O remote origin nao corresponde a AmandhaYumi/Cp-Java. Confira a configuracao antes de publicar.'
}
Write-Host 'Autentique o Git com a conta que possui acesso de escrita a AmandhaYumi/Cp-Java.'
& git push -u origin main
if ($LASTEXITCODE -ne 0) {
    throw 'A publicacao falhou. Entre na conta GitHub correta no IntelliJ/Git Credential Manager e tente novamente.'
}
Write-Host 'Projeto publicado: https://github.com/AmandhaYumi/Cp-Java'
