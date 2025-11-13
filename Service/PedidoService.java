package Service;

import Model.Cliente;
import Model.ItemPedido;
import Model.Pedido;
import Model.Status;
import Repository.IRepositorioPedido;

import java.util.List;

import Exceptions.PedidoInvalidoException;
import Util.PedidoValidator;

public class PedidoService {
  private final IRepositorioPedido repositorio;

  public PedidoService(IRepositorioPedido repositorio) {
    this.repositorio = repositorio;
  }

  /**
   * Cria pedido, calcula total, salva e enfileira.
   * Transição: ABERTO -> FILA
   */
  public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens) {
    PedidoValidator.validarCriacao(cliente, itens);

    Pedido pedido = new Pedido(cliente, itens);
    pedido.setStatus(Status.ABERTO);
    repositorio.adicionar(pedido);

    try {
      repositorio.enfileirar(pedido);
      pedido.setStatus(Status.FILA);
      repositorio.adicionar(pedido);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println("Thread interrompida ao enfileirar pedido");
      throw new PedidoInvalidoException("Falha ao enfileirar pedido");
    }
    return pedido;
  }

  public List<Pedido> listarPedidos() {
    return repositorio.listar();
  }

  public Pedido buscarPorId(String id) {
    return repositorio.buscarPorId(id);
  }
}
