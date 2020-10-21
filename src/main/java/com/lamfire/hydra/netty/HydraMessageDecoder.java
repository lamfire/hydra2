package com.lamfire.hydra.netty;


import com.lamfire.hydra.DataPacketSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class HydraMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    DataPacketSerializer serializer = new DataPacketSerializer();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(serializer.decode(msg));
    }
}
