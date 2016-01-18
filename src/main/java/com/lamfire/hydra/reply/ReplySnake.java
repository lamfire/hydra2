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

    private long readTimeoutMillis = 6000;
    private int heartbeatIntervalMillis = 15000;
    private boolean heartbeatEnable = true;

    private Snake snake ;

    public synchronized void startup(String host,int port){
        if(snake != null){
            return;
        }
        SnakeBuilder builder = new SnakeBuilder();
        builder.host(host).port(port).messageReceivedListener(this).heartbeatEnable(heartbeatEnable).heartbeatInterval(heartbeatIntervalMillis);
        snake = builder.build();
        snake.startup();

        cleanService.scheduleWithFixedDelay(new FutureTimeoutClean(replys),15,15,TimeUnit.SECONDS);
    }

    public void shutdown(){
        if(cleanService != null){
            cleanService.shutdown();
        }
        if(snake != null){
            snake.shutdown();
            snake = null;
        }
    }

    public long getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(long readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public int getHeartbeatIntervalMillis() {
        return heartbeatIntervalMillis;
    }

    public void setHeartbeatIntervalMillis(int heartbeatIntervalMillis) {
        this.heartbeatIntervalMillis = heartbeatIntervalMillis;
    }

    public boolean isHeartbeatEnable() {
        return heartbeatEnable;
    }

    public void setHeartbeatEnable(boolean heartbeatEnable) {
        this.heartbeatEnable = heartbeatEnable;
    }

    public Future send(byte[] bytes){
        Message m = MessageFactory.message(counter.incrementAndGet(),0,bytes);
        Future f = new Future();
        f.setTimeout(readTimeoutMillis);
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
