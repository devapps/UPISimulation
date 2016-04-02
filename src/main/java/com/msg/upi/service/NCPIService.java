package com.msg.upi.service;

import com.msg.upi.dao.NCPIDao;
import com.msg.upi.dao.RequestStore;
import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.BalanceResponse;
import com.msg.upi.model.WaitingUPIRequest;
import com.msg.upi.model.npci.Ack;
import com.msg.upi.model.npci.NPCIFetchBalanceRequest;
import com.msg.upi.model.npci.NPCIFetchBalanceResponse;
import com.msg.upi.model.npci.NPCIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class NCPIService {

    private NCPIDao ncpiDao;
    private RequestStore cacheRequestStore;


    @Autowired
    public NCPIService(NCPIDao ncpiDao, RequestStore cacheRequestStore) {
        this.ncpiDao = ncpiDao;
        this.cacheRequestStore = cacheRequestStore;
    }

    public void requestFetchBalance(BalanceRequest balanceRequest, CountDownLatch latch) {
        cacheRequestStore.put(balanceRequest.getUid(), new WaitingUPIRequest(balanceRequest, latch));
        NPCIFetchBalanceRequest npciRequest = new NPCIFetchBalanceRequest();
        Ack ack = ncpiDao.invokeFetchBalance(npciRequest);
    }

    public void callbackFetchBalance(NPCIFetchBalanceResponse npciResponse) {
        String requestId = npciResponse.getUid();

        WaitingUPIRequest ifPresent = cacheRequestStore.get(requestId);
        ifPresent.withNPCIResponse(npciResponse);
        cacheRequestStore.put(requestId, ifPresent);

        ifPresent.getLatch().countDown();
    }

    public BalanceResponse processResponse(String requestId) {
        WaitingUPIRequest ifPresent = cacheRequestStore.get(requestId);
        NPCIResponse npciResponse = ifPresent.getNpciResponse();
        //process response

        cacheRequestStore.removeFromStore(requestId);

        return new BalanceResponse(requestId);
    }
}
