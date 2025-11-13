package Exceptions;

public class EmailInvalidoException extends RuntimeException {
  public EmailInvalidoException() {
    super("O email informado é inválido.");
  }
}
