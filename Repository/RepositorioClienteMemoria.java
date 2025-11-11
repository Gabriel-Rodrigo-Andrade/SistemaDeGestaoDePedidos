package Repository;

import Model.Cliente;
import Exceptions.IdDuplicadoException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RepositorioClienteMemoria implements IRepositorioCliente {
  private Map<String, Cliente> clientes = new ConcurrentHashMap<>();

  @Override
  public void adicionar(Cliente cliente) {
    // putIfAbsent é atômico e evita condição de corrida quando há acesso
    // concorrente. Se já existir um cliente com o mesmo ID, lançamos
    // uma exceção para manter a contract da aplicação.
    Cliente previous = clientes.putIfAbsent(cliente.getId(), cliente);
    if (previous != null) {
      throw new IdDuplicadoException("Cliente");
    }
  }

  @Override
  public List<Cliente> listar() {
    return new ArrayList<>(clientes.values());
  }

  @Override
  public Cliente buscarPorId(String id) {
    return clientes.get(id);
  }
}
