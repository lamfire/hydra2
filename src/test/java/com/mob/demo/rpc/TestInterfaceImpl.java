package com.mob.demo.rpc;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public class TestInterfaceImpl implements TestInterface{
    public String getName(){
        return "linfan";
    }

    @Override
    public int div(int value, int value1) {
        return value / value1;
    }

    @Override
    public void a() {
        System.out.println("--------------------------------");
    }
}
