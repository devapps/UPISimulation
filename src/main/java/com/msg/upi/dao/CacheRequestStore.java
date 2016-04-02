package com.msg.upi.dao;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.msg.upi.model.WaitingUPIRequest;
import com.msg.upi.service.UPIRequestIdNotFoundInCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheRequestStore implements RequestStore {
    static final int MAXIMUM_CACHE_SIZE = 10000;
    private Cache<String, WaitingUPIRequest> upiRequestCache;

    public CacheRequestStore() {
        this.upiRequestCache = CacheBuilder.newBuilder()
                .maximumSize(MAXIMUM_CACHE_SIZE)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(2000)
                .build();
    }

    @Override
    public void put(String requestId, WaitingUPIRequest waitingUPIRequest) {
        upiRequestCache.put(requestId, waitingUPIRequest);
    }

    @Override
    public WaitingUPIRequest get(String requestId) {
        WaitingUPIRequest ifPresent = upiRequestCache.getIfPresent(requestId);

        if (ifPresent == null) {
            throw new UPIRequestIdNotFoundInCache("Request Id not found, release the waiting mobile thread" + requestId);
        }

        return ifPresent;
    }

    @Override
    public void removeFromStore(String requestId) {
        upiRequestCache.invalidate(requestId);
    }
}
