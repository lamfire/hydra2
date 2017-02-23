package com.lamfire.hydra.rpc;

import com.lamfire.utils.Bytes;

import java.io.Serializable;


public class JavaSerializer implements RpcSerializer{

    public byte[] encode(Object obj) {
        return Bytes.toBytes((Serializable) obj);
    }


    public <T> T decode(byte[] bytes,Class<T> cls) {
        try {
            return (T) Bytes.toObject(bytes);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
