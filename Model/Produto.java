package Model;

import Util.ProdutoValidator;

public class Produto {
  private int id;
  private String nome;
  private double preco;
  private Categoria categoria;

  public Produto(int id, String nome, double preco, Categoria categoria) {
    // Delegar validação ao ProdutoValidator para evitar duplicação com cliente
    ProdutoValidator.validateId(id);
    ProdutoValidator.validateNome(nome);
    ProdutoValidator.validatePreco(preco);
    ProdutoValidator.validateCategoria(categoria);

    this.id = id;
    this.nome = nome;
    this.preco = preco;
    this.categoria = categoria;
  }

  public int getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public double getPreco() {
    return preco;
  }

  public Categoria getCategoria() {
    return categoria;
  }

  @Override
  public String toString() {
    return String.format("ID: %d | Nome: %s | Preço: R$ %.2f | Categoria: %s",
        id, nome, preco, categoria);
  }
}
