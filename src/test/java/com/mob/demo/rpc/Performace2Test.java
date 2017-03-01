package com.mob.demo.rpc;

import com.lamfire.hydra.rpc.DiscoveryConfig;
import com.lamfire.hydra.rpc.HydraRPC;
import com.lamfire.utils.OPSMonitor;

import java.util.concurrent.ThreadPoolExecutor;

import static com.lamfire.utils.Threads.newFixedThreadPool;

public class Performace2Test implements Runnable{
    private HydraRPC rpc ;
    private OPSMonitor monitor;

    public Performace2Test(HydraRPC rpc, OPSMonitor monitor) {
        this.rpc = rpc;
        this.monitor = monitor;
    }

    public void run(){
        String name = null;
        while(true) {
            try{
                TestInterface anInterface = rpc.lookup(TestInterface.class);
                name = (anInterface.getName());
                monitor.done();
            }catch (Throwable e){
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        OPSMonitor monitor = new OPSMonitor("test");
        monitor.debug(true);
        monitor.startup();


        DiscoveryConfig discovery = new DiscoveryConfig();
        discovery.setGroupId("RPC_PROVIDER");
        discovery.setGroupAddr(DiscoveryConfig.DEFAULT_DISCOVERY_ADDRESS);
        discovery.setGroupPort(8888);


        HydraRPC rpc = new HydraRPC();
        rpc.setDiscoveryConfig(discovery);
        //rpc.setSerializer(new KryoSerializer(10 * 1024 * 1024));
        rpc.startupDiscovery();
        rpc.waitProviders();

        TestInterface t = rpc.lookup(TestInterface.class);
        System.out.println(t.getName());


        int threads = 10;
        ThreadPoolExecutor executor = newFixedThreadPool(threads);
        for(int i=0;i<threads;i++) {
            executor.submit(new Performace2Test(rpc, monitor));
        }
    }
}
