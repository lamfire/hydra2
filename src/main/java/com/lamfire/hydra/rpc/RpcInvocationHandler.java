package com.lamfire.hydra.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


class RpcInvocationHandler implements InvocationHandler {
    private final Class<?> interfaceClass;
    private Invoker invoker;
    private RpcClient client;

    public RpcInvocationHandler(Class<?> interfaceClass, Invoker invoker, RpcClient client){
        this.interfaceClass = interfaceClass;
        this.invoker = invoker;
        this.client = client;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(proxy, args);
        }
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return proxy.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return proxy.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return proxy.equals(args[0]);
        }
        Class<?> returnType =  method.getReturnType();
        Invocation message = new Invocation();
        message.setRpcInterface(this.interfaceClass);
        message.setMethodName(methodName);
        message.setParameterTypes(parameterTypes);
        message.setParameters(args);
        message.setReturnType(returnType);

        Object result = invoker.invoke(message,client);
        if(result instanceof Throwable){
            throw (Throwable) result;
        }

        return result;
    }
}
