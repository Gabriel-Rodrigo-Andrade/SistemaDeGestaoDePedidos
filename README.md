## Sistema de Gestão de Pedidos

Este repositório contém uma aplicação Java simples para gestão de clientes e produtos. Também tentamos demonstrar uma arquitetura em camadas (UI -> Controller -> Service -> Repository -> Model) com validações e tratamento de exceções.

-------------------------

## Visão geral das camadas

- UI (Ui/*): menus de interação com o usuário (`MenuCliente`, `MenuProduto`).
- Controller (Controller/*): recebe entrada da UI, delega ao Service e trata exceções para mensagens amigáveis (`ClienteController`, `ProdutoController`).
- Service (Service/*): lógica de negócio e validações de alto nível (`ClienteService`, `ProdutoService`).
- Repository (Repository/*): persistência em memória (implementações `RepositorioClienteMemoria`, `RepositorioProdutoMemoria` que usam `ConcurrentHashMap`).
- Model (Model/*): objetos de domínio (`Cliente`, `Produto`, `Categoria`).
- Util (Util/*): validadores utilitários (`ClienteValidator`, `ProdutoValidator`).
- Exceptions (Exceptions/*): classes de exceção usadas na aplicação.

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

- Service/  — Regras de negócio e validações de alto nível
  - `ClienteService.java` — Valida e cria clientes, chama repositório.
  - `ProdutoService.java` — Valida e cria produtos, chama repositório.

- Repository/  — Interfaces e implementações de persistência em memória
  - `IRepositorioCliente.java` — Interface do repositório de clientes.
  - `IRepositorioProduto.java` — Interface do repositório de produtos.
  - `RepositorioClienteMemoria.java` — Implementação em memória; usa `ConcurrentHashMap` para concorrência e detecção de IDs duplicados.
  - `RepositorioProdutoMemoria.java` — Implementação em memória; usa `ConcurrentHashMap` para concorrência e detecção de IDs duplicados.

- Model/  — Objetos de domínio
  - `Cliente.java` — Representa um cliente (id, nome, email).
  - `Produto.java` — Representa um produto (id, nome, preco, categoria); valida no construtor como defesa.
  - `Categoria.java` — Enum com categorias de produto.

- Ui/  — Menus de interação com o usuário
  - `MenuCliente.java` — Menu para cadastrar/listar clientes; coleta input e chama `ClienteController`.
  - `MenuProduto.java` — Menu para cadastrar/listar produtos; coleta input e chama `ProdutoController`.

- Exceptions/  — Exceções customizadas usadas nas camadas
  - `ClienteInvalidoException.java` — Lançada quando dados do cliente são inválidos.
  - `EmailInvalidoException.java` — Lançada quando email inválido.
  - `IdDuplicadoException.java` — Lançada quando há tentativa de inserir um registro com ID já existente (aceita entidade para mensagem).
  - `ProdutoInvalidoException.java` — Lançada quando dados do produto são inválidos.

- Util/  — Validadores e utilitários
  - `ClienteValidator.java` — Valida id/nome/email do cliente.
  - `ProdutoValidator.java` — Valida id/nome/preço/categoria do produto.

- `README.md` — Documentação do projeto (este arquivo).
