package Service;

import Model.Produto;
import Model.Categoria;
import Repository.IRepositorioProduto;
import Util.ProdutoValidator;

import java.util.List;

public class ProdutoService {
  private IRepositorioProduto repositorio;

  public ProdutoService(IRepositorioProduto repositorio) {
    this.repositorio = repositorio;
  }

  public void cadastrarProduto(int id, String nome, double preco, Categoria categoria) {
    // validação dupla intencional.
    ProdutoValidator.validateId(id);
    ProdutoValidator.validateNome(nome);
    ProdutoValidator.validatePreco(preco);
    ProdutoValidator.validateCategoria(categoria);
    Produto produto = new Produto(id, nome, preco, categoria);
    repositorio.adicionar(produto);
  }

  public List<Produto> listarProdutos() {
    return repositorio.listar();
  }

  public Produto buscarPorId(int id) {
    return repositorio.buscarPorId(id);
  }
}
