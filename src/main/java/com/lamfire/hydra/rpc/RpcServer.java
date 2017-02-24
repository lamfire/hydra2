package com.lamfire.hydra.rpc;

import com.lamfire.hydra.*;
import com.lamfire.logger.Logger;

public class RpcServer {
    private static final Logger LOGGER = Logger.getLogger(RpcServer.class);
    private Hydra hydra;
    private final RpcServerHandler handler = new RpcServerHandler();
    private String host = "0.0.0.0";
    private int port = 19800;
    private ServiceRegistryConfig serviceRegistry;
    private int threads = 32;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setServiceRegistry(ServiceRegistryConfig serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setSerializer(RpcSerializer serializer) {
        handler.setSerializer(serializer);
    }

    public void setProviderConfig(ProviderConfig config){
        this.setHost(config.getHost());
        this.setPort(config.getPort());
        this.setThreads(config.getThreads());
        this.setSerializer(config.getSerializer());
    }

    public synchronized void startup() {
        if(hydra != null){
            return;
        }
        HydraBuilder builder = new HydraBuilder();
        builder.bind(host).port(port).threads(threads);

        if(serviceRegistry == null || serviceRegistry.services() == 0){
            throw new RuntimeException("Not service has be exported");
        }

        handler.setServiceRegistry(serviceRegistry);

        if(handler.getSerializer() == null){
            setSerializer(new JavaSerializer());
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

        LOGGER.info("server startup on - " + host +":" + port);
    }

    public synchronized void shutdown(){
        if(hydra != null){
            hydra.shutdown();
        }
        hydra = null;
    }
}
