package App;

import Ui.MenuCliente;
import Ui.MenuProduto;
import Ui.MenuPedido;

import Repository.RepositorioPedidoMemoria;
import Service.PedidoService;
import Controller.PedidoController;
import Service.ProcessadorFilaPedidos;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws InterruptedException {
    // Inicializa componentes de pedido
    RepositorioPedidoMemoria repoPedido = new RepositorioPedidoMemoria();
    PedidoService pedidoService = new PedidoService(repoPedido);
    PedidoController pedidoController = new PedidoController(pedidoService);

    // Inicia worker de processamento em background (5 segundos por pedido)
    ProcessadorFilaPedidos worker = new ProcessadorFilaPedidos(repoPedido, 10000L);
    Thread workerThread = new Thread(worker, "pedido-worker-1");
    workerThread.start();

    // Cria repositório/serviço/controller de clientes para compartilhar entre menus
    Repository.RepositorioClienteMemoria repoCliente = new Repository.RepositorioClienteMemoria();
    Service.ClienteService clienteService = new Service.ClienteService(repoCliente);
    Controller.ClienteController clienteController = new Controller.ClienteController(clienteService);

    // Cria repositório/serviço/controller de produtos para compartilhar entre menus
    Repository.RepositorioProdutoMemoria repoProduto = new Repository.RepositorioProdutoMemoria();
    Service.ProdutoService produtoService = new Service.ProdutoService(repoProduto);
    Controller.ProdutoController produtoController = new Controller.ProdutoController(produtoService);

    // Cria instâncias dos menus (injeção de dependências)
    MenuCliente menuCliente = new MenuCliente(clienteController);
    MenuProduto menuProduto = new MenuProduto(produtoController);
    MenuPedido menuPedido = new MenuPedido(pedidoController, clienteController, produtoController);

    // Loop principal: permite navegar entre menus sem precisar preencher todos
    Scanner scanner = new Scanner(System.in);
    int opcao = -1;
    do {
      System.out.println("\n=== Menu Principal ===");
      System.out.println("1. Menu Clientes");
      System.out.println("2. Menu Produtos");
      System.out.println("3. Menu Pedidos");
      System.out.println("0. Sair");
      System.out.print("Escolha: ");
      String entrada = scanner.nextLine();
      try {
        opcao = Integer.parseInt(entrada.trim());
      } catch (NumberFormatException e) {
        System.out.println("Opção inválida. Digite um número.");
        continue;
      }

      switch (opcao) {
        case 1 -> menuCliente.exibirMenu();
        case 2 -> menuProduto.exibirMenu();
        case 3 -> menuPedido.exibirMenu();
        case 0 -> System.out.println("Saindo...");
        default -> System.out.println("Opção inválida.");
      }
    } while (opcao != 0);

    // Fecha scanner antes de encerrar
    scanner.close();

    // Encerramento: para o worker e aguarda término
    worker.stop();
    workerThread.interrupt();
    try {
      workerThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println("Main interrompido ao aguardar worker");
      throw e;
    }
  }
}