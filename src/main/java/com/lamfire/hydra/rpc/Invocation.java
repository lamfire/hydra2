package com.lamfire.hydra.rpc;


public class Invocation extends RpcMessage {
    public static final int STATUS_REQUEST = 0;
    public static final int STATUS_RESPONSE = 1;
    public static final int STATUS_EXCEPTION = 2;

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
