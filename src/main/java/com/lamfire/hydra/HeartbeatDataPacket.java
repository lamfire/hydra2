package com.lamfire.hydra;

public final class HeartbeatDataPacket extends HydraDataPacket {
    public static final HeartbeatDataPacket HEARTBEAT_REQUEST_MESSAGE = new HeartbeatDataPacket();
    public static final HeartbeatDataPacket HEARTBEAT_RESPONSE_MESSAGE = new HeartbeatDataPacket();

    private static final int OPTION_HEARTBEAT_REQUEST = 0x9999;
    private static final int OPTION_HEARTBEAT_RESPONSE = 0x8888;

    static {
        HEARTBEAT_REQUEST_MESSAGE.header().id(0);
        HEARTBEAT_REQUEST_MESSAGE.header().contentLength(0);
        HEARTBEAT_REQUEST_MESSAGE.header().option(OPTION_HEARTBEAT_REQUEST);

        HEARTBEAT_RESPONSE_MESSAGE.header().id(0);
        HEARTBEAT_RESPONSE_MESSAGE.header().contentLength(0);
        HEARTBEAT_RESPONSE_MESSAGE.header().option(OPTION_HEARTBEAT_RESPONSE);
    }

    public static boolean isHeartbeatRequest(int id, int len, int option) {
        return id == 0 && len == 0 && option == OPTION_HEARTBEAT_REQUEST;
    }

    public static boolean isHeartbeatResponse(int id, int len, int option) {
        return id == 0 && len == 0 && option == OPTION_HEARTBEAT_RESPONSE;
    }

    public boolean isHeartbeatRequest() {
        int id = this.header().id();
        int len = this.header().contentLength();
        int option = this.header().option();

        return isHeartbeatRequest(id, len, option);
    }

    public boolean isHeartbeatResponse() {
        int id = this.header().id();
        int len = this.header().contentLength();
        int option = this.header().option();

        return isHeartbeatResponse(id, len, option);
    }

    public String toString() {
        if (isHeartbeatRequest()) {
            return "REQUEST";
        }

        if (isHeartbeatResponse()) {
            return "RESPONSE";
        }

        return "NORMAL";
    }
}
