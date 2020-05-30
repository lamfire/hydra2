package com.lamfire.hydra.netty;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageReceivedListener;
import com.lamfire.hydra.Session;
import com.lamfire.logger.Logger;


class NettyHandleTask implements Runnable{
    private static final Logger LOGGER = Logger.getLogger(NettyHandleTask.class);
    private MessageReceivedListener messageReceivedListener;
    private Message message;
    private Session session;

    @Override
    public void run() {
        try {
            messageReceivedListener.onMessageReceived(session, message);
        }catch (Throwable t){
            LOGGER.error(t.getMessage(),t);
        }
    }

    public MessageReceivedListener getMessageReceivedListener() {
        return messageReceivedListener;
    }

    public void setMessageReceivedListener(MessageReceivedListener messageReceivedListener) {
        this.messageReceivedListener = messageReceivedListener;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
