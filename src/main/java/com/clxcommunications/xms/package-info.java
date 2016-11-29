/**
 * This package contains a Java interface for the CLX Communications XMS API.
 * This API is intended for managing and sending batches of SMS messages as well
 * as receiving mobile originated messages.
 * <p>
 * The typical use case of this library is to create a long lived
 * {@link com.clxcommunications.xms.ApiConnection} object which is then used to
 * submit batches and perform other interaction with the XMS endpoint.
 * <p>
 * As a simple initial example we can consider a program that, when run, sends a
 * single SMS and exits.
 * 
 * <pre>
 * import com.clxcommunications.xms.ApiConnection;
 * import com.clxcommunications.xms.ClxApi;
 * import com.clxcommunications.xms.api.MtBatchTextSmsResult;
 * 
 * public class Example {
 * 
 *     public static void main(String[] args) {
 *         ApiConnection conn = ApiConnection.builder()
 *                 .username("my username")
 *                 .token("my token")
 *                 .start();
 * 
 *         try {
 *             MtBatchTextSmsResult result =
 *                     conn.createBatch(ClxApi.buildBatchTextSms()
 *                             .from("my short code")
 *                             .addTo("my destination")
 *                             .body("my message")
 *                             .build());
 * 
 *             System.out.println("The batch ID is " + result.id());
 *         } catch (Exception e) {
 *             System.err.println("Failed to send message: " + e.getMessage());
 *         }
 *     }
 * 
 * }
 * </pre>
 */
package com.clxcommunications.xms;
