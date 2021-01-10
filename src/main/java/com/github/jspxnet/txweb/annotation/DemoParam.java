package com.github.jspxnet.txweb.annotation;

import java.lang.annotation.*;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/1/10 16:24
 * @description: 演示参数
 **/
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DemoParam {
    String value();
}
