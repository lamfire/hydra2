package com.lamfire.hydra;

/**
 * HeartbeatListener
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public interface HeartbeatListener {
    void onHeartbeat(Session session, HeartbeatMessage message);
}
