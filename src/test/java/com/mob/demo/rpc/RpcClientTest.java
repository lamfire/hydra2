package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class RpcClientTest {
    public static void main(String[] args) {
        ProviderConfig config = new ProviderConfig("1001");
        config.setServiceAddr("127.0.0.1");
        config.setPort(19800);
        config.setThreads(32);

        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(DiscoveryConfig.DEFAULT_DISCOVERY_ADDRESS);
        discovery.setGroupPort(8888);

        HydraRPC rpc = new HydraRPC();
        //rpc.setSerializer(new KryoSerializer(10 * 1024 * 1024));
        rpc.startupDiscovery(discovery);

        //rpc.addProvider(config);
        if(!rpc.hashProviders()){
            rpc.waitProviders();
        }


        TestInterface t = rpc.lookup(TestInterface.class);
        //System.out.println(t.getName());

        //System.out.println(t.div(10,2));
        try {
            System.out.println(t.div(10, 1));
        }catch (Exception e){
            e.printStackTrace();
        }

        List<String> list = t.getList();
        System.out.println(list.size());
    }
}
