package com.lamfire.hydra.rpc;

import java.util.Set;

public interface RPC {
    Set<Class<?>> services();
    void setSerializer(RpcSerializer serializer);
    void setProvider(ProviderConfig config);
    void setDiscovery(DiscoveryConfig discoveryConfig);
}
