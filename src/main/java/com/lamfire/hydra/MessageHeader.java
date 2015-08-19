package com.lamfire.hydra;

/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:26
 * To change this template use File | Settings | File Templates.
 */
public class MessageHeader {
    public static final int HEADER_LENGTH = 12;
    private int id;
    private int contentLength;
    private int checksum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "id=" + id +
                ", contentLength=" + contentLength +
                ", checksum=" + checksum +
                '}';
    }
}
