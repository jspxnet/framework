/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.util;

import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-19
 * Time: 16:54:11
 */
public class SessionMap extends HashMap implements Map, Cloneable, Serializable {
    final private HttpSession session;

    public SessionMap(HttpSession session) {
        this.session = session;
    }

    @Override
    public Object get(Object key) {

        if (key == null) {
            return null;
        }
        Object o = super.get(key);
        if (o == null) {
            if (TXWeb.token.equals(key)) {
                String sid = (String) session.getAttribute(TXWeb.token);
                if (StringUtil.hasLength(sid)) {
                    return sid;
                }
                return session.getId();
            } else if (ClassUtil.isDeclaredMethod(session.getClass(), ClassUtil.METHOD_NAME_GET + StringUtil.capitalize((String) key))) {
                o = BeanUtil.getProperty(session, (String) key);
            } else {
                return session.getAttribute((String) key);
            }
        }
        return o;
    }

}