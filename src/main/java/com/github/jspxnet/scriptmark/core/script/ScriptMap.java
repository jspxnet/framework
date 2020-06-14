/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.script;


import java.util.*;
import java.io.Serializable;

import com.github.jspxnet.json.JSONObject;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-11
 * Time: 15:17:38
 * Map 适配器         extends ScriptableObject
 */

import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-11
 * Time: 15:17:38
 * Map 适配器         extends ScriptableObject
 */
public class ScriptMap extends HashMap<String, Object> implements Map<String, Object>, Serializable {

    public ScriptMap(Map map) {
        super.putAll(map);
    }

    @Override
    public Object get(Object key) {
        Object o = super.get(key);
        if (o == null) {
            return StringUtil.empty;
        }
        return o;
    }

    public Object get(String key, Object def) {
        Object o = super.get(key);
        if (o == null) {
            return def;
        }
        return o;
    }

    public boolean getBoolean(String key) {
        return ObjectUtil.toBoolean(super.get(key));
    }


    @Override
    public String toString() {
        JSONObject jo = new JSONObject(this, true);
        return jo.toString(4);
    }
}