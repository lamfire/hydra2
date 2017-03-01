package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.KryoSerializer;
import com.lamfire.utils.Lists;
import com.lamfire.utils.RandomUtils;

import java.util.List;

/**
 * Created by linfan on 2017/3/1.
 */
public class SerializerTest {
    public static void main(String[] args) {
        List<String> list = Lists.newArrayList();
        for(int i=0;i<10;i++){
            list.add(RandomUtils.randomTextWithFixedLength(200));
        }

        KryoSerializer kryo = new KryoSerializer(1024 * 1024 * 2);
        byte[] bytes = kryo.encode(list);
        System.out.println("Size : " + bytes.length);
    }
}
