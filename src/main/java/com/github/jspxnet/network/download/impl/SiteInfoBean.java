/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.download.impl;

import java.net.URL;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-8-4
 * Time: 21:15:44
 */
public class SiteInfoBean {
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    private URL url; //Site's URL

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

    private String tempFilePath; //Saved File's Path

    public String getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(String saveFile) {
        this.saveFile = saveFile;
    }

    private String saveFile; //Saved File's Name


    public int getSplitter() {
        return splitter;
    }

    public void setSplitter(int splitter) {
        this.splitter = splitter;
    }

    private int splitter = 1; //Count of Splited Downloading File


    public SiteInfoBean() {
    }

    public SiteInfoBean(URL url, String tempFilePath, String saveFile, int splitter) {
        this.url = url;
        this.tempFilePath = tempFilePath;
        this.saveFile = saveFile;
        this.splitter = splitter;
    }

}