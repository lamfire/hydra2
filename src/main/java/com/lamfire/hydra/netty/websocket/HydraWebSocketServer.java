package com.lamfire.hydra.netty.websocket;

import com.lamfire.hydra.*;
import com.lamfire.hydra.netty.NettyInboundHandler;
import com.lamfire.logger.Logger;
import com.lamfire.utils.Threads;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.timeout.IdleStateHandler;


public class HydraWebSocketServer implements Hydra {
    private static final Logger LOGGER = Logger.getLogger(HydraWebSocketServer.class);
    private static final int MAX_IDLE_SECONDS = 300;
    private final HydraSessionMgr mgr = new HydraSessionMgr("NettyServer");
    private MessageReceivedListener messageReceivedListener;
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;
    private HeartbeatListener heartbeatListener;
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture bindFuture;
    private String bind = "0.0.0.0";
    private int port = 1980;
    private int workerThreads = 4;
    private String websocketPath = "/ws";
    private int maxContentLength = 65535;

    public HydraWebSocketServer(int port) {
        this.port = port;
    }

    public HydraWebSocketServer(String bind, int port) {
        this.bind = bind;
        this.port = port;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
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

    @Override
    public SessionMgr getSessionMgr() {
        return mgr;
    }

    public synchronized void startup() {
        if (bootstrap != null) {
            LOGGER.error("Bootstrap was running,system shutdown now...");
            System.exit(-1);
        }
        bossGroup = new NioEventLoopGroup(1, Threads.makeThreadFactory("Hydra/Boss"));
        workerGroup = new NioEventLoopGroup(workerThreads, Threads.makeThreadFactory("Hydra/Worker"));
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(maxContentLength));
                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            pipeline.addLast(new WebSocketServerProtocolHandler(websocketPath, null, true));
                            pipeline.addLast(new IdleStateHandler(0, 0, MAX_IDLE_SECONDS));
                            //ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                            //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
                            ch.pipeline().addLast(new WebSocketMessageDecoder());
                            ch.pipeline().addLast(new WebSocketMessageEncoder());
                            ch.pipeline().addLast(new NettyInboundHandler(mgr, messageReceivedListener, heartbeatListener, sessionCreatedListener, sessionClosedListener));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 100).childOption(ChannelOption.SO_KEEPALIVE, true);

            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bindFuture = bootstrap.bind(bind, port).sync();
            LOGGER.info("startup on - " + bind + ":" + port + "[" + workerThreads + "]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void shutdown() {
        try {
            LOGGER.info("Shutdown listener channel...");
            bindFuture.channel().close().sync();
        } catch (Exception e) {

        }
        LOGGER.info("Shutdown worker group...");
        workerGroup.shutdownGracefully();

        LOGGER.info("Shutdown boss group...");
        bossGroup.shutdownGracefully();
        bossGroup = null;
        workerGroup = null;
        bindFuture = null;
        bootstrap = null;
    }

    @Override
    public MessageReceivedListener getMessageReceivedListener() {
        return messageReceivedListener;
    }

    @Override
    public void setMessageReceivedListener(MessageReceivedListener listener) {
        this.messageReceivedListener = listener;
    }

    public String getWebsocketPath() {
        return websocketPath;
    }

    public void setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
    }

    public int getMaxContentLength() {
        return maxContentLength;
    }

    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
}
