package com.lamfire.hydra;

import io.netty.buffer.ByteBuf;

public class MessageSerializer {

    public void encode(Message msg, ByteBuf out) {
        MessageHeader header = msg.header();
        out.writeInt(header.id());
        out.writeInt(header.option());
        out.writeInt(header.contentLength());
        if (header.contentLength() > 0) {
            out.writeBytes(msg.content());
        }
    }

    public Message decode(ByteBuf buf) {
        int id = buf.readInt();
        int option = buf.readInt();
        int contentLength = buf.readInt();

        byte[] content = null;

        //is heartbeat request
        if (HeartbeatMessage.isHeartbeatRequest(id, contentLength, option)) {
            return HeartbeatMessage.HEARTBEAT_REQUEST_MESSAGE;
        }

        //is heartbeat request
        if (HeartbeatMessage.isHeartbeatResponse(id, contentLength, option)) {
            return HeartbeatMessage.HEARTBEAT_RESPONSE_MESSAGE;
        }


        //is normal message
        if (contentLength > 0) {
            content = new byte[contentLength];
            buf.readBytes(content);
        }
        return MessageFactory.message(id, option, content);
    }
}
