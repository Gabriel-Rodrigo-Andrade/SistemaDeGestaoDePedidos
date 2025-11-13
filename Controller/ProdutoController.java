package Controller;

import Model.Produto;
import Model.Categoria;
import Service.ProdutoService;
import Exceptions.*;

import java.util.List;

public class ProdutoController {
  private ProdutoService service;

  public ProdutoController(ProdutoService service) {
    this.service = service;
  }

  public boolean cadastrarProduto(int id, String nome, double preco, Categoria categoria) {
    try {
      service.cadastrarProduto(id, nome, preco, categoria);
      System.out.println("✅ Produto cadastrado com sucesso!");
      return true;
    } catch (ProdutoInvalidoException e) {
      System.out.println("❌ Erro: " + e.getMessage());
      return false;
    } catch (IdDuplicadoException e) {
      System.out.println("❌ Erro: ID já cadastrado.");
      return false;
    } catch (Exception e) {
      System.out.println("⚠️ Erro inesperado: " + e.getMessage());
      return false;
    }
  }

  public List<Produto> listarProdutos() {
    return service.listarProdutos();
  }

  public Produto buscarPorId(int id) {
    return service.buscarPorId(id);
  }
}
