package com.lamfire.hydra.rpc;

import com.lamfire.logger.Logger;
import com.lamfire.utils.StringUtils;

import java.net.InetAddress;

public class HydraRPC implements DiscoveryListener{
    private static final Logger LOGGER = Logger.getLogger(HydraRPC.class);
    public static final  String DEFAULT_DISCOVERY_ADDRESS = "224.0.0.224";
    public static final  int DEFAULT_DISCOVERY_PORT = 6666;
    public static final RpcSerializer JAVA_SERIALIZER = new JavaSerializer();
    public static final RpcSerializer KRYO_SERIALIZER = new KryoSerializer();

    private DiscoveryConfig discoveryConfig;
    private DiscoveryMultiCaster discoveryMultiCaster;
    private final ProviderPool pool = new ProviderPool();
    private RpcSerializer serializer = HydraRPC.KRYO_SERIALIZER;

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

    public <T> T lookup(Class<?> interfaceClass){
        return ProxyManager.getProxy(interfaceClass,pool.getRpcClient(),serializer);
    }

    public void startupDiscovery(){
        try {
            discoveryMultiCaster = new DiscoveryMultiCaster(InetAddress.getByName(discoveryConfig.getGroupAddr()), discoveryConfig.getGroupPort());
            discoveryMultiCaster.setOnMessageListener(this);
            discoveryMultiCaster.startup();
            sendDiscoveryRequest();
        }catch (Exception e){
            LOGGER.error("Not starting discovery ",e);
        }
    }

    public boolean hashProvider(){
        return !this.pool.isEmpty();
    }

    public synchronized void waitProvider(long timeMillis){
        try {
            this.wait(timeMillis);
        }catch (Exception e){

        }
    }

    public synchronized void waitProvider(){
        try {
            this.wait();
        }catch (Exception e){

        }
    }

    private void sendDiscoveryRequest(){
        DiscoveryMessage dm = new DiscoveryMessage();
        dm.setType(DiscoveryMessage.TYPE_REQUEST);
        dm.setDiscoveryConfig(discoveryConfig);
        dm.setId(0);
        dm.setWhere(discoveryConfig.getGroupId());
        try {
            discoveryMultiCaster.send(HydraRPC.KRYO_SERIALIZER.encode(dm));
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
        DiscoveryMessage dm = HydraRPC.KRYO_SERIALIZER.decode(message,DiscoveryMessage.class);
        LOGGER.debug("onDiscoveryMessage -------------------------" + dm);
        if(dm.getType() == DiscoveryMessage.TYPE_RESPONSE && StringUtils.equals(dm.getWhere(),this.discoveryConfig.getGroupId())){
            DiscoveryConfig dc = dm.getDiscoveryConfig();
            ProviderConfig pc = dc.getProviderConfig();
            if(pc != null){
                this.addProvider(pc);
                LOGGER.info("Add Provider from discovery : " + pc);
            }
        }
    }
}
