package com.mob.demo;

import com.lamfire.code.CRC32;
import com.lamfire.utils.RandomUtils;
import com.lamfire.utils.Threads;
import com.lamfire.hydra.*;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class SnakeBootstrap implements MessageReceivedListener {

    public static void main(String[] args) throws Exception {
        SnakeBuilder builder = new SnakeBuilder();
        builder.host("127.0.0.1").port(1980).messageReceivedListener(new SnakeBootstrap()).heartbeatEnable(true).heartbeatInterval(5000);

        Snake snake = builder.build();
        snake.startup();


        Session session = snake.getSession();

        int i=0;
        while(true){
            String data = RandomUtils.randomText(100);
            byte[] content = data.getBytes();
            int option = CRC32.digest(content);
            //System.out.println(i + " - CRC32 = " + crc32 + " -> " + data);

            int id = i++;
            Message m = MessageFactory.message(id,option,content);
            session.send(m);

            Threads.sleep(10);
        }
    }

    @Override
    public void onMessageReceived(Session session, Message message) {
        System.out.println("[MESSAGE] : "+message.header() +" -> " + (message.content()==null?"":new String(message.content())));
    }
}
