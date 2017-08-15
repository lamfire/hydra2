package com.lamfire.hydra;

import com.lamfire.hydra.netty.NettySession;
import com.lamfire.logger.Logger;
import com.lamfire.utils.Maps;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Map;

public class HydraSessionMgr implements SessionMgr {
    private static final Logger LOGGER = Logger.getLogger(HydraSessionMgr.class);
    private final Map<Long,Session> sessions = Maps.newConcurrentMap();

    private SessionClosedListener closedListener = new SessionClosedListener() {
        @Override
        public void onClosed(Session session) {
            LOGGER.debug("[REMOVE] session was closed,remove it -> " + session);
            remove(session);
        }
    };

    @Override
    public void add(Session session) {
        if(session == null){
            return;
        }
        sessions.put(session.getId(),session);
        session.addCloseListener(closedListener);
    }

    @Override
    public Session get(long id) {
        return sessions.get(id);
    }

    public Session get(Channel channel){
        for(Session s : sessions.values()){
            NettySession session = (NettySession)s;
            if(channel.equals(session.channel())){
                return session;
            }
        }
        return null;
    }

    @Override
    public Collection<Session> all() {
        return sessions.values();
    }

    public void remove(Session session){
        if(session == null){
            return;
        }
        remove(session.getId());
    }

    @Override
    public Session remove(long id) {
        Session session = sessions.remove(id);
        session.removeCloseListener(closedListener);
        return session;
    }

    public void close(){
        for(Session s : sessions.values()){
            s.close();
        }
    }

    @Override
    public int size() {
        return sessions.size();
    }

    @Override
    public boolean isEmpty() {
        return sessions.isEmpty();
    }
}
