package Ui;

import Controller.ClienteController;
import Service.ClienteService;
import Repository.RepositorioClienteMemoria;

import java.util.Scanner;

public class MenuCliente {
  private ClienteController controller;
  private Scanner scanner = new Scanner(System.in);

  public MenuCliente() {
    controller = new ClienteController(new ClienteService(new RepositorioClienteMemoria()));
  }

  public void exibirMenu() {
    int opcao = -1;
    do {
      System.out.println("\n=== Menu Clientes ===");
      System.out.println("1. Cadastrar Cliente");
      System.out.println("2. Listar Clientes");
      System.out.println("0. Voltar");
      System.out.print("Escolha: ");
      String entrada = scanner.nextLine();
      try {
        opcao = Integer.parseInt(entrada.trim());
      } catch (NumberFormatException e) {
        System.out.println("Opção inválida. Digite um número.");
        continue;
      }

      switch (opcao) {
        case 1 -> cadastrar();
        case 2 -> controller.listarClientes();
        case 0 -> System.out.println("Voltando...");
        default -> System.out.println("Opção inválida.");
      }
    } while (opcao != 0);
  }

  private void cadastrar() {
    // Repetir até o usuário inserir dados válidos e o cadastro ser bem-sucedido
    while (true) {
      System.out.print("ID: ");
      String id = scanner.nextLine();
      System.out.print("Nome: ");
      String nome = scanner.nextLine();
      System.out.print("Email: ");
      String email = scanner.nextLine();

      boolean ok = controller.cadastrarCliente(id, nome, email);
      if (ok) {
        break;
      } else {
        System.out.println("Por favor, tente novamente.\n");
      }
    }
  }
}
