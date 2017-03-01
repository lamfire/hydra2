package com.lamfire.hydra.rpc;

import com.lamfire.logger.Logger;
import com.lamfire.utils.CircularLinkedList;
import com.lamfire.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class ProviderPool {
    private static final Logger LOGGER = Logger.getLogger(ProviderPool.class);
    private final Map<String,ProviderConfig> providers = new HashMap<String, ProviderConfig>();
    private final Map<String,RpcClient> clients = new HashMap<String, RpcClient>();
    private final CircularLinkedList<String> providerNames = new CircularLinkedList<String>();

    private final Lock lock = new ReentrantLock();

    public synchronized void addProvider(ProviderConfig config){
        try {
            lock.lock();
            String providerName = config.getName();
            ProviderConfig old = providers.get(providerName);
            if (old != null) {
                replaceProvider(config);
            }else {
                addNewProvider(config);
            }
            getOrCreateRpcClient(providerName);
        }finally {
            lock.unlock();
        }
    }

    private synchronized void addNewProvider(ProviderConfig config){
        LOGGER.info("add new provider :" + config);
        String providerName = config.getName();
        providers.put(providerName,config);
        if(!providerNames.contains(config.getName())) {
            providerNames.add(config.getName());
        }
    }

    private synchronized void replaceProvider(ProviderConfig config){
        LOGGER.info("replace provider : "  +config);
        String providerName = config.getName();
        ProviderConfig old = providers.get(providerName);
        providers.put(providerName,config);

        //仅修改设置
        if(StringUtils.equals(old.getServiceAddr(),config.getServiceAddr()) && old.getPort() == config.getPort()){
            providers.put(providerName,config);
            LOGGER.info("update provider setting - " + old + " -> " +config);
            return;
        }


        //替换client
        RpcClient client = clients.remove(config.getName());
        if(client != null){
            LOGGER.info("shutdown old provider client");
            client.shutdown();
        }
    }

    private synchronized RpcClient getOrCreateRpcClient(String providerName){
        RpcClient client = clients.get(providerName);
        try {
            lock.lock();
            if (client == null) {
                ProviderConfig config = providers.get(providerName);
                if (config == null) {
                    throw new RpcException("The provider not found - " + providerName);
                }
                RpcClientImpl c = new RpcClientImpl(config.getServiceAddr(), config.getPort());
                c.setThreads(config.getThreads());
                c.setTimeout(config.getTimeoutMillis());
                c.startup();
                clients.put(providerName, c);
                client = c;
            }
        }catch (Throwable e){
            LOGGER.error(e.getMessage(),e);
        }finally {
            lock.unlock();
        }
        return client;
    }


    private RpcClient getNextRpcClient(){
        String name = providerNames.next();
        RpcClient c = getOrCreateRpcClient(name);
        return c;
    }

    public synchronized RpcClient getRpcClient(){
        try {
            lock.lock();
            for (int i = 0; i < providerNames.size(); i++) {
                RpcClient c = getNextRpcClient();
                if (c != null && c.isAvailable()) {
                    return c;
                }
            }
            throw new RpcException("Not found available RpcClient");
        }finally {
            lock.unlock();
        }
    }

    public boolean isEmpty(){
        return providers.isEmpty();
    }
}
