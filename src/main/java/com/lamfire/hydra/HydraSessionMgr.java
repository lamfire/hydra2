package com.lamfire.hydra;

import com.lamfire.hydra.netty.NettySession;
import com.lamfire.logger.Logger;
import com.lamfire.utils.Maps;

import java.util.Collection;
import java.util.Map;

public class HydraSessionMgr implements SessionMgr {
    private static final Logger LOGGER = Logger.getLogger(HydraSessionMgr.class);

    private static final String SESSION_ATTR_KEY = "_G_K";
    private final Map<Object,Session> sessions = Maps.newConcurrentMap();

    private final String name;

    private final SessionClosedListener closedListener = new SessionClosedListener() {

        public void onClosed(Session session) {
            LOGGER.debug("[REMOVE]{"+name+"} session was closed,remove it -> " + session);
            _remove(session);
        }
    };


    public HydraSessionMgr(String name) {
        this.name = name;
    }

    public void put(Object key, Session session) {
        if(session == null){
            return;
        }
        sessions.put(key,session);
        session.attr(SESSION_ATTR_KEY,key);
        NettySession s = ((NettySession)session);
        s.addCloseListener(closedListener);
    }


    public Session get(Object key) {
        return sessions.get(key);
    }


    public Collection<Session> all() {
        return sessions.values();
    }



    private void _remove(Session session){
        if(session == null){
            return;
        }
        Object key = session.attr(SESSION_ATTR_KEY);
        if(key != null) {
            sessions.remove(key);
            NettySession s = ((NettySession) session);
            s.removeCloseListener(closedListener);
        }
    }

    public void remove(Session session){
        _remove(session);
    }

    public Session remove(Object key){
        Session session = sessions.remove(key);
        if(session == null){
            return null;
        }
        _remove(session);
        return session;
    }

    public void close(){
        for(Session s : sessions.values()){
            s.close();
        }
    }


    public int size() {
        return sessions.size();
    }


    public boolean isEmpty() {
        return sessions.isEmpty();
    }
}
