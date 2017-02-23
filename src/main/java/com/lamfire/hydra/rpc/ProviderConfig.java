package com.lamfire.hydra.rpc;


public class ProviderConfig {
    private String name;
    private int threads = 32;
    private RpcSerializer serializer = HydraRPC.KRYO_SERIALIZER;
    private String host;
    private int port;

    public ProviderConfig(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
