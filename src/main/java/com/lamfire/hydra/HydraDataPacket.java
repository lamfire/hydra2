package com.lamfire.hydra;


import java.nio.charset.Charset;

/**
 * HydraDataPacket
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:31
 * To change this template use File | Settings | File Templates.
 */
class HydraDataPacket implements DataPacket {
    private final DataPacketHeader header = new DataPacketHeader();
    private byte[] content;

    HydraDataPacket() {

    }

    HydraDataPacket(int id, byte[] content) {
        id(id);
        content(content);
    }

    public void id(int id) {
        header.id(id);
    }

    public int id() {
        return header.id();
    }

    @Override
    public DataPacketHeader header() {
        return header;
    }

    @Override
    public byte[] content() {
        return content;
    }

    public void content(byte[] content) {
        this.content = content;
    }

    @Override
    public int getId() {
        return header().id();
    }

    @Override
    public int getOption() {
        return header().option();
    }

    @Override
    public int getContentLength() {
        return header().contentLength();
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public String getContentAsString(String charset) {
        if (content == null) {
            return null;
        }
        return new String(content, Charset.forName(charset));
    }
}
