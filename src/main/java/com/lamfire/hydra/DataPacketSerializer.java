package com.lamfire.hydra;

import io.netty.buffer.ByteBuf;

public class DataPacketSerializer {

    public void encode(DataPacket msg, ByteBuf out) {
        DataPacketHeader header = msg.header();
        out.writeInt(header.id());
        out.writeInt(header.option());
        out.writeInt(header.contentLength());
        if (header.contentLength() > 0) {
            out.writeBytes(msg.content());
        }
    }

    public DataPacket decode(ByteBuf buf) {
        int id = buf.readInt();
        int option = buf.readInt();
        int contentLength = buf.readInt();

        byte[] content = null;

        //is heartbeat request
        if (HeartbeatDataPacket.isHeartbeatRequest(id, contentLength, option)) {
            return HeartbeatDataPacket.HEARTBEAT_REQUEST_MESSAGE;
        }

        //is heartbeat request
        if (HeartbeatDataPacket.isHeartbeatResponse(id, contentLength, option)) {
            return HeartbeatDataPacket.HEARTBEAT_RESPONSE_MESSAGE;
        }


        //is normal message
        if (contentLength > 0) {
            content = new byte[contentLength];
            buf.readBytes(content);
        }
        return DataPacketFactory.make(id, option, content);
    }
}
