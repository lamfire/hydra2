package com.lamfire.hydra.netty;

import com.lamfire.hydra.DataPacket;
import com.lamfire.hydra.DataPacketSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class HydraMessageEncoder extends MessageToByteEncoder<DataPacket> {
    DataPacketSerializer serializer = new DataPacketSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, DataPacket msg, ByteBuf out) throws Exception {
        serializer.encode(msg, out);
    }
}
