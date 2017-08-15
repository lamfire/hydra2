package com.lamfire.hydra.netty;

import com.lamfire.code.UUIDGen;
import com.lamfire.logger.Logger;
import com.lamfire.hydra.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ThreadPoolExecutor;

public class NettyInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = Logger.getLogger(NettyInboundHandler.class);
    private final HydraSessionMgr sessionMgr;
    private final MessageReceivedListener messageReceivedListener;
    private HeartbeatListener heartbeatListener;
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;
    private ThreadPoolExecutor threadPoolExecutor;

    public NettyInboundHandler(HydraSessionMgr sessionMgr, MessageReceivedListener messageReceivedListener, HeartbeatListener heartbeatListener, SessionCreatedListener sessionCreatedListener, SessionClosedListener sessionClosedListener, ThreadPoolExecutor threadPoolExecutor){
        this.sessionMgr = sessionMgr;
        this.messageReceivedListener = messageReceivedListener;
        this.heartbeatListener = heartbeatListener;
        this.sessionCreatedListener = sessionCreatedListener;
        this.sessionClosedListener = sessionClosedListener;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HeartbeatMessage){
            if(heartbeatListener == null){
                return;
            }
            HeartbeatMessage hm = (HeartbeatMessage)msg;
            heartbeatListener.onHeartbeat(sessionMgr.get(ctx.channel()),hm);
            return;
        }

        if(messageReceivedListener == null){
            return;
        }

        if(msg instanceof Message){
            Message m = (Message) msg;
            if(threadPoolExecutor != null) {
                NettyHandleTask task = new NettyHandleTask();
                task.setMessageReceivedListener(messageReceivedListener);
                task.setMessage(m);
                task.setSession(sessionMgr.get(ctx.channel()));
                this.threadPoolExecutor.submit(task);
                return;
            }
            messageReceivedListener.onMessageReceived(sessionMgr.get(ctx.channel()), m);
        }
    }


    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.debug(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        long id = UUIDGen.getTimeSafe();
        NettySession s = new NettySession(id,ctx);
        sessionMgr.add(s);
        if(sessionCreatedListener != null){
            sessionCreatedListener.onCreated(s);
        }
        if(sessionClosedListener != null){
            s.addCloseListener(sessionClosedListener);
        }
        LOGGER.debug("channelActive - " + s);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.debug("channelInactive - " + ctx.channel());
    }
}
