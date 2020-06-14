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
 * Time: 12:25:31
 * 字符串序列
 */
public class StringIterator extends ScriptableObject implements ListIterator {
    private String array;

    private int length;

    private int index = 0;

    public StringIterator() {

    }

    @Override
    public int getIndex() {
        return index;
    }

    public void jsConstructor(String array) {
        this.array = array;
        this.length = array.length();
    }

    @Override
    public String getClassName() {
        return "StringIterator";
    }

    public StringIterator(String array) {
        this.array = array;
        this.length = array.length();
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
        return array.charAt(index++);
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