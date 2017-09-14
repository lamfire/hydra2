package com.lamfire.hydra;

/**
 * Message
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public interface Message {
    public MessageHeader header();
    public byte[] content();
    public void content(byte[] content);
    public int getId();
    public int getOption();
    public int getContentLength();
    public byte[] getContent();
    public String getContentAsString(String charset);
}
