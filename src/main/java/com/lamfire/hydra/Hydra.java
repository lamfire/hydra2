package com.lamfire.hydra;

/**
 * Hydra
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:32
 * To change this template use File | Settings | File Templates.
 */
public interface Hydra {
    SessionMgr getSessionMgr();

    void startup();

    void shutdown();

    MessageReceivedListener getMessageReceivedListener();

    void setMessageReceivedListener(MessageReceivedListener listener);

    int getWorkerThreads();

    void setWorkerThreads(int workerThreads);
}
