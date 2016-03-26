package com.websystique.springmvc.model;

import java.io.Serializable;

public class UPIResponse implements Serializable {
    protected String uid;

    public UPIResponse(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }
}
