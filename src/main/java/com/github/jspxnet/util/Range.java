/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import com.github.jspxnet.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-6-18
 * Time: 19:58:46
 */
public class Range implements Serializable {
    private String id;
    private byte[] bytes;
    private long seek = 0;
    private int readLength = 0;
    private long length = 0;
    private int pack = 0;


    public Range() {

    }

    public void setSeek(long seek) {
        this.seek = seek;
    }

    public long getSeek() {
        return seek;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }


    public void setReadLength(int readLength) {
        this.readLength = readLength;
    }

    public int getReadLength() {
        return readLength;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }

    public int getPack() {
        return pack;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id").append(StringUtil.EQUAL).append(id).append("\r\n");
        sb.append("pack").append(StringUtil.EQUAL).append(pack).append("\r\n");
        sb.append("readLength").append(StringUtil.EQUAL).append(readLength).append("\r\n");
        sb.append("length").append(StringUtil.EQUAL).append(length).append("\r\n");
        sb.append("seek").append(StringUtil.EQUAL).append(seek).append("\r\n");
        return sb.toString();
    }

}