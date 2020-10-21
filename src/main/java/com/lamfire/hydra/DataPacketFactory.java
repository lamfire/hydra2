package com.lamfire.hydra;

/**
 * DataPacketFactory
 * User: linfan
 * Date: 15-8-19
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
public class DataPacketFactory {
    private DataPacketFactory() {
    }

    public static DataPacket makeHeartbeatRequest() {
        return HeartbeatDataPacket.HEARTBEAT_REQUEST_MESSAGE;
    }

    public static DataPacket makeHeartbeatResponse() {
        return HeartbeatDataPacket.HEARTBEAT_RESPONSE_MESSAGE;
    }

    public static DataPacket make(int id, int option, byte[] content) {
        HydraDataPacket m = new HydraDataPacket();
        m.header().id(id);
        m.header().option(option);

        if (content != null) {
            m.header().contentLength(content.length);
            m.content(content);
        }
        return m;
    }

}
