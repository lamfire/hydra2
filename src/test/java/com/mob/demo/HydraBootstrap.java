package com.mob.demo;

import com.lamfire.hydra.*;
import com.lamfire.utils.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class HydraBootstrap implements MessageReceivedListener {

    public static void main(String[] args) {
        String host = "0.0.0.0";
        int port = 1980;
        if(args != null){
            for(String arg : args){
                if(StringUtils.contains(arg, "-p")){
                    port = Integer.valueOf(StringUtils.substringAfter(arg,"-p").trim());
                }

                if(StringUtils.contains(arg,"-h")){
                    host = (StringUtils.substringAfter(arg, "-h").trim());
                }
            }
        }

        HydraBuilder builder = new HydraBuilder();
        builder.bind(host).port(port).messageReceivedListener(new HydraBootstrap());

        Hydra hydra = builder.build();
        hydra.startup();
    }

    @Override
    public void onMessageReceived(Session session, Message message) {
        //System.out.println("[MESSAGE] : "+message.header() +" -> " + (message.content()==null?"":new String(message.content())));
        session.send(message);
    }

}
