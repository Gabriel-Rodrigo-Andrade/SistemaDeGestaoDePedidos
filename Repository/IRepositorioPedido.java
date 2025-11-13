package Repository;

import Model.Pedido;
import java.util.List;

public interface IRepositorioPedido {
  void adicionar(Pedido pedido);

  List<Pedido> listar();

  Pedido buscarPorId(String id);

  // operacoes da fila blocking
  void enfileirar(Pedido pedido) throws InterruptedException;

  Pedido retirarDaFila() throws InterruptedException;

  int tamanhoFila();
}
