package com.lamfire.hydra.rpc;


public interface RpcSerializer {

    public byte[] encode(Object obj);

    public  <T> T decode(byte[] bytes, Class<T> cls);
}
