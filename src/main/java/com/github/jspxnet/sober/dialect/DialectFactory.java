/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.dialect;

import com.github.jspxnet.utils.ClassUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 22:10:21
 */
public class DialectFactory {
    public static Dialect createDialect(String databaseType) throws Exception {
        return (Dialect) ClassUtil.newInstance("com.github.jspxnet.sober.dialect." + databaseType + "Dialect");
    }
}