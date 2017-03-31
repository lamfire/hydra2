package com.lamfire.hydra.rpc;

import com.lamfire.hydra.*;
import com.lamfire.logger.Logger;
import com.lamfire.utils.StringUtils;

import java.net.InetAddress;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public class RpcServer implements DiscoveryListener,RPC{
    private static final Logger LOGGER = Logger.getLogger(RpcServer.class);
    private Hydra hydra;
    private final RpcServerHandler handler = new RpcServerHandler();
    private ProviderConfig providerConfig;
    private DiscoveryConfig discoveryConfig;
    private DiscoveryMultiCaster discoveryMultiCaster;
    private ServiceRegistryConfig serviceRegistry;
    private boolean discoveryEnable = false;
    private RpcSerializer serializer = Serials.DEFAULT_SERIALIZER;
    private ThreadPoolExecutor executor = null;

    public DiscoveryConfig getDiscoveryConfig() {
        return discoveryConfig;
    }

    public boolean isDiscoveryEnable() {
        return discoveryEnable;
    }

    public void setDiscoveryEnable(boolean discoveryEnable) {
        this.discoveryEnable = discoveryEnable;
    }

    public void setDiscovery(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = discoveryConfig;
    }

    public void setServiceRegistry(ServiceRegistryConfig serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }


    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
        handler.setSerializer(serializer);
    }

    public void setProvider(ProviderConfig config){
        this.providerConfig = config;
    }

    public synchronized void startup() {
        if(hydra != null){
            return;
        }

        if(providerConfig == null){
            throw new RuntimeException("Not found ProviderConfig ,please use 'setProviderConfig' to setting");
        }

        HydraBuilder builder = new HydraBuilder();
        builder.bind(providerConfig.getBindAddr()).port(providerConfig.getPort()).threads(providerConfig.getThreads());

        if(serviceRegistry == null || serviceRegistry.services() == 0){
            throw new RuntimeException("Not service has be exported");
        }

        if(discoveryEnable){
            startupDiscovery();
        }

        serviceRegistry.registerService(RPC.class,this);
        handler.setServiceRegistry(serviceRegistry);

        if(handler.getSerializer() == null){
            this.serializer = Serials.DEFAULT_SERIALIZER;
            setSerializer(this.serializer);
        }

        builder.messageReceivedListener(handler);

        builder.heartbeatListener(new HeartbeatListener() {
            @Override
            public void onHeartbeat(Session session, HeartbeatMessage message) {
                session.send(HeartbeatMessage.HEARTBEAT_RESPONSE_MESSAGE);
            }
        }) ;

        hydra = builder.build();
        hydra.startup();

        LOGGER.info("server startup on - " + providerConfig);
    }

    public synchronized void shutdown(){
        if(this.executor != null){
            this.executor.shutdown();
        }
        this.executor = null;

        if(hydra != null){
            hydra.shutdown();
        }
        hydra = null;
    }

    private void startupDiscovery(){
        if(discoveryConfig == null) {
            throw new RuntimeException("Not found DiscoveryConfig ,please use 'setDiscoveryConfig' to setting");
        }
        try {
            discoveryMultiCaster = new DiscoveryMultiCaster(InetAddress.getByName(discoveryConfig.getGroupAddr()), discoveryConfig.getGroupPort());
            discoveryMultiCaster.setOnMessageListener(this);
            discoveryMultiCaster.startup();
            LOGGER.info("Discovery starting : " + discoveryConfig);
            sendDiscoveryResponse();
        }catch (Exception e){
            LOGGER.error("Not starting discovery ",e);
        }
    }

    private void sendDiscoveryResponse(){
        DiscoveryMessage dm = new DiscoveryMessage();
        dm.setType(DiscoveryMessage.TYPE_RESPONSE);
        dm.setDiscoveryConfig(discoveryConfig);
        dm.setId(0);
        try {
            discoveryMultiCaster.send(this.serializer.encode(dm));
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    @Override
    public void onDiscoveryMessage(DiscoveryContext context, byte[] message) {
        DiscoveryMessage dm = this.serializer.decode(message,DiscoveryMessage.class);
        if(dm.getType() == DiscoveryMessage.TYPE_REQUEST && StringUtils.equals(dm.getWhere(),this.discoveryConfig.getGroupId())){
            dm.setDiscoveryConfig(this.discoveryConfig);
            dm.setType(DiscoveryMessage.TYPE_RESPONSE);
            try {
                LOGGER.debug("Sending DiscoveryMessage - " + dm);
                discoveryMultiCaster.send(this.serializer.encode(dm));
            }catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
        }
    }

    @Override
    public Set<Class<?>> services() {
        return this.serviceRegistry.getServiceClasses();
    }
}
