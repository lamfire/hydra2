package com.lamfire.hydra.rpc;


import java.io.Serializable;

public class ProviderConfig implements Serializable{
    private String name;
    private int threads = 32;
    private String serviceAddr;
    private String bindAddr = "0.0.0.0";
    private int port = 19800;
    private long timeoutMillis = 30000;

    public ProviderConfig(){}

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

    public String getServiceAddr() {
        return serviceAddr;
    }

    public void setServiceAddr(String serviceAddr) {
        this.serviceAddr = serviceAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public String getBindAddr() {
        return bindAddr;
    }

    public void setBindAddr(String bindAddr) {
        this.bindAddr = bindAddr;
    }

    @Override
    public String toString() {
        return "ProviderConfig{" +
                "name='" + name + '\'' +
                ", threads=" + threads +
                ", serviceAddr='" + serviceAddr + '\'' +
                ", bindAddr='" + bindAddr + '\'' +
                ", port=" + port +
                ", timeoutMillis=" + timeoutMillis +
                '}';
    }
}
