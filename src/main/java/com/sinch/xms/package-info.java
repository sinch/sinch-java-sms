/*-
 * #%L
 * SDK for Sinch SMS
 * %%
 * Copyright (C) 2016 Sinch
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * This package contains a Java interface for the Sinch XMS API. This API is intended for managing
 * and sending batches of SMS messages as well as receiving mobile originated messages.
 *
 * <p>The typical use case of this library is to create a long lived {@link
 * com.sinch.xms.ApiConnection} object which is then used to submit batches and perform other
 * interaction with the XMS endpoint.
 *
 * <p>As a simple initial example we can consider a program that, when run, sends a single SMS and
 * exits.
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
 *                 .servicePlanId("my spid")
 *                 .token("my token")
 *                 .start();
 *
 *         try {
 *             MtBatchTextSmsResult result =
 *                     conn.createBatch(ClxApi.batchTextSms()
 *                             .sender("my short code")
 *                             .addRecipient("my destination")
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
package com.sinch.xms;
