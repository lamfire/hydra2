package com.mob.demo.rpc;

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
public class RpcTest {
    public static void main(String[] args) {
        ProviderConfig config = new ProviderConfig("1001");
        config.setHost("127.0.0.1");
        config.setPort(19800);
        config.setThreads(32);
        config.setSerializer(HydraRPC.KRYO_SERIALIZER);


        HydraRPC rpc = new HydraRPC();
        rpc.addProvider(config);
        rpc.setSerializer(new KryoSerializer());


        TestInterface t = rpc.lookup(TestInterface.class);
        System.out.println(t.getName());
    }
}
