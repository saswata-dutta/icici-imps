import java.io.IOException;
import java.util.UUID;

public class Main {
  public static void main(String[] args) {
    IciciConfig config = new IciciConfig("url", "bc", "pass", "remit-name", "remit-mobile");

    String transactionRef = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    ImpsParams reqParams = new ImpsParams("acc", "ifsc", 1, transactionRef, "Dev test");

    try {
      String transferResponse =
          HttpExecutor.apply(config.getTransferUrl(), config.getTransferParams(reqParams))
              .orElse("BAD TRANSFER RESPONSE");
      System.out.println(transferResponse);

      Thread.sleep(30L * 1000);

      String statusResponse =
          HttpExecutor.apply(config.getStatusUrl(), config.getStatusParams(transactionRef))
              .orElse("BAD STATUS RESPONSE");

      System.out.println(statusResponse);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
