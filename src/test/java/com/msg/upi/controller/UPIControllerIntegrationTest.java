package com.msg.upi.controller;

import com.msg.upi.AbstractSpringIntegrationTest;
import com.msg.upi.model.BalanceRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
        String jsonResponse = "{\"uid\" : \"1\"}";

        mockMvc.perform(post("/upi/1.0/fetchBalance")
                .contentType(contentType)
                .content(asJsonString(new BalanceRequest("1"))))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }
}