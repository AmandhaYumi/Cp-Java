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

Captura fornecida pelo aluno. Os demais prints da interface ainda dependem de captura no navegador; respostas HTTP 200 comprovam renderizacao de template, nao verificacao visual de layout.

## Limites e pendencias externas

- Banco Tranquilo e um simulador educacional, nao uma conexao bancaria real.
- Mailpit nao encaminha mensagens para caixas externas. Para e-mail externo no deploy, informe um SMTP real em `MAIL_*`.
- O Docker Compose foi preparado, mas o motor Docker desta maquina nao iniciou; a validacao completa usou os binarios portateis.
- Identificacao preenchida: Amandha Yumi Toyota Artulino, RM563549, turma 2TDSPO.
- Repositorio definido: `https://github.com/AmandhaYumi/Cp-Java`. Commit local preparado; envio bloqueado por falha de autenticacao do Git. A conexao GitHub disponivel nesta sessao possui apenas leitura nesse repositorio.
- O Render abriu a tela de login; o deploy depende de entrar na conta.
- O navegador continua negando acesso ao localhost por uma permissao salva, apesar da autorizacao em texto; os demais prints aguardam liberacao dessa permissao.
- Video de 5 a 7 minutos e credenciais SMTP para entrega externa ainda pendentes.
