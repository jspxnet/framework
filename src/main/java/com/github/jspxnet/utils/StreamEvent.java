/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-9-22
 * Time: 18:46:23
 * StreamUtil 中拷贝流的时候我们有时候需要看到流量，得到进度
 */
public interface StreamEvent extends Serializable {

    void setFullSize(long size);

    void setSize(long size);

}