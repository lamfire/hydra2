package com.lamfire.hydra;


/**
 * Created with IntelliJ IDEA.
 * User: linfan
 * Date: 15-8-18
 * Time: 上午11:31
 * To change this template use File | Settings | File Templates.
 */
class HydraMessage implements Message {
    private final MessageHeader header = new MessageHeader();
    private byte[] content;

    HydraMessage(){

    }

    HydraMessage(int id,byte[] content){
        id(id);
        content(content);
    }

    public void id(int id){
        header.setId(id);
    }

    public int id(){
        return header.getId();
    }

        @Override
    public MessageHeader header() {
        return header;
    }

    @Override
    public byte[] content() {
        return content;
    }

    public void content(byte[] content) {
        this.content = content;
    }
}
