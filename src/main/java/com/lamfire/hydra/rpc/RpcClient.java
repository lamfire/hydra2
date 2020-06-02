package com.lamfire.hydra.rpc;


import java.util.concurrent.TimeoutException;


public interface RpcClient {

    byte[] invoke(byte[] bytes) throws TimeoutException;

    boolean isAvailable();

    void startup();

    void shutdown();
}
