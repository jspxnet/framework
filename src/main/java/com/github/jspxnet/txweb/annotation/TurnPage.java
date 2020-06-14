/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.annotation;

import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-6-13
 * Time: 23:43:23
 * 翻页标签
 */


@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TurnPage {
    //模版文件名 turnpage.ftl
    String file() default "turnpage.ftl";

    //得到总行数方法
    String totalCount() default "@totalCount";

    //默认行数
    String rows() default "@count";

    //翻页的脚本模板
    String currentPage() default "@currentPage";

    //显示个数
    String count() default "@count";

    //翻页按钮个数
    String bound() default "3";

    //请求中的参数querystring
    String params() default "";

    //开关,为了提高性能，提交时候传递参数判断释放运行,当为auto的时候 如果action name 里边有list 就为true
    //正规表达式使用 [ ] 挂起表示
    String enable() default "[list|\\w+list|list\\S*]";
}