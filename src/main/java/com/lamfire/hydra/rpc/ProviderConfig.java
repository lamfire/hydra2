package com.lamfire.hydra.rpc;


public class ProviderConfig {
    private String name;
    private int threads = 32;
    private String host;
    private int port;
    private long timeoutMillis = 6000;

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

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public String toString() {
        return "ProviderConfig{" +
                "name='" + name + '\'' +
                ", threads=" + threads +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", timeoutMillis=" + timeoutMillis +
                '}';
    }
}
