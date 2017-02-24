package com.lamfire.hydra.rpc;

class RpcInvoker implements Invoker {
    private RpcSerializer serializer;

    public RpcInvoker(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public Object invoke(Invocation invocation, RpcClient client) throws RpcException {
        if(serializer == null){
            throw new RpcException("RpcSerializer Not found");
        }
        try {
            invocation.setStatus(Invocation.STATUS_REQUEST);
            byte[] paramBytes = serial(invocation);
            byte[] bytes = client.invoke(paramBytes);
            invocation = unSerial(bytes);
            if(invocation.getStatus() == Invocation.STATUS_RESPONSE) {
                return invocation.getReturnValue();
            }
            throw new RemoteException(invocation.getReturnValue().toString());
        }catch (Exception e){
            throw  new RpcException(e);
        }

    }

    private synchronized byte[] serial(Object obj){
        return serializer.encode(obj);
    }

    private synchronized Invocation unSerial(byte[] bytes){
        return serializer.decode(bytes,Invocation.class);
    }
}
