package com.lamfire.hydra.rpc;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageFactory;
import com.lamfire.hydra.MessageReceivedListener;
import com.lamfire.hydra.Session;
import com.lamfire.logger.Logger;

import java.lang.reflect.Method;


class RpcServerHandler implements MessageReceivedListener {
    private static final Logger LOGGER = Logger.getLogger(RpcServerHandler.class);
    private RpcSerializer serializer;
    private ServiceRegistryConfig serviceRegistry;

    public void setServiceRegistry(ServiceRegistryConfig serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    public void onMessageReceived(Session session, Message message) {
        byte[] bytes = message.content();
        RpcMessage msg = unSerial(bytes);
        Object result = invoke(msg);
        msg.setReturnValue(result);
        byte[] resultBytes = serial(msg);
        session.send(MessageFactory.makeMessage(message.getId(),message.getOption(),resultBytes));
    }

    private synchronized byte[] serial(Object obj){
        return serializer.encode(obj);
    }

    private synchronized RpcMessage unSerial(byte[] bytes){
        return serializer.decode(bytes,RpcMessage.class);
    }

    private Object invoke(RpcMessage msg) {
        try {
            Class<?> rpcInterface = msg.getRpcInterface();
            Object instance = serviceRegistry.lookupService(rpcInterface);
            Method method = rpcInterface.getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object result = method.invoke(instance, msg.getParameters());
            return result;
        } catch(Throwable t){
            LOGGER.error(t.getMessage(),t);
            return t;
        }
    }
}
