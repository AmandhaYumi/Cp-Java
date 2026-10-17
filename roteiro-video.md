# Roteiro do vídeo (5 a 7 minutos)

1. **0:00-0:40 - Contexto e arquitetura:** apresente o README e o fluxo navegador → Spring MVC → PostgreSQL → RabbitMQ → Banco Tranquilo → e-mail.
2. **0:40-1:30 - Segurança:** mostre a tela de login personalizada, o cadastro e os perfis `CLIENTE` e `GERENTE`.
3. **1:30-2:30 - CRUD:** entre como gerente, crie, edite, consulte e exclua logicamente um produto.
4. **2:30-4:15 - Compra:** entre como cliente, adicione produtos ao carrinho e finalize com o cartão `4111111111111111`.
5. **4:15-5:15 - RabbitMQ:** abra o painel da sua instancia no CloudAMQP, mostre a exchange e as duas filas. Repita uma compra observando as taxas de mensagens e os consumidores. As filas podem ficar vazias quando as mensagens sao consumidas rapidamente.
6. **5:15-5:50 - E-mail:** abra `http://localhost:8025` e mostre a confirmacao capturada pelo Mailpit, depois o registro ENVIADO na tabela `tds_email_log_mercado`.
7. **5:50-6:30 - Postman e dashboard:** execute a coleção, depois mostre receita, lucro, estoque, e-mails e o gráfico gerencial.
8. **6:30-7:00 - Fechamento:** mostre as tabelas no PostgreSQL e o link do deploy.

Antes de gravar, suba o ambiente com `.\iniciar-local.ps1` e deixe abertas as abas da aplicacao, do painel CloudAMQP, Mailpit e Postman. Mostre tambem o servico Java separado em `banco-tranquilo/` e `http://localhost:9090/actuator/health`.
