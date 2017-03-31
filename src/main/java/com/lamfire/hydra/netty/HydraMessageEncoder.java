package com.lamfire.hydra.netty;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class HydraMessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        MessageHeader header = msg.header();
        out.writeInt(header.id());
        out.writeInt(header.contentLength());
        out.writeInt(header.option());
        if(header.contentLength() > 0){
            out.writeBytes(msg.content());
        }
    }
}
