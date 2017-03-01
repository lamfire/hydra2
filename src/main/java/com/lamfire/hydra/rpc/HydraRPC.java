package com.lamfire.hydra.rpc;

import com.lamfire.logger.Logger;
import com.lamfire.utils.Maps;
import com.lamfire.utils.StringUtils;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HydraRPC implements DiscoveryListener,RPC{
    private static final Logger LOGGER = Logger.getLogger(HydraRPC.class);
    private DiscoveryConfig discoveryConfig;
    private DiscoveryMultiCaster discoveryMultiCaster;
    private final ProviderPool pool = new ProviderPool();
    private RpcSerializer serializer = Serials.DEFAULT_SERIALIZER;
    private final Set<Class<?>> services = new HashSet<Class<?>>();
    private final Map<Class<?>,Object> interfaceProxyInstances = Maps.newHashMap();
    private boolean enableDiscovery = false;

    public synchronized void addProvider(ProviderConfig config){
        pool.addProvider(config);
        this.notifyAll();
    }

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public void setProvider(ProviderConfig config) {
        this.addProvider(config);
    }

    @Override
    public void setDiscovery(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = discoveryConfig;
    }

    public synchronized  <T> T lookup(Class<?> interfaceClass){
        Object instance = interfaceProxyInstances.get(interfaceClass);
        if(instance != null) {
            return (T)instance;
        }
        Collection<Class<?>> services = services();
        if(!services.contains(interfaceClass)){
            throw new RpcException("RPC service not found : " + interfaceClass);
        }

        instance = createRpcProxy(interfaceClass);
        interfaceProxyInstances.put(interfaceClass,instance);
        return (T)instance;
    }

    @Override
    public synchronized Set<Class<?>> services() {
        if(!services.isEmpty()){
            return services;
        }
        RPC rpc = createRpcProxy(RPC.class);
        services.addAll(rpc.services());
        return services;
    }

    private <T> T createRpcProxy(Class<?> interfaceClass){
        return ProxyManager.getProxy(interfaceClass,pool,serializer);
    }

    public void startupDiscovery(){
        if(this.discoveryConfig == null){
            throw new RuntimeException("Not found DiscoveryConfig ,please use 'setDiscoveryConfig' to setting");
        }
        startupDiscovery(this.discoveryConfig);
    }

    public void startupDiscovery(DiscoveryConfig discoveryConfig){
        if(discoveryConfig == null){
            throw new IllegalArgumentException("The arg 'iscoveryConfig' cannot be 'null'");
        }
        this.discoveryConfig = discoveryConfig;
        try {
            discoveryMultiCaster = new DiscoveryMultiCaster(InetAddress.getByName(discoveryConfig.getGroupAddr()), discoveryConfig.getGroupPort());
            discoveryMultiCaster.setOnMessageListener(this);
            discoveryMultiCaster.startup();
            sendDiscoveryRequest();
        }catch (Exception e){
            LOGGER.error("Not starting discovery ",e);
        }
    }

    public boolean hashProviders(){
        return !this.pool.isEmpty();
    }

    public synchronized void waitProviders(long timeMillis){
        if(pool.isEmpty()) {
            try {
                this.wait(timeMillis);
            } catch (Exception e) {

            }
        }
    }

    public synchronized void waitProviders(){
        if(pool.isEmpty()) {
            try {
                this.wait();
            } catch (Exception e) {

            }
        }
    }

    private void sendDiscoveryRequest(){
        DiscoveryMessage dm = new DiscoveryMessage();
        dm.setType(DiscoveryMessage.TYPE_REQUEST);
        dm.setDiscoveryConfig(discoveryConfig);
        dm.setId(0);
        dm.setWhere(discoveryConfig.getGroupId());
        try {
            discoveryMultiCaster.send(serializer.encode(dm));
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    public DiscoveryConfig getDiscoveryConfig() {
        return discoveryConfig;
    }

    public void setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = discoveryConfig;
    }

    public boolean isEnableDiscovery() {
        return enableDiscovery;
    }

    public void setEnableDiscovery(boolean enableDiscovery) {
        this.enableDiscovery = enableDiscovery;
    }

    @Override
    public void onDiscoveryMessage(DiscoveryContext context, byte[] message) {
        DiscoveryMessage dm = serializer.decode(message,DiscoveryMessage.class);
        //LOGGER.debug("onDiscoveryMessage -------------------------" + dm);
        if(dm.getType() == DiscoveryMessage.TYPE_RESPONSE ){
            DiscoveryConfig dc = dm.getDiscoveryConfig();
            if(dc != null && StringUtils.equals(dc.getGroupId(),this.discoveryConfig.getGroupId())) {
                ProviderConfig pc = dc.getProviderConfig();
                if (pc != null ) {
                    LOGGER.info("Add Provider from discovery : " + pc);
                    this.addProvider(pc);
                }
            }
        }
    }
}
