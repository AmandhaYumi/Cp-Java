# Validacao executada em 17/10/2026

## Ambiente usado

- JDK 21 e Spring Boot 4.1.1.
- PostgreSQL 17.11 portatil em `127.0.0.1:55432`, com autenticacao por senha e dados persistentes.
- RabbitMQ real no CloudAMQP, conectado com TLS na porta 5671.
- Mailpit local, SMTP 1025 e interface HTTP 8025.
- Servico Java `banco-tranquilo/` em processo separado na porta 9090.

## Resultados observados

| Verificacao | Resultado |
|---|---|
| Maven - testes da aplicacao | 4 testes, zero falhas, zero erros |
| Maven - pacote do Banco Tranquilo | BUILD SUCCESS |
| CRUD REST com perfil gerente | criar, consultar, atualizar e excluir logicamente: aprovado |
| Cliente acessando dashboard gerencial | HTTP 403 |
| Templates de catalogo, formulario, carrinho, historico, login, cadastro e dashboard | HTTP 200 para os acessos permitidos |
| Compra aprovada via RabbitMQ | status APROVADA persistido, transacao preenchida |
| Compra recusada via RabbitMQ | status RECUSADA persistido, estoque devolvido |
| E-mail de confirmacao | recebido no SMTP local e registro ENVIADO no PostgreSQL |
| Reinicio da aplicacao | compras 1 a 4 e dois registros de e-mail mantidos no PostgreSQL |
| Script completo com servico bancario separado | compras 34 e 35: aprovada e recusada; estoque final 3; e-mail recebido; CRUD OK |

O teste automatico de checkout usa banco H2 e substitui BancoTranquiloClient e JavaMailSender por mocks para verificar transacoes. O teste manual/script acima usou PostgreSQL, RabbitMQ, HTTP bancario e SMTP reais, com dados ficticios.

## Evidencia visual disponivel

![Filas no painel RabbitMQ](rabbitmq-filas.png)

Captura RabbitMQ fornecida pelo aluno. Prints reais do deploy foram adicionados ao README: landing, login, cadastro, catalogo, formulario CRUD, carrinho, compras e dashboard. Layout de landing, catalogo, formulario, carrinho e dashboard inspecionado visualmente.

## Validacao do deploy Render

- Site publico acessivel: https://mercado-express-cp5-rm563549.onrender.com
- Compilacao Docker concluida e painel confirmou "Your service is live".
- Logs confirmaram PostgreSQL 18.6 e conexao RabbitMQ TLS na porta 5671.
- Login do gerente concluido na interface.
- Compra ficticia #1 de R$ 39,90: de PENDENTE para APROVADA, transacao `e420b91c-9b09-477d-b560-8bc95eb56462`.
- Dashboard confirmou uma venda, receita R$ 39,90 e lucro R$ 15,40. E-mails enviados: zero, pois SMTP externo pendente.
- Testes Maven repetidos apos configuracao: quatro testes, zero falhas e zero erros.

## Limites e pendencias externas

### Atualizacao para Mercadinho

Nome do site, textos e produtos simplificados; tema rosa; nomes de templates, scripts e documentos em portugues. Identificadores internos preservados. Sete testes Maven passaram, incluindo tres novos testes HTTP das telas renomeadas e da restricao do painel para clientes.

Deploy `c6b0712` confirmado no Render em 17/10/2026. Conferidos no navegador: inicio, cadastro, login do gerente, catalogo com novos nomes, formulario de produto, carrinho, historico e painel. Compra #1 e indicadores anteriores preservados. Capturas atualizadas no README com tema rosa.

- Banco Tranquilo e um simulador educacional, nao uma conexao bancaria real.
- Mailpit nao encaminha mensagens para caixas externas. Para e-mail externo no deploy, informe um SMTP real em `MAIL_*`.
- O Docker Compose foi preparado, mas o motor Docker desta maquina nao iniciou; a validacao completa usou os binarios portateis.
- Identificacao preenchida: Amandha Yumi Toyota Artulino, RM563549, turma 2TDSPO.
- Repositorio publicado: `https://github.com/AmandhaYumi/Cp-Java`. Autenticacao via Git Credential Manager concluida pela aluna; push de `main` confirmado no remoto.
- Render configurado com Docker Free, PostgreSQL 18 Free e RabbitMQ externo TLS. Senhas exclusivamente nas variaveis privadas. Banco gratuito com expiracao indicada para 16/11/2026.
- O navegador continua negando acesso ao localhost por uma permissao salva; as capturas das telas foram realizadas no deploy publico. Print da caixa SMTP local ainda pendente.
- Video de 5 a 7 minutos e credenciais SMTP para entrega externa ainda pendentes.
