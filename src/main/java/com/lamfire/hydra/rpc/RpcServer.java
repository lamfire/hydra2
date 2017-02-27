package com.lamfire.hydra.rpc;

import com.lamfire.hydra.*;
import com.lamfire.logger.Logger;
import com.lamfire.utils.StringUtils;

import java.net.InetAddress;

public class RpcServer implements DiscoveryListener{
    private static final Logger LOGGER = Logger.getLogger(RpcServer.class);
    private Hydra hydra;
    private final RpcServerHandler handler = new RpcServerHandler();
    private ProviderConfig providerConfig;
    private DiscoveryConfig discoveryConfig;
    private DiscoveryMultiCaster discoveryMultiCaster;
    private ServiceRegistryConfig serviceRegistry;
    private boolean enableDiscovery = false;

    public DiscoveryConfig getDiscoveryConfig() {
        return discoveryConfig;
    }

    public boolean isEnableDiscovery() {
        return enableDiscovery;
    }

    public void setEnableDiscovery(boolean enableDiscovery) {
        this.enableDiscovery = enableDiscovery;
    }

    public void setDiscoveryConfig(DiscoveryConfig discoveryConfig) {
        this.discoveryConfig = discoveryConfig;
    }

    public void setServiceRegistry(ServiceRegistryConfig serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }


    public void setSerializer(RpcSerializer serializer) {
        handler.setSerializer(serializer);
    }

    public void setProviderConfig(ProviderConfig config){
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

        if(enableDiscovery){
            startupDiscovery();
        }

        handler.setServiceRegistry(serviceRegistry);

        if(handler.getSerializer() == null){
            setSerializer(new KryoSerializer());
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
            discoveryMultiCaster.send(HydraRPC.KRYO_SERIALIZER.encode(dm));
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    @Override
    public void onDiscoveryMessage(DiscoveryContext context, byte[] message) {
        DiscoveryMessage dm = HydraRPC.KRYO_SERIALIZER.decode(message,DiscoveryMessage.class);
        //LOGGER.debug("onDiscoveryMessage - " + dm);
        if(dm.getType() == DiscoveryMessage.TYPE_REQUEST && StringUtils.equals(dm.getWhere(),this.discoveryConfig.getGroupId())){
            dm.setDiscoveryConfig(this.discoveryConfig);
            dm.setType(DiscoveryMessage.TYPE_RESPONSE);
            try {
                LOGGER.debug("Sending DiscoveryMessage - " + dm);
                discoveryMultiCaster.send(HydraRPC.KRYO_SERIALIZER.encode(dm));
            }catch (Exception e){
                LOGGER.error(e.getMessage(),e);
            }
        }
    }
}
