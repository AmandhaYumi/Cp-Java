$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
$downloadDir = Join-Path $PSScriptRoot '.runtime\downloads'
New-Item -ItemType Directory -Force -Path $downloadDir | Out-Null

if (-not (Test-Path '.runtime\postgres\pgsql\bin\pg_ctl.exe')) {
    Write-Host 'Baixando PostgreSQL 17.11 (binarios oficiais EDB)...'
    Invoke-WebRequest -Uri 'https://sbp.enterprisedb.com/getfile.jsp?fileid=1260616' -OutFile "$downloadDir\postgresql.zip"
    New-Item -ItemType Directory -Force -Path '.runtime\postgres' | Out-Null
    & tar.exe -xf "$downloadDir\postgresql.zip" -C '.runtime\postgres' pgsql/bin pgsql/lib pgsql/share
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao extrair PostgreSQL.' }
}
if (-not (Test-Path '.runtime\mailpit\mailpit.exe')) {
    Write-Host 'Baixando Mailpit 1.31.6 (release oficial)...'
    Invoke-WebRequest -Uri 'https://github.com/axllent/mailpit/releases/download/v1.31.6/mailpit-windows-amd64.zip' -OutFile "$downloadDir\mailpit.zip"
    Expand-Archive -Path "$downloadDir\mailpit.zip" -DestinationPath '.runtime\mailpit' -Force
}
Write-Host 'Dependencias locais prontas. Execute .\INICIAR-LOCAL.ps1.'
