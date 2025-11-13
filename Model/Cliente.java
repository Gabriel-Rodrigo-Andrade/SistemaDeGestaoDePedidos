package Model;

import Util.ClienteValidator;

public class Cliente {
  private String id;
  private String nome;
  private String email;

  public Cliente(String id, String nome, String email) {
    // Delegar validação ao ClienteValidator para manter consistência com Produto
    ClienteValidator.validateId(id);
    ClienteValidator.validateNome(nome);
    ClienteValidator.validateEmail(email);

    this.id = id;
    this.nome = nome;
    this.email = email;
  }

  public String getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return String.format("Cliente[id=%s, nome=%s, email=%s]", id, nome, email);
  }
}
