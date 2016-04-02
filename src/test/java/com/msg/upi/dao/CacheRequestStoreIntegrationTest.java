package com.msg.upi.dao;

import com.msg.upi.AbstractSpringIntegrationTest;
import com.msg.upi.model.BalanceRequest;
import com.msg.upi.model.WaitingUPIRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.msg.upi.dao.CacheRequestStore.MAXIMUM_CACHE_SIZE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CacheRequestStoreIntegrationTest extends AbstractSpringIntegrationTest {

    @Autowired
    private RequestStore sut;

    @Test
    public void should_hold_ten_thousand_requests() throws Exception {
        List<WaitingUPIRequest> requestList = new ArrayList<>();

        for (int i = 0; i < MAXIMUM_CACHE_SIZE; i++) {
            BalanceRequest balanceRequest = new BalanceRequest(String.valueOf(i));
            requestList.add(new WaitingUPIRequest(balanceRequest, new CountDownLatch(1)));

            sut.put(String.valueOf(i), requestList.get(i));
        }

        Thread.sleep(1000);

        assertThat(sut.get(String.valueOf(0)), is(requestList.get(0)));
        assertThat(sut.get(String.valueOf(100)), is(requestList.get(100)));
    }

    @Test
    public void should_hold_more_than_ten_thousand_requests() throws Exception {
        List<WaitingUPIRequest> requestList = new ArrayList<>();

        int cachedCount = MAXIMUM_CACHE_SIZE;
        for (int i = 0; i < cachedCount; i++) {
            BalanceRequest balanceRequest = new BalanceRequest(String.valueOf(i));
            requestList.add(new WaitingUPIRequest(balanceRequest, new CountDownLatch(1)));

            sut.put(String.valueOf(i), requestList.get(i));
        }

        Thread.sleep(1000);

        for (int i = cachedCount; i < cachedCount * 2; i++) {
            BalanceRequest balanceRequest = new BalanceRequest(String.valueOf(i));
            requestList.add(new WaitingUPIRequest(balanceRequest, new CountDownLatch(1)));

            sut.put(String.valueOf(i), requestList.get(i));
        }

        assertThat(sut.get(String.valueOf(100)), is(nullValue()));
        assertThat(sut.get(String.valueOf(cachedCount)), is(requestList.get(cachedCount)));
        assertThat(sut.get(String.valueOf(cachedCount+1111)), is(requestList.get(cachedCount+1111)));
    }
}