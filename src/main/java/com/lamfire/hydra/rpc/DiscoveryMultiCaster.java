package com.lamfire.hydra.rpc;

import com.lamfire.logger.Logger;
import com.lamfire.utils.Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadPoolExecutor;

public class DiscoveryMultiCaster {
    private static final Logger LOGGER = Logger.getLogger(DiscoveryMultiCaster.class);
    public static final  String DEFAULT_MULTI_CAST_ADDRESS = "224.0.0.224";
    public static final  int DEFAULT_MULTI_CAST_PORT = 6666;
    private ThreadPoolExecutor executor ;
    private InetAddress address;
    private int port = DEFAULT_MULTI_CAST_PORT;
    private MulticastSocket multicastSocket;
    private DiscoveryListener messageListener;
    private DiscoveryHandler handler;

    public DiscoveryMultiCaster(){
        this.port = DEFAULT_MULTI_CAST_PORT;
        try{
            this.address = InetAddress.getByName(DEFAULT_MULTI_CAST_ADDRESS);
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    public DiscoveryMultiCaster(int port){
        this.port = port;
        try{
            this.address = InetAddress.getByName(DEFAULT_MULTI_CAST_ADDRESS);
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
    }

    public DiscoveryMultiCaster(InetAddress address, int port){
        this.address = address;
        this.port = port;
    }


    public void setOnMessageListener(DiscoveryListener listener){
        this.messageListener = listener;
    }

    public void open()throws IOException{
        if(multicastSocket == null){
            this.multicastSocket = new MulticastSocket(port);
            this.multicastSocket.joinGroup(address);
        }
    }

    void receive(DatagramPacket packet) throws IOException {
        if(multicastSocket == null){
            throw new IOException("Not opened DiscoveryMultiCaster.");
        }
        this.multicastSocket.receive(packet);
    }

    public void startup() throws IOException {
        open();

        if(this.handler == null){
            this.handler = new DiscoveryHandler(this,messageListener);
        }

        if(executor == null){
            executor = Threads.newSingleThreadScheduledExecutor(Threads.makeThreadFactory("DiscoveryHandler")) ;
        }
        executor.submit(this.handler);
        LOGGER.info("startup on " + address + ":" + port);
    }

    public void shutdown(){
        close();

        if(handler != null){
            this.handler.setShutdown(true);
            this.handler = null;
        }

        if(executor != null){
            this.executor.shutdown();
            this.executor = null;
        }
    }

    public void close(){
        if(multicastSocket != null){
            try {
                multicastSocket.leaveGroup(this.address);
            } catch (IOException e) {
                LOGGER.warn(e.getMessage(),e);
            }
            multicastSocket.close();
            multicastSocket = null;
        }
    }

    public void send(byte[] bytes) throws IOException {
        if(this.multicastSocket == null){
            throw new IOException("Not opened DiscoveryMultiCaster.");
        }
        ByteBuffer buffer = ByteBuffer.allocate(4 + bytes.length);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
        buffer.flip();

        bytes = buffer.array();
        DatagramPacket packet = new DatagramPacket(bytes,bytes.length,address,port);
        this.multicastSocket.send(packet);
    }
}
