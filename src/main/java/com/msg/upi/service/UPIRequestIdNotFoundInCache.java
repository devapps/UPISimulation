package com.msg.upi.service;

public class UPIRequestIdNotFoundInCache extends RuntimeException {
    public UPIRequestIdNotFoundInCache(String message) {
        super(message);
    }
}
