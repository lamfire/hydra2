package com.lamfire.hydra.rpc;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-9-1
 * Time: 上午9:50
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveryContext {
    private DiscoveryMultiCaster multiCaster;
    private DatagramPacket datagramPacket;

    DiscoveryContext(DiscoveryMultiCaster multiCaster, DatagramPacket datagramPacket){
        this.multiCaster = multiCaster;
        this.datagramPacket = datagramPacket;
    }

    public DiscoveryMultiCaster getMultiCaster() {
        return multiCaster;
    }


    public InetAddress getAddress() {
        return datagramPacket.getAddress();
    }

    public DatagramPacket getDatagramPacket(){
        return this.datagramPacket;
    }

    public int getPort(){
        return this.datagramPacket.getPort();
    }
}
