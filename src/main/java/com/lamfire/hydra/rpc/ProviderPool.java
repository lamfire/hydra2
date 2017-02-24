package com.lamfire.hydra.rpc;

import com.lamfire.logger.Logger;
import com.lamfire.utils.CircularLinkedList;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class ProviderPool {
    private static final Logger LOGGER = Logger.getLogger(ProviderPool.class);
    private final Map<String,ProviderConfig> providers = new HashMap<String, ProviderConfig>();
    private final Map<String,RpcClient> clients = new HashMap<String, RpcClient>();
    private final AtomicInteger counter = new AtomicInteger();
    private final CircularLinkedList<String> providerNames = new CircularLinkedList<String>();

    public void addProvider(ProviderConfig config){
        providers.put(config.getName(),config);
        if(!providerNames.contains(config.getName())) {
            providerNames.add(config.getName());
        }
    }

    public synchronized RpcClient getRpcClient(String providerName){
        try {
            RpcClient client = clients.get(providerName);
            if (client == null) {
                ProviderConfig config = providers.get(providerName);
                if (config == null) {
                    throw new RpcException("The provider not found - " + providerName);
                }
                RpcClientImpl c = new RpcClientImpl(config.getHost(), config.getPort());
                c.setThreads(config.getThreads());
                c.setTimeout(config.getTimeoutMillis());
                c.startup();
                clients.put(providerName, c);
                client = c;
            }
            return client;
        }catch (Throwable e){
            LOGGER.error(e.getMessage(),e);
        }
        return null;
    }

    public synchronized RpcClient getRpcClient(){
        for(int i=0;i<providerNames.size();i++){
            String name = providerNames.getLast();
            RpcClient c = getRpcClient(name);
            if(c != null && c.isAvailable()){
                return c;
            }
            clients.remove(name);
        }
        throw new RpcException("Not found available provider");
    }

    public boolean isEmpty(){
        return providers.isEmpty();
    }
}
