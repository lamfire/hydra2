package com.lamfire.hydra.rpc;

import java.lang.reflect.Proxy;

class ProxyManager {
    public static <T> T getProxy(Class<?> interfaceClass,ProviderPool pool,RpcSerializer serializer) {
        Class<?>[] ifaces = new Class<?>[1];
        ifaces[0] = interfaceClass;
        Invoker invoker = new RpcInvoker(serializer);
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), ifaces, new RpcInvocationHandler(interfaceClass,invoker,pool));
    }
}
