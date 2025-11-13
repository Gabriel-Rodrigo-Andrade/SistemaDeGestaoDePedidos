package Ui;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

import Controller.PedidoController;
import Controller.ClienteController;
import Model.ItemPedido;
import Model.Pedido;
import Model.Produto;
import Model.Cliente;

public class MenuPedido {
  private final PedidoController controller;
  private final Scanner scanner = new Scanner(System.in);
  private final ClienteController clienteController;
  private final Controller.ProdutoController produtoController;

  public MenuPedido(PedidoController controller, ClienteController clienteController,
      Controller.ProdutoController produtoController) {
    this.controller = controller;
    this.clienteController = clienteController;
    this.produtoController = produtoController;
  }

  public void exibirMenu() {
    while (true) {
      System.out.println("\n === Menu Pedido ===");
      System.out.println("1) Criar pedido");
      System.out.println("2) Listar pedidos");
      System.out.println("0) Voltar");
      System.out.print("Escolha: ");
      String op = scanner.nextLine().trim();
      switch (op) {
        case "1":
          criarPedidoInterativo();
          break;
        case "2":
          listarPedidos();
          break;
        case "0":
          return;
        default:
          System.out.println("Opcao invalida");
          break;
      }
    }
  }

  private void criarPedidoInterativo() {
    try {
      System.out.print("Digite o id do cliente: ");
      String clienteId = scanner.nextLine().trim();

      // Busca cliente já cadastrado
      Cliente cliente = clienteController.buscarPorId(clienteId);
      if (cliente == null) {
        System.out.println("Cliente não encontrado com id: " + clienteId);
        System.out.println("Deseja listar clientes cadastrados? (S/N)");
        String resp = scanner.nextLine().trim();
        if (resp.equalsIgnoreCase("S")) {
          clienteController.listarClientes();
        }
        return;
      }

      List<ItemPedido> itens = new ArrayList<>();
      while (true) {
        System.out.print("Deseja listar produtos cadastrados? (S/N): ");
        String listar = scanner.nextLine().trim();
        if (listar.equalsIgnoreCase("S")) {
          // for-each vem de produtoController.listarProdutos() — que retorna uma
          // List<Produto>
          for (Model.Produto p : produtoController.listarProdutos()) {
            System.out.println(p);
          }
        }

        System.out.print("Digite o id do produto: ");
        int produtoId;
        try {
          produtoId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
          System.out.println("ID inválido. Tente novamente.");
          continue;
        }

        Produto produto = produtoController.buscarPorId(produtoId);
        if (produto == null) {
          System.out.println("Produto não encontrado com id: " + produtoId);
          continue;
        }

        System.out.print("Quantidade: ");
        int qtd = Integer.parseInt(scanner.nextLine().trim());
        ItemPedido item = new ItemPedido(produto, qtd);
        itens.add(item);

        System.out.print("Adicionar outro produto? (S/N): ");
        String resp = scanner.nextLine().trim();
        if (!resp.equalsIgnoreCase("S")) {
          break;
        }
      }

      Pedido pedido = controller.criarPedido(cliente, itens);
      if (pedido != null) {
        System.out.println("Pedido criado: " + pedido.getId() + " total = R$ "
            + String.format("%.2f", pedido.getTotal()));
      } else {
        System.out.println("Pedido não foi criado.");
      }
    } catch (Exception e) {
      System.out.println("Erro ao criar pedido: " + e.getMessage());
    }
  }

  private void listarPedidos() {
    List<Pedido> pedidos = controller.listaPedidos();
    if (pedidos.isEmpty()) {
      System.out.println("Nenhum pedido encontrado");
      return;
    }
    for (Pedido p : pedidos) {
      System.out.println(p);
    }
  }
}