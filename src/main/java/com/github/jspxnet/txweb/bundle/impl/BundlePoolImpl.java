/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle.impl;

import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.bundle.BundlePool;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-2-6
 * Time: 16:13:10
 */
public class BundlePoolImpl implements BundlePool {
    private Map<String, Bundle> pool = new Hashtable<String, Bundle>();

    public BundlePoolImpl() {

    }

    @Override
    public void setPool(Map<String, Bundle> pool) {
        this.pool.clear();
        this.pool = null;
        this.pool = pool;
    }

    @Override
    public Map<String, Bundle> getPool() {
        return pool;
    }

    @Override
    public void setBundle(Bundle bundle) {
        pool.put(bundle.getDataType(), bundle);
    }

    @Override
    public Bundle getBundleProvider(String locale) {
        return pool.get(locale);
    }


    @Override
    public boolean containsKey(String locale) {
        return pool.containsKey(locale);
    }

}