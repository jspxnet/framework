/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2004-4-15
 * Time: 16:22:16
 */
public class FileInfo {
    public FileInfo() {

    }

    private String name = StringUtil.empty;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private Date date = DateUtil.empty;

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    private long size = 0;

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    private String type = StringUtil.empty;

    public void setType(String type) {

        this.type = type;
    }

    public String getType() {
        return type;
    }

    private int isDir = 0;

    public void setIsDir(int isDir) {
        this.isDir = isDir;
    }

    public int getIsDir() {
        return isDir;
    }

    private String absolutePath = StringUtil.empty;

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getFullName() {
        return FileUtil.mendPath(absolutePath);
    }

    private String decreasePath = StringUtil.empty;

    public String getDecreasePath() {
        return decreasePath;
    }

    public void setDecreasePath(String decreasePath) {
        this.decreasePath = decreasePath;
    }
}