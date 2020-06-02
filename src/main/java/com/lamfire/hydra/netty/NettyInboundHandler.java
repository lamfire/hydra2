package com.lamfire.hydra.netty;

import com.lamfire.hydra.*;
import com.lamfire.logger.Logger;
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

    public NettyInboundHandler(HydraSessionMgr sessionMgr, MessageReceivedListener messageReceivedListener, HeartbeatListener heartbeatListener, SessionCreatedListener sessionCreatedListener, SessionClosedListener sessionClosedListener){
        this.sessionMgr = sessionMgr;
        this.messageReceivedListener = messageReceivedListener;
        this.heartbeatListener = heartbeatListener;
        this.sessionCreatedListener = sessionCreatedListener;
        this.sessionClosedListener = sessionClosedListener;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        long sessionId = ctx.channel().hashCode();
        Session session = sessionMgr.get(sessionId);

        if(msg instanceof HeartbeatMessage){
            if(heartbeatListener == null){
                return;
            }
            HeartbeatMessage hm = (HeartbeatMessage)msg;
            heartbeatListener.onHeartbeat(session,hm);
            return;
        }

        if(messageReceivedListener != null && msg instanceof Message) {
            messageReceivedListener.onMessageReceived(session, (Message)msg);
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
        NettySession s = new NettySession(ctx.channel().hashCode(),ctx);
        sessionMgr.put(s.getId(),s);
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
