package com.mob.demo;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.reply.Future;
import com.lamfire.hydra.reply.ReplySnake;
import com.lamfire.utils.RandomUtils;
import com.lamfire.utils.Threads;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-9-18
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
public class ReplyBootstrap {
    public static void main(String[] args) throws Exception {
        ReplySnake reply = new ReplySnake();
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
        }, 1, 1, TimeUnit.SECONDS);

        while(true){
            c.incrementAndGet();
            String data = RandomUtils.randomText(100);
            byte[] content = data.getBytes();
            Future f = reply.send(content);
            byte[] bytes = f.getResponse() ;
            if(bytes != null){
                //System.out.println(new String(m.content()));
            }
        }
    }
}
