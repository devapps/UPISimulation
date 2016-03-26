package com.msg.upi.controller;

import com.msg.upi.AbstractSpringIntegrationTest;
import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.npci.NPCIFetchBalanceRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class UPIControllerIntegrationTest extends AbstractSpringIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void should_fetch_correct_balance() throws Exception {
        String requestId = "1";
        String expectedJsonResponse = "{\"uid\":\""+requestId+"\"}";
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<String> futureResponse = executor.submit(new CallUPI(requestId));
        Thread.sleep(1000);
        Future<String> callbackFutureResponse = executor.submit(new CallUPICallback(requestId));

        String responseJson = futureResponse.get();
        String result = callbackFutureResponse.get();

        assertEquals(expectedJsonResponse, responseJson);
        assertEquals("done", result);
    }

    @Test
    public void should_process_multiple_requests_together() throws Exception {
        String requestId;
        String expectedJsonResponse;

        int nThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        Map<Future<String>, String> responses = new HashMap<>();

        for (int i=0; i < nThreads / 2; i++) {
            requestId = String.valueOf(new Random().nextInt(1000));
            expectedJsonResponse = "{\"uid\":\""+requestId+"\"}";

            Future<String> futureResponse = executor.submit(new CallUPI(requestId));
            Thread.sleep(1000);
            Future<String> callbackFutureResponse = executor.submit(new CallUPICallback(requestId));

            responses.put(futureResponse, expectedJsonResponse);
            responses.put(callbackFutureResponse, "done");
        }

        responses.forEach((response, expectedResult) -> {
            try {
                assertEquals(expectedResult, response.get());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void should_process_random_multiple_request() throws Exception {
        String requestId;
        String expectedJsonResponse;

        int nThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        Map<Future<String>, String> responses = new HashMap<>();

        for (int i=0; i < nThreads / 2; i++) {
            requestId = String.valueOf(new Random().nextInt(1000));
            expectedJsonResponse = "{\"uid\":\""+requestId+"\"}";

            Future<String> futureResponse = executor.submit(new CallUPI(requestId));
            Thread.sleep(1000);
            Future<String> callbackFutureResponse = executor.submit(new CallUPICallback(requestId));

            responses.put(futureResponse, expectedJsonResponse);
            responses.put(callbackFutureResponse, "done");
        }

        responses.forEach((response, expectedResult) -> {
            try {
                assertEquals(expectedResult, response.get());
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    private class CallUPI implements Callable<String> {
        private String requestId;

        private CallUPI(String requestId) {
            this.requestId = requestId;
        }

        public String call() throws Exception {
            MvcResult mvcResult = mockMvc.perform(post("/upi/1.0/fetchBalance")
                    .contentType(contentType)
                    .content(asJsonString(new BalanceRequest(requestId))))
                    .andExpect(status().isOk())
                    .andReturn();

            return mvcResult.getResponse().getContentAsString();
        }
    }

    private class CallUPICallback implements Callable<String> {
        private String callbackRequestId;

        private CallUPICallback(String callbackRequestId) {
            this.callbackRequestId = callbackRequestId;
        }

        public String call() throws Exception {
            mockMvc.perform(post("/upi/1.0/callback/fetchBalance")
                    .contentType(contentType)
                    .content(asJsonString(new NPCIFetchBalanceRequest(callbackRequestId))))
                    .andExpect(status().isOk());

            return "done";
        }
    }
}