package com.lamfire.hydra;

import java.util.Collection;


public interface SessionMgr {
    void put(Object key,Session session);
    void remove(Session session);
    Session remove(Object id);
    Session get(Object id);
    Collection<Session> all();
    void close();
    int size();
    boolean isEmpty();
}
