package com.lamfire.hydra.netty.websocket;

import com.lamfire.hydra.DataPacket;
import com.lamfire.hydra.DataPacketHeader;
import com.lamfire.hydra.DataPacketSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;


public class WebSocketMessageEncoder extends MessageToMessageEncoder<DataPacket> {
    DataPacketSerializer serializer = new DataPacketSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, DataPacket msg, List<Object> out) {
        DataPacketHeader header = msg.header();
        int bytesLen = header.contentLength() + 12;
        ByteBuf buffer = ctx.alloc().buffer(bytesLen);
        serializer.encode(msg, buffer);
        WebSocketFrame frame = new BinaryWebSocketFrame(buffer);
        out.add(frame);
    }
}
