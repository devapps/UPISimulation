package com.msg.upi.dao;

import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.WaitingUPIRequest;

import java.util.concurrent.CountDownLatch;

public interface RequestStore {
    void put(BalanceRequest balanceRequest, CountDownLatch latch);
    WaitingUPIRequest get(String requestId);
}
