package com.lamfire.hydra.netty;

import com.lamfire.hydra.*;
import com.lamfire.logger.Logger;
import com.lamfire.utils.Threads;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;


public class NettyServer implements Hydra {
    private static final Logger LOGGER = Logger.getLogger(NettyServer.class);
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
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 4;

    public NettyServer(int port) {
        this.port = port;
    }

    public NettyServer(String bind, int port) {
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
            LOGGER.error("Bootstrap was already running,system shutdown now...");
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
                            ch.pipeline().addLast(new LengthFieldPrepender(4, false));
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            ch.pipeline().addLast(new HydraMessageDecoder());
                            ch.pipeline().addLast(new HydraMessageEncoder());
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

        this.getSessionMgr().close();
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
}
