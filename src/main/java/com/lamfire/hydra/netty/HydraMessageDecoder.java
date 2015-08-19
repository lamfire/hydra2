package com.lamfire.hydra.netty;

import com.lamfire.hydra.HeartbeatMessage;
import com.lamfire.hydra.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:12
 * To change this template use File | Settings | File Templates.
 */
public class HydraMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private boolean checksumEnable = false;

    public HydraMessageDecoder(){

    }

    public HydraMessageDecoder(boolean checksumEnable){
        this.checksumEnable = checksumEnable;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int id = msg.readInt();
        int contentLength = msg.readInt();
        int checksum = msg.readInt();
        byte[] content = null;

        //is heartbeat request
        if(HeartbeatMessage.isHeartbeatRequest(id,contentLength,checksum)){
            out.add(HeartbeatMessage.HEARTBEAT_REQUEST_MESSAGE);
            return ;
        }

        //is heartbeat request
        if(HeartbeatMessage.isHeartbeatResponse(id, contentLength, checksum)){
            out.add(HeartbeatMessage.HEARTBEAT_RESPONSE_MESSAGE);
            return ;
        }


        //is normal message
        if(contentLength > 0){
            content = new byte[contentLength];
            msg.readBytes(content);
        }
        out.add(MessageFactory.message(id,checksum,content));
    }
}
