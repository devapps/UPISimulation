package com.msg.upi.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.WaitingUPIRequest;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class RequestStore {
    private Cache<String, WaitingUPIRequest> upiRequestCache;

    public RequestStore() {
        this.upiRequestCache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(1000)
                .build();
    }

    public void put(String uid, BalanceRequest balanceRequest, CountDownLatch latch) {
        upiRequestCache.put(balanceRequest.getUid(), new WaitingUPIRequest(balanceRequest, latch));
    }

    public WaitingUPIRequest get(String requestId) {

        WaitingUPIRequest ifPresent = upiRequestCache.getIfPresent(requestId);
        upiRequestCache.invalidate(requestId);

        return ifPresent;
    }
}
