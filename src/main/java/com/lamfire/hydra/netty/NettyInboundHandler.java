package com.lamfire.hydra.netty;

import com.lamfire.code.UUIDGen;
import com.lamfire.logger.Logger;
import com.lamfire.hydra.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-12
 * Time: 上午11:08
 * To change this template use File | Settings | File Templates.
 */
public class NettyInboundHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = Logger.getLogger(NettyInboundHandler.class);
    private final NettySessionMgr  sessionMgr;
    private final MessageReceivedListener messageReceivedListener;
    private HeartbeatListener heartbeatListener;
    private SessionCreatedListener sessionCreatedListener;
    private SessionClosedListener sessionClosedListener;

    public NettyInboundHandler(NettySessionMgr sessionMgr, MessageReceivedListener messageReceivedListener,HeartbeatListener heartbeatListener,SessionCreatedListener sessionCreatedListener,SessionClosedListener sessionClosedListener){
        this.sessionMgr = sessionMgr;
        this.messageReceivedListener = messageReceivedListener;
        this.heartbeatListener = heartbeatListener;
        this.sessionCreatedListener = sessionCreatedListener;
        this.sessionClosedListener = sessionClosedListener;
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

        if(msg instanceof Message){
            if(messageReceivedListener == null){
                return;
            }
            Message m = (Message) msg;
            messageReceivedListener.onMessageReceived(sessionMgr.get(ctx.channel()),m);
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
