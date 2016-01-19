package com.lamfire.hydra;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public interface Snake {
    public SessionMgr getSessionMgr();
    public void startup();
    public void shutdown();
    public MessageReceivedListener getMessageReceivedListener();
    public void setMessageReceivedListener(MessageReceivedListener listener);
    public Session getSession();
    public boolean isAvailable();
}
