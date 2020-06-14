package com.github.jspxnet.util;

import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

import java.util.HashMap;

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
     * @param type 对象的类型
     * @return 判断是否为代理对象
     */
    public static boolean isProxy(Class<?> type) {
        if (type == null) {
            return false;
        }
        try {
            return type.getName().contains("CGLIB$$") || Enhancer.isEnhanced(type);
        } catch (Exception e)
        {
            log.info(type.getName(),e);
        }
        return false;
    }

    /**
     *
     * @param object 对象
     * @return 得到
     */
    public static String getClassName(Object object)
    {
        if (isProxy(object.getClass()))
        {
            return StringUtil.substringBefore(object.getClass().getName(),"$$");
        }

        return object.getClass().getName();
    }

    public static String getClassName(Class<?> cls)
    {
        if (isProxy(cls))
        {
            return StringUtil.substringBefore(cls.getName(),"$$");
        }
        return cls.getName();
    }


    /**
     *
     * @param cls 类
     * @return 得到真实的触发对象类型
     */
    public static Class<?> getClass(Class<?> cls)
    {
        if (isProxy(cls))
        {
            String className = getClassName(cls);
            if ("net.sf.cglib.empty.Object".equals(className))
            {
                return null;
            }
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
