package com.lamfire.hydra.netty;

import com.lamfire.hydra.DataPacket;
import com.lamfire.hydra.MessageReceivedListener;
import com.lamfire.hydra.Session;
import com.lamfire.logger.Logger;


class NettyHandleTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(NettyHandleTask.class);
    private MessageReceivedListener messageReceivedListener;
    private DataPacket dataPacket;
    private Session session;

    @Override
    public void run() {
        try {
            messageReceivedListener.onMessageReceived(session, dataPacket);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
    }

    public MessageReceivedListener getMessageReceivedListener() {
        return messageReceivedListener;
    }

    public void setMessageReceivedListener(MessageReceivedListener messageReceivedListener) {
        this.messageReceivedListener = messageReceivedListener;
    }

    public DataPacket getDataPacket() {
        return dataPacket;
    }

    public void setDataPacket(DataPacket dataPacket) {
        this.dataPacket = dataPacket;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
