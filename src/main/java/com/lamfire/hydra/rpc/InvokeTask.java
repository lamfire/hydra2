package com.lamfire.hydra.rpc;

import com.lamfire.hydra.Message;
import com.lamfire.hydra.MessageFactory;
import com.lamfire.hydra.Session;
import com.lamfire.logger.Logger;
import com.lamfire.utils.StringUtils;

import java.lang.reflect.Method;

public class InvokeTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(InvokeTask.class);
    private Session session;
    private Message message;
    private RpcSerializer serializer;
    private ServiceRegistryConfig serviceRegistry;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public RpcSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(RpcSerializer serializer) {
        this.serializer = serializer;
    }

    public ServiceRegistryConfig getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistryConfig serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void run() {
        byte[] resultBytes = null;
        try {
            byte[] bytes = message.content();
            Invocation msg = serializer.decode(bytes, Invocation.class);
            invoke(msg);
            resultBytes = serializer.encode(msg);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        } finally {
            session.send(MessageFactory.makeMessage(message.getId(), message.getOption(), resultBytes));
        }
    }

    private void invoke(Invocation msg) {
        try {
            Class<?> rpcInterface = msg.getRpcInterface();
            Object instance = serviceRegistry.lookupService(rpcInterface);
            Method method = rpcInterface.getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object result = method.invoke(instance, msg.getParameters());
            msg.setStatus(Invocation.STATUS_RESPONSE);
            msg.setReturnValue(result);
        } catch (Throwable t) {
            //LOGGER.debug(t.getMessage(),t);
            msg.setStatus(Invocation.STATUS_EXCEPTION);
            msg.setReturnValue(StringUtils.dumpStackTraceAsString(t));
        }
    }
}
