package Model;

public class ItemPedido {
  private final Produto produto;
  private final int quantidade;

  public ItemPedido(Produto produto, int quantidade) {
    if (produto == null) {
      throw new IllegalArgumentException("Produto nao pode ser nulo");
    }
    if (quantidade <= 0) {
      throw new IllegalArgumentException("Quantidade deve ser > 0");
    }
    this.produto = produto;
    this.quantidade = quantidade;
  }

  public Produto getProduto() {
    return produto;
  }

  public int getQuantidade() {
    return quantidade;
  }

  public double getSubtotal() {
    // Produto.getPreco() retorna double
    return produto.getPreco() * quantidade;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof ItemPedido))
      return false;
    ItemPedido that = (ItemPedido) o;
    return quantidade == that.quantidade && produto.equals(that.produto);
  }

  @Override
  public String toString() {
    return String.format("%s x%d => R$ %.2f", produto.getNome(), quantidade, getSubtotal());
  }

}
