package com.lamfire.hydra;

import com.lamfire.code.CRC32;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-19
 * Time: 上午10:30
 * To change this template use File | Settings | File Templates.
 */
public class MessageFactory {
    private MessageFactory (){}

    public static Message makeHeartbeatRequestMessage(){
        return HeartbeatMessage.HEARTBEAT_REQUEST_MESSAGE;
    }

    public static Message makeHeartbeatResponseMessage(){
        return HeartbeatMessage.HEARTBEAT_RESPONSE_MESSAGE;
    }

    public static Message makeMessage(int id,int option,byte[] content){
        return message(id,option,content);
    }

    public static Message makeMessage(int id,byte[] content){
        return message(id,content);
    }


    public static Message message(int id,int option,byte[] content){
        HydraMessage m = new HydraMessage();
        m.header().id(id);
        m.header().option(option);

        if(content != null){
            m.header().contentLength(content.length);
            m.content(content);
        }
        return m;
    }

    public static Message message(int id,byte[] content){
        HydraMessage m = new HydraMessage();
        m.header().id(id);
        m.header().option(0);
        if(content != null){
            m.header().contentLength(content.length);
            m.content(content);
        }
        return m;
    }


}
