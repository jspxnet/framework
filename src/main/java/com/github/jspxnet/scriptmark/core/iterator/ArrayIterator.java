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

import java.lang.reflect.Array;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-30
 * Time: 12:11:14
 * 数值封装
 */
public class ArrayIterator extends ScriptableObject implements ListIterator {
    private Object array;

    private int length;

    private int index = 0;

    public ArrayIterator() {

    }

    @Override
    public String getClassName() {
        return "ArrayIterator";
    }

    @Override
    public int getIndex() {
        return index;
    }

    public ArrayIterator(Object array) {
        jsConstructor(array);
    }

    public void jsConstructor(Object array) {
        this.array = array;
        this.length = Array.getLength(array);
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
        return Array.get(array, index++);
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