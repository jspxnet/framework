package com.github.jspxnet.boot;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/3/27 18:50
 * @description: 整合spring ioc
 **/
@Component
public class SpringBeanContext {
    @Autowired
    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationContext)  {
        if (context == null) {
            context = applicationContext;
        }
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        if (context==null)
        {
            return null;
        }
        return context.getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        if (context==null)
        {
            return null;
        }
        return context.getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        if (context==null)
        {
            return null;
        }
        return context.getBean(name, clazz);
    }
}
