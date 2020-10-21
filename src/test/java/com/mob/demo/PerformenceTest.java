package com.mob.demo;

import com.lamfire.hydra.*;
import com.lamfire.utils.OPSMonitor;
import com.lamfire.utils.RandomUtils;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class PerformenceTest implements MessageReceivedListener {

    private OPSMonitor monitor = new OPSMonitor("1");

    public PerformenceTest(){
        monitor.debug(true);
        monitor.startup();
    }

    public static void main(String[] args) throws Exception {
        SnakeBuilder builder = new SnakeBuilder();
        builder.host("127.0.0.1").port(1980).threads(8).messageReceivedListener(new PerformenceTest()).heartbeatEnable(true).heartbeatInterval(5000);

        Snake snake = builder.build();
        snake.startup();
        //snake.waitSessionCreated();

        Session session = snake.getSession();

        String data = RandomUtils.randomText(100);
        byte[] content = data.getBytes();
        DataPacket m = DataPacketFactory.message(0,0,content);

        for(int i=0;i<100;i++) {
            session.send(m);
        }

    }

    @Override
    public void onMessageReceived(Session session, DataPacket dataPacket) {
        monitor.done();
        session.send(dataPacket);
    }
}
