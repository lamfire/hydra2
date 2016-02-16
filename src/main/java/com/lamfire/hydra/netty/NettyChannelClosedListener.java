package com.lamfire.hydra.netty;

import com.lamfire.logger.Logger;
import com.lamfire.hydra.SessionClosedListener;
import com.lamfire.utils.Lists;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collection;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午3:42
 * To change this template use File | Settings | File Templates.
 */
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
