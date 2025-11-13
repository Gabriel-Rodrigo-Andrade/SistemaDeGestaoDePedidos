package Service;

import Model.Pedido;
import Model.Status;
import Repository.IRepositorioPedido;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker simples que consome a fila e processa pedidos.
 * Usa System.out.println para feedback (coerente com o projeto).
 */
public class ProcessadorFilaPedidos implements Runnable {
  private final IRepositorioPedido repositorio;
  private final long processamentoMillis;
  private final AtomicBoolean running = new AtomicBoolean(true);

  public ProcessadorFilaPedidos(IRepositorioPedido repositorio, long processamentoMillis) {
    this.repositorio = repositorio;
    this.processamentoMillis = processamentoMillis;
  }

  public void stop() {
    running.set(false);
  }

  @Override
  public void run() {
    try {
      while (running.get() && !Thread.currentThread().isInterrupted()) {
        Pedido pedido = repositorio.retirarDaFila(); // bloqueante
        if (pedido == null) {
          continue;
        }

        System.out.println("Iniciando processamento de pedido " + pedido.getId());
        pedido.setStatus(Status.PROCESSANDO);
        repositorio.adicionar(pedido);

        try {
          Thread.sleep(processamentoMillis);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          System.out.println("Processador interrompido durante o sleep");
          break;
        }

        pedido.setStatus(Status.FINALIZADO);
        repositorio.adicionar(pedido);
        System.out.println("Pedido " + pedido.getId() + " finalizado");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.println("Processador interrompido e encerrado");
    } catch (Exception ex) {
      System.out.println("Erro no processador: " + ex.getMessage());
    } finally {
      System.out.println("Processador encerrado");
    }
  }
}
