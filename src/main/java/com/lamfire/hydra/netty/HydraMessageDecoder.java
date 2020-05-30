package com.lamfire.hydra.netty;


import com.lamfire.hydra.MessageSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class HydraMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    MessageSerializer serializer = new MessageSerializer();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(serializer.decode(msg));
    }
}
