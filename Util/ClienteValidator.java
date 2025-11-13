package Util;

import Exceptions.ClienteInvalidoException;
import Exceptions.EmailInvalidoException;

public class ClienteValidator {
  public static void validateId(String id) {
    if (id == null || id.isBlank()) {
      throw new ClienteInvalidoException("ID não pode ser vazio.");
    }
    if (!id.matches("\\d+")) {
      throw new ClienteInvalidoException("ID deve conter apenas números inteiros.");
    }
  }

  public static void validateNome(String nome) {
    if (nome == null || nome.isBlank()) {
      throw new ClienteInvalidoException("Nome não pode ser vazio.");
    }
  }

  public static void validateEmail(String email) {
    if (email == null || !email.contains("@") || !email.contains(".")) {
      throw new EmailInvalidoException();
    }
  }
}
