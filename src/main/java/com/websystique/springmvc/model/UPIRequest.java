package com.websystique.springmvc.model;

import java.io.Serializable;

public class UPIRequest implements Serializable {
    protected String uid;

    public UPIRequest() {
        
    }

    public UPIRequest(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
