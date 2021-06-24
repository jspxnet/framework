package com.github.jspxnet.boot.annotation;

import com.github.jspxnet.utils.StringUtil;
import java.lang.annotation.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/3 1:16
 * description:启动配置
 **/
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JspxNetBootApplication {
    //命名空间,ioc 注入是用
    int port() default 80;

    //网页目录
    String webPath() default StringUtil.empty;

    int threads() default 2;

    boolean cors() default true;

    String ip() default "127.0.0.1";


    /*

         boolean openRedis = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_SESSION_REDIS));
        String redisConfig = properties.getProperty(Environment.SERVER_REDISSON_SESSION_CONFIG);

        int port = StringUtil.toInt(properties.getProperty(Environment.SERVER_PORT,"8080"));
        String webPath = properties.getProperty(Environment.SERVER_WEB_PATH,System.getProperty("user.dir"));
        String ip = properties.getProperty(Environment.SERVER_IP,"127.0.0.1");
        boolean cors = StringUtil.toBoolean(properties.getProperty(Environment.SERVER_CORS,"true"));
        int threads = StringUtil.toInt(properties.getProperty(Environment.SERVER_THREADS,"3"));
     */
}
