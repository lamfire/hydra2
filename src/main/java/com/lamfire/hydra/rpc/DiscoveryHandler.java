package com.lamfire.hydra.rpc;


import com.lamfire.logger.Logger;
import com.lamfire.utils.Bytes;

import java.net.DatagramPacket;


class DiscoveryHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(DiscoveryHandler.class);
    private DiscoveryMultiCaster multiCaster;
    private DiscoveryListener listener;
    private boolean shutdown = false;

    public DiscoveryHandler(DiscoveryMultiCaster multiCaster, DiscoveryListener listener) {
        this.multiCaster = multiCaster;
        this.listener = listener;
    }

    public void run() {
        while (!shutdown) {
            handleNext();
        }
    }

    private void handleNext() {
        try {
            byte[] buffer = new byte[8192];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            this.multiCaster.receive(packet);

            int len = Bytes.toInt(buffer);
            byte[] data = Bytes.subBytes(buffer, 4, len);

            if (listener != null) {
                DiscoveryContext ctx = new DiscoveryContext(multiCaster, packet);
                listener.onDiscoveryMessage(ctx, data);
            }
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
