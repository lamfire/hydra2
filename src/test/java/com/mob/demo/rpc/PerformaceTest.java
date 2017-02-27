package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.DiscoveryConfig;
import com.lamfire.hydra.rpc.HydraRPC;
import com.lamfire.hydra.rpc.ProviderConfig;
import com.lamfire.utils.OPSMonitor;

import java.util.concurrent.ThreadPoolExecutor;

import static com.lamfire.utils.Threads.newFixedThreadPool;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 17-1-18
 * Time: 下午2:52
 * To change this template use File | Settings | File Templates.
 */
public class PerformaceTest implements Runnable{
    private TestInterface anInterface;
    private OPSMonitor monitor;

    public PerformaceTest(TestInterface anInterface, OPSMonitor monitor) {
        this.anInterface = anInterface;
        this.monitor = monitor;
    }

    public void run(){
        String name = null;
        while(true) {
            try{
                name = (anInterface.getName());
                monitor.done();
            }catch (Exception e){}

        }
    }

    public static void main(String[] args) {
        OPSMonitor monitor = new OPSMonitor("test");
        monitor.debug(true);
        monitor.startup();


        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(HydraRPC.DEFAULT_DISCOVERY_ADDRESS);
        discovery.setGroupPort(8888);


        HydraRPC rpc = new HydraRPC();
        rpc.setDiscoveryConfig(discovery);
        rpc.setSerializer(HydraRPC.KRYO_SERIALIZER);
        rpc.startupDiscovery();
        rpc.waitProvider();

        TestInterface t = rpc.lookup(TestInterface.class);
        System.out.println(t.getName());


        int threads = 10;
        ThreadPoolExecutor executor = newFixedThreadPool(threads);
        for(int i=0;i<threads;i++) {
            executor.submit(new PerformaceTest(t, monitor));
        }
    }
}
