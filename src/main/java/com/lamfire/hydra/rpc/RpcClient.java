package com.lamfire.hydra.rpc;


import java.util.concurrent.TimeoutException;


public interface RpcClient{

    public byte[] invoke(byte[] bytes) throws TimeoutException ;

    public boolean isAvailable();

    public  void startup();

    public void shutdown();
}
