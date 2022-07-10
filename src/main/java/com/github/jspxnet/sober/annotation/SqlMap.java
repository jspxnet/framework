package com.github.jspxnet.sober.annotation;

import com.github.jspxnet.sober.enums.ExecuteEnumType;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.utils.StringUtil;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/17 23:20
 * description: jspbox
 *
 *
 * @author Administrator*/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlMap
{
    //sql xml id
    String id() default StringUtil.empty;
    //返回单一对象 int 单个Bean
    QueryModelEnumType mode() default QueryModelEnumType.LIST;
    //空间
    String namespace() default StringUtil.empty;
    //执行方式
    ExecuteEnumType execute() default ExecuteEnumType.QUERY;

    //当前页变量名称
    String currentPage() default "currentPage";

    //每月返回条数 变量名称
    String count() default "count";

    //载入映射对象
    boolean nexus() default false;

    //拦截器,多个用分号隔开
    String[] intercept() default StringUtil.empty;


}
