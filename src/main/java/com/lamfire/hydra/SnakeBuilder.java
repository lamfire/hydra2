package com.lamfire.hydra;

import com.lamfire.hydra.netty.NettyClient;
import com.lamfire.utils.Asserts;

/**
 * SnakeBuilder
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class SnakeBuilder {
    private String host;
    private int port = 1980;
    private int threads = 1;
    private MessageReceivedListener  messageReceivedListener;
    private boolean heartbeatEnable = false;
    private boolean autoConnectRetry = false;
    private int heartbeatInterval = 15000;
    private HeartbeatListener heartbeatListener = new DefaultHeartbeatListener();
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;

    public SnakeBuilder host(String host){
        this.host = host;
        return this;
    }

    public SnakeBuilder port(int port){
        this.port = port;
        return this;
    }

    public SnakeBuilder threads(int threads){
        this.threads = threads;
        return this;
    }

    public SnakeBuilder heartbeatInterval(int heartbeatInterval){
        this.heartbeatInterval = heartbeatInterval;
        return this;
    }

    public SnakeBuilder heartbeatEnable(boolean heartbeatEnable){
        this.heartbeatEnable = heartbeatEnable;
        return this;
    }

    public SnakeBuilder heartbeatListener(HeartbeatListener heartbeatListener){
        this.heartbeatListener = heartbeatListener;
        return this;
    }

    public SnakeBuilder messageReceivedListener(MessageReceivedListener listener){
        this.messageReceivedListener = listener;
        return this;
    }

    public SnakeBuilder autoConnectRetry(boolean autoConnectRetry){
        this.autoConnectRetry = autoConnectRetry;
        return this;
    }

    public SnakeBuilder sessionCreatedListener(SessionCreatedListener listener){
        this.sessionCreatedListener = listener;
        return this;
    }

    public SnakeBuilder sessionClosedListener(SessionClosedListener sessionClosedListener){
        this.sessionClosedListener = sessionClosedListener;
        return this;
    }

    public Snake build(){
        Asserts.notNullAssert(host);
        NettyClient client = new NettyClient(host,port);
        client.setMessageReceivedListener(messageReceivedListener);
        client.setWorkerThreads(threads);
        client.setHeartbeatEnable(heartbeatEnable);
        client.setHeartbeatInterval(heartbeatInterval);
        client.setHeartbeatListener(heartbeatListener);
        client.setAutoConnectRetry(autoConnectRetry);
        client.setSessionCreatedListener(sessionCreatedListener);
        client.setSessionClosedListener(sessionClosedListener);
        return client;
    }
}
