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

    int maxPostSize() default 10000000; //100M

    boolean debug() default true;

}
