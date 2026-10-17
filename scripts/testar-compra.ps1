param([string]$BaseUrl = 'http://localhost:8080')
$ErrorActionPreference = 'Stop'

function Assert-That($Condition, [string]$Message) {
    if (-not $Condition) { throw $Message }
}
function Auth([string]$Credentials) {
    return @{ Authorization = 'Basic ' + [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($Credentials)) }
}
$manager = Auth 'gerente@mercadoexpress.com:Gerente@123'
$customer = Auth 'cliente@mercadoexpress.com:Cliente@123'

$blocked = Invoke-WebRequest "$BaseUrl/admin/dashboard" -Headers $customer -SkipHttpErrorCheck
Assert-That ($blocked.StatusCode -eq 403) 'Cliente conseguiu acessar o dashboard gerencial.'

$productPayload = @{ name='Produto verificacao CP5'; category='Teste'; description='Criado pelo script de integracao'; price=10.00; costPrice=6.00; stock=5; active=$true }
$product = Invoke-RestMethod "$BaseUrl/api/products" -Method Post -Headers $manager -ContentType 'application/json' -Body ($productPayload | ConvertTo-Json)
Assert-That ($product.id -gt 0) 'Falha ao criar produto.'
$productPayload.name = 'Produto verificacao CP5 atualizado'
$updated = Invoke-RestMethod "$BaseUrl/api/products/$($product.id)" -Method Put -Headers $manager -ContentType 'application/json' -Body ($productPayload | ConvertTo-Json)
Assert-That ($updated.name -eq $productPayload.name) 'Falha ao atualizar produto.'

function Buy([string]$Card) {
    $payload = @{ items=@(@{ productId=$product.id; quantity=2 }); cardHolder='CLIENTE DEMO'; cardNumber=$Card }
    return Invoke-RestMethod "$BaseUrl/api/purchases" -Method Post -Headers $customer -ContentType 'application/json' -Body ($payload | ConvertTo-Json -Depth 5)
}
function Wait-Purchase([long]$Id, [string]$ExpectedStatus) {
    for ($attempt=0; $attempt -lt 30; $attempt++) {
        $page = Invoke-WebRequest "$BaseUrl/purchases" -Headers $customer
        $row = [regex]::Match($page.Content, '(?s)<tr>\s*<td>#' + $Id + '</td>.*?</tr>').Value
        if ($row -match $ExpectedStatus) { return }
        Start-Sleep -Seconds 1
    }
    throw "Compra $Id nao chegou ao status $ExpectedStatus em 30 segundos."
}

$approved = Buy '4111111111111111'
Wait-Purchase $approved.purchaseId 'APROVADA'
$declined = Buy '4111111111110000'
Wait-Purchase $declined.purchaseId 'RECUSADA'
$catalog = Invoke-RestMethod "$BaseUrl/api/products" -Headers $manager
$stock = ($catalog | Where-Object id -EQ $product.id).stock
Assert-That ($stock -eq 3) 'Estoque incorreto depois da aprovacao e da recusa.'

$emails = Invoke-RestMethod 'http://localhost:8025/api/v1/messages'
$confirmation = $emails.messages | Where-Object Subject -EQ "Mercadinho - pagamento confirmado #$($approved.purchaseId)"
Assert-That ($null -ne $confirmation) 'Confirmacao de pagamento nao encontrada no SMTP local.'

Invoke-RestMethod "$BaseUrl/api/products/$($product.id)" -Method Delete -Headers $manager
$catalog = Invoke-RestMethod "$BaseUrl/api/products" -Headers $manager
Assert-That (-not ($catalog | Where-Object id -EQ $product.id)) 'Produto excluido continua no catalogo.'

[pscustomobject]@{
    Resultado='APROVADO'; CompraAprovada=$approved.purchaseId; CompraRecusada=$declined.purchaseId
    EstoqueFinal=$stock; EmailRecebido=$true; ClienteBloqueadoDashboard=$true; CRUD='OK'
}
