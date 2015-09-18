package com.lamfire.hydra.reply;

import com.lamfire.hydra.*;
import com.lamfire.utils.Maps;
import com.lamfire.utils.RandomUtils;
import com.lamfire.utils.Threads;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class ReplySnake implements MessageReceivedListener {
    final ScheduledExecutorService cleanService = Executors.newSingleThreadScheduledExecutor(Threads.makeThreadFactory("FutureTimeoutClean"));
    final Map<Integer,Future> replys = Maps.newConcurrentMap();
    final AtomicInteger counter = new AtomicInteger();

    private Snake snake ;

    public void startup(String host,int port){
        SnakeBuilder builder = new SnakeBuilder();
        builder.host(host).port(port).messageReceivedListener(this).heartbeatEnable(true).heartbeatInterval(5000);
        snake = builder.build();
        snake.startup();

        cleanService.scheduleWithFixedDelay(new FutureTimeoutClean(replys),15,15,TimeUnit.SECONDS);
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
}
