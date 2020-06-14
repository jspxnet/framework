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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.SystemUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-16
 * Time: 11:15:01
 * 系统启动的时候运行一个shell命令
 * <p>
 * startShell=start.bat 启动时候要执行的命令
 * stopShell=start.bat 停止时候要执行的命令
 */

public class ShellAutoRun {
    final private static Logger log = LoggerFactory.getLogger(ShellAutoRun.class.getName());
    private String startShell;
    private String stopShell;
    private boolean enable = true;

    public String getStartShell() {
        return startShell;
    }

    public void setStartShell(String startShell) {
        this.startShell = startShell;
    }

    public String getStopShell() {
        return stopShell;
    }

    public void setStopShell(String stopShell) {
        this.stopShell = stopShell;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    public void run() {
        if (enable && StringUtil.hasLength(startShell)) {
            try {
                log.info(SystemUtil.shell(startShell));
            } catch (Exception e) {
                log.error(startShell, e);
                e.printStackTrace();
            }
        }
    }

}