package com.github.jspxnet.util;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.github.jspxnet.security.utils.Base64;
import com.github.jspxnet.utils.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class HessianSerializableUtil {

    private HessianSerializableUtil()
    {

    }

    public static HessianProxyFactory factory = new HessianProxyFactory();

    static {
        factory.setOverloadEnabled(true);
        factory.setHessian2Reply(true);
        factory.setHessian2Request(true);
        factory.getSerializerFactory().setSendCollectionType(true);
        factory.setChunkedPost(true);
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(10000);
    }

    public static SerializerFactory getSerializerFactory() {
        return factory.getSerializerFactory();
    }

    /**
     * @param object bean
     * @return Hessian的序列化输出 成base64
     * @throws IOException 异常
     */
    public static byte[] getSerializable(Object object) throws IOException {
        if (object == null) {
            return new byte[0];
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
            AbstractHessianOutput ho = factory.getHessianOutput(outputStream);
            ho.setSerializerFactory(factory.getSerializerFactory());
            ho.writeObject(object);
            ho.flush();
            return outputStream.toByteArray();
        }
    }

    /**
     * @param str 反序列化
     * @return 对象
     * @throws IOException 异常
     */
    public static Object getUnSerializable(String str) throws IOException {
        return getUnSerializable(str, null);
    }

    /**
     * @param str base64编码
     * @param cla 类型
     * @param <T> 泛型
     * @return HessianI饭序列化
     * @throws IOException 异常
     */
    public static <T> T getUnSerializable(String str, Class<T> cla) throws IOException {
        if (StringUtil.isEmpty(str) || "{}".equals(str)) {
            return null;
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decode(str, Base64.NO_WRAP))) {
            AbstractHessianInput input = factory.getHessian2Input(inputStream);
            if (cla == null) {
                return (T) input.readObject();
            } else {
                return (T) input.readObject(cla);
            }
        }
    }

    public static <T> T getUnSerializable(byte[] data) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            AbstractHessianInput input = factory.getHessian2Input(inputStream);
            return (T)input.readObject();
        }
    }

}
