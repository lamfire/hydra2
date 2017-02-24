package com.lamfire.hydra.rpc;

import java.io.Serializable;

public class RemoteException extends RpcException {

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }

}
