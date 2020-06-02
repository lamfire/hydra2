package com.lamfire.hydra.rpc;

import com.lamfire.hydra.reply.Future;
import com.lamfire.hydra.reply.ReplySnake;

import java.util.concurrent.TimeoutException;

class RpcClientImpl implements RpcClient {
    private String host;
    private int port;
    private long timeout = 15000l;
    private int threads = 16;
    private ReplySnake snake;

    public RpcClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public synchronized void startup() {
        if (snake != null) {
            return;
        }
        snake = new ReplySnake();
        snake.setReadTimeoutMillis(timeout);
        snake.setHeartbeatEnable(true);
        snake.setAutoConnectRetry(true);
        snake.startup(host, port);
        snake.waitConnections();
    }

    public void shutdown() {
        if (snake != null) {
            snake.shutdown();
            snake = null;
        }
    }

    public byte[] invoke(byte[] bytes) throws TimeoutException {
        if (snake == null) {
            throw new RpcException("RPC client cannot available");
        }
        Future f = snake.send(bytes);
        return f.getResponse();
    }

    @Override
    public boolean isAvailable() {
        if (snake == null) {
            return false;
        }
        return snake.isAvailable();
    }
}
