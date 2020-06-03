package com.lamfire.hydra;

import io.netty.bootstrap.ServerBootstrap;

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

    ServerBootstrap getServerBootstrap();

    MessageReceivedListener getMessageReceivedListener();

    void setMessageReceivedListener(MessageReceivedListener listener);

    int getWorkerThreads();

    void setWorkerThreads(int workerThreads);
}
