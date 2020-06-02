package com.lamfire.hydra.rpc;

import java.io.Serializable;

public class DiscoveryConfig implements Serializable {
    public static final String DEFAULT_DISCOVERY_ADDRESS = "224.0.0.224";
    public static final int DEFAULT_DISCOVERY_PORT = 6666;
    private String groupId;
    private String groupAddr;
    private int groupPort = DEFAULT_DISCOVERY_PORT;
    private ProviderConfig providerConfig;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupAddr() {
        return groupAddr;
    }

    public void setGroupAddr(String groupAddr) {
        this.groupAddr = groupAddr;
    }

    public int getGroupPort() {
        return groupPort;
    }

    public void setGroupPort(int groupPort) {
        this.groupPort = groupPort;
    }

    public ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    public void setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }

    @Override
    public String toString() {
        return "DiscoveryConfig{" +
                "groupId='" + groupId + '\'' +
                ", groupAddr='" + groupAddr + '\'' +
                ", groupPort=" + groupPort +
                ", providerConfig=" + providerConfig +
                '}';
    }
}
