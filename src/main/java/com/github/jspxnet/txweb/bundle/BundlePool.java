/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-11-8
 * Time: 16:45:47
 */
public interface BundlePool {
    void setPool(Map<String, Bundle> pool);

    Map<String, Bundle> getPool();

    void setBundle(Bundle bundle);

    Bundle getBundleProvider(String locale);

    boolean containsKey(String locale);

}