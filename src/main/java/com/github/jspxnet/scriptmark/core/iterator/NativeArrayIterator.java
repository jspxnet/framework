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
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-10
 * Time: 15:22:55
 * js array 封装
 */
public class NativeArrayIterator extends ScriptableObject implements ListIterator {
    private NativeArray array;

    private int length;

    private int index = 0;

    public NativeArrayIterator() {

    }

    @Override
    public String getClassName() {
        return "NativeArrayIterator";
    }

    @Override
    public int getIndex() {
        return index;
    }

    public NativeArrayIterator(NativeArray array) {
        jsConstructor(array);
    }

    public void jsConstructor(NativeArray array) {
        this.array = array;
        this.length = array.getIds().length;

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
        return array.get(index++, array.getParentScope());
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