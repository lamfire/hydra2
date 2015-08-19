package com.lamfire.hydra.netty;

import com.lamfire.logger.Logger;
import com.lamfire.utils.Threads;
import com.lamfire.hydra.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-12
 * Time: 上午11:07
 * To change this template use File | Settings | File Templates.
 */
public class NettyClient implements Snake {
    private static final Logger LOGGER = Logger.getLogger(NettyClient.class);
    private final NettySessionMgr mgr = new NettySessionMgr();
    private MessageReceivedListener messageReceivedListener;
    private Bootstrap bootstrap;
    private EventLoopGroup workerGroup;

    private ScheduledExecutorService heartbeatService ;
    private boolean heartbeatEnable = false;
    private int heartbeatInterval = 15000;
    private HeartbeatListener heartbeatListener;
    private boolean autoConnectRetry = false;

    private String host;
    private int port = 1980;
    private int workerThreads = 16;
    private boolean checksumEnable = false;

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

    public boolean isChecksumEnable() {
        return checksumEnable;
    }

    public void setChecksumEnable(boolean checksumEnable) {
        this.checksumEnable = checksumEnable;
    }


    public synchronized void startup() {
        if(bootstrap != null){
            LOGGER.error("Bootstrap was running,system shutdown now...");
            System.exit(-1);
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
                            ch.pipeline().addLast(new HydraMessageDecoder(checksumEnable));
                            ch.pipeline().addLast(new NettyInboundHandler(mgr, messageReceivedListener, heartbeatListener));
                        }
                    });

            bootstrap.connect(host,port).sync();
            while(mgr.all().isEmpty()){
                Threads.sleep(1);
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            LOGGER.error("Shutdown now...");
            shutdown();
            System.exit(-1);
        }

        //startup heartbeat
        if(heartbeatEnable && heartbeatService == null){
            heartbeatService = Executors.newSingleThreadScheduledExecutor(Threads.makeThreadFactory("Hydra/heartbeat"));
            heartbeatService.scheduleWithFixedDelay(heartbeat,heartbeatInterval,heartbeatInterval, TimeUnit.MILLISECONDS);
        }
    }

    void connect(){
        if(bootstrap == null){
            throw new RuntimeException("The client not startup.");
        }
        try{
            bootstrap.connect(host,port).sync();
        }catch (Throwable t){
            LOGGER.error(t.getMessage(),t);
        }
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
        if(mgr.isEmpty()){
           return null;
        }
        return mgr.all().iterator().next();
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
}
