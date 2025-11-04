package Repository;

import Model.Cliente;
import java.util.List;

public interface IRepositorioCliente {
    void adicionar(Cliente cliente);
    List<Cliente> listar();
    Cliente buscarPorId(String id);
}
