package com.lamfire.hydra.rpc;

public class HydraRPC {
    public static final RpcSerializer JAVA_SERIALIZER = new JavaSerializer();
    public static final RpcSerializer KRYO_SERIALIZER = new KryoSerializer();

    private final ProviderPool pool = new ProviderPool();
    private RpcSerializer serializer = HydraRPC.KRYO_SERIALIZER;

    public void addProvider(ProviderConfig config){
        pool.addProvider(config);
    }

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    public <T> T lookup(Class<?> interfaceClass){
        return ProxyManager.getProxy(interfaceClass,pool.getRpcClient(),serializer);
    }
}
