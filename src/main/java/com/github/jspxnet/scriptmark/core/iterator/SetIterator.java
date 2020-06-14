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

import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-27
 * Time: 14:38:56
 */
public class SetIterator extends ScriptableObject implements ListIterator {
    private Iterator array;

    private int length;

    private int index = 0;

    public SetIterator() {

    }

    @Override
    public String getClassName() {
        return "SetIterator";
    }

    @Override
    public int getIndex() {
        return index;
    }

    public SetIterator(Object array) {
        jsConstructor(array);
    }

    public void jsConstructor(Object array) {
        this.array = ((Set) array).iterator();
        this.length = ((Set) array).size();
    }

    @Override
    public boolean hasNext() {
        return index < length;
    }

    @Override
    public Object next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        index++;
        return array.next();
    }

    @Override
    public void remove() {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public int getLength() {
        return length;
    }
}