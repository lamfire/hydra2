package com.lamfire.hydra;

public final class HeartbeatMessage extends HydraMessage{
    public static final HeartbeatMessage HEARTBEAT_REQUEST_MESSAGE = new HeartbeatMessage();
    public static final HeartbeatMessage HEARTBEAT_RESPONSE_MESSAGE = new HeartbeatMessage();

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

    public static boolean isHeartbeatRequest(int id,int len,int option){
        if(id == 0 && len == 0 && option ==OPTION_HEARTBEAT_REQUEST){
            return true;
        }

        return false;
    }

    public static boolean isHeartbeatResponse(int id,int len,int option){
        if(id == 0 && len == 0 && option == OPTION_HEARTBEAT_RESPONSE){
            return true;
        }
        return false;
    }

    public boolean isHeartbeatRequest(){
        int id = this.header().id();
        int len = this.header().contentLength();
        int option = this.header().option();

        return isHeartbeatRequest(id,len,option);
    }

    public boolean isHeartbeatResponse(){
        int id = this.header().id();
        int len = this.header().contentLength();
        int option = this.header().option();

        return isHeartbeatResponse(id, len, option);
    }

    public String toString(){
        if(isHeartbeatRequest()){
            return "REQUEST";
        }

        if(isHeartbeatResponse()){
            return "RESPONSE";
        }

        return "NORMAL";
    }
}
