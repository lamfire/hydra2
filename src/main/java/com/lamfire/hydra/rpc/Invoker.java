package com.lamfire.hydra.rpc;


public interface Invoker {
    Object invoke(Invocation invocation, RpcClient client) throws RpcException;
}
