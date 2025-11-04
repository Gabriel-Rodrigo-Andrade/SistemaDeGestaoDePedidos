package Repository;

import Model.Cliente;
import java.util.*;

public class RepositorioClienteMemoria implements IRepositorioCliente {
    private Map<String, Cliente> clientes = new HashMap<>();

    @Override
    public void adicionar(Cliente cliente) {
        clientes.put(cliente.getId(), cliente);
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
