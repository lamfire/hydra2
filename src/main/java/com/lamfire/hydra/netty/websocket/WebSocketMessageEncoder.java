package com.lamfire.hydra.netty.websocket;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;


public class WebSocketMessageEncoder extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out){
        MessageHeader header = msg.header();
        int bytesLen = header.contentLength() + 12;
        ByteBuf buffer = ctx.alloc().buffer(bytesLen);
        buffer.writeInt(header.id());
        buffer.writeInt(header.contentLength());
        buffer.writeInt(header.option());
        if(header.contentLength() > 0){
            buffer.writeBytes(msg.content());
        }
        WebSocketFrame frame = new BinaryWebSocketFrame(buffer);
        out.add(frame);
    }
}
