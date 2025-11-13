package Ui;

import Controller.ProdutoController;
import Model.Categoria;
import Model.Produto;

import java.util.Scanner;
import java.util.List;

public class MenuProduto {
  private ProdutoController controller;

  private Scanner sc = new Scanner(System.in);

  // Injeção do controller para compartilhar repositório
  public MenuProduto(ProdutoController controller) {
    this.controller = controller;
  }

  public void exibirMenu() {
    int opcao = -1;
    do {
      System.out.println("\n=== MENU DE PRODUTOS ===");
      System.out.println("1. Cadastrar Produto");
      System.out.println("2. Listar Produtos");
      System.out.println("0. Voltar");
      System.out.print("Escolha: ");
      String entrada = sc.nextLine();
      try {
        opcao = Integer.parseInt(entrada.trim());
      } catch (NumberFormatException e) {
        System.out.println("Opção inválida. Digite um número.");
        continue;
      }

      switch (opcao) {
        case 1:
          cadastrarProduto();
          break;
        case 2:
          listarProdutos();
          break;
        case 0:
          System.out.println("Voltando...");
          break;
        default:
          System.out.println("Opção inválida!");
      }
    } while (opcao != 0);
  }

  private void cadastrarProduto() {
    try {
      System.out.print("ID: ");
      int id = Integer.parseInt(sc.nextLine().trim());

      System.out.print("Nome: ");
      String nome = sc.nextLine();

      System.out.print("Preço: ");
      double preco = Double.parseDouble(sc.nextLine().trim());

      System.out.println("Categorias disponíveis:");
      for (Categoria c : Categoria.values()) {
        System.out.println("- " + c);
      }

      System.out.print("Categoria: ");
      String catStr = sc.nextLine().toUpperCase();
      Categoria categoria = Categoria.valueOf(catStr);

      boolean ok = controller.cadastrarProduto(id, nome, preco, categoria);
      if (!ok) {
        System.out.println("Por favor, verifique os dados e tente novamente.\n");
      }

    } catch (IllegalArgumentException e) {
      System.out.println("❌ Categoria inválida!");
    } catch (Exception e) {
      System.out.println("❌ Erro: " + e.getMessage());
    }
  }

  private void listarProdutos() {
    System.out.println("\n=== LISTA DE PRODUTOS ===");
    List<Produto> produtos = controller.listarProdutos();
    if (produtos.isEmpty()) {
      System.out.println("Nenhum produto cadastrado.");
      return;
    }
    for (Produto p : produtos) {
      System.out.println(p);
    }
  }
}
