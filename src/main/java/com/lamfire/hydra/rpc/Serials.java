package com.lamfire.hydra.rpc;

/**
 * Created by linfan on 2017/3/1.
 */
public class Serials {
    public static final RpcSerializer JAVA_SERIALIZER = new JavaSerializer();
    public static final RpcSerializer KRYO_SERIALIZER = new KryoSerializer();
    public static final RpcSerializer DEFAULT_SERIALIZER = KRYO_SERIALIZER;
}
