package Repository;

import Model.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class RepositorioPedidoMemoria implements IRepositorioPedido {
  private final ConcurrentHashMap<String, Pedido> map = new ConcurrentHashMap<>();
  private final BlockingQueue<Pedido> fila = new LinkedBlockingQueue<>();

  @Override
  public void adicionar(Pedido pedido) {
    map.put(pedido.getId(), pedido);
  }

  @Override
  public List<Pedido> listar() {
    return new ArrayList<>(map.values());
  }

  @Override
  public Pedido buscarPorId(String id) {
    return map.get(id);
  }

  @Override
  public void enfileirar(Pedido pedido) throws InterruptedException {
    fila.put(pedido);
  }

  @Override
  public Pedido retirarDaFila() throws InterruptedException {
    return fila.take();
  }

  @Override
  public int tamanhoFila() {
    return fila.size();
  }
}
