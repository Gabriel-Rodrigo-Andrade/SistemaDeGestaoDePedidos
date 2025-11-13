package Exceptions;

public class ProdutoInvalidoException extends RuntimeException {
  public ProdutoInvalidoException(String mensagem) {
    super(mensagem);
  }
}
