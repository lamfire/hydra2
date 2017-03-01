package com.mob.demo.rpc;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:51
 * To change this template use File | Settings | File Templates.
 */
public interface TestInterface {
    String getName();

    int div(int value,int value1);

    void a();

    List<String> getList();
}
