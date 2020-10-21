package com.lamfire.hydra.rpc;

import com.lamfire.hydra.DataPacket;
import com.lamfire.hydra.DataPacketFactory;
import com.lamfire.hydra.Session;
import com.lamfire.logger.Logger;
import com.lamfire.utils.StringUtils;

import java.lang.reflect.Method;

public class InvokeTask implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(InvokeTask.class);
    private Session session;
    private DataPacket dataPacket;
    private RpcSerializer serializer;
    private ServiceRegistryConfig serviceRegistry;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public DataPacket getDataPacket() {
        return dataPacket;
    }

    public void setDataPacket(DataPacket dataPacket) {
        this.dataPacket = dataPacket;
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
            byte[] bytes = dataPacket.content();
            Invocation msg = serializer.decode(bytes, Invocation.class);
            invoke(msg);
            resultBytes = serializer.encode(msg);
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        } finally {
            session.send(DataPacketFactory.make(dataPacket.getId(), dataPacket.getOption(), resultBytes));
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
            //LOGGER.debug(t.getDataPacket(),t);
            msg.setStatus(Invocation.STATUS_EXCEPTION);
            msg.setReturnValue(StringUtils.dumpStackTraceAsString(t));
        }
    }
}
