package com.lamfire.hydra.reply;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-9-18
 * Time: 上午10:33
 * To change this template use File | Settings | File Templates.
 */
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
