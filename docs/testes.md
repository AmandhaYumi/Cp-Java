# Testes do Mercadinho

## Testes automatizados

Execução em 17/10/2026 com Java 21 pelo comando `./mvnw.cmd test`: sete testes, zero falhas, zero erros e nenhum ignorado.

| Classe | Casos |
|---|---|
| `MercadoexpressApplicationTests` | Inicialização do contexto |
| `TelasTests` | Páginas públicas e CSS; telas do gerente; acesso negado ao painel para cliente |
| `CheckoutFlowIntegrationTests` | Aprovação; recusa com devolução de estoque; estoque insuficiente |

Os testes usam H2 isolado. Os serviços bancário e de e-mail são substituídos por mocks nos testes de checkout. Portanto, o resultado automatizado não comprova disponibilidade de PostgreSQL, RabbitMQ ou SMTP externos.

## Integração

Em 17/10/2026, o fluxo também foi validado com PostgreSQL persistente, RabbitMQ CloudAMQP por TLS, simulador Banco Tranquilo em processo Java separado e caixa SMTP local Mailpit. Foram verificados CRUD, aprovação, recusa, estoque, registro de e-mail e persistência após reinício.

No Render, foram conferidos login do gerente, catálogo, formulário de produto, carrinho, histórico e painel. As compras fictícias #1, #2 e #3 foram aprovadas. A confirmação por e-mail da compra #3 recebeu o estado **Entregue** no Brevo. O painel registrou três vendas aprovadas e um e-mail enviado na captura atual.

As imagens atuais das telas estão no README. A captura do Brevo comprova o estado informado pelo provedor, não leitura pelo destinatário.

## Limites

- O Banco Tranquilo é um simulador; nenhum teste movimenta dinheiro real.
- No Render, o simulador bancário é embarcado na aplicação. O Compose define o módulo separado.
- O Docker Compose está disponível para reprodução, mas não foi executado integralmente nesta máquina, pois o motor Docker não iniciou. A integração local foi validada com os serviços portáteis.
- O script `scripts/testar-compra.ps1` altera o ambiente de destino com produtos e compras fictícias; deve ser executado em ambiente de teste.
