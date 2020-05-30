package com.lamfire.hydra.netty.websocket;

import com.lamfire.hydra.HeartbeatMessage;
import com.lamfire.hydra.MessageFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

public class WebSocketMessageDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame>{
    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame frame, List<Object> out) {
        ByteBuf msg = frame.content();
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
