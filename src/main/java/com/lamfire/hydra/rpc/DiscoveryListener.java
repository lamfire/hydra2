package com.lamfire.hydra.rpc;

interface DiscoveryListener {
    void onDiscoveryMessage(DiscoveryContext context, byte[] message);
}
