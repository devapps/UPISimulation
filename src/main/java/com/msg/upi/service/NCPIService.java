package com.msg.upi.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.msg.upi.dao.NCPIDao;
import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.BalanceResponse;
import com.msg.upi.model.npci.Ack;
import com.msg.upi.model.npci.NPCIFetchBalanceRequest;
import com.msg.upi.model.npci.NPCIFetchBalanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class NCPIService {

    private NCPIDao ncpiDao;
    private Cache<String, WaitingUPIRequest> upiRequestCache;

    @Autowired
    public NCPIService(NCPIDao ncpiDao) {
        this.ncpiDao = ncpiDao;
        this.upiRequestCache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(1000)
                .build();
    }

    public void requestFetchBalance(BalanceRequest balanceRequest, CountDownLatch latch) {
        upiRequestCache.put(balanceRequest.getUid(), new WaitingUPIRequest(balanceRequest, latch));
        NPCIFetchBalanceRequest npciRequest = new NPCIFetchBalanceRequest();
        Ack ack = ncpiDao.invokeFetchBalance(npciRequest);
    }

    public BalanceResponse callbackFetchBalance(NPCIFetchBalanceResponse npciResponse) {
        String requestId = npciResponse.getUid();
        WaitingUPIRequest ifPresent = upiRequestCache.getIfPresent(requestId);
        if (ifPresent == null) {
            throw new UPIRequestIdNotFoundInCache("Request Id not found, release the waiting mobile thread" + requestId);
        }
        ifPresent.getLatch().countDown();
        upiRequestCache.invalidate(requestId);

        return new BalanceResponse(requestId);
    }

    private class WaitingUPIRequest {
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
}
