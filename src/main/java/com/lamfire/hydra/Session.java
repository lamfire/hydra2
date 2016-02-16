package com.lamfire.hydra;

import java.net.SocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午3:17
 * To change this template use File | Settings | File Templates.
 */
public interface Session {

    public long getId();

    public void send(Message message);

    public void close();

    public SocketAddress getRemoteAddress();

    public boolean isActive();

    public boolean isOpen();

    public boolean isWritable();

    public Object attr(String name);

    public void attr(String name,Object value);

    public void heartbeat();

    public void addCloseListener(SessionClosedListener listener);

    public void removeCloseListener(SessionClosedListener listener);
}
