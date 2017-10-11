package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.HydraRPC;
import com.lamfire.hydra.rpc.KryoSerializer;
import com.lamfire.hydra.rpc.ProviderConfig;
import com.lamfire.utils.Threads;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class RpcClientFailedOverTest {
    public static void main(String[] args) {
        ProviderConfig config = new ProviderConfig("1002");
        config.setServiceAddr("127.0.0.1");
        config.setPort(19800);
        config.setThreads(2);


        HydraRPC rpc = new HydraRPC();
        rpc.setSerializer(new KryoSerializer(10 * 1024 * 1024));
        rpc.addProvider(config);

        TestInterface t = rpc.lookup(TestInterface.class);
        System.out.println(t.getName());

        System.out.println(t.div(10, 2));

        while (true) {
            try {
                System.out.println(t.div(10, 1));

            } catch (Exception e) {
                e.printStackTrace();
            }
            Threads.sleep(1000);
        }
    }

}
