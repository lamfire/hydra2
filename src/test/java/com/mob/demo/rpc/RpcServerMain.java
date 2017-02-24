package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.*;

/**
 * Created by linfan on 2017/2/23.
 */
public class RpcServerMain {

    public static void main(String[] args) {
        ServiceRegistryConfig serviceRegistry = new ServiceRegistryConfig();
        serviceRegistry.registerService(TestInterface.class,new TestInterfaceImpl());

        ProviderConfig provider = new ProviderConfig("1001");
        provider.setHost("127.0.0.1");
        provider.setPort(19800);
        provider.setThreads(64);

        //设置并开启服务发现机制
        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(DiscoveryMultiCaster.DEFAULT_MULTI_CAST_ADDRESS);
        discovery.setGroupPort(8888);
        discovery.setProviderConfig(provider);

        RpcServer server = new RpcServer();
        server.setProviderConfig(provider);
        server.setServiceRegistry(serviceRegistry);
        server.setSerializer(HydraRPC.KRYO_SERIALIZER);
        server.setDiscoveryConfig(discovery);
        server.setThreads(32);
        //server.setEnableDiscovery(true);
        server.startup();
    }
}
