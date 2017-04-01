package com.lamfire.hydra.netty;

import com.lamfire.logger.Logger;
import com.lamfire.utils.Lists;
import com.lamfire.utils.Maps;
import com.lamfire.hydra.Session;
import com.lamfire.hydra.SessionClosedListener;
import com.lamfire.hydra.SessionMgr;
import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NettySessionMgr implements SessionMgr {
    private static final Logger LOGGER = Logger.getLogger(NettySessionMgr.class);
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
        NettySession s = ((NettySession)session);
        s.addCloseListener(closedListener);
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
        sessions.remove(session.getId());
        NettySession s = ((NettySession)session);
        s.removeCloseListener(closedListener);
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
