package com.lamfire.hydra.netty;

import com.lamfire.logger.Logger;
import com.lamfire.utils.*;
import com.lamfire.hydra.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Collection;
import java.util.concurrent.*;

public class NettyClient implements Snake,SessionCreatedListener {
    private static final Logger LOGGER = Logger.getLogger(NettyClient.class);
    private final HydraSessionMgr mgr = new HydraSessionMgr("NettyClient");
    private MessageReceivedListener messageReceivedListener;
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;
    private ThreadPoolExecutor threadPoolExecutor;

    private ScheduledExecutorService heartbeatService ;
    private boolean heartbeatEnable = false;
    private int heartbeatInterval = 300000;
    private HeartbeatListener heartbeatListener;
    private boolean autoConnectRetry = false;
    private int connectionTimeout = 15000;
    private String host;
    private int port = 1980;
    private int workerThreads = 16;

    public NettyClient(int port){
        this.port = port;
    }

    public NettyClient(String host, int port){
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

    public void setSessionCreatedListener(SessionCreatedListener listener){
        this.sessionCreatedListener = listener;
    }

    public void setSessionClosedListener(SessionClosedListener sessionClosedListener){
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
        if(bootstrap != null){
            LOGGER.error("Bootstrap was running,ignore...");
            return;
        }
        workerGroup = new NioEventLoopGroup(workerThreads, Threads.makeThreadFactory("Hydra/worker"));
        if(workerThreads > 0) {
            threadPoolExecutor = new ThreadPoolExecutor(workerThreads, workerThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(), new com.lamfire.utils.ThreadFactory("task"));
        }

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
                            ch.pipeline().addLast(new NettyInboundHandler(mgr, messageReceivedListener, heartbeatListener,NettyClient.this,sessionClosedListener,threadPoolExecutor));
                        }
                    });

            bootstrap.connect(host,port).await();
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        //startup heartbeat
        startupHeartbeat();
        waitConnections();
    }

    private void startupHeartbeat(){
        if(heartbeatEnable && heartbeatService == null){
            heartbeatService = Executors.newSingleThreadScheduledExecutor(Threads.makeThreadFactory("Hydra/heartbeat"));
            heartbeatService.scheduleWithFixedDelay(heartbeat,heartbeatInterval,heartbeatInterval, TimeUnit.MILLISECONDS);
        }
    }

    public synchronized void waitConnections(){
        if(mgr.isEmpty()){
            try {
                this.wait(connectionTimeout);
            }catch (Throwable t){

            }
        }
    }

    ChannelFuture connect(){
        if(bootstrap == null){
            throw new RuntimeException("The client not startup.");
        }
        return bootstrap.connect(host,port);
    }

    public synchronized void shutdown(){
        try {
            if(heartbeatService != null){
                LOGGER.info("Shutdown heartbeat...");
                heartbeatService.shutdown();
                heartbeatService = null;
            }
        }catch (Exception e){

        }

        try {
            LOGGER.info("Shutdown channel...");
            mgr.close();
        }catch (Exception e){

        }

        threadPoolExecutor.shutdown();

        LOGGER.info("Shutdown worker group...");
        workerGroup.shutdownGracefully();

        workerGroup = null;
        bootstrap = null;
        threadPoolExecutor = null;
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
        if(mgr.isEmpty()){
           return null;
        }
        return mgr.all().iterator().next();
    }

    public synchronized void waitAvailable(){
        while(!isAvailable()){
            Threads.sleep(10);
        }
    }

    @Override
    public boolean isAvailable() {
        if(!mgr.isEmpty()){
            return true;
        }
        return false;
    }

    Runnable heartbeat = new Runnable() {
        @Override
        public void run() {
            try{
                Collection<Session> sessions = mgr.all();

                LOGGER.debug("[HEARTBEAT] Active sessions = " + sessions.size());

                if(sessions.isEmpty() && autoConnectRetry){
                    LOGGER.debug("[HEARTBEAT] retry connect to = " + host +":" + port);
                    connect();
                }
                for(Session s : sessions){
                    if(s.isActive() && s.isWritable()){
                        s.heartbeat();
                    }
                }
            }catch (Throwable t){
                LOGGER.error(t.getMessage(),t);
            }
        }
    };

    @Override
    public synchronized void onCreated(Session session) {
        if(this.sessionCreatedListener != null){
            this.sessionCreatedListener.onCreated(session);
        }
        this.notifyAll();
    }
}
