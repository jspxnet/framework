/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.type;

import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-30
 * Time: 4:35:25
 */
public class DoubleProvider extends AbstractType {
    @Override
    public String toString(Object o) {
        return new DecimalFormat(format).format(o); // DecimalFormat非线程安全, 所以每次均new实例.
    }
}