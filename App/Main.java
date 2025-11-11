package App;

import Ui.MenuCliente;
import Ui.MenuProduto;

public class Main {
  public static void main(String[] args) {
    new MenuCliente().exibirMenu();
    new MenuProduto().exibirMenu();
  }
}