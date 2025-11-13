package Util;

import java.util.List;
import Exceptions.PedidoInvalidoException;
import Model.Cliente;
import Model.ItemPedido;

public class PedidoValidator {
  public static void validarCriacao(Cliente cliente, List<ItemPedido> itens) {
    if (cliente == null) {
      throw new PedidoInvalidoException("Cliente é obrigatório");
    }
    if (itens == null || itens.isEmpty()) {
      throw new PedidoInvalidoException("Pedido deve conter ao menos 1 item");
    }
    for (ItemPedido ip : itens) {
      if (ip == null)
        throw new PedidoInvalidoException("Item inválido no pedido");
      if (ip.getQuantidade() <= 0)
        throw new PedidoInvalidoException("Quantidade deve ser maior que 0");
    }
  }
}