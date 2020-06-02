package com.lamfire.hydra.netty;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class HydraMessageEncoder extends MessageToByteEncoder<Message> {
    MessageSerializer serializer = new MessageSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        serializer.encode(msg, out);
    }
}
