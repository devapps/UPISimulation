package com.msg.upi.model;

import com.msg.upi.model.npci.NPCIResponse;

import java.util.concurrent.CountDownLatch;

public class WaitingUPIRequest {
    private BalanceRequest balanceRequest;
    private NPCIResponse npciResponse;
    private CountDownLatch latch;

    public WaitingUPIRequest(BalanceRequest balanceRequest, CountDownLatch latch) {
        this.balanceRequest = balanceRequest;
        this.latch = latch;
    }

    public WaitingUPIRequest withNPCIResponse(NPCIResponse npciResponse) {
        this.npciResponse = npciResponse;
        return this;
    }

    public NPCIResponse getNpciResponse() {
        return npciResponse;
    }

    public BalanceRequest getBalanceRequest() {
        return balanceRequest;
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
