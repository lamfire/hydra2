package com.lamfire.hydra.netty;

import com.lamfire.hydra.SessionClosedListener;
import com.lamfire.logger.Logger;
import com.lamfire.utils.Lists;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collection;


class NettyChannelClosedListener implements GenericFutureListener<Future<Void>> {
    private static final Logger LOGGER = Logger.getLogger(NettyChannelClosedListener.class);

    private NettySession session;

    public NettyChannelClosedListener(NettySession s){
        this.session = s;
    }

    @Override
    public void operationComplete(Future<Void> future) throws Exception {
        Collection<SessionClosedListener> listeners = Lists.newArrayList(session.closedListeners());
        for(SessionClosedListener l : listeners){
            l.onClosed(session);
        }
    }

    public NettySession session(){
        return session;
    }
}
