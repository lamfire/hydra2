package com.lamfire.hydra.reply;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FutureTimeoutClean implements Runnable{

    private Map<Integer,Future> replys;

    FutureTimeoutClean(Map<Integer, Future> replys){
        this.replys = replys;
    }


    @Override
    public void run() {
        try{
            List<Integer> list = new ArrayList<Integer>();
            for(Map.Entry<Integer,Future> e : replys.entrySet()){
                Future f = e.getValue();
                if(f.isTimeout()){
                    list.add(e.getKey());
                }
            }

            for(Integer key : list){
                replys.remove(key);
            }
        }catch (Throwable t){

        }
    }
}
