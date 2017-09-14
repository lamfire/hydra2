package com.lamfire.hydra;

import com.lamfire.logger.Logger;

/**
 * DefaultHeartbeatListener
 * User: linfan
 * Date: 15-8-19
 * Time: 下午2:04
 * To change this template use File | Settings | File Templates.
 */
public class DefaultHeartbeatListener implements HeartbeatListener {
    private static final Logger LOGGER = Logger.getLogger(DefaultHeartbeatListener.class);
    @Override
    public void onHeartbeat(Session session, HeartbeatMessage message) {
        LOGGER.debug("onHeartbeat - " + session +" = " + message);
        if(message.isHeartbeatRequest()){
            session.send(HeartbeatMessage.HEARTBEAT_RESPONSE_MESSAGE);
        }
    }
}
