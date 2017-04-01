package com.lamfire.hydra.netty;

import com.lamfire.utils.Lists;
import com.lamfire.hydra.HeartbeatMessage;
import com.lamfire.hydra.Message;
import com.lamfire.hydra.Session;
import com.lamfire.hydra.SessionClosedListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;


public class NettySession implements Session {
    private long id;
    private ChannelHandlerContext channelContext;
    private final List<SessionClosedListener> listeners = Lists.newLinkedList();
    private final NettyChannelClosedListener channelClosedListener;

    public NettySession(long id,ChannelHandlerContext channelContext){
        this.id = id;
        this.channelContext = channelContext;
        this.channelClosedListener = new NettyChannelClosedListener(this);
        this.channelContext.channel().closeFuture().addListener(channelClosedListener);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void send(Message message) {
        channelContext.writeAndFlush(message);
    }

    public SocketAddress getRemoteAddress(){
        return channelContext.channel().remoteAddress();
    }

    public boolean isActive(){
        return channelContext.channel().isActive();
    }

    public boolean isOpen(){
        return channelContext.channel().isOpen();
    }

    public boolean isWritable(){
        return channelContext.channel().isWritable();
    }

    public Object attr(String name){
        Attribute<Object> attrVal =  channelContext.attr(AttributeKey.valueOf(name));
        if(attrVal == null){
            return null;
        }
        return attrVal.get();
    }

    public void attr(String name,Object value){
        Attribute<Object> attrVal =  channelContext.attr(AttributeKey.valueOf(name));
        attrVal.set(value);
    }

    @Override
    public void close() {
        try {
            channelContext.close().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void heartbeat(){
        send(HeartbeatMessage.HEARTBEAT_REQUEST_MESSAGE);
    }

    public Channel channel(){
        return channelContext.channel();
    }

    public Collection<SessionClosedListener> closedListeners(){
        return listeners;
    }

    public void addCloseListener(SessionClosedListener listener){
        listeners.add(listener);
    }

    public void removeCloseListener(SessionClosedListener listener){
        listeners.remove(listener);
    }

    @Override
    public String toString() {
        return "NettySession{" +
                "id=" + id +
                ", channel=" + channelContext.channel() +
                '}';
    }
}
