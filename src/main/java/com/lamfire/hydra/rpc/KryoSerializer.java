package com.lamfire.hydra.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;


public class KryoSerializer implements RpcSerializer {
    private Kryo kryo = new Kryo();

    public KryoSerializer() {
        kryo.setRegistrationRequired(false);
        kryo.setReferences(false);
    }

    @Override
    public synchronized byte[] encode(Object obj) {
        Output output = new Output(512);
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
