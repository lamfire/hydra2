package com.lamfire.hydra;

import java.util.Collection;


public interface SessionMgr {
    void add(Session session);
    void remove(Session session);
    Session remove(long id);
    Session get(long id);
    Collection<Session> all();
    void close();
    int size();
    boolean isEmpty();
}
