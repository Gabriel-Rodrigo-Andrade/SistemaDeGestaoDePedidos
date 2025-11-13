package Util;

import Exceptions.ProdutoInvalidoException;
import Model.Categoria;

public class ProdutoValidator {
  public static void validateId(int id) throws ProdutoInvalidoException {
    if (id <= 0) {
      throw new ProdutoInvalidoException("ID deve ser maior que zero.");
    }
  }

  public static void validateNome(String nome) throws ProdutoInvalidoException {
    if (nome == null || nome.isBlank()) {
      throw new ProdutoInvalidoException("Nome do produto não pode ser vazio.");
    }
  }

  public static void validatePreco(double preco) throws ProdutoInvalidoException {
    if (preco <= 0) {
      throw new ProdutoInvalidoException("Preço deve ser maior que zero.");
    }
  }

  public static void validateCategoria(Categoria categoria) throws ProdutoInvalidoException {
    if (categoria == null) {
      throw new ProdutoInvalidoException("Categoria inválida.");
    }
  }
}
