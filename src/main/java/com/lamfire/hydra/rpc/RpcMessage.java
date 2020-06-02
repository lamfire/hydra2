package com.lamfire.hydra.rpc;

import java.io.Serializable;

class RpcMessage implements Serializable {
    private Class<?> rpcInterface;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
    private Class<?> returnType;
    private Object returnValue;

    public Class<?> getRpcInterface() {
        return rpcInterface;
    }

    public void setRpcInterface(Class<?> rpcInterface) {
        this.rpcInterface = rpcInterface;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
