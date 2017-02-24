package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.*;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class RpcTest {
    public static void main(String[] args) {
        ProviderConfig config = new ProviderConfig("1001");
        config.setHost("127.0.0.1");
        config.setPort(19800);
        config.setThreads(32);

        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(DiscoveryMultiCaster.DEFAULT_MULTI_CAST_ADDRESS);
        discovery.setGroupPort(8888);

        HydraRPC rpc = new HydraRPC();
        rpc.setSerializer(new KryoSerializer());
        rpc.setDiscoveryConfig(discovery);
        rpc.startupDiscovery();

        //rpc.addProvider(config);
        if(!rpc.hashProvider()){
            rpc.waitProvider();
        }


        TestInterface t = rpc.lookup(TestInterface.class);
        //System.out.println(t.getName());

        //System.out.println(t.div(10,2));
        try {
            System.out.println(t.div(10, 1));
        }catch (Exception e){
            e.printStackTrace();
        }

        t.a();
        t.a();
        t.a();
        t.a();
        t.a();
    }
}
