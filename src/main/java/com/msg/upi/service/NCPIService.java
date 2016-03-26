package com.msg.upi.service;

import com.msg.upi.dao.NCPIDao;
import com.msg.upi.dao.RequestStore;
import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.BalanceResponse;
import com.msg.upi.model.WaitingUPIRequest;
import com.msg.upi.model.npci.Ack;
import com.msg.upi.model.npci.NPCIFetchBalanceRequest;
import com.msg.upi.model.npci.NPCIFetchBalanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class NCPIService {

    private NCPIDao ncpiDao;
    private RequestStore requestStore;


    @Autowired
    public NCPIService(NCPIDao ncpiDao, RequestStore requestStore) {
        this.ncpiDao = ncpiDao;
        this.requestStore = requestStore;
    }

    public void requestFetchBalance(BalanceRequest balanceRequest, CountDownLatch latch) {
        requestStore.put(balanceRequest.getUid(), balanceRequest, latch);
        NPCIFetchBalanceRequest npciRequest = new NPCIFetchBalanceRequest();
        Ack ack = ncpiDao.invokeFetchBalance(npciRequest);
    }

    public BalanceResponse callbackFetchBalance(NPCIFetchBalanceResponse npciResponse) {
        String requestId = npciResponse.getUid();
        WaitingUPIRequest ifPresent = requestStore.get(requestId);
        if (ifPresent == null) {
            throw new UPIRequestIdNotFoundInCache("Request Id not found, release the waiting mobile thread" + requestId);
        }

        ifPresent.getLatch().countDown();

        return new BalanceResponse(requestId);
    }

}
