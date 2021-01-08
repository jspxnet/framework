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
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-1
 * Time: 12:46:28
 * Map封装
 */
public class MapIterator extends ScriptableObject implements ListIterator {
    private Iterator<Object> array;

    private int length;

    private int index = 0;

    public MapIterator() {

    }

    @Override
    public String getClassName() {
        return "MapIterator";
    }

    @Override
    public int getIndex() {
        return index;
    }

    public MapIterator(Object array) {
        jsConstructor(array);
    }

    public void jsConstructor(Object array) {
        this.array = ((Map) array).values().iterator();// .values().iterator();
        this.length = ((Map) array).size();
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