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
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ObjectUtil;
import org.mozilla.javascript.ScriptableObject;


/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-15
 * Time: 15:32:27
 */
public class RangeIterator extends ScriptableObject implements ListIterator {
    private int begin = 0;
    private int length;
    private int index = 0;
    private int end = 0;
    private boolean isChar = false;
    private boolean lowToHeight = true;

    public RangeIterator() {

    }

    public RangeIterator(Object begin, Object send) {
        jsConstructor(begin, send);
    }

    @Override
    public String getClassName() {
        return "RangeIterator";
    }

    public void jsConstructor(Object sbegin, Object send) {

        if (StringUtil.isStandardNumber(sbegin.toString()) && StringUtil.isStandardNumber(send.toString())) {
            begin = ObjectUtil.toInt(sbegin);
            end = ObjectUtil.toInt(send);
            isChar = false;
            if (begin > end) {
                lowToHeight = false;
            }
        } else if (sbegin instanceof Character || sbegin instanceof String || sbegin.getClass().getName().toLowerCase().contains("char")) {
            begin = sbegin.toString().charAt(0);
            end = send.toString().charAt(0);
            if (begin > end) {
                lowToHeight = false;
            }
            isChar = true;
        }
        this.length = Math.abs(end - begin) + 1;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean hasNext() {
        if (lowToHeight) {
            return index < length;
        } else {
            return length - Math.abs(index) > 0;
        }
    }

    @Override
    public Object next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        int result;
        if (lowToHeight) {
            result = begin + index;
            index++;
        } else {
            result = begin + index;
            index--;
        }
        if (isChar) {
            Character character = (char) result;
            return character.toString();
        } else {
            return result;
        }
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