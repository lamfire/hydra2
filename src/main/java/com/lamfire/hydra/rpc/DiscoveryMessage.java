package com.lamfire.hydra.rpc;

import java.io.Serializable;

public class DiscoveryMessage implements Serializable {
    public static final int TYPE_REQUEST = 0;
    public static final int TYPE_RESPONSE = 1;
    private int type;
    private int id;
    private DiscoveryConfig discoveryConfig;
    private String where;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return discoveryConfig;
    }

    public void setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = discoveryConfig;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    @Override
    public String toString() {
        return "DiscoveryMessage{" +
                "type=" + type +
                ", id=" + id +
                ", discoveryConfig=" + discoveryConfig +
                ", where='" + where + '\'' +
                '}';
    }
}
