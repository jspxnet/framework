/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.iterator;

import com.github.jspxnet.scriptmark.ListIterator;
import org.mozilla.javascript.ScriptableObject;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-30
 * Time: 12:29:41
 * 空封装
 */
public class NullIterator extends ScriptableObject implements ListIterator {
    public NullIterator() {

    }

    @Override
    public int getIndex() {
        return 0;
    }

    public void jsConstructor() {

    }

    @Override
    public String getClassName() {
        return "NullIterator";
    }


    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        return null;
    }

    @Override
    public void remove() {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public int getLength() {
        return 0;
    }
}