import java.util.UUID;

public class Main {
  public static void main(String[] args) {
    IciciConfig config = new IciciConfig("url", "bc", "pass", "remit-name", "remit-mobile");

    String transactionRef = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    ImpsParams reqParams = new ImpsParams(args[0], args[1], 1, transactionRef, "Dev test");

    String transferResponse =
        HttpExecutor.apply(config.getTransferUrl(), config.getTransferParams(reqParams))
            .orElse("BAD TRANSFER RESPONSE");
    System.out.println(transferResponse);

    pause(30);

    String statusResponse =
        HttpExecutor.apply(config.getStatusUrl(), config.getStatusParams(transactionRef))
            .orElse("BAD STATUS RESPONSE");

    System.out.println(statusResponse);
    HttpExecutor.shutDown();
  }

  private static void pause(long secs) {
    try {
      System.out.println("Pausing for " + secs + " secs ...");
      Thread.sleep(secs * 1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      e.printStackTrace();
    }
  }
}
