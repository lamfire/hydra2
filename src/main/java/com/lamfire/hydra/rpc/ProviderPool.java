package com.lamfire.hydra.rpc;

import com.lamfire.logger.Logger;
import com.lamfire.utils.CircularLinkedList;
import com.lamfire.utils.StringUtils;
import com.lamfire.utils.Threads;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ProviderPool implements Runnable{
    private static final Logger LOGGER = Logger.getLogger(ProviderPool.class);
    private final Map<String,ProviderConfig> providers = new HashMap<String, ProviderConfig>();
    private final Map<String,RpcClient> clients = new HashMap<String, RpcClient>();
    private final CircularLinkedList<String> providerNames = new CircularLinkedList<String>();

    private final Lock lock = new ReentrantLock();

    public ProviderPool(){
        Threads.scheduleWithFixedDelay(this,5,5, TimeUnit.SECONDS);
    }

    public synchronized void addProvider(ProviderConfig config){
        LOGGER.info("[ADD_PROVIDER] :" + config);
        try {
            lock.lock();
            String providerName = config.getName();
            ProviderConfig old = providers.get(providerName);
            if (old != null) {
                replaceProvider(config);
            }else {
                addNewProvider(config);
            }
            createNewRpcClient(config);
        }finally {
            lock.unlock();
        }
    }

    private synchronized void addNewProvider(ProviderConfig config){
        LOGGER.info("[NEW_PROVIDER] :" + config);
        String providerName = config.getName();
        providers.put(providerName,config);
        if(!providerNames.contains(providerName)) {
            providerNames.add(providerName);
        }
    }

    private synchronized void replaceProvider(ProviderConfig config){
        LOGGER.info("[REPLACE_PROVIDER] : "  +config);
        String providerName = config.getName();
        ProviderConfig old = providers.get(providerName);
        providers.put(providerName,config);

        //仅修改设置
        if(StringUtils.equals(old.getServiceAddr(),config.getServiceAddr()) && old.getPort() == config.getPort()){
            providers.put(providerName,config);
            LOGGER.info("[UPDATE_PROVIDER] - " + old + " -> " +config);
            return;
        }


        //替换client
        RpcClient client = clients.remove(config.getName());
        if(client != null){
            LOGGER.info("[SHUTDOWN_PROVIDER] - " + old);
            client.shutdown();
        }
    }

    private synchronized RpcClient createNewRpcClient(ProviderConfig config){
        LOGGER.info("[CREATE_PRC_CLIENT] : "  +config);
        RpcClient client = null;
        try {
            if (client == null) {
                RpcClientImpl c = new RpcClientImpl(config.getServiceAddr(), config.getPort());
                c.setThreads(config.getThreads());
                c.setTimeout(config.getTimeoutMillis());
                c.startup();
                if(c.isAvailable()) {
                    clients.put(config.getName(), c);
                    client = c;
                    LOGGER.info("[CREATE_PRC_CLIENT_SUCCESS] : "  +config.getName());
                }
            }
        }catch (Throwable e){
            LOGGER.error(e.getMessage(),e);
        }finally {

        }
        if(client == null){
            LOGGER.error("[CREATE_PRC_CLIENT_FAILED] : "  +config.getName());
        }
        return client;
    }

    private synchronized RpcClient getOrCreateRpcClient(String providerName){
        RpcClient client = clients.get(providerName);
        try {
            if (client == null) {
                ProviderConfig config = providers.get(providerName);
                if (config == null) {
                    throw new RpcException("The provider not found - " + providerName);
                }
                client = createNewRpcClient(config);
            }
        }catch (Throwable e){
            LOGGER.error(e.getMessage(),e);
        }finally {

        }
        return client;
    }


    private RpcClient getNextRpcClient(){
        String name = providerNames.next();
        RpcClient client = clients.get(name);
        return client;
    }

    public RpcClient getRpcClient(){
        try {
            for (int i = 0; i < providers.size(); i++) {
                RpcClient c = getNextRpcClient();
                if(c == null){
                    continue;
                }
                if (c.isAvailable()) {
                    return c;
                }
            }
            throw new RpcException("Not found available RPC providers");
        }finally {

        }
    }

    public boolean isEmpty(){
        return providers.isEmpty() || clients.isEmpty();
    }

    @Override
    public void run() {
        for(Map.Entry<String,ProviderConfig> provider : providers.entrySet()){
            RpcClient client = clients.get(provider.getKey());
            if(client == null){
                client = createNewRpcClient(provider.getValue());
                if(client != null){
                    clients.put(provider.getKey(),client);
                }
            }
        }
    }
}
