package com.mob.demo;

import com.lamfire.hydra.*;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:05
 * To change this template use File | Settings | File Templates.
 */
public class HydraBootstrap implements MessageReceivedListener {

    public static void main(String[] args) {
        HydraBuilder builder = new HydraBuilder();
        builder.bind("0.0.0.0").port(1980).messageReceivedListener(new HydraBootstrap());

        Hydra hydra = builder.build();
        hydra.startup();
    }

    @Override
    public void onMessageReceived(Session session, Message message) {
        //System.out.println("[MESSAGE] : "+message.header() +" -> " + (message.content()==null?"":new String(message.content())));
        session.send(message);
    }

}
