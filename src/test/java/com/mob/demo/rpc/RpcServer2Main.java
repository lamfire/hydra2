package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.DiscoveryConfig;
import com.lamfire.hydra.rpc.ProviderConfig;
import com.lamfire.hydra.rpc.RpcServer;
import com.lamfire.hydra.rpc.ServiceRegistryConfig;

/**
 * Created by linfan on 2017/2/23.
 */
public class RpcServer2Main {

    public static void main(String[] args) {
        ServiceRegistryConfig serviceRegistry = new ServiceRegistryConfig();
        serviceRegistry.registerService(TestInterface.class,new TestInterfaceImpl());

        ProviderConfig provider = new ProviderConfig("1002");
        provider.setServiceAddr("127.0.0.1");
        provider.setBindAddr("0.0.0.0");
        provider.setPort(19801);
        provider.setThreads(64);

        //设置并开启服务发现机制
        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(DiscoveryConfig.DEFAULT_DISCOVERY_ADDRESS);
        discovery.setGroupPort(8888);
        discovery.setProviderConfig(provider);

        RpcServer server = new RpcServer();
        server.setProvider(provider);
        server.setServiceRegistry(serviceRegistry);
        //server.setSerializer(new KryoSerializer(10 * 1024 * 1024));
        server.setDiscovery(discovery);
        server.setDiscoveryEnable(true);
        server.startup();
    }
}
