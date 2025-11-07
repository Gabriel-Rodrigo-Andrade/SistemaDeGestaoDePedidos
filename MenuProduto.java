
import java.util.Scanner;

public class MenuProduto {
    private IRepositorioProduto repositorio = new RepositorioProdutoMemoria();
    private Scanner sc = new Scanner(System.in);

    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n=== MENU DE PRODUTOS ===");
            System.out.println("1. Cadastrar Produto");
            System.out.println("2. Listar Produtos");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");
            opcao = sc.nextInt();
            sc.nextLine(); 

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
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Nome: ");
            String nome = sc.nextLine();

            System.out.print("Preço: ");
            double preco = sc.nextDouble();
            sc.nextLine();

            System.out.println("Categorias disponíveis:");
            for (Categoria c : Categoria.values()) {
                System.out.println("- " + c);
            }

            System.out.print("Categoria: ");
            String catStr = sc.nextLine().toUpperCase();
            Categoria categoria = Categoria.valueOf(catStr);

            Produto produto = new Produto(id, nome, preco, categoria);
            repositorio.adicionar(produto);
            System.out.println("✅ Produto cadastrado com sucesso!");

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Categoria inválida!");
        } catch (ProdutoInvalidoException e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
    }

    private void listarProdutos() {
        System.out.println("\n=== LISTA DE PRODUTOS ===");
        for (Produto p : repositorio.listar()) {
            System.out.println(p);
        }
    }
}