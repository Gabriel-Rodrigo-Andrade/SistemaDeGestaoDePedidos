package Service;

import Model.Cliente;
import Repository.IRepositorioCliente;
import Util.ClienteValidator;

import java.util.List;

public class ClienteService {
  private IRepositorioCliente repositorio;

  public ClienteService(IRepositorioCliente repositorio) {
    this.repositorio = repositorio;
  }

  public void cadastrarCliente(String id, String nome, String email) {
    // Validações centrais via utilitário
    ClienteValidator.validateId(id);
    ClienteValidator.validateNome(nome);
    ClienteValidator.validateEmail(email);
    Cliente cliente = new Cliente(id, nome, email);
    repositorio.adicionar(cliente);
  }

  public List<Cliente> listarClientes() {
    return repositorio.listar();
  }
}
