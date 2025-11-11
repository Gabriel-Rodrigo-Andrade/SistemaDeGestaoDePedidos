package Exceptions;

public class IdDuplicadoException extends RuntimeException {
  public IdDuplicadoException() {
    super("Já existe um registro com este ID.");
  }

  public IdDuplicadoException(String entidade) {
    super(String.format("Já existe um(a) %s com este ID.", entidade));
  }
}
