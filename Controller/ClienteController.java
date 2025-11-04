package Controller;

import Model.Cliente;
import Service.ClienteService;
import Exceptions.*;

import java.util.List;

public class ClienteController {
  private ClienteService service;

  public ClienteController(ClienteService service) {
    this.service = service;
  }

  public boolean cadastrarCliente(String id, String nome, String email) {
    // Validação rápida no controller para melhorar UX (evita chamada desnecessária
    // ao Service)
    try {
      Util.ClienteValidator.validateId(id);
    } catch (ClienteInvalidoException e) {
      System.out.println("❌ Erro: " + e.getMessage());
      return false;
    }

    try {
      service.cadastrarCliente(id, nome, email);
      System.out.println("✅ Cliente cadastrado com sucesso!");
      return true;
    } catch (IdDuplicadoException e) {
      System.out.println("❌ Erro: ID já cadastrado.");
      return false;
    } catch (EmailInvalidoException e) {
      System.out.println("❌ Erro: Email inválido.");
      return false;
    } catch (ClienteInvalidoException e) {
      System.out.println("❌ Erro: " + e.getMessage());
      return false;
    } catch (Exception e) {
      System.out.println("⚠️ Erro inesperado: " + e.getMessage());
      return false;
    }
  }

  public void listarClientes() {
    List<Cliente> clientes = service.listarClientes();
    if (clientes.isEmpty()) {
      System.out.println("📭 Nenhum cliente cadastrado.");
    } else {
      clientes.forEach(System.out::println);
    }
  }
}
