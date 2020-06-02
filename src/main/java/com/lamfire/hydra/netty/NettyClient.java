package com.lamfire.hydra.netty;

import com.lamfire.hydra.*;
import com.lamfire.logger.Logger;
import com.lamfire.utils.Threads;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient implements Snake, SessionCreatedListener {
    private static final Logger LOGGER = Logger.getLogger(NettyClient.class);
    private final HydraSessionMgr mgr = new HydraSessionMgr("NettyClient");
    private MessageReceivedListener messageReceivedListener;
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private ScheduledExecutorService monitorService;
    private boolean heartbeatEnable = false;
    private int heartbeatInterval = 300000;
    private HeartbeatListener heartbeatListener;
    private boolean autoConnectRetry = false;
    private int autoConnectRetryInterval = 5000;
    private int connectionTimeout = 15000;
    private String host;
    private int port = 1980;
    Runnable heartbeatTask = new Runnable() {
        @Override
        public void run() {
            try {
                Collection<Session> sessions = mgr.all();

                LOGGER.debug("[HEARTBEAT] Active sessions = " + sessions.size());

                if (sessions.isEmpty() && autoConnectRetry) {
                    LOGGER.debug("[HEARTBEAT] retry connect to = " + host + ":" + port);
                    connect();
                }
                for (Session s : sessions) {
                    if (s.isActive() && s.isWritable()) {
                        s.heartbeat();
                    }
                }
            } catch (Throwable t) {
                LOGGER.error(t.getMessage(), t);
            }
        }
    };
    Runnable autoConnectTask = new Runnable() {
        @Override
        public void run() {
            try {
                Collection<Session> sessions = mgr.all();
                if (sessions.isEmpty() && autoConnectRetry) {
                    LOGGER.debug("[AUTO_CONNECT_RETRY] retry connect to = " + host + ":" + port);
                    connect();
                }

            } catch (Throwable t) {
                LOGGER.error(t.getMessage(), t);
            }
        }
    };
    private int workerThreads = 1;

    public NettyClient(int port) {
        this.port = port;
    }

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean isHeartbeatEnable() {
        return heartbeatEnable;
    }

    public void setHeartbeatEnable(boolean heartbeatEnable) {
        this.heartbeatEnable = heartbeatEnable;
    }

    public void setHeartbeatListener(HeartbeatListener heartbeatListener) {
        this.heartbeatListener = heartbeatListener;
    }

    public void setSessionCreatedListener(SessionCreatedListener listener) {
        this.sessionCreatedListener = listener;
    }

    public void setSessionClosedListener(SessionClosedListener sessionClosedListener) {
        this.sessionClosedListener = sessionClosedListener;
    }

    public boolean isAutoConnectRetry() {
        return autoConnectRetry;
    }

    public void setAutoConnectRetry(boolean autoConnectRetry) {
        this.autoConnectRetry = autoConnectRetry;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public synchronized void startup() {
        if (bootstrap != null) {
            LOGGER.error("Bootstrap was running,ignore...");
            return;
        }
        workerGroup = new NioEventLoopGroup(workerThreads, Threads.makeThreadFactory("Hydra/worker"));
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new HydraMessageEncoder());
                            ch.pipeline().addLast(new HydraMessageDecoder());
                            ch.pipeline().addLast(new NettyInboundHandler(mgr, messageReceivedListener, heartbeatListener, NettyClient.this, sessionClosedListener));
                        }
                    });

            bootstrap.connect(host, port).await();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        //startup heartbeat
        startupHeartbeatTask();
        startupAutoConnectRetryTask();
        waitConnections();
    }

    private synchronized ScheduledExecutorService getMonitorService() {
        if (monitorService == null) {
            monitorService = Executors.newSingleThreadScheduledExecutor(Threads.makeThreadFactory("Hydra/monitor"));
        }
        return monitorService;
    }

    private synchronized void startupHeartbeatTask() {
        if (heartbeatEnable) {
            getMonitorService().scheduleWithFixedDelay(heartbeatTask, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
        }
    }

    private synchronized void startupAutoConnectRetryTask() {
        if (autoConnectRetry) {
            getMonitorService().scheduleWithFixedDelay(autoConnectTask, autoConnectRetryInterval, autoConnectRetryInterval, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void waitConnections() {
        if (mgr.isEmpty()) {
            try {
                this.wait(connectionTimeout);
            } catch (Throwable t) {

            }
        }
    }

    ChannelFuture connect() {
        if (bootstrap == null) {
            throw new RuntimeException("The client not startup.");
        }
        return bootstrap.connect(host, port);
    }

    public synchronized void shutdown() {
        try {
            if (monitorService != null) {
                LOGGER.info("Shutdown monitor...");
                monitorService.shutdown();
                monitorService = null;
            }
        } catch (Exception e) {

        }

        try {
            LOGGER.info("Shutdown channel...");
            mgr.close();
        } catch (Exception e) {

        }
        LOGGER.info("Shutdown worker group...");
        workerGroup.shutdownGracefully();

        workerGroup = null;
        bootstrap = null;
    }

    @Override
    public SessionMgr getSessionMgr() {
        return mgr;
    }

    @Override
    public MessageReceivedListener getMessageReceivedListener() {
        return messageReceivedListener;
    }

    @Override
    public void setMessageReceivedListener(MessageReceivedListener listener) {
        this.messageReceivedListener = listener;
    }

    @Override
    public Session getSession() {
        if (mgr.isEmpty()) {
            return null;
        }
        return mgr.all().iterator().next();
    }

    public synchronized void waitAvailable() {
        if (!isAvailable()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void waitAvailable(long millis) {
        if (!isAvailable()) {
            try {
                this.wait(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return !mgr.isEmpty();
    }

    @Override
    public synchronized void onCreated(Session session) {
        this.notifyAll();
        if (this.sessionCreatedListener != null) {
            this.sessionCreatedListener.onCreated(session);
        }
    }

    public int getAutoConnectRetryInterval() {
        return autoConnectRetryInterval;
    }

    public void setAutoConnectRetryInterval(int autoConnectRetryInterval) {
        this.autoConnectRetryInterval = autoConnectRetryInterval;
    }

}
