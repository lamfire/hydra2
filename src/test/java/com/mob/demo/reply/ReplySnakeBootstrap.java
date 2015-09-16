package com.mob.demo.reply;

import com.lamfire.code.CRC32;
import com.lamfire.hydra.*;
import com.lamfire.utils.Maps;
import com.lamfire.utils.RandomUtils;
import com.lamfire.utils.Threads;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class ReplySnakeBootstrap implements MessageReceivedListener {
    final Map<Integer,Future> replys = Maps.newConcurrentMap();
    final AtomicInteger counter = new AtomicInteger();

    Snake snake ;

    public void startup(String host,int port){
        SnakeBuilder builder = new SnakeBuilder();
        builder.host(host).port(port).messageReceivedListener(this).heartbeatEnable(true).heartbeatInterval(5000);
        snake = builder.build();
        snake.startup();
    }


    public Future send(byte[] bytes){
        Message m = MessageFactory.message(counter.incrementAndGet(),0,bytes);
        Future f = new Future();
        replys.put(m.header().id(),f);
        snake.getSession().send(m);
        return f;
    }



    @Override
    public void onMessageReceived(Session session, Message message) {
        Integer id = message.header().id();
        Future f = replys.remove(id);
        if(f != null){
            f.onResponse(message);
        }
    }


    public static void main(String[] args) throws Exception {
        ReplySnakeBootstrap reply = new ReplySnakeBootstrap();
        reply.startup("127.0.0.1",1980);

        final AtomicInteger c = new AtomicInteger();

        Threads.scheduleAtFixedRate(new Runnable() {
            int pre = 0;
            @Override
            public void run() {
                int cur = c.get();
                System.out.println((cur - pre) + "/s");
                pre = cur;
            }
        },1,1, TimeUnit.SECONDS);

        while(true){
            c.incrementAndGet();
            String data = RandomUtils.randomText(100);
            byte[] content = data.getBytes();
            Future f = reply.send(content);
            Message m = f.getResponse() ;
            if(m.content() != null){
                //System.out.println(new String(m.content()));
            }
        }
    }
}
