/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.filter;

import com.github.jspxnet.component.zhex.WordFilter;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-1-30
 * Time: 下午1:01
 */
public abstract class AbstractWordFilter implements WordFilter {
    final public static String GB_BIG5 = "gbToBig5";
    final public static String BIG5_GB = "big5ToGb";
}