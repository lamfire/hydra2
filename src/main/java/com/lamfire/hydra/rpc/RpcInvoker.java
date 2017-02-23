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
            byte[] paramBytes = serial(invocation);
            byte[] bytes = client.invoke(paramBytes);
            RpcMessage msg = unSerial(bytes);
            return msg.getReturnValue();
        }catch (Exception e){
            throw  new RpcException(e);
        }
    }

    private synchronized byte[] serial(Object obj){
        return serializer.encode(obj);
    }

    private synchronized RpcMessage unSerial(byte[] bytes){
        return serializer.decode(bytes,RpcMessage.class);
    }
}
