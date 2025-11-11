package Model;

import Exceptions.ProdutoInvalidoException;

public class Produto {
  private int id;
  private String nome;
  private double preco;
  private Categoria categoria;

  public Produto(int id, String nome, double preco, Categoria categoria) {
    if (preco <= 0) {
      throw new ProdutoInvalidoException("Preço deve ser maior que zero!");
    }
    if (nome == null || nome.trim().isEmpty()) {
      throw new ProdutoInvalidoException("Nome do produto não pode estar vazio!");
    }
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
