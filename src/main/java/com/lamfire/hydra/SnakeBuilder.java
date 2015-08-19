package com.lamfire.hydra;

import com.lamfire.utils.Asserts;
import com.lamfire.hydra.netty.NettyClient;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class SnakeBuilder {
    private String host;
    private int port = 1980;
    private int threads = 16;
    private MessageReceivedListener  messageReceivedListener;
    private boolean checksumEnable = false;
    private boolean heartbeatEnable = false;
    private boolean autoConnectRetry = false;
    private int heartbeatInterval = 15000;
    private HeartbeatListener heartbeatListener = new DefaultHeartbeatListener();

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

    public SnakeBuilder checksumEnable(boolean checksumEnable){
        this.checksumEnable = checksumEnable;
        return this;
    }

    public SnakeBuilder autoConnectRetry(boolean autoConnectRetry){
        this.autoConnectRetry = autoConnectRetry;
        return this;
    }

    public Snake build(){
        Asserts.notNullAssert(host);
        Asserts.notNullAssert(messageReceivedListener);
        NettyClient client = new NettyClient(host,port);
        client.setMessageReceivedListener(messageReceivedListener);
        client.setWorkerThreads(threads);
        client.setChecksumEnable(checksumEnable);
        client.setHeartbeatEnable(heartbeatEnable);
        client.setHeartbeatInterval(heartbeatInterval);
        client.setHeartbeatListener(heartbeatListener);
        client.setAutoConnectRetry(autoConnectRetry);
        return client;
    }
}
