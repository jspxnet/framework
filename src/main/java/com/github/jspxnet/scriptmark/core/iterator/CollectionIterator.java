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

import java.util.Collection;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-30
 * Time: 12:19:38
 * Collection封装
 */
public class CollectionIterator extends ScriptableObject implements ListIterator {
    private Iterator<?> array;

    private int length;

    private int index = 0;

    public CollectionIterator() {

    }

    @Override
    public String getClassName() {
        return "CollectionIterator";
    }

    @Override
    public int getIndex() {
        return index;
    }

    public CollectionIterator(Object array) {
        jsConstructor(array);
    }

    public void jsConstructor(Object array) {
        this.array = ((Collection) array).iterator();
        this.length = ((Collection) array).size();
        index = 0;
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