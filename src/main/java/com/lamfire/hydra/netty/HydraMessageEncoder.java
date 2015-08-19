package com.lamfire.hydra.netty;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageHeader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午1:54
 * To change this template use File | Settings | File Templates.
 */
public class HydraMessageEncoder extends MessageToByteEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        MessageHeader header = msg.header();
        out.writeInt(header.getId());
        out.writeInt(header.getContentLength());
        out.writeInt(header.getChecksum());
        if(header.getContentLength() > 0){
            out.writeBytes(msg.content());
        }
    }
}
