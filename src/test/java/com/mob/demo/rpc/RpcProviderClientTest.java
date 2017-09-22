package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.DiscoveryConfig;
import com.lamfire.hydra.rpc.HydraRPC;
import com.lamfire.hydra.rpc.KryoSerializer;
import com.lamfire.hydra.rpc.ProviderConfig;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class RpcProviderClientTest {
    public static void main(String[] args) {
        ProviderConfig provider = new ProviderConfig("1001");
        provider.setServiceAddr("127.0.0.1");
        provider.setPort(19800);
        provider.setThreads(32);

        HydraRPC rpc = new HydraRPC();
        rpc.setSerializer(new KryoSerializer());
        rpc.addProvider(provider);

        if(!rpc.hashAvailableProviders()){
            rpc.waitProviders();
        }


        TestInterface t = rpc.lookup(TestInterface.class);
        System.out.println(t.getName());

        try {
            System.out.println(t.div(10, 1));
        }catch (Exception e){
            e.printStackTrace();
        }

        t.a();
    }
}
