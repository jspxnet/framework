package com.github.jspxnet.sioc.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.annotation.*;
import com.github.jspxnet.sioc.scheduler.TaskProxy;
import com.github.jspxnet.sober.annotation.SqlMap;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Transaction;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

import java.lang.reflect.Method;

public final class AnnotationUtil {
    private AnnotationUtil() {
    }

    /**
     * 执行Init 标签
     *
     * @param bean bean对象
     * @throws Exception 异常
     */
    public static void invokeInit(Object bean) throws Exception {
        if (bean == null) {
            return;
        }
        //如果没有配置就检查是否有标签方式的
        Method[] methods = ClassUtil.getDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            Init init = method.getAnnotation(Init.class);
            if (init != null && method.getParameterTypes().length==0) {
                method.setAccessible(true);
                method.invoke(bean);
            }
        }
    }

    /**
     * 执行Init 标签
     *
     * @param bean bean对象
     * @throws Exception 异常
     */
    public static void invokeDestroy(Object bean) throws Exception {
        if (bean == null) {
            return;
        }
        //如果没有配置就检查是否有标签方式的
        Method[] methods = ClassUtil.getDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            Destroy destroy = method.getAnnotation(Destroy.class);
            if (destroy != null) {
                BeanUtil.invoke(bean, method.getName());
            }
        }
    }

    /**
     * @param taskProxy 任务对象
     * @return 生成任务ID
     */
    public static String getScheduledId(TaskProxy taskProxy) {
        if (taskProxy == null) {
            return StringUtil.empty;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(taskProxy.getClass().getName()).append("_")
                .append(taskProxy.getMethodName()).append("_").append(taskProxy.getPattern())
                .append("_").append(taskProxy.isOnce()).append("_").append(taskProxy.getDelayed());
        return EncryptUtil.getMd5(stringBuilder.toString());
    }


    /**
     * @param cls 类对象
     * @return 判断是否存在定时任务标签
     */
    public static boolean hasScheduled(Class<?> cls) {

        //全局开关
        boolean sysUseSchedule = ObjectUtil.toBoolean(EnvFactory.getEnvironmentTemplate().getString(Environment.USE_SCHEDULE, "true"));
        Method[] methods = ClassUtil.getDeclaredMethods(cls);
        if (methods == null) {
            return false;
        }
        for (Method method : methods) {
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (scheduled==null)
            {
                return false;
            }
            if (scheduled.force()) {
                //强制开启
                return true;
            }
            else if (sysUseSchedule)
            {
                //通过系统开关控制
                return true;
            }
        }
        return false;
    }

    /**
     * @param cls 类对象
     * @return 自动生成beanId
     */
    public static String getBeanId(Class<?> cls) {
        if (cls == null) {
            return "";
        }
        RpcClient rpcClient = cls.getAnnotation(RpcClient.class);
        if (rpcClient != null && !Empty.class.equals(rpcClient.bind())) {
            return rpcClient.bind().getName();
        }
        Bean bean =  cls.getAnnotation(Bean.class);
        if (bean != null && !Empty.class.equals(bean.bind())) {
            return bean.bind().getName();
        }
        Class<?> ices = null;
        HttpMethod httpMethod = cls.getAnnotation(HttpMethod.class);
        if (httpMethod != null)
        {
            ices = ClassUtil.getImplements(cls);
            if (ices==null)
            {
                return cls.getName();
            }
        }
        ices = ClassUtil.getImplements(cls);
        if (!ObjectUtil.isEmpty(ices)) {
            return ices.getName();

        }
        return cls.getName();
    }

    /**
     *
     * @param cls 类
     * @return  判断是否有需要代理执行的方法
     */
    public static boolean hasProxyMethod(Class<?> cls) {
        if (cls==null)
        {
            return false;
        }
        for (Method method:cls.getDeclaredMethods())
        {
            if (method.getAnnotation(Transaction.class)!=null)
            {
                return true;
            }
            if (method.getAnnotation(SqlMap.class)!=null)
            {
                return true;
            }

        }
        return false;
    }

}
