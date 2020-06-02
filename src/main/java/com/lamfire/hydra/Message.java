package com.lamfire.hydra;

/**
 * Message
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public interface Message {
    MessageHeader header();

    byte[] content();

    void content(byte[] content);

    int getId();

    int getOption();

    int getContentLength();

    byte[] getContent();

    String getContentAsString(String charset);
}
