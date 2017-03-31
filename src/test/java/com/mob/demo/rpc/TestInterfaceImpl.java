package com.mob.demo.rpc;

import com.lamfire.utils.Lists;
import com.lamfire.utils.RandomUtils;
import com.lamfire.utils.Threads;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public class TestInterfaceImpl implements TestInterface{
    public String getName(){
        //System.out.println("getName()");
        //Threads.sleep(20);
        return "linfan";
    }

    @Override
    public int div(int value, int value1) {
        //System.out.println("div(int value, int value1)");
        return value / value1;
    }

    @Override
    public void a() {
        System.out.println("--------------------------------");
    }

    @Override
    public List<String> getList() {
        List<String> list = Lists.newArrayList();
        for(int i=0;i<1000;i++){
            list.add(RandomUtils.randomText(50,200));
        }
        return list;
    }
}
