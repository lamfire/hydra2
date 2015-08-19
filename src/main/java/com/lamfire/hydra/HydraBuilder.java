package com.lamfire.hydra;

import com.lamfire.utils.Asserts;
import com.lamfire.hydra.netty.NettyServer;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class HydraBuilder {
    private String bind = "0.0.0.0";
    private int port = 1980;
    private int threads = 16;
    private MessageReceivedListener  messageReceivedListener;
    private HeartbeatListener heartbeatListener = new DefaultHeartbeatListener();
    private boolean checksumEnable = false;

    public HydraBuilder bind(String bind){
        this.bind = bind;
        return this;
    }

    public HydraBuilder port(int port){
        this.port = port;
        return this;
    }

    public HydraBuilder heartbeatListener(HeartbeatListener heartbeatListener){
        this.heartbeatListener = heartbeatListener;
        return this;
    }

    public HydraBuilder threads(int threads){
        this.threads = threads;
        return this;
    }

    public HydraBuilder checksumEnable(boolean checksumEnable){
        this.checksumEnable = checksumEnable;
        return this;
    }

    public HydraBuilder messageReceivedListener(MessageReceivedListener listener){
        this.messageReceivedListener = listener;
        return this;
    }

    public Hydra build(){
        Asserts.notNullAssert(messageReceivedListener);
        NettyServer server = new NettyServer(bind,port);
        server.setMessageReceivedListener(messageReceivedListener);
        server.setHeartbeatListener(heartbeatListener);
        server.setWorkerThreads(threads);
        return server;
    }
}
