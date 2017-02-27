package com.lamfire.hydra.rpc;

import com.lamfire.logger.Logger;
import com.lamfire.utils.CircularLinkedList;
import com.lamfire.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class ProviderPool {
    private static final Logger LOGGER = Logger.getLogger(ProviderPool.class);
    private final Map<String,ProviderConfig> providers = new HashMap<String, ProviderConfig>();
    private final Map<String,RpcClient> clients = new HashMap<String, RpcClient>();
    private final CircularLinkedList<String> providerNames = new CircularLinkedList<String>();

    public synchronized void addProvider(ProviderConfig config){
        String providerName = config.getName();
        ProviderConfig old = providers.get(providerName);
        if(old != null){
            replaceProvider(config);
            return;
        }
        addNewProvider(config);
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
        RpcClient client = clients.get(config.getName());
        if(client != null){
            clients.remove(config.getName());
            LOGGER.info("shutdown old provider client");
            client.shutdown();
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
                RpcClientImpl c = new RpcClientImpl(config.getServiceAddr(), config.getPort());
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
            String name = providerNames.next();
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
