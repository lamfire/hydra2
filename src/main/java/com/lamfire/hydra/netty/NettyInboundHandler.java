package com.lamfire.hydra.netty;

import com.lamfire.hydra.*;
import com.lamfire.logger.Logger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = Logger.getLogger(NettyInboundHandler.class);
    private final HydraSessionMgr sessionMgr;
    private final MessageReceivedListener messageReceivedListener;
    private HeartbeatListener heartbeatListener;
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;
    private Session session;

    public NettyInboundHandler(HydraSessionMgr sessionMgr, MessageReceivedListener messageReceivedListener, HeartbeatListener heartbeatListener, SessionCreatedListener sessionCreatedListener, SessionClosedListener sessionClosedListener) {
        this.sessionMgr = sessionMgr;
        this.messageReceivedListener = messageReceivedListener;
        this.heartbeatListener = heartbeatListener;
        this.sessionCreatedListener = sessionCreatedListener;
        this.sessionClosedListener = sessionClosedListener;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if (msg instanceof HeartbeatDataPacket) {
            if (heartbeatListener == null) {
                return;
            }
            HeartbeatDataPacket hm = (HeartbeatDataPacket) msg;
            heartbeatListener.onHeartbeat(session, hm);
            return;
        }

        if (messageReceivedListener != null && msg instanceof DataPacket) {
            messageReceivedListener.onMessageReceived(session, (DataPacket) msg);
        }
    }


    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.debug(cause.getMessage(), cause);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.session = new NettySession(ctx.channel().hashCode(), ctx);
        sessionMgr.put(session.getId(), session);
        if (sessionCreatedListener != null) {
            sessionCreatedListener.onCreated(session);
        }
        if (sessionClosedListener != null) {
            session.addCloseListener(sessionClosedListener);
        }
        LOGGER.debug("channelActive - " + session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOGGER.debug("channelInactive - " + ctx.channel());
    }
}
