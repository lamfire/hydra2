package com.lamfire.hydra;

import com.lamfire.hydra.netty.websocket.HydraWebSocketServer;
import com.lamfire.utils.Asserts;

/**
 * HydraBuilder
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:38
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketHydraBuilder {
    private String bind = "0.0.0.0";
    private int port = 1980;
    private int threads = 16;
    private String websocketPath = "/ws";
    private int maxContentLength = 65535;
    private MessageReceivedListener messageReceivedListener;
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;
    private HeartbeatListener heartbeatListener = new DefaultHeartbeatListener();

    public WebSocketHydraBuilder bind(String bind) {
        this.bind = bind;
        return this;
    }

    public WebSocketHydraBuilder port(int port) {
        this.port = port;
        return this;
    }

    public WebSocketHydraBuilder heartbeatListener(HeartbeatListener heartbeatListener) {
        this.heartbeatListener = heartbeatListener;
        return this;
    }

    public WebSocketHydraBuilder threads(int threads) {
        this.threads = threads;
        return this;
    }

    public WebSocketHydraBuilder messageReceivedListener(MessageReceivedListener listener) {
        this.messageReceivedListener = listener;
        return this;
    }

    public WebSocketHydraBuilder sessionCreatedListener(SessionCreatedListener listener) {
        this.sessionCreatedListener = listener;
        return this;
    }

    public WebSocketHydraBuilder sessionClosedListener(SessionClosedListener sessionClosedListener) {
        this.sessionClosedListener = sessionClosedListener;
        return this;
    }

    public WebSocketHydraBuilder websocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
        return this;
    }

    public WebSocketHydraBuilder maxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }

    public Hydra build() {
        Asserts.notNullAssert(messageReceivedListener);
        HydraWebSocketServer server = new HydraWebSocketServer(bind, port);
        server.setMessageReceivedListener(messageReceivedListener);
        server.setHeartbeatListener(heartbeatListener);
        server.setSessionCreatedListener(sessionCreatedListener);
        server.setSessionClosedListener(sessionClosedListener);
        server.setWorkerThreads(threads);
        server.setMaxContentLength(maxContentLength);
        server.setWebsocketPath(websocketPath);
        return server;
    }
}
