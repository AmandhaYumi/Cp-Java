$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

if (Test-Path 'C:\Program Files\Java\jdk-21\bin\java.exe') {
    $env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
}

if (-not (Test-Path '.secrets\cloudamqp.properties')) {
    throw 'Configure .secrets/cloudamqp.properties antes de iniciar. Veja o README.'
}

Write-Host 'Iniciando Mercadinho com banco de demonstracao e RabbitMQ CloudAMQP.'
Write-Host 'Acesse http://localhost:8080 quando a inicializacao terminar.'
& .\mvnw.cmd spring-boot:run '-Dspring-boot.run.profiles=demo,cloud'
exit $LASTEXITCODE
