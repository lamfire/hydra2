package com.lamfire.hydra.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class KryoSerializer implements RpcSerializer {
    static final int BUFFER_SIZE = 512;
    static final int MAX_BUFFER_SIZE = 16 * 1024;
    private Kryo kryo = new Kryo();

    public KryoSerializer() {
        kryo.setRegistrationRequired(false);
        kryo.setReferences(false);
    }

    @Override
    public synchronized byte[] encode(Object obj) {
        ByteBufferOutput output = new ByteBufferOutput(BUFFER_SIZE,MAX_BUFFER_SIZE);
        try{
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            output.close();
        }
    }

    public <T> T decode(byte[] bytes,Class<T> cls) {
        Input input = new Input(bytes);
        try{
            return (T)kryo.readClassAndObject(input);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            input.close();
        }
    }
}
