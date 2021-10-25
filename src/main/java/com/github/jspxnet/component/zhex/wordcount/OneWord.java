/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.wordcount;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-25
 * Time: 16:48:07
 * 单词容器
 */
public class OneWord {
    private String value;

    /**
     * Construct a Word object,has the count 1.
     *
     * @param value 字符串
     */
    public OneWord(String value) {
        this.value = value.toLowerCase();
    }

    private int count = 1;

    /**
     * this method is only invoked by WordCount class
     */
    protected void increase() {
        count++;
    }

    /**
     * @return the word as the lower case.
     */
    public String getWord() {
        return value;
    }

    /**
     * @return the apperance times of this word.
     */
    public int getCount() {
        return count;
    }

    /**
     * @return if the word was the same ignore case,return true.
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof OneWord) && (((OneWord) o).value.equals(value));
    }

    /**
     * @return the hashCode of the word.
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}