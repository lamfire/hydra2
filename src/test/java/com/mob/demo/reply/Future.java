package com.mob.demo.reply;

import com.lamfire.hydra.Message;

import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-9-16
 * Time: 下午4:32
 * To change this template use File | Settings | File Templates.
 */
public class Future {
    private Message response;
    private long timeout = 6000;

    public synchronized Message getResponse() throws TimeoutException {
        if(response == null){
            try {
                this.wait(timeout);
            } catch (InterruptedException e) {

            }
        }

        if(response == null){
            throw new TimeoutException();
        }

        return response;
    }

    synchronized void onResponse(Message response){
        this.response = response;
        this.notifyAll();
    }

}
