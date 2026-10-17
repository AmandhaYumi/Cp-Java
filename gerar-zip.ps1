$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$deliveryDir = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\entrega'))
New-Item -ItemType Directory -Force -Path $deliveryDir | Out-Null
$archivePath = Join-Path $deliveryDir 'Mercadinho-CP5.zip'
& tar.exe -a -c -f $archivePath --exclude='./target' --exclude='*/target' --exclude='./.runtime' --exclude='./.secrets' --exclude='./.env' --exclude='./.git' --exclude='./.idea' -C $PSScriptRoot .
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar ZIP.' }
$entries = & tar.exe -tf $archivePath
if ($entries -match '(^|/)(\.secrets|\.runtime|target|\.git|\.env)(/|$)') {
    throw 'ZIP contem arquivos privados ou de build; nao envie este arquivo.'
}
Write-Host "ZIP gerado sem credenciais, dados privados ou binarios: $archivePath"
