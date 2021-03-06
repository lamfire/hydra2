package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.DiscoveryConfig;
import com.lamfire.hydra.rpc.HydraRPC;
import com.lamfire.hydra.rpc.KryoSerializer;
import com.lamfire.hydra.rpc.ProviderConfig;
import com.lamfire.utils.Threads;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class RpcDiscoveryClientTest {
    public static void main(String[] args) {
        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(DiscoveryConfig.DEFAULT_DISCOVERY_ADDRESS);
        discovery.setGroupPort(8888);

        HydraRPC rpc = new HydraRPC();
        rpc.setSerializer(new KryoSerializer());
        rpc.startupDiscovery(discovery);

        //rpc.addProvider(config);
        if(!rpc.hashAvailableProviders()){
            rpc.waitProviders();
        }



        TestInterface t = rpc.lookup(TestInterface.class);
        System.out.println(t.getName());

        Threads.sleep(2000);
        System.out.println(t.div(10,2));

        while(true) {
            try {
                System.out.println(t.div(10, 1));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Threads.sleep(1000);
        }
    }
}
