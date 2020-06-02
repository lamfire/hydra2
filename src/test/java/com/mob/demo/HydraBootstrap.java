package com.mob.demo;

import com.lamfire.hydra.*;
import com.lamfire.utils.StringUtils;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class HydraBootstrap implements MessageReceivedListener ,SessionCreatedListener{

    AutoRemoveSessionGroup group = new AutoRemoveSessionGroup("TEST_AUTO_CLOSE");

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

        HydraBootstrap sample = new HydraBootstrap();
        HydraBuilder builder = new HydraBuilder();
        builder.bind(host).port(port).messageReceivedListener(sample).sessionCreatedListener(sample).threads(4);

        Hydra hydra = builder.build();
        hydra.startup();
    }

    @Override
    public void onMessageReceived(Session session, Message message) {
        //System.out.println("[MESSAGE] : "+message.header() +" -> " + (message.content()==null?"":new String(message.content())));
        session.send(message);
    }

    @Override
    public void onCreated(Session session) {
        long id = session.getId();
        group.put("SESSION-" +id,session);

        Collection<Session> sessions = group.all();
        for(Session s : sessions){
            System.out.println("----" + s);
        }
    }


}
