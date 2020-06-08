package example;

import com.sinch.xms.ApiConnection;
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.api.MtBatchTextSmsResult;

public class Example {

  private static final String SERVICE_PLAN_ID = "SERVICE_PLAN_ID";
  private static final String TOKEN = "SERVICE_TOKEN";
  private static final String [] RECIPIENTS = {"1232323131", "3213123"}  ;
  private static final String SENDER = "SENDER";

  public static void main(String[] args) {

    try (ApiConnection conn =
        ApiConnection.builder()
            .servicePlanId(SERVICE_PLAN_ID)
            .token(TOKEN)
            .start()) {
      MtBatchTextSmsResult batch =
          conn.createBatch(
              SinchSMSApi.batchTextSms()
                  .sender(SENDER)
                  .addRecipient(RECIPIENTS)
                  .body("Something good")
                  .build());

      System.out.println("Successfully sent batch " + batch.id());
    } catch (Exception e) {
      System.out.println("Batch send failed: " + e.getMessage());
    }
  }
}
