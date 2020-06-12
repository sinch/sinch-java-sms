package example;

import com.sinch.xms.ApiConnection;
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.api.GroupResult;
import com.sinch.xms.api.MtBatchTextSmsResult;

public class Example {

  private static final String SERVICE_PLAN_ID = "SERVICE_PLAN_ID";
  private static final String TOKEN = "SERVICE_TOKEN";
  private static final String[] RECIPIENTS = {"1232323131", "3213123"};
  private static final String SENDER = "SENDER";

  public static void main(String[] args) {
    try (ApiConnection conn =
        ApiConnection.builder().servicePlanId(SERVICE_PLAN_ID).token(TOKEN).start()) {

      // Sending a simple Text Message
      MtBatchTextSmsResult batch =
          conn.createBatch(
              SinchSMSApi.batchTextSms()
                  .sender(SENDER)
                  .addRecipient(RECIPIENTS)
                  .body("Something good")
                  .build());

      System.out.println("Successfully sent batch " + batch.id());

      // Creating simple Group
      GroupResult group = conn.createGroup(SinchSMSApi.groupCreate().name("Subscriber").build());

      // Adding members (numbers) into the group
      conn.updateGroup(
          group.id(), SinchSMSApi.groupUpdate().addMemberInsertion("15418888", "323232").build());

      // Sending a message to the group
      batch = conn.createBatch(
          SinchSMSApi.batchTextSms()
              .addRecipient(group.id().toString())
              .body("Something good")
              .build());

      System.out.println("Successfully sent batch " + batch.id());
    } catch (Exception e) {
      System.out.println("Batch send failed: " + e.getMessage());
    }
  }
}
