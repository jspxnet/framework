package com.github.jspxnet.util;

import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/7/10 21:44
 * description: Cglib 创建对象
 **/
@Slf4j
public class CglibProxyUtil {

    /**
     *
     * @param cls 类型
     * @param callback 拦截器
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T getProxyInstance(Class<T> cls, Callback callback){
        return (T) Enhancer.create(cls, callback);
    }



    /**
     *
     * @param object 对象
     * @return 得到
     */
    public static String getClassName(Object object)
    {
        if (ClassUtil.isProxy(object.getClass()))
        {
            return StringUtil.substringBefore(object.getClass().getName(),"$$");
        }

        return object.getClass().getName();
    }


    /**
     *
     * @param cls 类
     * @return 得到真实的触发对象类型
     */
    public static Class<?> getClass(Class<?> cls)
    {
        if (ClassUtil.isProxy(cls))
        {
            String className = getClassName(cls);
            if (!StringUtil.isNull(className))
            {
                try {
                    cls = ClassUtil.loadClass(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return cls;
    }
}
