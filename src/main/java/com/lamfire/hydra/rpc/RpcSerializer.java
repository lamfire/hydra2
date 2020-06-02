package com.lamfire.hydra.rpc;


public interface RpcSerializer {

    byte[] encode(Object obj);

    <T> T decode(byte[] bytes, Class<T> cls);
}
