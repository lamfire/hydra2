package com.lamfire.hydra;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-19
 * Time: 下午1:38
 * To change this template use File | Settings | File Templates.
 */
public final class HeartbeatMessage extends HydraMessage{
    public static final HeartbeatMessage HEARTBEAT_REQUEST_MESSAGE = new HeartbeatMessage();
    public static final HeartbeatMessage HEARTBEAT_RESPONSE_MESSAGE = new HeartbeatMessage();

    static {
        HEARTBEAT_REQUEST_MESSAGE.header().setId(0);
        HEARTBEAT_REQUEST_MESSAGE.header().setContentLength(0);
        HEARTBEAT_REQUEST_MESSAGE.header().setChecksum(0);

        HEARTBEAT_RESPONSE_MESSAGE.header().setId(0);
        HEARTBEAT_RESPONSE_MESSAGE.header().setContentLength(0);
        HEARTBEAT_RESPONSE_MESSAGE.header().setChecksum(-1);
    }

    public static boolean isHeartbeatRequest(int id,int len,int checksum){
        if(id == 0 && len == 0 && checksum ==0){
            return true;
        }

        return false;
    }

    public static boolean isHeartbeatResponse(int id,int len,int checksum){
        if(id == 0 && len == 0 && checksum ==-1){
            return true;
        }
        return false;
    }

    public boolean isHeartbeatRequest(){
        int id = this.header().getId();
        int len = this.header().getContentLength();
        int checksum = this.header().getChecksum();

        return isHeartbeatRequest(id,len,checksum);
    }

    public boolean isHeartbeatResponse(){
        int id = this.header().getId();
        int len = this.header().getContentLength();
        int checksum = this.header().getChecksum();

        return isHeartbeatResponse(id, len, checksum);
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
