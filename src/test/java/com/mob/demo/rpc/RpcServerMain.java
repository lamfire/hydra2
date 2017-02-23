package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.*;

/**
 * Created by linfan on 2017/2/23.
 */
public class RpcServerMain {

    public static void main(String[] args) {
        ServiceRegistryConfig exporter = new ServiceRegistryConfig();
        exporter.registerService(TestInterface.class,new TestInterfaceImpl());

        ProviderConfig provider = new ProviderConfig("1001");
        provider.setHost("0.0.0.0");
        provider.setPort(19800);
        provider.setThreads(64);
        provider.setSerializer(HydraRPC.KRYO_SERIALIZER);

        RpcServer server = new RpcServer();
        server.setProviderConfig(provider);
        server.setServiceRegistry(exporter);
        server.startup();
    }
}
