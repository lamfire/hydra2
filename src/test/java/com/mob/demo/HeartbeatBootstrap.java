package com.mob.demo;

import com.lamfire.hydra.*;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class HeartbeatBootstrap implements MessageReceivedListener {

    public static void main(String[] args) throws Exception {
        SnakeBuilder builder = new SnakeBuilder();
        builder.host("127.0.0.1").port(1980).messageReceivedListener(new HeartbeatBootstrap()).heartbeatEnable(true).heartbeatInterval(5000).autoConnectRetry(true);

        Snake snake = builder.build();
        snake.startup();


        Session session = snake.getSession();
        session.heartbeat();

    }

    @Override
    public void onMessageReceived(Session session, DataPacket dataPacket) {
        System.out.println("[MESSAGE] : "+ dataPacket.header() +" -> " + (dataPacket.content()==null?"":new String(dataPacket.content())));
    }
}
