package com.lamfire.hydra.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class KryoSerializer implements RpcSerializer {
    static final int BUFFER_SIZE = 64;
    static final int MAX_BUFFER_SIZE = 64 * 1024;
    private Kryo kryo = new Kryo();

    private int maxBuffer = MAX_BUFFER_SIZE;

    public KryoSerializer() {
        kryo.setRegistrationRequired(false);
        kryo.setReferences(false);
    }

    public KryoSerializer(int maxBuffer) {
        this.maxBuffer = maxBuffer;
    }

    @Override
    public synchronized byte[] encode(Object obj) {
        ByteBufferOutput output = new ByteBufferOutput(BUFFER_SIZE,maxBuffer);
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
