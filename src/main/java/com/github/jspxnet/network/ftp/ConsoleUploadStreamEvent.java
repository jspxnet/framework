/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.network.ftp;

import com.github.jspxnet.utils.StreamEvent;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-9-22
 * Time: 19:01:22
 */
public class ConsoleUploadStreamEvent implements StreamEvent {

    private long size = 0;

    @Override
    public void setFullSize(long fullSize) {
        if (fullSize != size) {
            System.out.println("上传中断");
        } else {
            System.out.println("上传完成，文件大小为:" + fullSize / 1024 + "k");
        }

    }

    @Override
    public void setSize(long size) {
        this.size = size;
        System.out.print("上传完成:" + size / 1024 + "k");

    }
}