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
public class RpcDiscoveryClientTest {
    public static void main(String[] args) {
        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(HydraRPC.DEFAULT_DISCOVERY_ADDRESS);
        discovery.setGroupPort(8888);

        HydraRPC rpc = new HydraRPC();
        rpc.setSerializer(new KryoSerializer());
        rpc.startupDiscovery(discovery);

        //rpc.addProvider(config);
        if(!rpc.hashProvider()){
            rpc.waitProvider();
        }


        TestInterface t = rpc.lookup(TestInterface.class);
        System.out.println(t.getName());
        System.out.println(t.div(10,2));
        try {
            System.out.println(t.div(10, 1));
        }catch (Exception e){
            e.printStackTrace();
        }
        t.a();
    }
}
