/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.type;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-30
 * Time: 11:54:57
 */
public class ListIterator implements Iterator {

    private Object array;

    private int length;

    private int index = 0;

    public ListIterator(Object array) {
        if (!array.getClass().isArray()) {
            return;
        }
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


}