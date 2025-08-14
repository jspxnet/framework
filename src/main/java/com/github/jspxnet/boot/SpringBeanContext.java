package com.github.jspxnet.boot;


import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/27 18:50
 * description: 整合spring ioc
 **/

public class SpringBeanContext {
    private static Object context;

    public static void setApplicationContext(Object applicationContext)  {
        if (context == null) {
            context = applicationContext;
        }
    }

    /**
     *
     * @return 获取applicationContext
     */
    public static Object getApplicationContext() {
        return context;
    }

    /**
     *
     * @param name bean name
     * @return bean object
     */
    public static Object getBean(String name) {
        if (context==null || name==null)
        {
            return null;
        }
        //spring 的bean id, 不包括包明

        String beanId = StringUtil.getSpringBeanId(name);
        if (StringUtil.isEmpty(beanId))
        {
            return null;
        }
        return BeanUtil.getProperty(context,"getBean",new Object[]{beanId},false);
    }

}
