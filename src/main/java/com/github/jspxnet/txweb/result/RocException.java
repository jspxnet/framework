package com.github.jspxnet.txweb.result;

public class RocException extends Exception {
    private RocResponse response;

    public RocException(RocResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    public RocResponse getResponse() {
        return response;
    }

    public void setResponse(RocResponse response) {
        this.response = response;
    }
}
