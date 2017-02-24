package com.lamfire.hydra.rpc;

interface DiscoveryListener {
    public void onDiscoveryMessage(DiscoveryContext context, byte[] message);
}
