package Repository;

import Model.Produto;
import Exceptions.IdDuplicadoException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioProdutoMemoria implements IRepositorioProduto {
  // Use map concorrente para evitar problemas em acesso paralelo e permitir
  // verificação atômica de ids duplicados.
  private Map<Integer, Produto> produtos = new ConcurrentHashMap<>();

  @Override
  public void adicionar(Produto produto) {
    Produto previous = produtos.putIfAbsent(produto.getId(), produto);
    if (previous != null) {
      throw new IdDuplicadoException("Produto");
    }
  }

  @Override
  public List<Produto> listar() {
    return new ArrayList<>(produtos.values());
  }

  @Override
  public Produto buscarPorId(int id) {
    return produtos.get(id);
  }
}