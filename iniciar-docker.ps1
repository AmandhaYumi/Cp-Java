$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (Test-Path 'C:\Program Files\Java\jdk-21\bin\java.exe') {
    $env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
}
if (-not (Test-Path '.secrets\cloudamqp.properties')) {
    throw 'Configure .secrets/cloudamqp.properties conforme o README.'
}

& docker compose up -d --wait postgres mailhog
if ($LASTEXITCODE -ne 0) {
    throw 'Nao foi possivel iniciar PostgreSQL/MailHog. Abra o Docker Desktop e tente novamente.'
}
Write-Host 'PostgreSQL persistente + RabbitMQ CloudAMQP + e-mail MailHog.'
Write-Host 'Aplicacao: http://localhost:8080 | E-mails: http://localhost:8025'
& .\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=cloud'
exit $LASTEXITCODE
