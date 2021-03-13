package com.github.jspxnet.json;


import org.springframework.http.converter.json.AbstractJsonHttpMessageConverter;
import org.springframework.lang.Nullable;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

/**
 * Created by chenYuan
 *
 * author: chenYuan
 * date: 2021/3/10 22:17
 * description: 替代spring的json解析器
 *  implements WebMvcConfigurer
 * Override
 * <pre>{@code
 *     public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
 *         converters.clear();
 *         JsonHttpMessageConverter jsonConverter = new JsonHttpMessageConverter();
 *         converters.add(jsonConverter);
 *     }
 *    }
 * }</pre>
 **/
public class JsonHttpMessageConverter extends AbstractJsonHttpMessageConverter {

    @Override
    protected Object readInternal(Type resolvedType, Reader reader) throws Exception {
        return  GsonUtil.createGson().fromJson(reader,resolvedType);
    }

    @Override
    protected void writeInternal(Object object, @Nullable Type type, Writer writer) throws Exception {
        JSONObject json = new JSONObject(object);
        writer.write(json.toString(4));
    }
}
