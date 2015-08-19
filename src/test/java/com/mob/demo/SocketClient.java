package com.mob.demo;

import com.lamfire.code.CRC32;
import com.lamfire.utils.Bytes;
import com.lamfire.utils.RandomUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:00
 * To change this template use File | Settings | File Templates.
 */
public class SocketClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1",1980));
        OutputStream os = socket.getOutputStream();

        for(int i=0;i<1;i++){
            String data = RandomUtils.randomText(100);
            byte[] content = data.getBytes();
            int option = CRC32.digest(content);
            System.out.println(i + " - CRC32 = " + option + " -> " + data);


            os.write(Bytes.toBytes(content.length + 12));
            os.write(Bytes.toBytes(i));                          //id
            os.write(Bytes.toBytes(content.length));            //length
            os.write(Bytes.toBytes(option));                      //checksum
            os.write(content);                                   //content
            os.flush();
        }
    }
}
