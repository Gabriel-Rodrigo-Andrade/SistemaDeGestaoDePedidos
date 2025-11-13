package Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pedido {
  private final String id; // repositorio dos pia usam id como String tbm
  private final Cliente cliente;
  private final List<ItemPedido> itens = new ArrayList<>();
  private volatile Status status;
  private double total = 0.0;
  private final LocalDateTime criadoEm;

  public Pedido(Cliente cliente, List<ItemPedido> itensInicial) {
    if (cliente == null)
      throw new IllegalArgumentException("Cliente não pode ser nulo");
    this.id = UUID.randomUUID().toString();
    this.cliente = cliente;
    this.criadoEm = LocalDateTime.now();
    this.status = Status.ABERTO;

    if (itensInicial != null) {
      for (ItemPedido ip : itensInicial) {
        adicionarItem(ip);
      }
    }
    calcularTotal();
  }

  public String getId() {
    return id;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public List<ItemPedido> getItens() {
    return Collections.unmodifiableList(new ArrayList<>(itens));
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public double getTotal() {
    return total;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status novoStatus) {
    this.status = novoStatus;
  }

  public void adicionarItem(ItemPedido item) {
    if (item == null)
      throw new IllegalArgumentException("Item nao pode ser nulo");
    itens.add(item);
    total += item.getSubtotal();
  }

  public void calcularTotal() {
    double soma = 0.0;
    for (ItemPedido ip : itens) {
      soma += ip.getSubtotal();
    }
    this.total = soma;
  }

  @Override
  public String toString() {
    // StringBuilder evita criar muitas Strings temporárias durante concatenações
    // sucessivas
    // (melhor quando tem muitas chamadas a append).
    StringBuilder sb = new StringBuilder();
    sb.append("Pedido[id=").append(id)
        .append(", status=").append(status)
        .append(", criadoEm=").append(criadoEm)
        .append(String.format(", total=R$ %.2f", total))
        .append("]\n");
    sb.append("Cliente: ").append(cliente).append("\n");
    sb.append("Itens:");
    for (ItemPedido ip : itens) {
      sb.append(" - ").append(ip).append("\n");
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof Pedido))
      return false;
    Pedido pedido = (Pedido) o;
    return id.equals(pedido.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
