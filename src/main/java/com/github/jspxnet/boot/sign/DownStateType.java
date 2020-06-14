/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.sign;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-6-10
 * Time: 18:55:58
 */
public abstract class DownStateType {
    private DownStateType() {

    }

    public static final int FINISH = 0;

    public static final int WAITING = 1;  //等待将要下载的电影

    public static final int INITIALIZE = 2;  //等待将要下载的电影

    public static final int DOWNLOADING = 4; //在下载的电影

    public static final int BT_DOWNLOADING = 5; //在下载的电影

    public static final int STOP = 10;   //停止取消下载

    //public static final int HAVOC = 20;  //下载不了的，删除

    public static final int DELETE = 50;  //下载了又删除的

    public static final int ERROR = 100;  //下载发生错误的电影

    public static final int NO_DOWN = 200;   //标志为不下载的电影

    public static final int PAUSE = 300;   //标志为不下载的电影


    public static final int STATE_QUEUED = 75;

    public static final int All = 1000;


}