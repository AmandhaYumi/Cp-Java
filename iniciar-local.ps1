$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (Test-Path 'C:\Program Files\Java\jdk-21\bin\java.exe') {
    $env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
}
if (-not (Test-Path '.secrets\cloudamqp.properties')) {
    throw 'Configure .secrets/cloudamqp.properties conforme o README.'
}
if (-not (Test-Path '.runtime\postgres\pgsql\bin\pg_ctl.exe') -or
    -not (Test-Path '.runtime\mailpit\mailpit.exe')) {
    throw 'Execute .\preparar-ambiente.ps1 uma vez antes de iniciar.'
}

$pgBin = Join-Path $PSScriptRoot '.runtime\postgres\pgsql\bin'
$pgData = Join-Path $PSScriptRoot '.runtime\postgres-data'
$passwordFile = Join-Path $PSScriptRoot '.secrets\postgres-password.txt'
if (-not (Test-Path $passwordFile)) {
    if (Test-Path "$pgData\PG_VERSION") { throw 'O banco existe mas seu arquivo de senha esta ausente.' }
    [System.IO.File]::WriteAllText($passwordFile, [Guid]::NewGuid().ToString('N'))
}
$env:LOCAL_DATABASE_PASSWORD = (Get-Content -Raw $passwordFile).Trim()
$env:PGPASSWORD = $env:LOCAL_DATABASE_PASSWORD

if (-not (Test-Path "$pgData\PG_VERSION")) {
    & "$pgBin\initdb.exe" -D $pgData -U mercadoexpress --pwfile=$passwordFile --auth=scram-sha-256 --encoding=UTF8 --locale=C
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao inicializar PostgreSQL.' }
}
& "$pgBin\pg_ctl.exe" -D $pgData status *> $null
if ($LASTEXITCODE -ne 0) {
    & "$pgBin\pg_ctl.exe" -D $pgData -l "$PSScriptRoot\.runtime\postgres.log" -o '-h 127.0.0.1 -p 55432' -w start
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao iniciar PostgreSQL. Veja .runtime/postgres.log.' }
}
$databaseExists = & "$pgBin\psql.exe" -h 127.0.0.1 -p 55432 -U mercadoexpress -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='mercadoexpress'"
if ($LASTEXITCODE -ne 0) { throw 'Falha ao conectar no PostgreSQL.' }
if ($databaseExists -ne '1') {
    & "$pgBin\createdb.exe" -h 127.0.0.1 -p 55432 -U mercadoexpress mercadoexpress
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao criar banco mercadoexpress.' }
}
Remove-Item Env:\PGPASSWORD

if (-not (Get-NetTCPConnection -State Listen -LocalPort 1025 -ErrorAction SilentlyContinue)) {
    Start-Process -FilePath "$PSScriptRoot\.runtime\mailpit\mailpit.exe" -WindowStyle Hidden -WorkingDirectory $PSScriptRoot -ArgumentList @('--listen','127.0.0.1:8025','--smtp','127.0.0.1:1025','--database',"$PSScriptRoot\.runtime\mailpit.db",'--disable-version-check') -RedirectStandardOutput "$PSScriptRoot\.runtime\mailpit.stdout.log" -RedirectStandardError "$PSScriptRoot\.runtime\mailpit.stderr.log"
}
if (-not (Get-NetTCPConnection -State Listen -LocalPort 9090 -ErrorAction SilentlyContinue)) {
    & .\mvnw.cmd -f banco-tranquilo\pom.xml -DskipTests package
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar o servico Banco Tranquilo.' }
    Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" -WindowStyle Hidden -WorkingDirectory $PSScriptRoot -ArgumentList @('-jar',"$PSScriptRoot\banco-tranquilo\target\banco-tranquilo-0.0.1-SNAPSHOT.jar") -RedirectStandardOutput "$PSScriptRoot\.runtime\banco.stdout.log" -RedirectStandardError "$PSScriptRoot\.runtime\banco.stderr.log"
    $bankReady = $false
    for ($attempt = 0; $attempt -lt 30; $attempt++) {
        try {
            $bankHealth = Invoke-RestMethod 'http://127.0.0.1:9090/actuator/health'
            if ($bankHealth.status -eq 'UP') { $bankReady = $true; break }
        } catch { Start-Sleep -Seconds 1 }
    }
    if (-not $bankReady) { throw 'Banco Tranquilo nao iniciou. Veja .runtime/banco.stderr.log.' }
}
Write-Host 'PostgreSQL persistente + RabbitMQ real + caixa SMTP local + Banco Tranquilo separado.'
Write-Host 'Aplicacao: http://localhost:8080 | E-mails: http://localhost:8025'
& .\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=local,cloud'
exit $LASTEXITCODE
