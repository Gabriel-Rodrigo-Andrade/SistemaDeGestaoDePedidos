package Controller;

import Model.Pedido;
import Model.ItemPedido;
import Model.Cliente;
import Service.PedidoService;
import Exceptions.PedidoInvalidoException;

import java.util.List;

public class PedidoController {
  private final PedidoService service;

  public PedidoController(PedidoService service) {
    this.service = service;
  }

  /**
   * Tenta criar um pedido; trata exceções e fornece feedback via console
   * Retorna o pedido criado em caso de sucesso, ou null em caso de erro.
   */
  public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens) {
    try {
      Pedido pedido = service.criarPedido(cliente, itens);
      System.out.println("✅ Pedido criado com sucesso: " + pedido.getId());
      return pedido;
    } catch (PedidoInvalidoException e) {
      System.out.println("❌ Erro ao criar pedido: " + e.getMessage());
      return null;
    } catch (Exception e) {
      System.out.println("⚠️ Erro inesperado ao criar pedido: " + e.getMessage());
      return null;
    }
  }

  public List<Pedido> listaPedidos() {
    return service.listarPedidos();
  }

  public Pedido buscarPorId(String id) {
    return service.buscarPorId(id);
  }
}
