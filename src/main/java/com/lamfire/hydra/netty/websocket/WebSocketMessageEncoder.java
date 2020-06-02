package com.lamfire.hydra.netty.websocket;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageHeader;
import com.lamfire.hydra.MessageSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;


public class WebSocketMessageEncoder extends MessageToMessageEncoder<Message> {
    MessageSerializer serializer = new MessageSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) {
        MessageHeader header = msg.header();
        int bytesLen = header.contentLength() + 12;
        ByteBuf buffer = ctx.alloc().buffer(bytesLen);
        serializer.encode(msg, buffer);
        WebSocketFrame frame = new BinaryWebSocketFrame(buffer);
        out.add(frame);
    }
}
