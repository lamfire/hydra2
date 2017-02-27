package com.lamfire.hydra.rpc;

import java.util.*;


public class ServiceRegistryConfig {
    private final Map<Class<?> ,Object > interfaces = new HashMap();

    public void registerService(Class<?> interfaceCls,Object instance){
        interfaces.put(interfaceCls,instance);
    }

    public Object lookupService(Class<?> interfaceCls){
        return interfaces.get(interfaceCls);
    }

    public int services() {
        return interfaces.size();
    }

    public Set<Class<?>> getServiceClasses(){
        Set<Class<?>> result = new HashSet();
        result.addAll(interfaces.keySet());
        return result;
    }
}
