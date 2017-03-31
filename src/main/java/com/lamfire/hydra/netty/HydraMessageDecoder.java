package com.lamfire.hydra.netty;

import com.lamfire.hydra.HeartbeatMessage;
import com.lamfire.hydra.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class HydraMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    public HydraMessageDecoder(){

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int id = msg.readInt();
        int contentLength = msg.readInt();
        int option = msg.readInt();
        byte[] content = null;


        //is heartbeat request
        if (HeartbeatMessage.isHeartbeatRequest(id, contentLength, option)) {
            out.add(HeartbeatMessage.HEARTBEAT_REQUEST_MESSAGE);
            return;
        }

        //is heartbeat request
        if (HeartbeatMessage.isHeartbeatResponse(id, contentLength, option)) {
            out.add(HeartbeatMessage.HEARTBEAT_RESPONSE_MESSAGE);
            return;
        }


        //is normal message
        if (contentLength > 0) {
            content = new byte[contentLength];
            msg.readBytes(content);
        }
        out.add(MessageFactory.message(id, option, content));

    }
}
