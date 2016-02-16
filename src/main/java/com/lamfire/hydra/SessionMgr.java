package com.lamfire.hydra;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午3:21
 * To change this template use File | Settings | File Templates.
 */
public interface SessionMgr {
    public void add(Session session);
    public Session get(long id);
    public Collection<Session> all();
    public void close();
    public int size();
    public boolean isEmpty();
    public void addSessionClosedListener(SessionClosedListener closedListener);
    public void removeSessionCloseListener(SessionClosedListener listener);
}
