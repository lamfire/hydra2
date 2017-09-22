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
public class MultProvidersClientTest {
    public static void main(String[] args) {
        ProviderConfig provider1 = new ProviderConfig("1001");
        provider1.setServiceAddr("127.0.0.1");
        provider1.setPort(19800);
        provider1.setThreads(8);

        ProviderConfig provider2 = new ProviderConfig("1002");
        provider2.setServiceAddr("127.0.0.1");
        provider2.setPort(19801);
        provider2.setThreads(8);


        HydraRPC rpc = new HydraRPC();
        rpc.setSerializer(new KryoSerializer(10 * 1024 * 1024));
        rpc.addProvider(provider1);   //add provider1
        rpc.addProvider(provider2);   //add provider2


        TestInterface t = rpc.lookup(TestInterface.class);

        while (true) {
            try {
                System.out.println(t.div(10, 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Threads.sleep(500);
        }
    }
}
