## Sistema de Gestão de Pedidos

Este repositório contém uma aplicação Java simples para gestão de clientes e produtos. Também tentamos demonstrar uma arquitetura em camadas (UI -> Controller -> Service -> Repository -> Model) com validações e tratamento de exceções.

-------------------------
## Diagrama

```
     +-----------------+        +------------------------+        +---------------------------+
     |     UI (CLI)    |  --->  |       Controller       |  --->  |          Service          |
     | MenuCliente/    |        | Cliente/Produto/Pedido |        | Cliente/Produto/Pedido    |
     | MenuProduto/    |        +------------------------+        | (inclui ProcessadorFila)  |
     |  MenuPedido     |                                          +---------------------------+
                  |                                                       |
                  v                                                       v
                +---------------------------------------------------------------+
                |                         Repository                            |
                |  {map: ConcurrentHashMap}   {fila: LinkedBlockingQueue}       |
                |  (Cliente/Produto/Pedido)    (fila de pedidos para worker)     |
                +---------------------------------------------------------------+
                                  |                          ^
                                  v                          |
                            +----------------+       worker | consumes
                            |     Model      | <-------------+ (ProcessadorFilaPedidos)
                            | Cliente/Produto/Pedido/ItemPedido/Status |
                            +----------------+

Notas:
- As setas representam o fluxo principal de chamadas (UI -> Controller -> Service -> Repository -> Model).
- A fila (`LinkedBlockingQueue`) implementa o padrão produtor/consumidor entre o `PedidoService` (produtor) e o `ProcessadorFilaPedidos` (consumidor).
- Os repositórios usam `ConcurrentHashMap` e `putIfAbsent` para evitar condições de corrida e detectar IDs duplicados.
```

### Métodos principais por camada

```
UI (MenuCliente/MenuProduto/MenuPedido)
  - exibirMenu()
  - coletar input (id, nome, email / preco, categoria / clienteId + itens)
  - chamar Controller.cadastrar*/listar* / criarPedido/listarPedidos

Controller (ClienteController / ProdutoController / PedidoController)
  - cadastrarCliente(id,nome,email) -> chama ClienteService.cadastrarCliente(...)
  - listarClientes() -> chama ClienteService.listarClientes()
  - cadastrarProduto(id,nome,preco,categoria) -> chama ProdutoService.cadastrarProduto(...)
  - listarProdutos() -> chama ProdutoService.listarProdutos()
  - criarPedido(clienteId, List<ItemPedido>) -> chama PedidoService.criarPedido(...)
  - listaPedidos() -> chama PedidoService.listarPedidos()

Service (ClienteService / ProdutoService / PedidoService / ProcessadorFilaPedidos)
  - validar dados via ClienteValidator / ProdutoValidator / PedidoValidator
  - construir objeto de domínio (new Cliente(...) / new Produto(...) / new Pedido(...))
  - chamar repositorio.adicionar(obj)
  - PedidoService: enfileirar pedido -> repositorio.enfileirar(pedido) e atualizar status
  - ProcessadorFilaPedidos.run(): retirarDaFila() -> setStatus(PROCESSANDO) -> processa -> setStatus(FINALIZADO)
  - expor listar() que delega ao repositorio.listar()

Repository (IRepositorio*/Repositorio*Memoria)
  - adicionar(T obj) -> usa putIfAbsent(id,obj) e lança IdDuplicadoException(entidade)
  - listar() -> retorna coleção atual (snapshot)
  - buscarPorId(id) -> retorna objeto ou null
  - enfileirar(Pedido) -> fila.put(pedido) (bloqueante)
  - retirarDaFila() -> fila.take() (bloqueante)
  - tamanhoFila() -> retorna tamanho da fila

Model (Cliente / Produto / Pedido / ItemPedido / Status)
  - construtor com validações defensivas (lança ClienteInvalidoException / ProdutoInvalidoException / PedidoInvalidoException)
  - getters, `getTotal()` em `Pedido` e `toString()`

Exceptions / Util
  - Validators (ClienteValidator, ProdutoValidator, PedidoValidator) -> quebram a requisição com exceptions amigáveis para UI
  - IdDuplicadoException(entidade) -> mensagem parametrizada (e.g. "Já existe um(a) Produto com este ID.")
  - PedidoInvalidoException -> usado quando há falha ao criar/enfileirar/processar pedido

Observação: O Service executa validações prévias para melhorar UX; o modelo valida novamente como defesa final.
```

-------------------------

## Feature: Cliente

Descrição curta
- Permite cadastrar e listar clientes.

Fluxo (caminho feliz)
1. O usuário escolhe a opção de cadastrar cliente no `MenuCliente`.
2. A UI coleta `id`, `nome` e `email` via `Scanner` e chama `ClienteController.cadastrarCliente(id, nome, email)`.
3. O `ClienteController` delega ao `ClienteService.cadastrarCliente(...)`.
4. O `ClienteService` chama `ClienteValidator` para validar `id`, `nome` e `email`.
5. Se as validações passarem, o `Service` cria `new Cliente(id, nome, email)` e chama `repositorio.adicionar(cliente)`.
6. O repositório em memória (`RepositorioClienteMemoria`) usa `ConcurrentHashMap.putIfAbsent` para inserir o cliente. Se já existir um ID, lança `IdDuplicadoException`.
7. A UI exibe mensagem de sucesso ou erro conforme a exceção tratada pelo Controller.

Construção do objeto
- A classe `Cliente` é simples e imutável (apenas getters). A validação ocorre no `ClienteValidator` (Service) e o objeto é criado com valores já validados.

Tratamento de erros
- `ClienteValidator` lança `ClienteInvalidoException` e `EmailInvalidoException` quando aplicável (atualmente unchecked).
- `RepositorioClienteMemoria.adicionar` lança `IdDuplicadoException("Cliente")` em caso de ID já existente.
- `ClienteController` captura essas exceções e exibe mensagens amigáveis ao usuário.

Observações
- Antes das alterações, havia uma validação rápida no `ClienteController`; ela foi removida para evitar duplicação e manter o Service como fonte da verdade.

-------------------------

## Feature: Produto

Descrição curta
- Permite cadastrar e listar produtos com categoria e preço.

Fluxo (caminho feliz)
1. O usuário escolhe cadastrar produto em `MenuProduto`.
2. A UI coleta `id`, `nome`, `preco` e `categoria` (como string) e chama `ProdutoController.cadastrarProduto(...)`.
3. `ProdutoController` delega ao `ProdutoService.cadastrarProduto(...)`.
4. `ProdutoService` usa `ProdutoValidator` para validar `id`, `nome`, `preco` e `categoria` (validações de UX antes de instanciar o objeto).
5. O Service cria `new Produto(id, nome, preco, categoria)`. A classe `Produto` também faz validações no construtor (defesa final).
6. `RepositorioProdutoMemoria` usa `ConcurrentHashMap.putIfAbsent` para inserir o produto; em caso de duplicidade de ID, lança `IdDuplicadoException("Produto")`.
7. Exibe mensagem de sucesso ou erro via Controller/UI.

Construção do objeto
- `Produto` valida `preco` e `nome` no próprio construtor (lançando `ProdutoInvalidoException`).
- `ProdutoValidator` foi adicionado para permitir validações no `Service` antes da construção do objeto (melhora UX e evita criar objetos inválidos quando possível).

Tratamento de erros
- `ProdutoValidator` e o construtor de `Produto` lançam `ProdutoInvalidoException` (agora unchecked) em caso de dados inválidos.
- `RepositorioProdutoMemoria.adicionar` lança `IdDuplicadoException("Produto")` em caso de ID duplicado.
- `ProdutoController` captura exceções e exibe mensagens amigáveis.

Observações
- A validação é propositalmente dupla: o Service valida para UX, o construtor do Model valida como defesa. Essa abordagem evita criar objetos inválidos mesmo se algum caminho pular o Service.

-------------------------

## Feature: Pedido

Descrição curta
- Permite criar, enfileirar, processar e listar pedidos. O processamento é feito por um worker em background que consome uma fila em memória.

Fluxo (caminho feliz)
1. O usuário escolhe criar pedido em `MenuPedido` e fornece `clienteId` e itens (produto + quantidade).
2. A UI chama `PedidoController.criarPedido(...)` que delega ao `PedidoService.criarPedido(...)`.
3. `PedidoService` valida via `PedidoValidator` e cria `new Pedido(cliente, itens)` com `Status.ABERTO`.
4. O `Service` salva o pedido no repositório (`RepositorioPedidoMemoria.adicionar`) e então enfileira o pedido (`RepositorioPedidoMemoria.enfileirar`), que usa uma `BlockingQueue` em memória.
5. Após enfileirar, o `Service` atualiza o status para `Status.FILA` e grava novamente no repositório.
6. Um worker em background (`Service/ProcessadorFilaPedidos`) executando em uma `Thread` chama `retirarDaFila()` (bloqueante), marca `PROCESSANDO`, simula o trabalho e depois marca `FINALIZADO`, atualizando o repositório a cada transição.

Construção do objeto / dados principais
- `Pedido` contém: id gerado, cliente, lista de `ItemPedido` (produto + quantidade), total e `Status` (enum: `ABERTO`, `FILA`, `PROCESSANDO`, `FINALIZADO`).
- `ItemPedido` referencia `Produto` e quantidade; o total do pedido é calculado somando (preço * quantidade).

Persistência / fila (em memória)
- `RepositorioPedidoMemoria` usa:
  - `ConcurrentHashMap<String, Pedido>` para armazenar pedidos e permitir acesso concorrente seguro.
  - `LinkedBlockingQueue<Pedido>` (implementação de `BlockingQueue`) para implementar o padrão produtor/consumidor entre quem cria pedidos e o worker que processa.

Processamento assíncrono
- `ProcessadorFilaPedidos` é um `Runnable` que consome pedidos da fila (`take()`), atualiza status, faz `Thread.sleep(...)` para simular processamento e marca `FINALIZADO` ao terminar.
- O worker é inicializado em `App/Main.java` (ex.: `new Thread(new ProcessadorFilaPedidos(repoPedido, 5000L)).start();`).

Tratamento de erros
- `PedidoService` captura `InterruptedException` ao enfileirar e lança `PedidoInvalidoException` em caso de falha.
- Validações prévias são feitas em `PedidoValidator` e exceções do tipo `PedidoInvalidoException` informam a UI.

Transições de status
- `ABERTO` -> `FILA` -> `PROCESSANDO` -> `FINALIZADO`

Observações
- A fila em memória é prática para demos e testes; para persistência entre reinícios ou escalabilidade considere filas externas (RabbitMQ, Redis, Kafka) ou persistência em BD.
- `ConcurrentHashMap` garante segurança para operações no mapa; não evita condições de corrida dentro de campos mutáveis dos objetos armazenados (cuidados se houver múltiplos workers atualizando o mesmo `Pedido`).

-------------------------

## Exceptions: resumo
- `ClienteInvalidoException` (unchecked) — usado para problemas de validação do cliente.
- `EmailInvalidoException` (unchecked) — email com formato inválido.
- `ProdutoInvalidoException` (agora unchecked) — problemas de validação do produto.
- `IdDuplicadoException` (unchecked) — agora aceita opcionalmente uma string com o nome da entidade para mensagens mais claras (`new IdDuplicadoException("Produto")`).

-------------------------

## Estrutura do projeto (arquivos e propósito)

- App/
  - `Main.java` — Ponto de entrada da aplicação; inicializa os menus de UI.

- Controller/  — Camada que recebe pedidos da UI e delega ao Service
  - `ClienteController.java` — Trata requisições relacionadas a Cliente (mensagens/erros para UI).
  - `ProdutoController.java` — Trata requisições relacionadas a Produto (mensagens/erros para UI).
  - `PedidoController.java` — Trata requisições relacionadas a Pedido (mensagens/erros para UI).

- Service/  — Regras de negócio e validações de alto nível
  - `ClienteService.java` — Valida e cria clientes, chama repositório.
  - `ProdutoService.java` — Valida e cria produtos, chama repositório.
  - `PedidoService.java` — Valida, cria, enfileira e lista pedidos; orquestra transições de status.
  - `ProcessadorFilaPedidos.java` — Worker que consome a fila de pedidos em background e processa cada pedido.

- Repository/  — Interfaces e implementações de persistência em memória
  - `IRepositorioCliente.java` — Interface do repositório de clientes.
  - `IRepositorioProduto.java` — Interface do repositório de produtos.
  - `RepositorioClienteMemoria.java` — Implementação em memória; usa `ConcurrentHashMap` para concorrência e detecção de IDs duplicados.
  - `RepositorioProdutoMemoria.java` — Implementação em memória; usa `ConcurrentHashMap` para concorrência e detecção de IDs duplicados.
  - `IRepositorioPedido.java` — Interface do repositório de pedidos (operações CRUD + fila).
  - `RepositorioPedidoMemoria.java` — Implementação em memória; usa `ConcurrentHashMap` para armazenar pedidos e `LinkedBlockingQueue` para fila de processamento.

- Model/  — Objetos de domínio
  - `Cliente.java` — Representa um cliente (id, nome, email).
  - `Produto.java` — Representa um produto (id, nome, preco, categoria); valida no construtor como defesa.
  - `Categoria.java` — Enum com categorias de produto.
  - `Pedido.java` — Representa um pedido (id, cliente, itens, total, status).
  - `ItemPedido.java` — Representa um item dentro do pedido (produto + quantidade).
  - `Status.java` — Enum com os estados do pedido (`ABERTO`, `FILA`, `PROCESSANDO`, `FINALIZADO`).

- Ui/  — Menus de interação com o usuário
  - `MenuCliente.java` — Menu para cadastrar/listar clientes; coleta input e chama `ClienteController`.
  - `MenuProduto.java` — Menu para cadastrar/listar produtos; coleta input e chama `ProdutoController`.
  - `MenuPedido.java` — Menu para criar/listar pedidos; coleta cliente, produtos e quantidades, e chama `PedidoController`.

- Exceptions/  — Exceções customizadas usadas nas camadas
  - `ClienteInvalidoException.java` — Lançada quando dados do cliente são inválidos.
  - `EmailInvalidoException.java` — Lançada quando email inválido.
  - `IdDuplicadoException.java` — Lançada quando há tentativa de inserir um registro com ID já existente (aceita entidade para mensagem).
  - `ProdutoInvalidoException.java` — Lançada quando dados do produto são inválidos.
  - `PedidoInvalidoException.java` — Lançada quando há problemas na criação/enfileiramento/validação do pedido.

- Util/  — Validadores e utilitários
  - `ClienteValidator.java` — Valida id/nome/email do cliente.
  - `ProdutoValidator.java` — Valida id/nome/preço/categoria do produto.
  - `PedidoValidator.java` — Valida cliente e itens ao criar um pedido (ex.: quantidade mínima, existência de produtos).

- `README.md` — Documentação do projeto (este arquivo).
