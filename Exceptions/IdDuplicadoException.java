package Exceptions;

public class IdDuplicadoException extends RuntimeException {
  public IdDuplicadoException() {
    super("Já existe um cliente com este ID.");
  }
}
