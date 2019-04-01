import lombok.Value;

@Value
public class ImpsParams {
  private final String accountNum;
  private final String ifsc;
  private final int amount;
  private final String transactionRefNum;
  private final String description;
}
