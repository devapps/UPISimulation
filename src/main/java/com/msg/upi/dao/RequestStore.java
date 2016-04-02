package com.msg.upi.dao;

import com.msg.upi.model.WaitingUPIRequest;

public interface RequestStore {
    void put(String requestId, WaitingUPIRequest latch);
    WaitingUPIRequest get(String requestId);
    void removeFromStore(String requestId);
}
