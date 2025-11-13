package Exceptions;

public class ClienteInvalidoException extends RuntimeException {
  public ClienteInvalidoException(String message) {
    super(message);
  }
}
