package com.github.jspxnet.json.redisson;

import com.github.jspxnet.util.HessianSerializableUtil;
import io.netty.buffer.*;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import java.io.IOException;
//com.github.jspxnet.json.redisson.HessianCodec
public class HessianCodec  extends BaseCodec {
    public static final HessianCodec INSTANCE = new HessianCodec();

    private final Decoder<Object> decoder;
    private final org.redisson.client.protocol.Encoder encoder;
    private final ClassLoader classLoader;

    public HessianCodec() {
        this((ClassLoader)null);
    }

    public HessianCodec(ClassLoader classLoader) {
        class HessianDecoder implements Decoder<Object> {
            HessianDecoder() {
            }
            @Override
            public Object decode(ByteBuf buf, State state) throws IOException {
                return HessianSerializableUtil.getUnSerializable(buf);
            }
        }

        this.decoder = new HessianDecoder();

        class HessianEncode implements org.redisson.client.protocol.Encoder {
            HessianEncode() {
            }
            @Override
            public ByteBuf encode(Object in) throws IOException {
                byte[] bytes = HessianSerializableUtil.getSerializable(in);
                return Unpooled.wrappedBuffer(bytes);
            }
        }

        this.encoder = new HessianEncode();
        this.classLoader = classLoader;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return this.decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return this.encoder;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader != null ? this.classLoader : Thread.currentThread().getContextClassLoader();
    }
}
