import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class IciciConfig {
  private String baseUrl;
  private String bcName;
  private String passCode;
  private String remitterName;
  private String remmitterMobileNo;

  static final String BENE_ACC_NO = "BeneAccNo";
  static final String BENE_IFSC = "BeneIFSC";
  static final String AMOUNT = "Amount";
  static final String TRAN_REF_NO = "TranRefNo";
  static final String PAYMENT_REF = "PaymentRef";
  static final String REM_NAME = "RemName";
  static final String REM_MOBILE = "RemMobile";
  static final String RETAILER_CODE = "RetailerCode";
  static final String PASS_CODE = "PassCode";

  public String getTransferUrl() {
    return baseUrl + "/imps-web-bc/api/transaction/bc/" + bcName + "/p2a";
  }

  public String getStatusUrl() {
    return baseUrl + "/imps-web-bc/api/transaction/bc/" + bcName + "/query";
  }

  public Map<String, String> getTransferParams(ImpsParams params) {
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put(BENE_ACC_NO, params.getAccountNum());
    queryParams.put(BENE_IFSC, params.getIfsc());
    queryParams.put(AMOUNT, params.getAmount() + "");
    queryParams.put(TRAN_REF_NO, params.getTransactionRefNum());
    queryParams.put(PAYMENT_REF, params.getDescription());
    queryParams.put(REM_NAME, remitterName);
    queryParams.put(REM_MOBILE, remmitterMobileNo);
    queryParams.put(RETAILER_CODE, bcName);
    queryParams.put(PASS_CODE, passCode);

    return queryParams;
  }

  public Map<String, String> getStatusParams(String transactionRefNum) {
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put(TRAN_REF_NO, transactionRefNum);
    queryParams.put(PASS_CODE, passCode);

    return queryParams;
  }
}
