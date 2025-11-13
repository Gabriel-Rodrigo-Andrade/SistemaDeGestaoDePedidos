package Repository;

import Model.Produto;
import java.util.List;

public interface IRepositorioProduto {
  void adicionar(Produto produto);

  List<Produto> listar();

  Produto buscarPorId(int id);
}
