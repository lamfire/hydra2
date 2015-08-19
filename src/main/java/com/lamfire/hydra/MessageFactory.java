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

    public static Message message(int id,byte[] content){
        return message(id,content,false);
    }

    public static Message message(int id,byte[] content,boolean checksumEnable){
        int checksum = -1;
        if(checksumEnable){
            checksum = CRC32.digest(content);
        }
        return message(id,checksum,content);
    }

    public static Message message(int id,int checksum,byte[] content){
        HydraMessage m = new HydraMessage();
        m.header().setId(id);
        m.header().setChecksum(checksum);

        if(content != null){
            m.header().setContentLength(content.length);
            m.content(content);
        }
        return m;
    }



}
