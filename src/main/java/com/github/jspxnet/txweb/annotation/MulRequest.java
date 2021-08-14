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

import com.github.jspxnet.txweb.enums.FileCoveringPolicyEnumType;
import java.lang.annotation.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-8-18
 * Time: 18:29:50
 * private boolean covering = false;
 * private int maxPostSize = 3 * 1024 * 1024;
 * 上传处理
 */


@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MulRequest {
    FileCoveringPolicyEnumType covering() default FileCoveringPolicyEnumType.JSPX; //是否覆盖文件

    String saveDirectory() default "saveDirectory"; //保存目录

    String fileTypes() default "*"; //允许上传的文件类型

    String maxPostSize() default "800944751"; //最大上传大小
}