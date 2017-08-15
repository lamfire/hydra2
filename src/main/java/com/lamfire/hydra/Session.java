package com.lamfire.hydra;

import java.net.SocketAddress;

public interface Session {

    long getId();

    void send(Message message);

    void send(Message message,boolean sync)throws InterruptedException;

    void close();

    SocketAddress getRemoteAddress();

    boolean isActive();

    boolean isOpen();

    boolean isWritable();

    Object attr(String name);

    void attr(String name,Object value);

    void heartbeat();

    void addCloseListener(SessionClosedListener listener);

    void removeCloseListener(SessionClosedListener listener);
}
