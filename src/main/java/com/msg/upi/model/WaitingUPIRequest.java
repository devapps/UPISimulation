package com.msg.upi.model;

import java.util.concurrent.CountDownLatch;

public class WaitingUPIRequest {
    private BalanceRequest balanceRequest;
    private CountDownLatch latch;

    public WaitingUPIRequest(BalanceRequest balanceRequest, CountDownLatch latch) {
        this.balanceRequest = balanceRequest;
        this.latch = latch;
    }

    public BalanceRequest getBalanceRequest() {
        return balanceRequest;
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
