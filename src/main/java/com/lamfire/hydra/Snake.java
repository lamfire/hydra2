package com.lamfire.hydra;

public interface Snake {
    SessionMgr getSessionMgr();

    void startup();

    void shutdown();

    MessageReceivedListener getMessageReceivedListener();

    void setMessageReceivedListener(MessageReceivedListener listener);

    Session getSession();

    boolean isAvailable();

    void waitConnections();

    void waitAvailable();

    void waitAvailable(long millis);
}
