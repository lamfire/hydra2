package com.lamfire.hydra;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public interface HeartbeatListener {
    public void onHeartbeat(Session session, HeartbeatMessage message);
}
