# Roteiro do vídeo — Mercadinho

Duração planejada: 6 minutos e 40 segundos, incluindo cliques e pequenas pausas. As falas são sugestões: pode falar do seu jeito, sem decorar. Não leia as instruções de tela em voz alta.

## Antes de começar

- Abra o site: https://mercado-express-cp5-rm563549.onrender.com/ e espere carregar antes de gravar. O plano gratuito pode demorar ao sair da inatividade.
- Deixe abertas as abas do GitHub/README, RabbitMQ Management, Brevo em Logs e sua caixa de e-mail FIAP. Deixe o cliente PostgreSQL conectado e o Postman com a coleção importada, caso vá mostrá-los.
- Prepare o acesso da sua conta de cliente e da conta de demonstração de gerente. Não filme a digitação de senhas; pause ou corte essa parte.
- Não mostre as variáveis privadas do Render, URLs de conexão com senha, chaves SMTP, tokens ou credenciais do RabbitMQ. No banco, mostre apenas tabelas e colunas necessárias, nunca hashes de senha ou números de cartão.
- Use somente cartão fictício. Não existe cobrança real neste projeto.
- Ensaie uma vez com cronômetro. Se precisar cortar tempo, corte pausas, não a comprovação das funcionalidades.

## 0:00–0:30 — Apresentação

**Na tela:** página inicial do Mercadinho.

**Fala:**

“Oi, eu sou a Amandha Yumi Toyota Artulino, RM 563549, da turma 2TDSPO. Esse é o Mercadinho, meu projeto das partes 1 e 2 da CP5 de Java Advanced. É uma loja online com cadastro, login, produtos, carrinho e pagamento simulado. O site está publicado no Render.”

## 0:30–1:15 — Cadastro, login e segurança

**Na tela:** clique em Criar minha conta, mostre os campos e faça um cadastro com um endereço que você controla. Entre como cliente. Se a conta FIAP já existe, não tente cadastrá-la novamente: use outro endereço seu ou explique que ela já foi cadastrada e faça login.

**Fala:**

“Aqui está o cadastro com nome, e-mail e senha. Os dados são validados e ficam salvos no PostgreSQL. O login é personalizado e usa Spring Security, com senhas protegidas por BCrypt. Existem dois perfis: o cliente faz compras e consulta seus pedidos; o gerente também pode administrar os produtos e acessar o painel.”

**Comprove:** como cliente, tente acessar `/admin/dashboard` e mostre a recusa de acesso. Volte aos produtos. Pause a gravação para sair e entrar como gerente.

## 1:15–2:15 — CRUD de produtos

**Na tela:** como gerente, clique em Novo produto. Use um produto separado, sem alterar os produtos das compras anteriores:

- Nome: Biscoito de teste; categoria: Mercearia; descrição: Produto para demonstração.
- Preço: 8,00; custo: 5,00; estoque: 10; Produto ativo marcado.
- Salve, localize o produto no catálogo, edite o preço para 9,00 e salve novamente.
- Exclua apenas esse produto de teste e confirme que ele saiu do catálogo.

**Fala, enquanto faz:**

“No perfil de gerente, eu consigo cadastrar um produto com nome, categoria, preço, custo e estoque. Depois ele aparece no catálogo. Agora vou editar o preço e salvar. Por último, vou excluir esse produto de teste. A exclusão é lógica: ele fica inativo, preservando o histórico no banco.”

## 2:15–3:25 — Compra e pagamento simulado

**Na tela:** pause para trocar para a conta de cliente vinculada ao seu e-mail. Em Produtos, adicione um item disponível e abra Carrinho. Mostre o total. Preencha Nome no cartão com CLIENTE TESTE e Número com `4111111111111111`. Clique em Finalizar compra e abra Compras. Se estiver PENDENTE, aguarde e atualize a página; mostre o resultado real.

**Fala:**

“Como cliente, escolho um produto e adiciono ao carrinho. Aqui aparece o valor total. Vou finalizar usando um cartão fictício, porque o Banco Tranquilo é um simulador educacional e não movimenta dinheiro. O pedido é registrado e o pagamento é processado por mensagens. No histórico posso acompanhar o status e o identificador da transação.”

**Ao aprovar:** “Esse pedido foi aprovado e recebeu este identificador de transação.” Aponte o número do pedido: você vai procurá-lo no e-mail.

## 3:25–4:10 — RabbitMQ e Banco Tranquilo

**Na tela:** RabbitMQ Management, abas Exchanges e Queues and Streams. Mostre:

- Exchange `mercado.pagamentos.exchange`.
- Fila `mercado.pagamentos.solicitacoes`.
- Fila `mercado.pagamentos.respostas`.

**Fala:**

“O RabbitMQ faz a comunicação assíncrona do pagamento. A solicitação vai para esta fila, a integração consulta a API do simulador Banco Tranquilo e a resposta retorna pela outra fila. A aplicação então atualiza a compra. As filas podem aparecer vazias porque as mensagens já foram consumidas; zero mensagens não significa que não houve processamento.”

**Importante:** neste deploy, o simulador bancário roda dentro da aplicação. O repositório também contém o serviço Java separado em `banco-tranquilo/` para o ambiente local completo. Não diga que são dois serviços independentes no Render.

## 4:10–4:50 — Confirmação por e-mail

**Na tela:** Brevo → Logs, procure a confirmação com o número do pedido recém-criado. Mostre o estado real e, se já recebeu, abra a mensagem na caixa FIAP. Esconda outras mensagens e dados pessoais.

**Fala:**

“Quando o pagamento é aprovado, a aplicação envia a confirmação por e-mail usando o SMTP do Brevo. Aqui está a mensagem referente ao mesmo pedido. Ela informa o valor e o identificador da transação. O resultado do envio também é registrado pela aplicação.”

Só diga “entregue” se esse for o estado mostrado no Brevo. Entregue não significa lido. Se o envio novo demorar, mostre a evidência já validada da compra #3 e explique que é um teste anterior; não a apresente como se fosse a compra recém-feita. Não use o Mailpit local como prova de entrega externa.

## 4:50–5:30 — Painel do gerente

**Na tela:** pause para entrar como gerente, abra Painel e mostre vendas aprovadas, receita, lucro, estoque, e-mails enviados, vendas por categoria e compras recentes.

**Fala:**

“O painel reúne os resultados da loja. Ele mostra a quantidade de vendas aprovadas, a receita, o lucro e o estoque. Também consigo conferir os e-mails enviados, as vendas por categoria e as compras recentes. Essas informações vêm dos registros persistidos, não são valores fixos da página.”

## 5:30–6:15 — Banco de dados, API e testes

**Na tela:** mostre as tabelas no PostgreSQL, sem credenciais nem campos sensíveis:

- `TDS_SEC_MVC_TB_MERCADO`: produtos.
- `TDS_COMPRAS_MERCADO` e `TDS_ITENS_COMPRA_MERCADO`: compras e itens.
- `TDS_EMAIL_LOG_MERCADO`: resultado dos envios.

No Postman, abra a coleção `postman/Mercadinho.postman_collection.json`, selecione a URL do deploy e execute uma consulta GET de produtos com a autenticação de demonstração já preparada. Não exponha a senha na gravação.

**Fala:**

“Os dados ficam no PostgreSQL. Aqui estão as tabelas de produtos, compras, itens e registros de e-mail. O projeto também oferece endpoints de API e uma coleção do Postman. Nesta consulta, posso ver os produtos retornados pela API. Foram executados sete testes automatizados, incluindo fluxo de compra e controle de acesso das telas, sem falhas na última execução registrada.”

Se o cliente de banco ou o Postman não estiver preparado, configure antes de gravar. Não apresente apenas uma lista no README como prova de execução dessas ferramentas.

## 6:15–6:40 — Código e encerramento

**Na tela:** GitHub, README, estrutura do projeto e print das dependências do Spring Initializr. Mostre rapidamente `banco-tranquilo/`, `Dockerfile` e `docs/testes.md`. Termine no endereço público do site.

**Fala:**

“O código está disponível neste repositório, com instruções de execução, dependências, prints e resultados dos testes. O projeto usa Java 21, Spring Boot, Thymeleaf, PostgreSQL e RabbitMQ. As credenciais de produção não estão no GitHub. Esse foi o Mercadinho. Obrigada!”

## Depois da gravação

1. Confira se o vídeo ficou entre 5 e 7 minutos e se nenhum segredo apareceu.
2. Publique no YouTube como **não listado** e teste o link sem estar logada.
3. Substitua `[COLAR LINK]` em `entrega.txt` pelo link do vídeo.
4. Envie as credenciais de acesso ao banco somente ao professor, em canal privado.
5. Atualize o repositório e gere novamente o ZIP com `./gerar-zip.ps1` antes de entregar.
