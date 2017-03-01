package com.lamfire.hydra;

public interface Snake {
    public SessionMgr getSessionMgr();
    public void startup();
    public void shutdown();
    public MessageReceivedListener getMessageReceivedListener();
    public void setMessageReceivedListener(MessageReceivedListener listener);
    public Session getSession();
    public boolean isAvailable();
    public void waitSessionCreated();
}
