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
    private int id = 0;
    private int contentLength = 0;
    private int option = 0;

    public int id() {
        return id;
    }

    public void id(int id) {
        this.id = id;
    }

    public int contentLength() {
        return contentLength;
    }

    public void contentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public int option() {
        return option;
    }

    public void option(int option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return "MessageHeader{" +
                "id=" + id +
                ", contentLength=" + contentLength +
                ", option=" + option +
                '}';
    }
}
