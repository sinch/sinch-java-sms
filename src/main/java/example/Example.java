

package example;

import com.sinch.xms.ApiConnection;
import com.sinch.xms.SinchSMSApi;
import com.sinch.xms.api.MtBatchTextSmsResult;

public class Example {
  public static void main(String[] args) {
    //
      try (ApiConnection conn = ApiConnection.builder()
              .servicePlanId("AAA_ra").token("e3c0a7c3423c44a180844661f13ae005").start()) {
          MtBatchTextSmsResult batch = conn.createBatch(
                  SinchSMSApi.batchTextSms().sender("18339472287").addRecipient("+15417909456", "+14047691562").body("+14047691562").build()
          );
        
          System.out.println("Successfully sent batch " + batch.id());
      } catch (Exception e) {
          System.out.println("Batch send failed: " + e.getMessage());
      }}
}
