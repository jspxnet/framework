/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sioc.scheduler.task;

import com.github.jspxnet.sioc.annotation.Scheduled;



import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-10-28
 * Time: 下午5:24
 */
@Slf4j
public class DeleteFileTask {
    private String folder = StringUtil.empty;
    private int day = 60;
    private String types;
    private int need = 1;
    private boolean enable = false;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getNeed() {
        return need;
    }

    public void setNeed(int need) {
        this.need = need;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    //每天23:59执行一次
    @Scheduled(cron = "* 59 23 * * *")
    public void run() {
        if (enable && !StringUtil.isNull(folder)) {
            try {
                File file = new File(folder);
                if (file.isDirectory() && file.exists()) {
                    FileUtil.deleteFile(file, day, types, need);
                }
            } catch (Exception e) {
                log.error(folder, e);
                e.printStackTrace();
            }
        }
    }
}