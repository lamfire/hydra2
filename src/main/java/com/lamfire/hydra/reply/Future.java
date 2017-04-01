package com.lamfire.hydra.reply;

import com.lamfire.hydra.Message;

import java.util.concurrent.TimeoutException;

public class Future {
    private final long createAt = System.currentTimeMillis();
    private Message response;
    private long timeout = 30000;

    void setTimeout(long timeoutMillis){
        this.timeout = timeoutMillis;
    }

    public synchronized Message getResponseMessage() throws TimeoutException {
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

    public byte[] getResponse()throws TimeoutException{
        return getResponseMessage().content();
    }

    synchronized void onResponse(Message response){
        this.response = response;
        this.notifyAll();
    }


    boolean isTimeout(){
        return System.currentTimeMillis() - createAt - timeout > 0;
    }
}
