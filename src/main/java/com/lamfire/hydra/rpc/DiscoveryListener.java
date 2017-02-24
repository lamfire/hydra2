package com.lamfire.hydra.rpc;

public interface DiscoveryListener {
    public void onDiscoveryMessage(DiscoveryContext context, byte[] message);
}
