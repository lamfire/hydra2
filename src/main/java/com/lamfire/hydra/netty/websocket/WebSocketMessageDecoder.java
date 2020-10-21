package com.lamfire.hydra.netty.websocket;

import com.lamfire.hydra.DataPacketSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    DataPacketSerializer serializer = new DataPacketSerializer();

    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame frame, List<Object> out) {
        ByteBuf msg = frame.content();
        out.add(serializer.decode(msg));
    }
}
